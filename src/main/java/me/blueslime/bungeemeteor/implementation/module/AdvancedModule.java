package me.blueslime.bungeemeteor.implementation.module;

import me.blueslime.bungeemeteor.BungeeMeteorPlugin;
import me.blueslime.bungeemeteor.implementation.Implementer;
import me.blueslime.bungeemeteor.implementation.entries.Entries;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.PluginManager;

public interface AdvancedModule extends PersistentModule, Implementer, Entries {

    default void registerAll(Listener... listeners) {
        BungeeMeteorPlugin plugin = fetch(BungeeMeteorPlugin.class);

        PluginManager manager = plugin.getProxy().getPluginManager();

        for (Listener listener : listeners) {
            manager.registerListener(plugin, listener);
        }
    }

    default void unregisterAll(Listener... listeners) {
        BungeeMeteorPlugin plugin = fetch(BungeeMeteorPlugin.class);

        PluginManager manager = plugin.getProxy().getPluginManager();

        for (Listener listener : listeners) {
            manager.unregisterListener(listener);
        }
    }
}
