package me.blueslime.bungeemeteor.storage.type;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import me.blueslime.bungeemeteor.implementation.Implements;
import me.blueslime.bungeemeteor.implementation.module.AdvancedModule;
import me.blueslime.bungeemeteor.storage.StorageDatabase;
import me.blueslime.bungeemeteor.storage.interfaces.*;
import me.blueslime.bungeemeteor.storage.object.ReferencedObject;
import org.bson.Document;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import static com.mongodb.client.model.Filters.eq;

@SuppressWarnings("unused")
public class MongoDatabaseService extends StorageDatabase implements AdvancedModule {

    private MongoClient mongoClient = null;
    private MongoDatabase database = null;

    /* Mongo data */
    private final String databaseName;
    private final String uri;

    /**
     * Initialize mongo database connection
     * @param uri for this connection
     * @param databaseName for this service
     * @param register to register this connection to the Implements
     */
    public MongoDatabaseService(String uri, String databaseName, RegistrationType register) {
        this.databaseName = databaseName;
        this.uri = uri;

        if (register == null) {
            register = RegistrationType.DONT_REGISTER;
        }

        if (register.isDouble() || register.isOnlyThis()) {
            registerImpl(MongoDatabaseService.class, this, true);
        }

        if (register.isDouble()) {
            registerImpl(StorageDatabase.class, this, true);
        }
    }

    /**
     * Initialize mongo database connection
     * @param uri for this connection
     * @param databaseName for this service
     * @param register to register this connection to the Implements
     * @param identifier used for the Implements in {@link Implements#fetch(Class, String)}
     */
    public MongoDatabaseService(String uri, String databaseName, RegistrationType register, String identifier) {
        this.databaseName = databaseName;
        this.uri = uri;

        boolean isSet = identifier != null;

        if (register == null) {
            register = RegistrationType.DONT_REGISTER;
        }

        if (register.isDouble() || register.isOnlyThis()) {
            if (isSet) {
                registerImpl(MongoDatabaseService.class, identifier, this, true);
            } else {
                registerImpl(MongoDatabaseService.class, this, true);
            }
        }

        if (register.isDouble()) {
            if (isSet) {
                registerImpl(StorageDatabase.class, identifier, this, true);
            } else {
                registerImpl(StorageDatabase.class, this, true);
            }
        }
    }

    public void connect() {
        MongoClientSettings mongoClientSettings = MongoClientSettings
            .builder()
            .applyConnectionString(
                new ConnectionString(this.uri)
            ).build();

        this.mongoClient = MongoClients.create(mongoClientSettings);
        this.database = mongoClient.getDatabase(this.databaseName);
    }

    public void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    @Override
    public CompletableFuture<Void> saveOrUpdateAsync(StorageObject obj) {
        return CompletableFuture.runAsync(() -> save(obj));
    }

    @Override
    public void saveOrUpdateSync(StorageObject obj) {
        save(obj);
    }

    private void save(StorageObject obj) {
        if (database == null) {
            throw new IllegalStateException("No connection established. Call connect() first.");
        }
        Class<?> clazz = obj.getClass();
        Document document = new Document();

        Set<String> extraIdentifier = new HashSet<>();

        String identifierValue = null;
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.isAnnotationPresent(StorageIgnore.class)) {
                    continue;
                }
                Object value = field.get(obj);
                if (field.isAnnotationPresent(StorageIdentifier.class)) {
                    identifierValue = value.toString();
                }
                if (field.isAnnotationPresent(StorageExtraIdentifier.class)) {
                    extraIdentifier.add(value.toString());
                }
                String name = field.getName();

