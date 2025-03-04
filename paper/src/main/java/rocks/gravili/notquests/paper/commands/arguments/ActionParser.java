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

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ActionParser<C> implements ArgumentParser<C, Action> {
    private final NotQuests main;

    protected ActionParser(NotQuests main) {
        this.main = main;
    }

    public static <C> @NonNull ParserDescriptor<C, Action> actionParser(final NotQuests main) {
        return ParserDescriptor.of(new ActionParser<>(main), Action.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Action> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        if (commandInput.isEmpty()) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Invalid Category: " + commandContext));
        }
        final String input = String.valueOf(commandInput.peek());
        final Action foundAction = main.getActionsYMLManager().getAction(input);

        if (foundAction == null) {return ArgumentParseResult.failure(new IllegalArgumentException("Action '" + input + "' does not exist!"));
        }

        return ArgumentParseResult.success(foundAction);
    }


    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return (context, input) -> {
            List<Suggestion> actionNames = main.getActionsYMLManager().getActionsAndIdentifiers().keySet().stream().map(Suggestion::suggestion).toList();
            main.getUtilManager().sendFancyCommandCompletion((CommandSender) context.sender(), input.input().split(" "), "[Action Name]", "[...]");
            return CompletableFuture.completedFuture(actionNames);
        };
    }
}