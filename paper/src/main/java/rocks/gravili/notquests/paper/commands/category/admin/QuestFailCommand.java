package rocks.gravili.notquests.paper.commands.category.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.BaseCommand;
import rocks.gravili.notquests.paper.structs.ActiveQuest;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;
import static rocks.gravili.notquests.paper.commands.arguments.ActiveQuestParser.activeQuestParser;

public class QuestFailCommand extends BaseCommand {

    public QuestFailCommand(NotQuests notQuests, Command.Builder<CommandSender> builder) {
        super(notQuests, builder);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(builder.literal("failQuest").commandDescription(Description.of("Fails an active quest for a player"))
                .required("player", playerParser(), Description.of("Player name whose quest should be failed."))
                .required("activeQuest", activeQuestParser(notQuests), Description.of("Active quest which should be failed."))
                .handler((context) -> {
                    final Player player = context.get("player");
                    final ActiveQuest activeQuest = context.get("activeQuest");
                    final QuestPlayer questPlayer = notQuests.getQuestPlayerManager().getActiveQuestPlayer(player.getUniqueId());
                    if (questPlayer != null) {
                        questPlayer.failQuest(activeQuest);
                        context.sender().sendMessage(notQuests.parse(
                                "<notQuests>The active quest <highlight>" + activeQuest.getQuest().getIdentifier() + "</highlight> has been failed for player <highlight2>" + player.getName() + "</highlight2>!"
                        ));

                    } else {
                        context.sender().sendMessage(notQuests.parse(
                                "<error>Player <highlight>" + player.getName() + "</highlight> seems to not have accepted any quests!"
                        ));
                    }
                }));
    }
}
