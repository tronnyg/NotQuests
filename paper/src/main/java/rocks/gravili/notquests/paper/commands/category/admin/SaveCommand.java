package rocks.gravili.notquests.paper.commands.category.admin;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.BaseCommand;

public class SaveCommand extends BaseCommand {
    public SaveCommand(NotQuests notQuests, Command.Builder<CommandSender> builder) {
        super(notQuests, builder);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(builder.commandDescription(Description.of("Saves the NotQuests configuration file."))
                .literal("save")
                .handler((context) -> {
                    notQuests.getDataManager().saveData();
                    context.sender().sendMessage(Component.empty());
                    context.sender().sendMessage(notQuests.parse("<success>NotQuests configuration and player data has been saved"));
                }));
    }
}
