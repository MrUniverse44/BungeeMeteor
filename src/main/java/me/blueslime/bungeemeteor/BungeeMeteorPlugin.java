package me.blueslime.bungeemeteor;

import me.blueslime.bungeemeteor.actions.Actions;
import me.blueslime.bungeemeteor.colors.TextUtilities;
import me.blueslime.bungeemeteor.conditions.Conditions;
import me.blueslime.bungeemeteor.getter.MeteorGetter;
import me.blueslime.bungeemeteor.implementation.Implements;
import me.blueslime.bungeemeteor.implementation.module.Module;
import me.blueslime.bungeemeteor.implementation.module.RegisteredModule;
import me.blueslime.bungeemeteor.implementation.registered.RegistrationData;
import me.blueslime.bungeemeteor.logs.LoggerType;
import me.blueslime.bungeemeteor.logs.MeteorLogger;
import me.blueslime.bungeemeteor.storage.StorageDatabase;
import me.blueslime.bungeemeteor.utils.FileUtil;

import me.blueslime.bungeeutilitiesapi.utils.consumer.PluginConsumer;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public abstract class BungeeMeteorPlugin extends Plugin implements MeteorLogger {
    private final Map<LoggerType, String> logMap = new EnumMap<>(LoggerType.class);
    private final Map<Class<?>, Module> moduleMap = new ConcurrentHashMap<>();

    /**
     * Use here the {@link BungeeMeteorPlugin#initialize(Object)} method to load the entire plugin data.
     */
    public abstract void onEnable();

    /**
     * Initialize the whole plugin
     * @param instance is the {@link Plugin} instanced class.
     */
    protected void initialize(Object instance) {
        new MeteorGetter(this);

        new Actions(this);
        new Conditions(this);
        registerDatabases();
        registerModules();

        loadModules();
    }

    @Override
    public void onDisable() {
        shutdown();
    }

    /**
     * @param modules to be registered in the plugin.
     * @return plugin instance
     */
    public BungeeMeteorPlugin registerModule(Module... modules) {
        if (modules != null && modules.length >= 1) {
            for (Module module : modules) {
                fetchDataEntries(module);
            }
        }
        return this;
    }

    /**
     * @param modules to be registered in the plugin.
     * @return plugin instance
     */
    @SafeVarargs
    public final BungeeMeteorPlugin registerModule(Class<? extends Module>... modules) {
        if (modules != null && modules.length >= 1) {
            for (Class<? extends Module> moduleClass : modules) {
                Module module = Implements.createInstance(moduleClass);
                fetchDataEntries(module);
            }
        }
        return this;
    }

    private void fetchDataEntries(Module module) {
        if (module == null) {
            return;
        }
        if (module instanceof RegisteredModule registeredModule) {
            if (registeredModule.hasIdentifier()) {
                if (registeredModule.getIdentifier().isEmpty()) {
                    Implements.addRegistrationData(
                        RegistrationData.fromData(registeredModule, registeredModule.getClass()), registeredModule
                    );
                } else {
                    Implements.addRegistrationData(
                        RegistrationData.fromData(registeredModule, registeredModule.getClass(), registeredModule.getIdentifier()), registeredModule
                    );
                }
            } else {
                Implements.addRegistrationData(
                    RegistrationData.fromData(registeredModule, registeredModule.getClass()), registeredModule
                );
            }
        }
        moduleMap.put(module.getClass(), module);
    }

    /**
     * Append all registered modules size in the console
     */
    public void finish() {
        getLogger().info("Registered " + moduleMap.size() + " module(s).");
    }

    private void finishOwn() {
        getLogger().info("Registered " + moduleMap.size() + " origin module(s).");
    }

    private void loadModules() {
        for (Module module : new HashSet<>(moduleMap.values())) {
            module.initialize();
        }
    }

    /**
     * Here you can use the {@link #registerModule(Module...)} or {@link #registerModule(Class[])}
     * This method is automatically used internally.
     */
    public abstract void registerModules();

    /**
     * Here we register our databases
     * Here you can use the {@link BungeeMeteorPlugin#registerDatabase(StorageDatabase...)}
     */
    public void registerDatabases() {

    }

    public void registerDatabase(StorageDatabase... databases) {
        for (StorageDatabase database : databases) {
            database.connect();
        }
    }

    /**
     * This method reloads all other modules
     */
    public void reload() {
        for (Module module : new HashSet<>(moduleMap.values())) {
            module.reload();
        }
    }

    /**
     * This method shutdown all other modules
     */
    public void shutdown() {
        for (Module module : new HashSet<>(moduleMap.values())) {
            module.shutdown();
        }
    }

    /**
     * Loads a file from the main plugin data folder
     * @param fileName file
     * @param resource if the file don't exist, it supports a resource to be loaded in that file, it supports null
     * @return FileConfiguration
     */
    public Configuration load(String fileName, String resource) {
        return load(new File(getDataFolder(), fileName), resource);
    }

    /**
     * Loads a FileConfiguration from a file
     * @param fetchFile file
     * @param resource if the file don't exist, it supports a resource to be loaded in that file, it supports null
     * @return FileConfiguration
     */
    public Configuration load(File fetchFile, String resource) {
        if (resource == null) {
            FileUtil.saveResource(fetchFile, null);
            return PluginConsumer.ofUnchecked(
                () -> ConfigurationProvider.getProvider(YamlConfiguration.class).load(fetchFile),
                e -> getLogs().error(e, "Can't load file: " + fetchFile.getName()),
                Configuration::new
            );
        }

        InputStream src = FileUtil.build(resource);
        src = src == null ? getResourceAsStream(resource) : src;

        FileUtil.saveResource(fetchFile, src);

        return PluginConsumer.ofUnchecked(
            () -> ConfigurationProvider.getProvider(YamlConfiguration.class).load(fetchFile),
            e -> getLogs().error(e, "Can't load file: " + fetchFile.getName()),
            Configuration::new
        );
    }

    /**
     * Save a Configuration file in a specified file
     * @param configuration file
     * @param file location to be saved
     * @param resource if the file don't exist, and you have a template, it supports null.
     */
    public void save(Configuration configuration, File file, String resource) {
        if (configuration == null || file == null) {
            return;
        }

        InputStream src = FileUtil.build(resource);
        src = src == null ? getResourceAsStream(resource) : src;

        FileUtil.saveResource(file, src);

        PluginConsumer.process(
            () -> ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file),
            e -> getLogs().error(e, "Can't save file: " + file.getName())
        );
    }

    public MeteorLogger getLogs() {
        return this;
    }

    @Override
    public void send(String... messages) {
        CommandSender sender = getProxy().getConsole();

        for (String message : messages) {
            String convert = TextUtilities.colorize(message);
            if (convert == null) {
                sender.sendMessage(
                    new TextComponent(message)
                );
            } else {
                sender.sendMessage(
                    new TextComponent(convert)
                );
            }
        }
    }

    @Override
    public void build() {
        // DO NOT NOTHING
    }


    /**
     * This method is actually deprecated
     * please use {@link Implements#fetch(Class)} or {@link Implements#fetch(Class, String)}
     * instead of this.
     * @param module to get
     * @return module instance
     * @param <T> type of module
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> module) {
        if (moduleMap.containsKey(module)) {
            return (T) moduleMap.get(module);
        }
        return Implements.fetch(module);
    }

    /**
     * Gets the module map
     * @return module list
     * Please use {@link Implements#getRegistrationMap()}
     */
    @Deprecated
    public Map<Class<?>, Module> getModules() {
        return moduleMap;
    }

    @Override
    public MeteorLogger setPrefix(LoggerType log, String prefix) {
        logMap.put(log, prefix);
        return this;
    }

    @Override
    public String getPrefix(LoggerType prefix) {
        return logMap.computeIfAbsent(
            prefix,
            (k) -> prefix.getDefaultPrefix(getDescription().getName())
        );
    }

    /**
     * Checks if a plugin is enabled
     * @param pluginName to check
     * @return result
     */
    public boolean isPluginEnabled(String pluginName) {
        return getProxy().getPluginManager().getPlugin(pluginName) != null;
    }
}
