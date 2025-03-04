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

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.bukkit.BukkitCaptionKeys;
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
import rocks.gravili.notquests.paper.commands.arguments.wrappers.ItemStackSelection;
import rocks.gravili.notquests.paper.managers.items.NQItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ItemStackSelectionParser<C> implements ArgumentParser<C, ItemStackSelection> {
    private final NotQuests main;

    protected ItemStackSelectionParser(NotQuests main) {
        this.main = main;
    }

    public static <C> @NonNull ParserDescriptor<C, ItemStackSelection> itemStackSelectionParser(final NotQuests main) {
        return ParserDescriptor.of(new ItemStackSelectionParser<>(main), ItemStackSelection.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull ItemStackSelection> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        if (commandInput.isEmpty()) {
            return ArgumentParseResult.failure(new IllegalArgumentException("No input provided"));
        }

        try {
            final ItemStackSelection itemStackSelection = new ItemStackSelection(main);

            for (final String inputPart : commandInput.input().split(",")) {
                if (inputPart.equalsIgnoreCase("hand")) {
                    if (commandContext.sender() instanceof final Player player) {
                        itemStackSelection.addItemStack(player.getInventory().getItemInMainHand());
                    } else {
                        return ArgumentParseResult.failure(new MaterialParseException(inputPart, commandContext));
                    }
                } else if (inputPart.equalsIgnoreCase("any")) {
                    itemStackSelection.setAny(true);
                } else {
                    try {
                        itemStackSelection.addMaterial(Material.valueOf(inputPart.toUpperCase(Locale.ROOT)));
                    } catch (Exception ignored) {
                        final NQItem nqItem = main.getItemsManager().getItem(inputPart);
                        if (nqItem != null) {
                            itemStackSelection.addNqItem(nqItem);
                        } else {
                            return ArgumentParseResult.failure(new MaterialParseException(inputPart, commandContext));
                        }
                    }
                }
            }
            return ArgumentParseResult.success(itemStackSelection);

        } catch (final IllegalArgumentException exception) {
            return ArgumentParseResult.failure(new MaterialParseException(commandInput.input(), commandContext));
        }
    }


    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return (context, input) -> {
            final List<Suggestion> possibleMaterials = new ArrayList<>();
            for (final Material value : Material.values()) {
                possibleMaterials.add(Suggestion.suggestion(value.name().toLowerCase()));
                possibleMaterials.add(Suggestion.suggestion(value.name().toLowerCase() + ","));
            }

            for (final NQItem nqItem : main.getItemsManager().getItems()) {
                possibleMaterials.add(Suggestion.suggestion(nqItem.getItemName()));
                possibleMaterials.add(Suggestion.suggestion(nqItem.getItemName() + ","));
            }

            possibleMaterials.add(Suggestion.suggestion("hand"));
            possibleMaterials.add(Suggestion.suggestion("hand,"));
            possibleMaterials.add(Suggestion.suggestion("any"));
            possibleMaterials.add(Suggestion.suggestion("any,"));
            main.getUtilManager().sendFancyCommandCompletion((CommandSender) context.sender(), input.input().split(" "), "[Item Name / 'hand' / 'any']. Can be separated by comma", "[...]");


            String rawInput = input.input();
            if (!rawInput.contains(",")) {
                return CompletableFuture.completedFuture(possibleMaterials);
            } else {
                final List<Suggestion> completions = new ArrayList<>();
                final String partAfterLastCommaInInput = rawInput.substring((rawInput.lastIndexOf(",") > rawInput.length() - 1) ? (rawInput.lastIndexOf(",")) : (rawInput.lastIndexOf(",") + 1));
                for (final String possibleMaterial : possibleMaterials.stream().map(Suggestion::suggestion).toList()) {
                    final String string = rawInput.substring(0, rawInput.length() - 1 - partAfterLastCommaInInput.length()) + "," + possibleMaterial;
                    completions.add(Suggestion.suggestion(string));
                }
                return CompletableFuture.completedFuture(completions);
            }
        };
    }

    public static final class MaterialParseException extends ParserException {

        private static final long serialVersionUID = 1615554107385965610L;
        private final String input;

        /**
         * Construct a new MaterialParseException
         *
         * @param input   Input
         * @param context Command context
         */
        public MaterialParseException(
                final @NonNull String input,
                final @NonNull CommandContext<?> context
        ) {
            super(
                    ItemStackSelectionParser.class,
                    context,
                    BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_MATERIAL,
                    CaptionVariable.of("input", input)
            );
            this.input = input;
        }

        /**
         * Get the input
         *
         * @return Input
         */
        public @NonNull String getInput() {
            return this.input;
        }

    }
}