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

package rocks.gravili.notquests.paper.managers;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessingContext;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessor;
import org.incendo.cloud.services.type.ConsumerService;
import org.jetbrains.annotations.NotNull;
import rocks.gravili.notquests.paper.NotQuests;

public class CommandPostProcessor<C> implements CommandPostprocessor<C> {
    private final NotQuests main;

    public CommandPostProcessor(final NotQuests main){
        this.main = main;
    }

    @Override
    public void accept(@NotNull final CommandPostprocessingContext<C> context) {
        if (main.getDataManager().isDisabled() && !(context.command().nonFlagArguments().size() >= 3 && (context.command().nonFlagArguments().get(2).name().equalsIgnoreCase("enablePluginAndSaving") || context.command().nonFlagArguments().get(2).name().equalsIgnoreCase("disablePluginAndSaving") || context.command().nonFlagArguments().get(2).name().equalsIgnoreCase("showErrorsAndWarnings")))) {
            if (context.commandContext().sender() instanceof final CommandSender commandSender) {
                main.getDataManager().sendPluginDisabledMessage(commandSender);
            }
            ConsumerService.interrupt();
        }
    }

}