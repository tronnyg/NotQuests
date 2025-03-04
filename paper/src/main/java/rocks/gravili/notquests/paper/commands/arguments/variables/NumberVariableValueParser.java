/*
 * NotQuests - A Questing plugin for Minecraft Servers
 * Copyright (C) 2022 Alessio Gravili
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rocks.gravili.notquests.paper.commands.arguments.variables;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.parser.flag.CommandFlag;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.managers.expressions.NumberExpression;
import rocks.gravili.notquests.paper.structs.QuestPlayer;
import rocks.gravili.notquests.paper.structs.variables.Variable;
import rocks.gravili.notquests.paper.structs.variables.VariableDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Getter
public class NumberVariableValueParser<C> implements ArgumentParser<C, String> {
    private final NotQuests main;

    private final String identifier;
    private final Variable<?> variable;

    private SuggestionProvider<C> suggestionProvider;

    private static final int MAX_SUGGESTIONS_INCREMENT = 10;
    private static final int NUMBER_SHIFT_MULTIPLIER = 10;

    protected NumberVariableValueParser(String identifier, Variable<?> variable) {
        this.main = NotQuests.getInstance();
        this.identifier = identifier;
        this.variable = variable;
    }

    public static <C> @NonNull NumberVariableValueParser<C> of(String identifier, Variable<?> variable, SuggestionProvider<C> suggestionProvider) {
        NumberVariableValueParser<C> parser = new NumberVariableValueParser<>(identifier, variable);
        parser.suggestionProvider = suggestionProvider;
        return parser;
    }

    public static <C> @NonNull NumberVariableValueParser<C> of(String identifier, Variable<?> variable) {
        return new NumberVariableValueParser<>(identifier, variable);
    }

    public static <C> @NonNull ParserDescriptor<C, String> numberVariableParser(String identifier, Variable<?> variable) {
        return ParserDescriptor.of(new NumberVariableValueParser<>(identifier, variable), String.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull String> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        if(commandInput.isEmpty()) {
            return ArgumentParseResult.failure(new IllegalArgumentException("No input provided"));
        }
        final String input = commandInput.input();

        try{
            final NumberExpression numberExpression = new NumberExpression(main, input);
            if (commandContext.sender() instanceof Player player) {
                try {
                    numberExpression.calculateValue(main.getQuestPlayerManager().getOrCreateQuestPlayer(player.getUniqueId()));
                } catch (Exception e) {
                    if (main.getConfiguration().isDebug()) {
                        e.printStackTrace();
                    }
                    return ArgumentParseResult.failure(new IllegalArgumentException("Invalid Expression: " + input + ". Error: " + e.toString()));
                }
            }
        } catch (Exception e){
            return ArgumentParseResult.failure(new IllegalArgumentException("Erroring Expression: " + input + ". Error: " + e.toString()));
        }


        return ArgumentParseResult.success(input);
    }


    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        if(suggestionProvider != null) return suggestionProvider;

        return ((context, input) -> {
            final List<Suggestion> completions = new ArrayList<>();
            completions.add(Suggestion.suggestion("<Enter Variable or Number>"));

            String rawInput = input.input();
            for (final String variableString : main.getVariablesManager().getVariableIdentifiers()) {
                final Variable<?> variable = main.getVariablesManager().getVariableFromString(variableString);
                if (variable == null || (variable.getVariableDataType() != VariableDataType.NUMBER && variable.getVariableDataType() != VariableDataType.BOOLEAN)) {
                    continue;
                }
                if (variable.getRequiredStrings().isEmpty() && variable.getRequiredNumbers().isEmpty() && variable.getRequiredBooleans().isEmpty() && variable.getRequiredBooleanFlags().isEmpty()) {
                    completions.add(Suggestion.suggestion(variableString));
                } else {
                    if (!rawInput.endsWith(variableString + "(")) {
                        if (rawInput.endsWith(",") && rawInput.contains(variableString + "(")) {
                            for (StringVariableValueParser<CommandSender> stringParser : variable.getRequiredStrings()) {
                                if (!rawInput.contains(stringParser.getIdentifier())) {
                                    completions.add(Suggestion.suggestion(rawInput + stringParser.getIdentifier() + ":"));
                                }
                            }
                            for (NumberVariableValueParser<CommandSender> numberParser : variable.getRequiredNumbers()) {
                                if (!rawInput.contains(numberParser.getIdentifier())) {
                                    completions.add(Suggestion.suggestion(rawInput + numberParser.getIdentifier() + ":"));
                                }
                            }
                            for (BooleanVariableValueParser<CommandSender> booleanParser : variable.getRequiredBooleans()) {
                                if (!rawInput.contains(booleanParser.getIdentifier())) {
                                    completions.add(Suggestion.suggestion(rawInput + booleanParser.getIdentifier() + ":"));
                                }
                            }
                            for (CommandFlag<Void> flag : variable.getRequiredBooleanFlags()) {
                                if (!rawInput.contains(flag.name())) {
                                    completions.add(Suggestion.suggestion(rawInput + "--" + flag.name()));
                                }
                            }
                        } else if (!rawInput.endsWith(")")) {
                            if (rawInput.contains(variableString + "(") && (!rawInput.contains(")") || (rawInput.lastIndexOf("(") < rawInput.lastIndexOf(")")))) {
                                final String subStringAfter = rawInput.substring(rawInput.indexOf(variableString + "("));

                                try {
                                    for (final StringVariableValueParser<CommandSender> stringParser : variable.getRequiredStrings()) {
                                        if (subStringAfter.contains(":")) {
                                            Iterable<Suggestion> suggestions = (Iterable<Suggestion>) stringParser.suggestionProvider().suggestionsFuture(context.get(identifier), input).get();
                                            if (subStringAfter.endsWith(":")) {
                                                suggestions.forEach(suggestion -> completions.add(Suggestion.suggestion(rawInput + suggestion.suggestion())));
                                            } else {
                                                final String[] splitDoubleDots = subStringAfter.split(":");
                                                final String stringAfterLastDoubleDot = splitDoubleDots[splitDoubleDots.length - 1];
                                                suggestions.forEach(suggestion -> completions.add(Suggestion.suggestion(rawInput.substring(0, rawInput.length() - stringAfterLastDoubleDot.length() - 1) + ":" + suggestion.suggestion())));
                                            }
                                        } else {
                                            completions.add(Suggestion.suggestion(variableString + "(" + stringParser.getIdentifier() + ":"));
                                        }
                                    }
                                    for (final NumberVariableValueParser<CommandSender> numberParser : variable.getRequiredNumbers()) {
                                        if (subStringAfter.contains(":")) {
                                            Iterable<Suggestion> suggestions = (Iterable<Suggestion>) numberParser.suggestionProvider().suggestionsFuture(context.get(identifier), input).get();
                                            if (subStringAfter.endsWith(":")) {
                                                suggestions.forEach(suggestion -> completions.add(Suggestion.suggestion(rawInput + suggestion.suggestion())));
                                            } else {
                                                final String[] splitDoubleDots = subStringAfter.split(":");
                                                final String stringAfterLastDoubleDot = splitDoubleDots[splitDoubleDots.length - 1];
                                                suggestions.forEach(suggestion -> completions.add(Suggestion.suggestion(rawInput.substring(0, rawInput.length() - stringAfterLastDoubleDot.length() - 1) + ":" + suggestion.suggestion())));
                                            }
                                        } else {
                                            completions.add(Suggestion.suggestion(variableString + "(" + numberParser.getIdentifier() + ":"));
                                        }
                                    }
                                    for (BooleanVariableValueParser<CommandSender> booleanParser : variable.getRequiredBooleans()) {
                                        if (subStringAfter.contains(":")) {
                                            Iterable<Suggestion> suggestions = (Iterable<Suggestion>) booleanParser.suggestionProvider().suggestionsFuture(context.get(identifier), input).get();
                                            if (subStringAfter.endsWith(":")) {
                                                suggestions.forEach(suggestion -> completions.add(Suggestion.suggestion(rawInput + suggestion.suggestion())));
                                            } else {

                                                final String[] splitDoubleDots = subStringAfter.split(":");
                                                final String stringAfterLastDoubleDot = splitDoubleDots[splitDoubleDots.length - 1];
                                                suggestions.forEach(suggestion -> completions.add(Suggestion.suggestion(rawInput.substring(0, rawInput.length() - stringAfterLastDoubleDot.length() - 1) + ":" + suggestion.suggestion())));
                                            }
                                        } else {
                                            completions.add(Suggestion.suggestion(variableString + "(" + booleanParser.getIdentifier() + ":"));
                                        }
                                    }
                                } catch (InterruptedException | ExecutionException e) {
                                    throw new RuntimeException(e);
                                }

                                for (CommandFlag<Void> flag : variable.getRequiredBooleanFlags()) {
                                    completions.add(Suggestion.suggestion(variableString + "(--" + flag.name() + ""));
                                }
                            } else {
                                completions.add(Suggestion.suggestion(variableString + "("));
                            }
                        }
                    } else {
                        for (StringVariableValueParser<CommandSender> stringParser : variable.getRequiredStrings()) {
                            completions.add(Suggestion.suggestion(variableString + "(" + stringParser.getIdentifier() + ":"));
                        }
                        for (NumberVariableValueParser<CommandSender> numberParser : variable.getRequiredNumbers()) {
                            completions.add(Suggestion.suggestion(variableString + "(" + numberParser.getIdentifier() + ":"));
                        }
                        for (BooleanVariableValueParser<CommandSender> booleanParser : variable.getRequiredBooleans()) {
                            completions.add(Suggestion.suggestion(variableString + "(" + booleanParser.getIdentifier() + ":"));
                        }
                        for (CommandFlag<Void> flag : variable.getRequiredBooleanFlags()) {
                            completions.add(Suggestion.suggestion(variableString + "(--" + flag.name() + ""));
                        }
                    }
                }
            }

            //Now the number completions
            final Set<Double> numbers = new TreeSet<>();
            double min = -Double.MAX_VALUE;
            double max = Double.MAX_VALUE;

            try {
                final double inputNum = Long.parseLong(rawInput.equals("-") ? "-0" : input.isEmpty() ? "0" : rawInput);
                final double inputNumAbsolute = Math.abs(inputNum);

                numbers.add(inputNumAbsolute); /* It's a valid number, so we suggest it */
                for (double i = 0; i < 1
                        && (inputNum * NUMBER_SHIFT_MULTIPLIER) + i <= max; i++) {
                    numbers.add((inputNumAbsolute * NUMBER_SHIFT_MULTIPLIER) + i);
                }
                for (double number : numbers) {
                    if (rawInput.startsWith("-")) {
                        number = -number; /* Preserve sign */
                    }
                    if (number < min || number > max) {
                        continue;
                    }
                    completions.add(Suggestion.suggestion(String.valueOf(number)));
                }
                //return suggestions;
            } catch (final Exception ignored) {
                //return Collections.emptyList();
            }
            for (int i = 0; i < 10; i++) {
                completions.add(Suggestion.suggestion(i + ".0"));
            }
            main.getUtilManager().sendFancyCommandCompletion((CommandSender) context.sender(), input.input().split(" "), "[Enter Variable / Mathematical Expression]", "[...]");

            if (context.sender() instanceof Player player) {
                final QuestPlayer questPlayer = main.getQuestPlayerManager().getOrCreateQuestPlayer(player.getUniqueId());

                if (variable == null || variable.getPossibleValues(questPlayer) == null) {
                    return CompletableFuture.completedFuture(completions);
                }
                completions.addAll(variable.getPossibleValues(questPlayer));
            } else {
                if (variable == null || variable.getPossibleValues(null) == null) {
                    return CompletableFuture.completedFuture(completions);
                }
                completions.addAll(variable.getPossibleValues(null));
            }
            return CompletableFuture.completedFuture(completions);
        });
    }

    public ParserDescriptor<C, String> getParserDescriptor() {
        return ParserDescriptor.of(this, String.class);
    }

    public String getIdentifier() {
        return identifier;
    }
}