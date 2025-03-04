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
import rocks.gravili.notquests.paper.structs.conditions.Condition;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConditionParser<C> implements ArgumentParser<C, Condition> {
    private final NotQuests main;

    protected ConditionParser(NotQuests main) {
        this.main = main;
    }

    public static <C> @NonNull ParserDescriptor<C, Condition> conditionParser(final NotQuests main) {
        return ParserDescriptor.of(new ConditionParser<>(main), Condition.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Condition> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        if (commandInput.isEmpty()) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Invalid Condition: " + commandContext));
        }
        String rawInput = commandInput.input();
        final Condition foundCondition = main.getConditionsYMLManager().getCondition(rawInput);
        if (foundCondition == null) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Condition '" + rawInput + "' does not exist!"));
        }

        return ArgumentParseResult.success(foundCondition);
    }


    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return (context, input) -> {
            List<Suggestion> conditionNames = main.getConditionsYMLManager().getConditionsAndIdentifiers().keySet().stream().map(Suggestion::suggestion).toList();
            main.getUtilManager().sendFancyCommandCompletion((CommandSender) context.sender(), input.input().split(" "), "[Condition Name]", "[...]");
            return CompletableFuture.completedFuture(conditionNames);
        };
    }
}