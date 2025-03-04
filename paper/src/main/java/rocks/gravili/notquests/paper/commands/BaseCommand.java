package rocks.gravili.notquests.paper.commands;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import rocks.gravili.notquests.paper.NotQuests;

public abstract class BaseCommand {
    protected final NotQuests notQuests;
    protected Command.Builder<CommandSender> builder;

    public BaseCommand(NotQuests notQuests, Command.Builder<CommandSender> builder) {
        this.notQuests = notQuests;
        this.builder = builder;
    }

    public abstract void apply(CommandManager<CommandSender> commandManager);
}
