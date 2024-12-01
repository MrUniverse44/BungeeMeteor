package me.blueslime.bungeemeteor.actions.action;

import me.blueslime.bungeemeteor.BungeeMeteorPlugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class Action {

    private final List<String> prefixes = new ArrayList<>();

    private boolean stop = false;

    public Action(String prefix, String... extraPrefixes) {
        this.prefixes.addAll(Arrays.asList(extraPrefixes));
        this.prefixes.add(prefix);
    }

    /**
     * Execute action
     *
     * @param plugin    of the event
     * @param players   players
     * @param parameter text
     */
    public void execute(BungeeMeteorPlugin plugin, String parameter, ProxiedPlayer... players) {
        execute(plugin, parameter, Arrays.asList(players));
    }

    /**
     * Execute action
     *
     * @param plugin    of the event
     * @param players   players
     * @param parameter text
     */
    public abstract void execute(BungeeMeteorPlugin plugin, String parameter, List<ProxiedPlayer> players);

    public String replace(String parameter) {
        for (String prefix : prefixes) {
            parameter = parameter.replace(" " + prefix + " ", "").replace(" " + prefix, "").replace(prefix + " ", "").replace(prefix, "");
        }
        return parameter;
    }

    public boolean isAction(String parameter) {
        if (parameter == null) {
            return false;
        }
        String param = parameter.toLowerCase(Locale.ENGLISH);
        for (String prefix : prefixes) {
            if (param.startsWith(" " + prefix.toLowerCase(Locale.ENGLISH)) || param.startsWith(prefix.toLowerCase(Locale.ENGLISH))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prefixes of your actions
     * @return the list of your prefixes
     */
    public List<String> getPrefixes() {
        return prefixes;
    }

    /**
     * Checks if a player can execute this action
     * @param plugin instance
     * @param player to check
     * @param parameter to check
     * @return execute value
     */
    public boolean canExecute(BungeeMeteorPlugin plugin, ProxiedPlayer player, String parameter) {
        return true;
    }

    /**
     * Check if a player is stopping upcoming actions
     * @param plugin instance
     * @param parameter used
     * @param player of this check
     * @return result
     */
    public boolean isStoppingUpcomingActions(BungeeMeteorPlugin plugin, String parameter, ProxiedPlayer player) {
        if (stop) {
            stop = false;
            return true;
        }
        return false;
    }
}

