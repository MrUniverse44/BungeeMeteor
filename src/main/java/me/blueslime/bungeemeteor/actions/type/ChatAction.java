package me.blueslime.bungeemeteor.actions.type;

import me.blueslime.bungeemeteor.BungeeMeteorPlugin;
import me.blueslime.bungeemeteor.actions.action.Action;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatAction extends Action {

    public ChatAction() {
        super("chat:", "[chat]", "<chat>");
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

        List<String> playerNames = null;

        if (parameter.contains("%for:")) {
            // well... this message will be sent for other(s) players.
            playerNames = new ArrayList<>();

            String regex = "%for:(.*?)%";

            Pattern pattern = Pattern.compile(regex);

            Matcher matcher = pattern.matcher(parameter);

            while (matcher.find()) {
                String userName = matcher.group(1);  // El contenido entre %for: y %
                playerNames.add(userName);
            }
        }

        if (playerNames == null) {

            parameter = replace(parameter).replace("\\n", "\n");

            for (ProxiedPlayer player : players) {

                String message = parameter;

                message = message.replace("\\n", "\n");

                player.chat(message);
            }
            return;
        }

        for (String userName : playerNames) {
            ProxiedPlayer player = plugin.getProxy().getPlayer(userName);

            if (player == null) {
                continue;
            }

            parameter = replace(parameter).replace("\\n", "\n");

            String message = parameter;

            message = message.replace("\\n", "\n");

            player.chat(message);
        }
    }
}
