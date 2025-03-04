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
import rocks.gravili.notquests.paper.structs.Quest;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QuestParser<C> implements ArgumentParser<C, Quest> {
    private boolean takeEnabledOnly = false;
    private final NotQuests main;

    protected QuestParser(NotQuests main, boolean takeEnabledOnly) {
        this.takeEnabledOnly = takeEnabledOnly;
        this.main = main;
    }

    public static <C> @NonNull ParserDescriptor<C, Quest> questParser(final NotQuests main, boolean takeEnabledOnly) {
        return ParserDescriptor.of(new QuestParser<>(main, takeEnabledOnly), Quest.class);
    }

    public static <C> @NonNull ParserDescriptor<C, Quest> questParser(final NotQuests main) {
        return ParserDescriptor.of(new QuestParser<>(main, false), Quest.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Quest> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        if (commandInput.isEmpty()) {
            return ArgumentParseResult.failure(new QuestParseException(commandContext));
        }
        final Quest foundQuest = main.getQuestManager().getQuest(commandInput.input());
        if (foundQuest == null) {
            if (commandContext.sender() instanceof Player player) {
                return ArgumentParseResult.failure(new IllegalArgumentException(main.getLanguageManager().getString("chat.quest-does-not-exist", player).replace("%QUESTNAME%", commandInput.input())));
            } else {
                return ArgumentParseResult.failure(new IllegalArgumentException(main.getLanguageManager().getString("chat.quest-does-not-exist", (QuestPlayer) null).replace("%QUESTNAME%", commandInput.input())));
            }
        }
        if (this.takeEnabledOnly && !foundQuest.isTakeEnabled() ) {
            if (commandContext.sender() instanceof final Player player) {
                if(main.getConfiguration().isQuestPreviewUseGUI()){
                    return ArgumentParseResult.failure(new IllegalArgumentException(main.getLanguageManager().getString("chat.take-disabled", player, foundQuest)));

                }else{
                    if (!main.getQuestManager().isPlayerCloseToCitizenOrArmorstandWithQuest(main.getQuestPlayerManager().getOrCreateQuestPlayer(player.getUniqueId()), foundQuest)) {
                        return ArgumentParseResult.failure(new IllegalArgumentException(main.getLanguageManager().getString("chat.take-disabled", player, foundQuest)));
                    }
                }
            } else {
                return ArgumentParseResult.failure(new IllegalArgumentException(main.getLanguageManager().getString("chat.take-disabled", (QuestPlayer) null, foundQuest)));
            }
        }
        return ArgumentParseResult.success(foundQuest);
    }


    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return (context, input) -> {
            List<Suggestion> questNames = new java.util.ArrayList<>();
            for (Quest quest : main.getQuestManager().getAllQuests()) {
                if (!this.takeEnabledOnly || quest.isTakeEnabled()) {
                    questNames.add(Suggestion.suggestion(quest.getIdentifier()));
                }
            }

            main.getUtilManager().sendFancyCommandCompletion((CommandSender) context.sender(), context.rawInput().input().split(" "), "[Quest Name]", "[...]");
            return CompletableFuture.completedFuture(questNames);
        };
    }

    public static final class QuestParseException extends ParserException {

        public QuestParseException(@Nullable Throwable cause, @NonNull CommandContext<?> context, @NonNull CaptionVariable... captionVariables) {
            super(cause, QuestParser.class, context, Caption.of(""), captionVariables);
        }

        public QuestParseException(@NonNull CommandContext<?> context,  @NonNull CaptionVariable... captionVariables) {
            super(QuestParser.class, context, Caption.of(""), captionVariables);
        }
    }
}