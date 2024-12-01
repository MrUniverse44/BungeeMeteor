package me.blueslime.bungeemeteor.actions.type;

import me.blueslime.bungeemeteor.BungeeMeteorPlugin;
import me.blueslime.bungeemeteor.actions.action.Action;
import me.blueslime.bungeeutilitiesapi.text.TextUtilities;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class TitlesAction extends Action {

    public TitlesAction() {
        super("[titles]", "<titles>", "titles:");
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

        parameter = replace(parameter).replace("\\n", "<subtitle>");

        String[] split = replace(parameter).split("\\n");

        String titleString = split[0];
        String subtitleString = split.length > 1 ? split[1] : "";

        Title title = plugin.getProxy().createTitle()
            .title(new TextComponent(TextUtilities.colorize(titleString)))
            .subTitle(new TextComponent(TextUtilities.colorize(subtitleString)))
            .stay(35)
            .fadeIn(15)
            .fadeOut(15);

        for (ProxiedPlayer player : players) {
            player.sendTitle(title);
        }
    }
}

