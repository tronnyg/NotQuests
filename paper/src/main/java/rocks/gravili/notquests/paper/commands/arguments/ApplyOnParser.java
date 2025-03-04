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
import rocks.gravili.notquests.paper.structs.Quest;
import rocks.gravili.notquests.paper.structs.objectives.Objective;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ApplyOnParser<C> implements ArgumentParser<C, Integer> {
    private final NotQuests main;
    private final String questContext;
    protected ApplyOnParser(NotQuests main, String questContext) {
        this.main = main;
        this.questContext = questContext;
    }

    public static <C> @NonNull ParserDescriptor<C, Integer> applyOnParser(final NotQuests main, String questContext) {
        return ParserDescriptor.of(new ApplyOnParser<>(main, questContext), Integer.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Integer> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        if (commandInput.isEmpty()) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Invalid Applier: " + commandContext));
        }
        final Quest quest = commandContext.get(questContext);

        String rawInput = commandInput.input();
        if (rawInput.equalsIgnoreCase("Quest")) {
            return ArgumentParseResult.success(0);
        } else {
            try {
                int objectiveID = Integer.parseInt(rawInput.toLowerCase(Locale.ROOT).replace("o", ""));
                if (quest.getObjectiveFromID(objectiveID) != null) {
                    return ArgumentParseResult.success(objectiveID);
                } else {
                    return ArgumentParseResult.failure(
                            new IllegalArgumentException(
                                    "ApplyOn Objective '" + rawInput + "' is not an objective of the Quest!"));
                }
            } catch (Exception e) {
                return ArgumentParseResult.failure(new IllegalArgumentException("ApplyOn Objective '" + rawInput + "' is not a valid applyOn objective!"));
            }
        }
    }


    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return (context, input) -> {
            List<Suggestion> entries = new java.util.ArrayList<>();
            final Quest quest = context.get(questContext);
            entries.add(Suggestion.suggestion("Quest"));
            for (Objective objective : quest.getObjectives()) {
                entries.add(Suggestion.suggestion("O" + objective.getObjectiveID()));
            }
            main.getUtilManager().sendFancyCommandCompletion((CommandSender) context.sender(), input.input().split(" "), "[Apply On]", "[...]");
            return CompletableFuture.completedFuture(entries);
        };
    }
}