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

import com.sun.jna.StringArray;
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

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommandParser<C> implements ArgumentParser<C, StringArray> {
    private final NotQuests main;

    protected CommandParser(NotQuests main) {
        this.main = main;
    }

    public static <C> @NonNull ParserDescriptor<C, StringArray> commandParser(final NotQuests main) {
        return ParserDescriptor.of(new CommandParser<>(main), StringArray.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull StringArray> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        final String[] result = new String[commandInput.input().split(" ").length];
        StringArray finalResult = new StringArray(result);
        for (int i = 0; i < result.length; i++) {
            result[i] = commandInput.peekString(); // TODO: Missing context
        }
        return ArgumentParseResult.success(finalResult);
    }


    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return (context, input) -> {
            String cmd = input.input().substring(input.input().indexOf("ConsoleCommand") + 15);
            List<Suggestion> completions = new java.util.ArrayList<>();
            // audience.sendMessage(main.parse(
            //        "Input: " + cmd
            // ));

            if (main.getCommandManager().getCommandMap() != null) {
                List<String> compl = main.getCommandManager().getCommandMap().tabComplete(main.getMain().getServer().getConsoleSender(), cmd);
                if (compl != null) {
                    for (String cmd1 : compl) {
                        completions.add(Suggestion.suggestion(cmd1));
                    }
                }
            }

            if (input.input().startsWith("{")) {
                main.getCommandManager().getAdminCommands().placeholders.forEach(s -> completions.add(Suggestion.suggestion(s)));
            }
            main.getUtilManager().sendFancyCommandCompletion((CommandSender) context.sender(), input.input().split(" "), "<Enter Console Command>", "");
            return CompletableFuture.completedFuture(completions);
        };
    }
}