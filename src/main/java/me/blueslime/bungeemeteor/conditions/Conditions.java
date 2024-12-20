package me.blueslime.bungeemeteor.conditions;

import me.blueslime.bungeemeteor.BungeeMeteorPlugin;
import me.blueslime.bungeemeteor.conditions.condition.Condition;
import me.blueslime.bungeemeteor.conditions.type.PlaceholderCondition;
import me.blueslime.bungeemeteor.implementation.Implements;
import me.blueslime.bungeemeteor.implementation.module.AdvancedModule;
import me.blueslime.bungeemeteor.implementation.registered.Register;
import me.blueslime.bungeemeteor.utils.list.ReturnableArrayList;
import me.blueslime.bungeeutilitiesapi.text.TextReplacer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.List;

public class Conditions implements AdvancedModule {
    private final List<Condition> externalConditions = new ReturnableArrayList<>();
    private final List<Condition> conditions = new ReturnableArrayList<>();
    private final BungeeMeteorPlugin plugin;

    public Conditions(BungeeMeteorPlugin plugin) {
        this.plugin = plugin;
        registerInternalCondition(
            new PlaceholderCondition()
        );
        Implements.register(this);
    }

    /**
     * Register conditions to the plugin
     * These actions will not be refreshed in a reload event.
     * @param conditions to register
     */
    public void registerInternalCondition(Condition... conditions) {
        this.conditions.addAll(Arrays.asList(conditions));
    }

    /**
     * Register conditions to the plugin
     * @param conditions to register
     */
    public void registerCondition(Condition... conditions) {
        externalConditions.addAll(Arrays.asList(conditions));
    }

    /**
     * Get the list of internal conditions
     * @return ArrayList
     */
    public List<Condition> getCondition() {
        return conditions;
    }

    /**
     * Get the list of external conditions
     * @return ArrayList
     */
    public List<Condition> getExternalCondition() {
        return externalConditions;
    }

    public boolean execute(List<String> actions) {
        return execute(actions, null);
    }

    public boolean execute(List<String> actions, ProxiedPlayer player) {
        if (actions == null || actions.isEmpty()) {
            return true;
        }

        List<Condition> entireList = new ReturnableArrayList<>();

        entireList.addAll(externalConditions);
        entireList.addAll(conditions);

        boolean hasConditions = true;

        final TextReplacer replacer = TextReplacer.builder();

        for (String param : actions) {
            if (!fetch(entireList, player, param, replacer)) {
                hasConditions = false;
                break;
            }
        }

        return hasConditions;
    }

    public boolean execute(List<String> actions, ProxiedPlayer player, TextReplacer replacer) {
        if (actions == null || actions.isEmpty()) {
            return true;
        }

        List<Condition> entireList = new ReturnableArrayList<>();

        entireList.addAll(externalConditions);
        entireList.addAll(conditions);

        boolean hasConditions = true;

        for (String param : actions) {
            if (!fetch(entireList, player, param, replacer)) {
                hasConditions = false;
                break;
            }
        }

        return hasConditions;
    }

    private boolean fetch(List<Condition> list, ProxiedPlayer player, String param, TextReplacer replacer) {
        for (Condition action : list) {
            if (action.isCondition(param)) {
                return action.execute(plugin, param, player, replacer);
            }
        }
        plugin.getLogger().info("'" + param + "' don't have an condition, please see conditions in our wiki. Returning false...");
        return false;
    }

    public boolean execute(String action, ProxiedPlayer player) {
        List<Condition> entireList = new ReturnableArrayList<>();

        entireList.addAll(externalConditions);
        entireList.addAll(conditions);

        final TextReplacer replacer = TextReplacer.builder();

        return fetch(entireList, player, action, replacer);
    }

    public boolean execute(String action, ProxiedPlayer player, TextReplacer replacer) {
        List<Condition> entireList = new ReturnableArrayList<>();

        entireList.addAll(externalConditions);
        entireList.addAll(conditions);

        return fetch(entireList, player, action, replacer);
    }

    @Register
    public Conditions provideConditions() {
        return this;
    }
}