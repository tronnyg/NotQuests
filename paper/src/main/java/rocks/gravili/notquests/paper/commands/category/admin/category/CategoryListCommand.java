package rocks.gravili.notquests.paper.commands.category.admin.category;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.BaseCommand;
import rocks.gravili.notquests.paper.managers.data.Category;

public class CategoryListCommand extends BaseCommand {
    public CategoryListCommand(NotQuests notQuests, Command.Builder<CommandSender> builder) {
        super(notQuests, builder);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(builder.commandDescription(Description.of("Lists all categories."))
                .literal("list")
                .handler((context) -> {
                    context.sender().sendMessage(Component.empty());
                    context.sender().sendMessage(notQuests.parse("<highlight>All categories:"));
                    int counter = 1;
                    for (final Category category : notQuests.getDataManager().getCategories()) {
                        context.sender().sendMessage(notQuests.parse("<highlight>" + counter + ".</highlight> <notQuests>" + category.getCategoryFullName()));
                        counter++;
                    }
                }));
    }
}
