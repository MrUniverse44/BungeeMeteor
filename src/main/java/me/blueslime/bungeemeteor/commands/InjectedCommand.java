package me.blueslime.bungeemeteor.commands;

import me.blueslime.bungeemeteor.BungeeMeteorPlugin;
import me.blueslime.bungeemeteor.implementation.Implements;
import me.blueslime.bungeemeteor.implementation.module.AdvancedModule;
import me.blueslime.bungeeutilitiesapi.commands.AdvancedCommand;
import net.md_5.bungee.config.Configuration;

import java.util.Collection;
import java.util.List;

public abstract class InjectedCommand extends AdvancedCommand<BungeeMeteorPlugin> implements AdvancedModule {

    public InjectedCommand(String command) {
        super(Implements.fetch(BungeeMeteorPlugin.class), command);
    }

    public InjectedCommand(String command, Collection<String> aliases) {
        super(Implements.fetch(BungeeMeteorPlugin.class), command, List.copyOf(aliases));
    }

    public InjectedCommand(Configuration configuration, String commandPath, String permissionPath, String aliasesPath) {
        super(Implements.fetch(BungeeMeteorPlugin.class), configuration, commandPath, permissionPath, aliasesPath);
    }

    @Override
    public boolean overwriteCommand() {
        return true;
    }
}
