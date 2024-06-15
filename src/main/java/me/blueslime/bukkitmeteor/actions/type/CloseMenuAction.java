package me.blueslime.bukkitmeteor.actions.type;

import me.blueslime.bukkitmeteor.BukkitMeteorPlugin;
import me.blueslime.bukkitmeteor.actions.action.Action;
import org.bukkit.entity.Player;

import java.util.List;

public class CloseMenuAction extends Action {
    public CloseMenuAction() {
        super("[close-menu]", "close-menu:", "<close-menu>");
    }

    /**
     * Execute action
     *
     * @param plugin    of the event
     * @param parameter text
     * @param players   players
     */
    @Override
    public void execute(BukkitMeteorPlugin plugin, String parameter, List<Player> players) {

    }
}
