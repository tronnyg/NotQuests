package rocks.gravili.notquests.paper.commands.category.admin;

import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.BaseCommand;

public class VersionCommand extends BaseCommand {
    public VersionCommand(NotQuests notQuests, Command.Builder<CommandSender> builder) {
        super(notQuests, builder);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(builder.commandDescription(Description.of("Displays the version of the NotQuests plugin you're using."))
                .literal("version", "ver", "v", "info")
                .handler((context) -> context.sender().sendMessage(notQuests.parse("<notQuests>NotQuests version: <highlight>" + notQuests.getMain().getDescription().getVersion() +
                                        "\n<notQuests>NotQuests module: <highlight>Paper" +
                                        "\n<notQuests>Server version: <highlight>" + Bukkit.getVersion() +
                                        "\n<notQuests>Server Brand: <highlight>" + Bukkit.getServer().getName() +
                                        "\n<notQuests>Java version: <highlight>" + (System.getProperty("java.version") != null ? System.getProperty("java.version") : "null") +
                                        "\n<notQuests>Enabled integrations: <highlight>" + notQuests.getIntegrationsManager().getEnabledIntegrationString()
                                )
                                .hoverEvent(HoverEvent.showText(notQuests.parse("<notQuests>Click to copy this information to your clipboard.")))
                                .clickEvent(ClickEvent.copyToClipboard("**NotQuests version:** " + notQuests.getMain().getDescription().getVersion() +
                                        "\n**NotQuests module:** Paper" +
                                        "\n**Server version:** " + Bukkit.getVersion() +
                                        "\n**Server Brand:** " + Bukkit.getServer().getName() +
                                        "\n**Java version:** " + (System.getProperty("java.version") != null ? System.getProperty("java.version") : "null") +
                                        "\n**Enabled integrations:**" + notQuests.getIntegrationsManager().getEnabledIntegrationDiscordString()
                                ))
                )));
    }
}
