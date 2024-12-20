package me.blueslime.bungeemeteor.conditions.condition;

import me.blueslime.bungeemeteor.BungeeMeteorPlugin;
import me.blueslime.bungeeutilitiesapi.text.TextReplacer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class Condition {

    private final List<String> prefixes = new ArrayList<>();

    public Condition(String prefix, String... extraPrefixes) {
        this.prefixes.addAll(Arrays.asList(extraPrefixes));
        this.prefixes.add(prefix);
    }

    /**
     * Execute condition
     *
     * @param plugin    of the event
     * @param player    player of this condition
     * @param parameter text
     * @return boolean true if the condition has been overpassed, false if not.
     */
    public boolean execute(BungeeMeteorPlugin plugin, String parameter, ProxiedPlayer player) {
        return execute(plugin, parameter, player, TextReplacer.builder());
    }

    /**
     * Execute condition
     *
     * @param plugin    of the event
     * @param player    player of this condition
     * @param parameter text
     * @param replacer  if you have texts with custom variables, here you can replace variables in the parameter.
     * @return boolean true if the condition has been overpassed, false if not.
     */
    public abstract boolean execute(BungeeMeteorPlugin plugin, String parameter, ProxiedPlayer player, TextReplacer replacer);

    public String replace(String parameter) {
        for (String prefix : prefixes) {
            parameter = parameter.replace(" " + prefix + " ", "").replace(" " + prefix, "").replace(prefix + " ", "").replace(prefix, "");
        }
        return parameter;
    }

    public boolean isCondition(String parameter) {
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
}
