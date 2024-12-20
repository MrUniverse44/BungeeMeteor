package me.blueslime.bungeemeteor.actions;

import me.blueslime.bungeemeteor.BungeeMeteorPlugin;
import me.blueslime.bungeemeteor.actions.action.Action;
import me.blueslime.bungeemeteor.actions.type.*;
import me.blueslime.bungeemeteor.implementation.Implements;
import me.blueslime.bungeemeteor.implementation.module.AdvancedModule;
import me.blueslime.bungeemeteor.implementation.registered.Register;
import me.blueslime.bungeemeteor.utils.list.ReturnableArrayList;
import me.blueslime.bungeeutilitiesapi.text.TextReplacer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.List;

public class Actions implements AdvancedModule {
    private final List<Action> externalActions = new ReturnableArrayList<Action>();
    private final List<Action> action = new ReturnableArrayList<Action>();
    private final BungeeMeteorPlugin plugin;

    public Actions(BungeeMeteorPlugin plugin) {
        this.plugin = plugin;
        registerInternalAction(
            new MessageAction(),
            new ActionBarAction(),
            new TitlesAction(),
            new ConsoleAction(),
            new ChatAction()
        );
        Implements.register(this);
    }

    /**
     * Register actions to the plugin
     * These actions will not be refreshed in a reload event.
     * @param actions to register
     */
    public void registerInternalAction(Action... actions) {
        action.addAll(Arrays.asList(actions));
    }

    /**
     * Register actions to the plugin
     * @param actions to register
     */
    public void registerAction(Action... actions) {
        externalActions.addAll(Arrays.asList(actions));
    }

    /**
     * Get the list of internal actions
     * @return ArrayList
     */
    public List<Action> getActions() {
        return action;
    }

    /**
     * Get the list of external actions
     * @return ArrayList
     */
    public List<Action> getExternalActions() {
        return externalActions;
    }

    public void execute(List<String> actions) {
        execute(actions, null);
    }

    public void execute(List<String> actions, ProxiedPlayer player) {
        List<Action> entireList = new ReturnableArrayList<Action>();

        entireList.addAll(externalActions);
        entireList.addAll(action);

        for (String param : actions) {
            if (fetch(entireList, player, param)) {
                break;
            }
        }
    }

    public void execute(List<String> actions, ProxiedPlayer player, TextReplacer replacer) {
        List<Action> entireList = new ReturnableArrayList<Action>();

        entireList.addAll(externalActions);
        entireList.addAll(action);

        for (String param : actions) {
            if (fetch(entireList, player, replacer.apply(param))) {
                break;
            }
        }
    }

    private boolean fetch(List<Action> list, ProxiedPlayer player, String param) {
        if (player == null) {
            return false;
        }
        for (Action action : list) {
            if (action.isAction(param)) {
                if (action.canExecute(plugin, player, param)) {
                    action.execute(plugin, param, player);
                }
                return action.isStoppingUpcomingActions(plugin, param, player);
            }
        }
        plugin.getLogger().info("'" + param + "' don't have an action, please see actions with /<command> actions");
        return false;
    }

    @Register
    public Actions provideActions() {
        return this;
    }
}

