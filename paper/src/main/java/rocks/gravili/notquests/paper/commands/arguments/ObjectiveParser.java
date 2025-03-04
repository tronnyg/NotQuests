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
import org.jetbrains.annotations.NotNull;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.structs.objectives.Objective;
import rocks.gravili.notquests.paper.structs.objectives.ObjectiveHolder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ObjectiveParser<C> implements ArgumentParser<C, Objective> {
    private final NotQuests main;
    private int level;

    protected ObjectiveParser(NotQuests main, int level) {
        this.main = main;
        this.level = level;
    }

    public static <C> @NonNull ParserDescriptor<C, Objective> objectiveParser(final NotQuests main, int level) {
        return ParserDescriptor.of(new ObjectiveParser<>(main, level), Objective.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Objective> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        if (commandInput.isEmpty()) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Invalid Objective: " + commandContext));
        }
        String rawInput = commandInput.input();
        List<Objective> entries = getObjectiveHolderForLevel(commandContext, level).getObjectives();
        for (Objective objective : entries) {
            if (String.valueOf(objective.getObjectiveID()).equalsIgnoreCase(rawInput)) {
                return ArgumentParseResult.success(objective);
            }
        }
        return ArgumentParseResult.failure(new IllegalArgumentException("No Category found: " + commandContext));
    }


    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return (context, input) -> {
            List<Suggestion> entries = new java.util.ArrayList<>();
            for (Objective objective : getObjectiveHolderForLevel(context, level).getObjectives()) {
                entries.add(Suggestion.suggestion(String.valueOf(objective.getObjectiveID())));
            }
            main.getUtilManager().sendFancyCommandCompletion((CommandSender) context.sender(), context.rawInput().input().split(" "), "[Player Name]", "[...]");
            return CompletableFuture.completedFuture(entries);
        };
    }

    private ObjectiveHolder getObjectiveHolderForLevel(final @NotNull CommandContext<C> context, int level) {
        final ObjectiveHolder objectiveHolder;
        if (level == 0) objectiveHolder = context.get("quest");
        else if (level == 1) objectiveHolder = context.get("objectiveId");
        else objectiveHolder = context.get("objectiveId" + level);
        return objectiveHolder;
    }
}