package me.blueslime.bukkitmeteor.storage;

import me.blueslime.bukkitmeteor.storage.interfaces.StorageObject;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class StorageDatabase {

    public abstract <T extends StorageObject> CompletableFuture<Optional<T>> loadByIdAsync(Class<T> clazz, String identifier);

    public abstract <T extends StorageObject> Optional<T> loadByIdSync(Class<T> clazz, String identifier);

    public abstract <T extends StorageObject> CompletableFuture<Void> deleteByIdAsync(Class<T> clazz, String identifier);

    public abstract <T extends StorageObject> void deleteByIdSync(Class<T> clazz, String identifier);

    public abstract CompletableFuture<Void> saveOrUpdateAsync(StorageObject obj);

    public abstract void saveOrUpdateSync(StorageObject obj);

    public abstract void closeConnection();

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Object convertValue(Class<?> clazz, String value) {
        if (clazz == String.class) {
            return value;
        }
        if (clazz == Integer.class || clazz == int.class) {
            return Integer.parseInt(value);
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return Boolean.parseBoolean(value);
        }
        if (clazz == Double.class || clazz == double.class) {
            return Double.parseDouble(value);
        }
        if (clazz == Float.class || clazz == float.class) {
            return Float.parseFloat(value);
        }
        if (clazz == Long.class || clazz == long.class) {
            return Long.parseLong(value);
        }
        if (clazz == Byte.class || clazz == byte.class) {
            return Byte.parseByte(value);
        }
        if (clazz == Short.class || clazz == short.class) {
            return Short.parseShort(value);
        }
        if (clazz == Character.class || clazz == char.class) {
            return value.charAt(0);
        }
        if (clazz.isEnum()) {
            return Enum.valueOf((Class<Enum>)clazz, value);
        }
        return value;
    }
}