package me.blueslime.bungeemeteor.getter;

import me.blueslime.bungeemeteor.BungeeMeteorPlugin;
import me.blueslime.bungeemeteor.implementation.module.Module;
import me.blueslime.bungeemeteor.implementation.registered.Register;
import me.blueslime.bungeemeteor.logs.MeteorLogger;

import net.md_5.bungee.config.Configuration;

import java.io.File;

public class MeteorGetter implements Module {
    private final BungeeMeteorPlugin plugin;

    public MeteorGetter(BungeeMeteorPlugin plugin) {
        this.plugin = plugin;
        registerImplementedModule(this);
    }

    @Register(identifier = "settings.yml")
    public Configuration provideSettings() {
        return plugin.load(new File(plugin.getDataFolder(), "settings.yml"), "settings.yml");
    }

    @Register(identifier = "folder")
    public File provideFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Register
    public MeteorLogger getLogs() {
        return plugin;
    }

    @Register
    public BungeeMeteorPlugin provideBukkitMeteorPlugin() {
        return plugin;
    }
}
