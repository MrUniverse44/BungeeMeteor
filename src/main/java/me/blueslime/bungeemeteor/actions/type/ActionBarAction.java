package me.blueslime.bungeemeteor.actions.type;

import me.blueslime.bungeemeteor.BungeeMeteorPlugin;
import me.blueslime.bungeemeteor.actions.action.Action;
import me.blueslime.bungeeutilitiesapi.text.TextUtilities;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class ActionBarAction extends Action {

    public ActionBarAction() {
        super("[actionbar]", "<actionbar>", "actionbar:");
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

            player.sendMessage(
                ChatMessageType.ACTION_BAR,
                new TextComponent(TextUtilities.colorize(message))
            );
        }
    }
}


