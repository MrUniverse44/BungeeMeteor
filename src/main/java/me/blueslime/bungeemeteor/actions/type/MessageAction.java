package me.blueslime.bungeemeteor.actions.type;

import me.blueslime.bungeemeteor.BungeeMeteorPlugin;
import me.blueslime.bungeemeteor.actions.action.Action;
import me.blueslime.bungeeutilitiesapi.commands.sender.Sender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class MessageAction extends Action {

    public MessageAction() {
        super("[message]", "<message>", "message:");
    }

    /**
     * Execute action
     *
     * @param plugin    of the event
     * @param parameter text
     * @param players   players
     */
    @Override
    public void execute(BungeeMeteorPlugin plugin, String parameter, List<ProxiedPlayer> players) {
        if (players == null || players.isEmpty()) {
            return;
        }

        parameter = replace(parameter).replace("\\n", "\n");

        for (ProxiedPlayer player : players) {

            String message = parameter;

            message = message.replace("\\n", "\n");

            Sender.build(player).send(message);
        }
    }
}

