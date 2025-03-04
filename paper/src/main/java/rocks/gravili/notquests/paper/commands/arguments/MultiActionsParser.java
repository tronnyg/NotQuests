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

package rocks.gravili.notquests.paper.commands.arguments;

import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.structs.actions.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MultiActionsParser<C> implements ArgumentParser<C, ActionList> {
    private final NotQuests main;

    protected MultiActionsParser(NotQuests main) {
        this.main = main;
    }

    public static <C> @NonNull ParserDescriptor<C, ActionList> multiActionsParser(final NotQuests main) {
        return ParserDescriptor.of(new MultiActionsParser<>(main), ActionList.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull ActionList> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        if (commandInput.isEmpty()) {
            return ArgumentParseResult.failure(new IllegalArgumentException("No Input provided!"));
        }
        final String rawData = commandInput.input();
        final ActionList actions = new ActionList();
        for (final String inputAction : rawData.split(",")) {
            final Action action = main.getActionsYMLManager().getAction(inputAction);
            if (action == null) {
                return ArgumentParseResult.failure(new IllegalArgumentException("Action '" + inputAction + "' does not exist!"));
            }
            actions.addValue(action);
        }
        if (actions.getValues().isEmpty()) {
            return ArgumentParseResult.failure(new IllegalArgumentException("No valid action found!"));
        }

        return ArgumentParseResult.success(actions);
    }


    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return (context, input) -> {
            List<Suggestion> suggestions = new ArrayList<>();

            String rawData = input.input();
            main.getUtilManager().sendFancyCommandCompletion((CommandSender) context.sender(), input.input().split(" "), "[Action Names, separated by a comma]", "[...]");
            if (rawData.endsWith(",")) {
                for (String actionName : main.getActionsYMLManager().getActionsAndIdentifiers().keySet()) {
                    suggestions.add(Suggestion.suggestion(rawData + actionName));
                }
            } else {
                if (!rawData.contains(",")) {
                    for (String actionName :
                            main.getActionsYMLManager().getActionsAndIdentifiers().keySet()) {
                        suggestions.add(Suggestion.suggestion(actionName));
                        if (rawData.endsWith(actionName)) {
                            suggestions.add(Suggestion.suggestion(actionName + ","));
                        }
                    }
                } else {
                    for (String actionName :
                            main.getActionsYMLManager().getActionsAndIdentifiers().keySet()) {
                        suggestions.add(Suggestion.suggestion(rawData.substring(0, rawData.lastIndexOf(",") + 1) + actionName));
                        if (rawData.endsWith(actionName)) {
                            suggestions.add(Suggestion.suggestion(rawData + ","));
                        }
                    }
                }
            }

            return CompletableFuture.completedFuture(suggestions);
        };
    }
}