package rocks.gravili.notquests.paper.commands.category.admin.structs;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.suggestion.Suggestion;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.BaseCommand;
import rocks.gravili.notquests.paper.managers.npc.NQNPC;
import rocks.gravili.notquests.paper.structs.ActiveObjective;
import rocks.gravili.notquests.paper.structs.ActiveQuest;
import rocks.gravili.notquests.paper.structs.Quest;
import rocks.gravili.notquests.paper.structs.QuestPlayer;
import rocks.gravili.notquests.paper.structs.objectives.Objective;
import rocks.gravili.notquests.paper.structs.objectives.TriggerCommandObjective;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class ObjectiveTriggerCommand extends BaseCommand {

    public ObjectiveTriggerCommand(NotQuests notQuests, Command.Builder<CommandSender> builder) {
        super(notQuests, builder);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(builder.commandDescription(Description.of("This triggers the Trigger Command which is needed to complete a TriggerObjective (don't mistake it with Triggers & actions)."))
                .literal("triggerObjective")
                .required("trigger-name", stringParser(), Description.of("Name of the trigger which should be triggered."), (context, input) -> {
                            notQuests.getUtilManager().sendFancyCommandCompletion(context.sender(), input.input().split(" "), "[Trigger Name]", "[Player Name]");

                            ArrayList<Suggestion> completions = new ArrayList<>();
                            for (final Quest quest : notQuests.getQuestManager().getAllQuests()) {
                                for (final Objective objective : quest.getObjectives()) {
                                    if (objective instanceof final TriggerCommandObjective triggerCommandObjective) {
                                        completions.add(Suggestion.suggestion(triggerCommandObjective.getTriggerName()));
                                    }
                                }
                            }
                            return CompletableFuture.completedFuture(completions);
                        }
                )
                .required("player", playerParser(), Description.of("Player whose trigger should e triggered."))
                .handler((context) -> {
                    final String triggerName = context.get("Trigger Name");
                    final Player player = context.get("player");
                    final QuestPlayer questPlayer = notQuests.getQuestPlayerManager().getActiveQuestPlayer(player.getUniqueId());
                    if (questPlayer != null) {
                        if (!questPlayer.getActiveQuests().isEmpty()) {
                            for (ActiveQuest activeQuest : questPlayer.getActiveQuests()) {
                                for (ActiveObjective activeObjective : activeQuest.getActiveObjectives()) {
                                    if (activeObjective.isUnlocked()) {
                                        if (activeObjective.getObjective() instanceof final TriggerCommandObjective triggerCommandObjective) {
                                            if (triggerCommandObjective.getTriggerName().equalsIgnoreCase(triggerName)) {
                                                activeObjective.addProgress(1, (NQNPC) null);

                                            }
                                        }
                                    }
                                }
                                activeQuest.removeCompletedObjectives(true);
                            }
                            questPlayer.removeCompletedQuests();
                        }
                    }
                }));
    }
}
