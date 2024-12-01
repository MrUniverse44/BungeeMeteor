package me.blueslime.bungeemeteor.implementation.entries;

import me.blueslime.bungeemeteor.BungeeMeteorPlugin;
import me.blueslime.bungeemeteor.implementation.Implements;
import me.blueslime.bungeemeteor.implementation.module.Module;

import java.util.List;

@SuppressWarnings("unchecked")
public interface Entries {
    default BungeeMeteorPlugin registerModule(List<Module> modules) {
        return getMeteorPlugin().registerModule(modules.toArray(new Module[0]));
    }

    default BungeeMeteorPlugin registerModuleByClass(List<Class<? extends Module>> modules) {
        return getMeteorPlugin().registerModule(modules.toArray(new Class[0]));
    }

    default BungeeMeteorPlugin getMeteorPlugin() {
        return Implements.fetch(BungeeMeteorPlugin.class);
    }
}
