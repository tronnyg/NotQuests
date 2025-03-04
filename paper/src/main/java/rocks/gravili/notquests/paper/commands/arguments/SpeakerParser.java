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
import rocks.gravili.notquests.paper.conversation.Conversation;
import rocks.gravili.notquests.paper.conversation.Speaker;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SpeakerParser<C> implements ArgumentParser<C, Speaker> {
    private final NotQuests main;
    private final String conversationContext;
    protected SpeakerParser(NotQuests main, String conversationContext) {
        this.main = main;
        this.conversationContext = conversationContext;
    }

    public static <C> @NonNull ParserDescriptor<C, Speaker> speakerParser(final NotQuests main, String conversationContext) {
        return ParserDescriptor.of(new SpeakerParser<>(main, conversationContext), Speaker.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Speaker> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        if (commandInput.isEmpty()) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Invalid Category: " + commandContext));
        }
        String rawInput = commandInput.input();
        final Conversation conversation = commandContext.get(conversationContext);

        for (final Speaker speaker : conversation.getSpeakers()) {
            if (speaker.getSpeakerName().equalsIgnoreCase(rawInput)) {
                return ArgumentParseResult.success(speaker);
            }
        }
        return ArgumentParseResult.failure(new IllegalArgumentException("Speaker '" + rawInput + "' was not found in conversation " + conversation.getIdentifier() + "!"));
    }


    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return (context, input) -> {
            List<Suggestion> entries = new java.util.ArrayList<>();
            final Conversation conversation = context.get(conversationContext);
            if (conversation.getSpeakers() != null && conversation.getSpeakers().size() > 0) {
                final int speakerCount = conversation.getSpeakers().size();
                for (int i = 0; i < speakerCount; i++) {
                    entries.add(Suggestion.suggestion(conversation.getSpeakers().get(i).getSpeakerName()));
                }
            }
            main.getUtilManager().sendFancyCommandCompletion((CommandSender) context.sender(), input.input().split(" "), "[Speaker Name]", "[...]");
            return CompletableFuture.completedFuture(entries);
        };
    }
}