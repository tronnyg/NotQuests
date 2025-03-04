package rocks.gravili.notquests.paper.commands.category.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.BaseCommand;
import rocks.gravili.notquests.paper.structs.Quest;

import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;
import static rocks.gravili.notquests.paper.commands.arguments.QuestParser.questParser;

public class QuestGiveCommand extends BaseCommand {

    public QuestGiveCommand(NotQuests notQuests, Command.Builder<CommandSender> builder) {
        super(notQuests, builder);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(builder.literal("give", Description.of("Gives a player a quest without bypassing the Quest requirements."))
                .required("player", playerParser(), Description.of("Player who should start the quest."))
                .required("quest", questParser(notQuests), Description.of("Name of the Quest the player should start."))
                .flag(commandManager.flagBuilder("force"))
                .handler((context) -> {
                    final Player player = context.get("player");
                    final Quest quest = context.get("quest");
                    if (context.flags().hasFlag("force")) {
                        context.sender().sendMessage(notQuests.parse("<notQuests>" + notQuests.getQuestPlayerManager().forceAcceptQuest(player.getUniqueId(), quest)));
                    } else {
                        context.sender().sendMessage(notQuests.parse("<notQuests>" + notQuests.getQuestPlayerManager().acceptQuest(player, quest, true, true)));
                        
                    }
                }));
    }
}
