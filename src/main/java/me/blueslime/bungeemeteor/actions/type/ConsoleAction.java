package me.blueslime.bungeemeteor.actions.type;

import me.blueslime.bungeemeteor.BungeeMeteorPlugin;
import me.blueslime.bungeemeteor.actions.action.Action;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class ConsoleAction extends Action {

    public ConsoleAction() {
        super("[console]", "<console>", "console:");
    }

    @Override
    public void execute(BungeeMeteorPlugin plugin, String parameter, List<ProxiedPlayer> players) {
        if (players == null || players.isEmpty()) {
            // Run command lonely
            plugin.getProxy().getPluginManager().dispatchCommand(
                plugin.getProxy().getConsole(),
                replace(parameter)
            );

            return;
        }
        // Run command per user
        for (ProxiedPlayer player : players) {
            plugin.getProxy().getPluginManager().dispatchCommand(
                plugin.getProxy().getConsole(),
                replace(parameter)
            );
        }
    }
}
