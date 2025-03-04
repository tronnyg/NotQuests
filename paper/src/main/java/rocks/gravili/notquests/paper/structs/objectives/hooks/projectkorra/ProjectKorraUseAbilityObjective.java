/*
 * NotQuests - A Questing plugin for Minecraft Servers
 * Copyright (C) 2021-2022 Alessio Gravili
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

package rocks.gravili.notquests.paper.structs.objectives.hooks.projectkorra;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.Command;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.suggestion.Suggestion;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.structs.ActiveObjective;
import rocks.gravili.notquests.paper.structs.QuestPlayer;
import rocks.gravili.notquests.paper.structs.objectives.Objective;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class ProjectKorraUseAbilityObjective extends Objective {
    private String abilityName = "";

    public ProjectKorraUseAbilityObjective(NotQuests main) {
        super(main);
    }

    public static void handleCommands(
            NotQuests main,
            LegacyPaperCommandManager<CommandSender> manager,
            Command.Builder<CommandSender> addObjectiveBuilder,
            final int level) {
        if (!main.getIntegrationsManager().isProjectKorraEnabled()) {
            return;
        }

        manager.command(addObjectiveBuilder
                .required("Ability", stringParser(), Description.of("Name of the ability"), (context, lastString) -> {
                    main.getUtilManager().sendFancyCommandCompletion(context.sender(), lastString.input().split(" "), "[Ability Name]", "");

                    return CompletableFuture.completedFuture(main.getIntegrationsManager().getProjectKorraManager().getAbilityCompletions().stream().map(Suggestion::suggestion).toList());
                })
                .required("amount", integerParser(1), Description.of("Amount of times to use the ability"))
                .handler(
                        (context) -> {
                            String abilityName = context.get("Ability");

                            if (!main.getIntegrationsManager().getProjectKorraManager().isAbility(abilityName)) {
                                context.sender().sendMessage(main.parse("<error>Error: The ability <highlight>" + abilityName + "</highlight> was not found."));
                                return;
                            }

                            ProjectKorraUseAbilityObjective projectKorraUseAbilityObjective = new ProjectKorraUseAbilityObjective(main);
                            projectKorraUseAbilityObjective.setProgressNeededExpression(context.get("amount"));
                            projectKorraUseAbilityObjective.setAbilityName(abilityName);
                            main.getObjectiveManager().addObjective(projectKorraUseAbilityObjective, context, level);
                        }));
    }

    public final String getAbilityName() {
        return abilityName;
    }

    public void setAbilityName(final String abilityName) {
        this.abilityName = abilityName;
    }

    @Override
    public String getTaskDescriptionInternal(final QuestPlayer questPlayer, final @Nullable ActiveObjective activeObjective) {
        return main.getLanguageManager().getString("chat.objectives.taskDescription.ProjectKorraUseAbility.base",
                questPlayer,
                activeObjective,
                Map.of("%ABILITY%", getAbilityName()));
    }

    @Override
    public void save(FileConfiguration configuration, String initialPath) {
        configuration.set(initialPath + ".specifics.ability", getAbilityName());
    }

    @Override
    public void load(FileConfiguration configuration, String initialPath) {
        abilityName = configuration.getString(initialPath + ".specifics.ability");
    }

    @Override
    public void onObjectiveUnlock(
            final ActiveObjective activeObjective,
            final boolean unlockedDuringPluginStartupQuestLoadingProcess) {
    }

    @Override
    public void onObjectiveCompleteOrLock(
            final ActiveObjective activeObjective,
            final boolean lockedOrCompletedDuringPluginStartupQuestLoadingProcess,
            final boolean completed) {
    }
}
