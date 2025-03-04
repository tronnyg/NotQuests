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
import rocks.gravili.notquests.paper.managers.data.Category;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CategoryParser<C> implements ArgumentParser<C, Category> {
    private final NotQuests main;

    protected CategoryParser(NotQuests main) {
        this.main = main;
    }

    public static <C> @NonNull ParserDescriptor<C, Category> categoryParser(final NotQuests main) {
        return ParserDescriptor.of(new CategoryParser<>(main), Category.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Category> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        if (commandInput.isEmpty()) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Invalid Category: " + commandContext));
        }
        String rawInput = commandInput.input();
        List<Category> entries = main.getDataManager().getCategories();
        Category foundCategory;
        for (Category category : entries) {
            if (category.getCategoryName().equalsIgnoreCase(rawInput)) {
                foundCategory = category;
                return ArgumentParseResult.success(foundCategory);
            }
        }
        return ArgumentParseResult.failure(new IllegalArgumentException("No Category found: " + commandContext));
    }


    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return (context, input) -> {
            List<Suggestion> entries = new java.util.ArrayList<>();
            for (Category category : main.getDataManager().getCategories()) {
                entries.add(Suggestion.suggestion(category.getCategoryName()));
            }
            main.getUtilManager().sendFancyCommandCompletion((CommandSender) context.sender(), context.rawInput().input().split(" "), "[Player Name]", "[...]");
            return CompletableFuture.completedFuture(entries);
        };
    }
}