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

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.structs.ActiveQuest;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ActiveQuestParser<C> implements ArgumentParser<C, ActiveQuest> {
    private final NotQuests main;

    protected ActiveQuestParser(NotQuests main) {
        this.main = main;
    }

    public static <C> @NonNull ParserDescriptor<C, ActiveQuest> activeQuestParser(final NotQuests main) {
        return ParserDescriptor.of(new ActiveQuestParser<>(main), ActiveQuest.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull ActiveQuest> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        OfflinePlayer offlinePlayer = commandContext.get("player");
        if (commandInput.isEmpty()) {
            return ArgumentParseResult.failure(new QuestParseException(commandContext));
        }
        final ActiveQuest activeQuest = main.getQuestPlayerManager().getActiveQuestPlayer(offlinePlayer.getUniqueId()).getActiveQuest(main.getQuestManager().getQuest(commandInput.input()));
        if (activeQuest == null) {
            if (commandContext.sender() instanceof Player player) {
                return ArgumentParseResult.failure(new IllegalArgumentException(main.getLanguageManager().getString("chat.quest-does-not-exist", player).replace("%QUESTNAME%", commandInput.input())));
            } else {
                return ArgumentParseResult.failure(new IllegalArgumentException(main.getLanguageManager().getString("chat.quest-does-not-exist", (QuestPlayer) null).replace("%QUESTNAME%", commandInput.input())));
            }
        }

        if (commandContext.sender() instanceof final Player player) {
            if (main.getConfiguration().isQuestPreviewUseGUI()) {
                return ArgumentParseResult.failure(new IllegalArgumentException(main.getLanguageManager().getString("chat.take-disabled", player, activeQuest)));

            }
        } else {
            return ArgumentParseResult.failure(new IllegalArgumentException(main.getLanguageManager().getString("chat.take-disabled", (QuestPlayer) null, activeQuest)));
        }
        return ArgumentParseResult.success(activeQuest);
    }


    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return (context, input) -> {
            OfflinePlayer offlinePlayer = context.get("player");
            List<Suggestion> questNames = new ArrayList<>();
            for (ActiveQuest quest : main.getQuestPlayerManager().getActiveQuestPlayer(offlinePlayer.getUniqueId()).getActiveQuests()) {
                questNames.add(Suggestion.suggestion(quest.getQuestIdentifier()));
            }

            main.getUtilManager().sendFancyCommandCompletion((CommandSender) context.sender(), context.rawInput().input().split(" "), "[Quest Name]", "[...]");
            return CompletableFuture.completedFuture(questNames);
        };
    }

    public static final class QuestParseException extends ParserException {

        public QuestParseException(@Nullable Throwable cause, @NonNull CommandContext<?> context, @NonNull CaptionVariable... captionVariables) {
            super(cause, ActiveQuestParser.class, context, Caption.of(""), captionVariables);
        }

        public QuestParseException(@NonNull CommandContext<?> context, @NonNull CaptionVariable... captionVariables) {
            super(ActiveQuestParser.class, context, Caption.of(""), captionVariables);
        }
    }
}