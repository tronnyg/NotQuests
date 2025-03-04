package rocks.gravili.notquests.paper.commands.category.admin;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.BaseCommand;
import rocks.gravili.notquests.paper.structs.ActiveQuest;
import rocks.gravili.notquests.paper.structs.CompletedQuest;
import rocks.gravili.notquests.paper.structs.Quest;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.ArrayList;

import static org.incendo.cloud.bukkit.parser.OfflinePlayerParser.offlinePlayerParser;
import static rocks.gravili.notquests.paper.commands.arguments.QuestParser.questParser;

public class QuestResetAndRemoveCommand extends BaseCommand {
    public QuestResetAndRemoveCommand(NotQuests notQuests, Command.Builder<CommandSender> builder) {
        super(notQuests, builder);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {

        builder = builder.commandDescription(Description.of("Removes the quest from a specific player players, removes it from completed quests, resets the accept cooldown and basically everything else."))
                .literal("resetAndRemoveQuest");
        commandManager.command(builder
                .required("player", offlinePlayerParser(), Description.of("Player name"))
                .required("quest", questParser(notQuests), Description.of("Name of the Quest which should be reset and removed."))
                .handler((context) -> {
                    context.sender().sendMessage(Component.empty());
                    final Player player = context.get("player");

                    removeQuest(player, context);
                    context.sender().sendMessage(notQuests.parse("<success>Operation done!"));
                }));

        commandManager.command(builder
                .literal("all")
                .required("quest", questParser(notQuests), Description.of("Name of the Quest which should be reset and removed."))
                .handler((context) -> {
                    context.sender().sendMessage(Component.empty());
                    final Player player = context.get("player");

                    notQuests.getQuestPlayerManager().getAllQuestPlayersForAllProfiles().forEach(questPlayer -> {
                        removeQuest(Bukkit.getOfflinePlayer(questPlayer.getUniqueId()), context);
                    });

                    context.sender().sendMessage(notQuests.parse("<success>Operation done!"));
                }));
    }

    private void removeQuest(OfflinePlayer offlinePlayer, CommandContext<CommandSender> context) {
        final QuestPlayer questPlayer = notQuests.getQuestPlayerManager().getActiveQuestPlayer(offlinePlayer.getUniqueId());

        if (questPlayer == null) {
            context.sender().sendMessage(notQuests.parse(
                    "<error>Error: QuestPlayer of Player <highlight>" + offlinePlayer.getName()+ "</highlight> not found."
            ));
            return;
        }
        final Quest quest = context.get("quest");
        final ArrayList<ActiveQuest> activeQuestsToRemove = new ArrayList<>();
        for (final ActiveQuest activeQuest : questPlayer.getActiveQuests()) {
            if (activeQuest.getQuest().equals(quest)) {
                activeQuestsToRemove.add(activeQuest);
                context.sender().sendMessage(notQuests.parse("<success>Removed the quest as an active quest for the player with the UUID <highlight>"
                        + questPlayer.getUniqueId().toString() + "</highlight> and name <highlight2>"
                        + Bukkit.getOfflinePlayer(questPlayer.getUniqueId()).getName() + "</highlight2>."
                ));

            }
        }

        questPlayer.getActiveQuests().removeAll(activeQuestsToRemove);

        final ArrayList<CompletedQuest> completedQuestsToRemove = new ArrayList<>();

        for (final CompletedQuest completedQuest : questPlayer.getCompletedQuests()) {
            if (completedQuest.getQuest().equals(quest)) {
                completedQuestsToRemove.add(completedQuest);
                context.sender().sendMessage(notQuests.parse("<success>Removed the quest as a completed quest for the player with the UUID <highlight>"
                        + questPlayer.getUniqueId().toString() + "</highlight> and name <highlight2>"
                        + Bukkit.getOfflinePlayer(questPlayer.getUniqueId()).getName() + "</highlight2>."
                ));
            }

        }

        questPlayer.getCompletedQuests().removeAll(completedQuestsToRemove);
    }
}
