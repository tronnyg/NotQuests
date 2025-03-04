package rocks.gravili.notquests.paper.commands.category.admin;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.BaseCommand;

public class EditorCommand extends BaseCommand {

    public EditorCommand(NotQuests notQuests, Command.Builder<CommandSender> builder) {
        super(notQuests, builder);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        builder.commandDescription(Description.of("Opens the web editor."))
                .literal("editor")
                .handler((context) -> context.sender().sendMessage(notQuests.parse("<main>This feature is still in development. The web editor does not work at all yet. Sorry! This command just acts as a placeholder. Consult the NotQuests documentation for a tutorial on how to use NotQuests.")));
    }
}
