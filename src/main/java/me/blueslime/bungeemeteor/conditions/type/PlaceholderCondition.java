package me.blueslime.bungeemeteor.conditions.type;

import me.blueslime.bungeemeteor.BungeeMeteorPlugin;
import me.blueslime.bungeemeteor.actions.Actions;
import me.blueslime.bungeemeteor.conditions.condition.Condition;
import me.blueslime.bungeemeteor.implementation.Implements;
import me.blueslime.bungeemeteor.logs.MeteorLogger;
import me.blueslime.bungeeutilitiesapi.text.TextReplacer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;

public class PlaceholderCondition extends Condition {

    public PlaceholderCondition() {
        super("[placeholder]", "<placeholder>");
    }

    /**
     * Execute action
     *
     * @param plugin    of the event
     * @param parameter text
     * @param player    player of this condition
     * @param replacer  if you have texts with custom variables, here you can replace variables in the parameter.
     * @return boolean true if the condition has been overpassed, false if not.
     */
    @Override
    public boolean execute(BungeeMeteorPlugin plugin, String parameter, ProxiedPlayer player, TextReplacer replacer) {
        String placeholder = replace(parameter);

        if (replacer != null) {
            placeholder = replacer.apply(placeholder);
        }

        String[] operations = placeholder.split("<orElse>");

        boolean result = executeValues(operations[0]);

        if (operations.length > 1 && !result) {
            String[] secondaryOperations = operations[1].split(";;");
            Actions actions = Implements.fetch(Actions.class);
            if (actions != null) {
                actions.execute(Arrays.asList(secondaryOperations), player, replacer);
            }
        }

        return result;
    }

    private boolean executeValues(String parameter) {
        String regex = "(==|>=|<=|>|<|!=|=i=|\\|-|-\\|)";
        String[] parts = parameter.split(regex);

        if (parts.length != 2) {
            Implements.fetch(MeteorLogger.class).error("Executor format should ve: 'value1 operator value2': " + parameter);
            return false;
        }

        String value1 = parts[0].trim();
        String value2 = parts[1].trim();

        String operator = parameter.substring(value1.length(), parameter.length() - value2.length()).trim();

        int operatorCode = getOperatorCode(operator);

        return compareValues(value1, value2, operatorCode);
    }

    private int getOperatorCode(String operator) {
        switch (operator) {
            case "==": return 0;
            case ">": return 1;
            case ">=": return 2;
            case "<": return 3;
            case "<=": return 4;
            case "!=": return 5;
            case "|-": return 6;
            case "-|": return 7;
            case "=i=": return 8;
            default:
                Implements.fetch(MeteorLogger.class).error("Operator is not valid, please use: ==, >, >=, <, <=: " + operator);
                return -1;
        }
    }

    private boolean compareValues(String value1, String value2, int operator) {
        try {
            double num1 = Double.parseDouble(value1);
            double num2 = Double.parseDouble(value2);

            switch (operator) {
                case 0: return num1 == num2;
                case 1: return num1 > num2;
                case 2: return num1 >= num2;
                case 3: return num1 < num2;
                case 4: return num1 <= num2;
                case 5: return num1 != num2;
                default:
                    Implements.fetch(MeteorLogger.class).error("Operator is not valid, please use: ==, >, >=, <, <=, !=: " + operator);
                    return false;
            }
        } catch (NumberFormatException e) {
            switch (operator) {
                case 0: return value1.equals(value2);
                case 1: return value1.compareTo(value2) > 0;
                case 2: return value1.compareTo(value2) >= 0;
                case 3: return value1.compareTo(value2) < 0;
                case 4: return value1.compareTo(value2) <= 0;
                case 5: return !value1.equals(value2);
                case 6: return value1.startsWith(value2);
                case 7: return value1.endsWith(value2);
                case 8: return value1.equalsIgnoreCase(value2);
                default:
                    Implements.fetch(MeteorLogger.class).error("Operator is not valid, please use: ==, >, >=, <, <=, !=, |-, -|, =i=: " + operator);
                    return false;
            }
        }
    }
}