                if (field.isAnnotationPresent(StorageKey.class)) {
                    StorageKey key = field.getAnnotation(StorageKey.class);
                    if (!key.key().isEmpty()) {
                        name = key.key();
                    }
                    if (value == null && !key.defaultValue().isEmpty()) {
                        value = convertValue(field.getType(), key.defaultValue());
                    }
                }
                if (isComplexObject(field.getType())) {
                    Document embeddedDocument = handleComplexObject(value);
                    document.append(name, embeddedDocument);
                } else {
                    document.append(name, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        MongoCollection<Document> collection = database.getCollection(clazz.getSimpleName());
        MongoCollection<Document> collectionNaming = database.getCollection(clazz.getSimpleName() + "-StringNaming");

        if (identifierValue != null) {
            collection.replaceOne(eq("_id", identifierValue), document, new ReplaceOptions().upsert(true));
            if (!extraIdentifier.isEmpty()) {
                Document idFetch = new Document();
                idFetch.append("referenced", identifierValue);

                for (String extra : extraIdentifier) {
                    Document completed = new Document();
                    completed.append("extra", extra);
                    completed.append("data", idFetch);

                    collectionNaming.replaceOne(
                        eq("_id", extra.toLowerCase(Locale.ENGLISH)),
                        completed,
                        new ReplaceOptions()
                            .upsert(true)
                    );
                }
            }
        } else {
            collection.insertOne(document);
        }
    }

    private Document handleComplexObject(Object obj) {
        if (obj == null) {
            return null;
        }
        Class<?> clazz = obj.getClass();
        Document embeddedDocument = new Document();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.isAnnotationPresent(StorageIgnore.class)) {
                    continue;
                }
                Object value = field.get(obj);

                String name = field.getName();

                if (field.isAnnotationPresent(StorageKey.class)) {
                    StorageKey key = field.getAnnotation(StorageKey.class);
                    if (!key.key().isEmpty()) {
                        name = key.key();
                    }
                    if (value == null && !key.defaultValue().isEmpty()) {
                        value = convertValue(field.getType(), key.defaultValue());
                    }
                }
                if (isComplexObject(field.getType())) {
                    Document fieldEmbeddedDocument = handleComplexObject(value);
                    embeddedDocument.append(name, fieldEmbeddedDocument);
                } else {
                    embeddedDocument.append(name, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return embeddedDocument;
    }

    @Override
    public <T extends StorageObject> CompletableFuture<Optional<ReferencedObject>> loadByExtraIdentifierAsync(Class<T> clazz, String extraIdentifier) {
        if (database == null) {
            throw new IllegalStateException("No connection established. Call connect() first.");
        }
        return CompletableFuture.supplyAsync(() -> loadByExtraIdentifierSync(clazz, extraIdentifier));
    }

    @Override
    public <T extends StorageObject> Optional<ReferencedObject> loadByExtraIdentifierSync(Class<T> clazz, String extraIdentifier) {
        if (database == null) {
            throw new IllegalStateException("No connection established. Call connect() first.");
        }
        String extra = extraIdentifier.toLowerCase(Locale.ENGLISH);

        MongoCollection<Document> collection = database.getCollection(clazz.getSimpleName() + "-StringNaming");
        Document doc = collection.find(eq("_id", extra)).first();

        if (doc != null) {
            String original = doc.getString("extra");

            Document document = (Document) doc.get("data");
            String reference = document.getString("referenced");

            return Optional.of(new ReferencedObject(
                    original,
                    reference
            ));
        }
        return Optional.empty();
    }

    @Override
    public <T extends StorageObject> CompletableFuture<Optional<T>> loadByIdAsync(Class<T> clazz, String identifier) {
        if (database == null) {
            throw new IllegalStateException("No connection established. Call connect() first.");
        }
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Document> collection = database.getCollection(clazz.getSimpleName());
            Document doc = collection.find(eq("_id", identifier)).first();

            if (doc != null) {
                return Optional.ofNullable(instantiateObject(clazz, doc, identifier));
            }
            return Optional.empty();
        });
    }

    @Override
    public <T extends StorageObject> Optional<T> loadByIdSync(Class<T> clazz, String identifier) {
        if (database == null) {
            throw new IllegalStateException("No connection established. Call connect() first.");
        }
        MongoCollection<Document> collection = database.getCollection(clazz.getSimpleName());
        Document doc = collection.find(eq("_id", identifier)).first();

        if (doc != null) {
            return Optional.ofNullable(instantiateObject(clazz, doc, identifier));
        }
        return Optional.empty();
    }

    @Override
    public <T extends StorageObject> CompletableFuture<Void> deleteByIdAsync(Class<T> clazz, String identifier) {
        return CompletableFuture.runAsync(() -> delete(clazz, identifier));
    }

    @Override
    public <T extends StorageObject> void deleteByIdSync(Class<T> clazz, String identifier) {
        delete(clazz, identifier);
    }

    @Override
    public <T extends StorageObject> CompletableFuture<Set<T>> loadAllAsync(Class<T> clazz) {
        return CompletableFuture.supplyAsync(
            () -> loadAllSync(clazz)
        );
    }

    @Override
    public <T extends StorageObject> Set<T> loadAllSync(Class<T> clazz) {
        Set<T> set = new HashSet<>();

        if (database == null) {
            throw new IllegalStateException("No connection established. Call connect() first.");
        }

        MongoCollection<Document> collection = database.getCollection(clazz.getSimpleName());

        for (Document doc : collection.find()) {
            T object = instantiateObject(clazz, doc, doc.getString("_id"));
            if (object != null) {
                set.add(object);
            }
        }

        return set;
    }

    private void delete(Class<?> clazz, String identifier) {
        if (database == null) {
            throw new IllegalStateException("No connection established. Call connect() first.");
        }
        MongoCollection<Document> collection = database.getCollection(clazz.getSimpleName());
        collection.deleteOne(eq("_id", identifier));
    }

    @Override
    public void closeConnection() {
        disconnect();
    }

    @SuppressWarnings("unchecked")
    private <T extends StorageObject> T instantiateObject(Class<?> clazz, Document doc, String identifier) {
        try {
            for (Constructor<?> constructor : clazz.getConstructors()) {
                if (constructor.isAnnotationPresent(StorageConstructor.class)) {
                    Parameter[] parameters = constructor.getParameters();
                    Object[] values = new Object[parameters.length];

                    for (int i = 0; i < parameters.length; i++) {
                        StorageKey paramAnnotation = parameters[i].getAnnotation(StorageKey.class);
                        String paramName = (paramAnnotation != null && !paramAnnotation.key().isEmpty())
                                ? paramAnnotation.key()
                                : parameters[i].getName();

                        if (isComplexObject(parameters[i].getType())) {
                            Document embeddedDoc = (Document) doc.get(paramName);
                            values[i] = instantiateObject(parameters[i].getType(), embeddedDoc, identifier);
                        } else {
                            if (parameters[i].isAnnotationPresent(StorageIdentifier.class)) {
                                values[i] = identifier;
                            } else {
                                values[i] = doc.get(paramName);
                            }
                        }

                        if (values[i] == null && paramAnnotation != null && !paramAnnotation.defaultValue().isEmpty()) {
                            values[i] = convertValue(parameters[i].getType(), paramAnnotation.defaultValue());
                        }

                    }

                    return (T) constructor.newInstance(values);
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

