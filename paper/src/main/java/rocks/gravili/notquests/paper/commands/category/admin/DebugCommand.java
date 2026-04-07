package rocks.gravili.notquests.paper.commands.category.admin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.BaseCommand;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.ArrayList;

import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;
import static org.incendo.cloud.bukkit.parser.location.LocationParser.locationParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class DebugCommand extends BaseCommand {
    public DebugCommand(NotQuests notQuests, Command.Builder<CommandSender> builder) {
        super(notQuests, builder);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(builder.literal("debug", Description.of("Toggles debug mode for yourself."))
                .senderType(Player.class)
                .handler((context) -> {

                    if (notQuests.getQuestManager().isDebugEnabledPlayer(((Player) context.sender()).getUniqueId())) {
                        notQuests.getQuestManager().removeDebugEnabledPlayer(((Player) context.sender()).getUniqueId());
                        context.sender().sendMessage(notQuests.parse("<success>Your debug mode has been disabled."));
                    } else {
                        notQuests.getQuestManager().addDebugEnabledPlayer(((Player) context.sender()).getUniqueId());
                        context.sender().sendMessage(notQuests.parse("<success>Your debug mode has been enabled."));
                    }

                }));

        commandManager.command(builder.commandDescription(Description.of("Clears your own chat"))
                .literal("debug")
                .literal("clearOwnChat")
                .handler((context) -> {
                    final Component componentToSend = Component.text("").append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline()).append(Component.newline());
                    context.sender().sendMessage(componentToSend);
                }));

        commandManager.command(builder.commandDescription(Description.of("Shows you information about the current world"))
                .literal("debug")
                .literal("worldInfo")
                .senderType(Player.class)
                .handler((context) -> {
                    context.sender().sendMessage(Component.empty());
                    Player player = (Player) context.sender();
                    player.sendMessage(notQuests.parse(
                            "<notQuests>Current world name: <highlight>" + player.getWorld().getName() + "\n" +
                                    "<notQuests>Current world UUD: <highlight>" + player.getWorld().getUID().toString()

                    ));
                }));


        commandManager.command(builder.commandDescription(Description.of("Calls the dataManager.reloadData() method. This starts loading all Config-, Quest-, and Player Data. Reload = Load"))
                .literal("debug")
                .literal("loadDataManagerUnsafe")
                .handler((context) -> {
                    context.sender().sendMessage(Component.empty());
                    context.sender().sendMessage(notQuests.parse(
                            "<notQuests>Reloading DataManager..."
                    ));
                    notQuests.getDataManager().reloadData(false);
                    context.sender().sendMessage(notQuests.parse(
                            "<success>DataManager has been reloaded!"

                    ));
                }));

        commandManager.command(builder.commandDescription(Description.of("Disables NotQuests, saving & loading"))
                .literal("debug")
                .literal("disablePluginAndSaving")
                .required("reason", stringParser(), Description.of("Reason for disabling the plugin"))
                .handler((context) -> {
                    context.sender().sendMessage(Component.empty());

                    if (notQuests.getDataManager().isDisabled()) {
                        context.sender().sendMessage(notQuests.parse(
                                "<error>Error: NotQuests is already disabled"
                        ));
                        return;
                    }

                    final String reason = context.get("reason");
                    context.sender().sendMessage(notQuests.parse(
                            "<notQuests>Disabling NotQuests..."
                    ));
                    notQuests.getDataManager().disablePluginAndSaving(reason);

                }));

        commandManager.command(builder.commandDescription(Description.of("Shows the current errors and warnings NotQuests collected"))
                .literal("debug")
                .literal("showErrorsAndWarnings")
                .flag(
                        commandManager.flagBuilder("printToConsole")
                                .withDescription(Description.of("Prints the output to the console"))
                )
                .handler((context) -> {
                    final boolean printToConsole = context.flags().contains("printToConsole");


                    if (!printToConsole) {
                        context.sender().sendMessage(Component.empty());
                        notQuests.getDataManager().sendErrorsAndWarnings(context.sender());
                    } else {
                        notQuests.getMain().getServer().getConsoleSender().sendMessage(Component.empty());
                        notQuests.getDataManager().sendErrorsAndWarnings(notQuests.getMain().getServer().getConsoleSender());
                        context.sender().sendMessage(
                                notQuests.parse(
                                        "<success>Error and warnings have been printed to console successfully!"
                                )
                        );
                    }
                }));

        commandManager.command(builder.commandDescription(Description.of("Enables NotQuests, saving & loading"))
                .literal("debug")
                .literal("enablePluginAndSaving")
                .required("reason", stringParser(), Description.of("Reason for enabling the plugin"))
                .handler((context) -> {
                    context.sender().sendMessage(Component.empty());

                    if (!notQuests.getDataManager().isDisabled()) {
                        context.sender().sendMessage(notQuests.parse(
                                "<error>Error: NotQuests is already enabled"
                        ));
                        return;
                    }

                    final String reason = context.get("reason");
                    context.sender().sendMessage(notQuests.parse(
                            "<notQuests>Enabling NotQuests..."
                    ));
                    notQuests.getDataManager().enablePluginAndSaving(reason);

                }));

        commandManager.command(builder.commandDescription(Description.of("You can probably ignore this."))
                .literal("debug")
                .literal("testcommand")
                .senderType(Player.class)
                .handler((context) -> {
                    context.sender().sendMessage(Component.empty());
                    Player player = (Player) context.sender();
                    ArrayList<Component> history = notQuests.getConversationManager().getChatHistory().get(player.getUniqueId());
                    if (history != null) {
                        Component collectiveComponent = Component.text("");
                        for (Component component : history) {
                            if (component != null) {
                                // audience.sendMessage(component.append(Component.text("fg9023zf729ofz")));
                                collectiveComponent = collectiveComponent.append(component).append(Component.newline());
                            }
                        }

                        context.sender().sendMessage(collectiveComponent);

                    } else {
                        context.sender().sendMessage(notQuests.parse("<error>No chat history!"));
                    }

                }));


        commandManager.command(builder.commandDescription(Description.of("You can probably ignore this."))
                .literal("debug")
                .literal("testcommand2")
                .senderType(Player.class)
                .handler((context) -> {
                    context.sender().sendMessage(Component.empty());
                    Player player = (Player) context.sender();
                    ArrayList<Component> history = notQuests.getConversationManager().getChatHistory().get(player.getUniqueId());
                    if (history != null) {
                        Component collectiveComponent = Component.text("");
                        for (int i = 0; i < history.size(); i++) {
                            Component component = history.get(i);
                            if (component != null) {
                                // audience.sendMessage(component.append(Component.text("fg9023zf729ofz")));
                                collectiveComponent = collectiveComponent.append(Component.text(i + ".", NamedTextColor.RED).append(component)).append(Component.newline());
                            }
                        }

                        context.sender().sendMessage(collectiveComponent);

                    } else {
                        context.sender().sendMessage(notQuests.parse("<error>No chat history!"));
                    }

                }));

        commandManager.command(builder.commandDescription(Description.of("Spawns a beacon beam"))
                .literal("debug")
                .literal("beaconBeam")
                .required("player", playerParser(), Description.of("Player name"))
                .required("location-name", stringParser(), Description.of("Location name"))
                .required("location", locationParser())
                .handler((context) -> {
                    final Player player = context.get("player");
                    final String locationName = context.get("location-name");
                    final Location location = context.get("location");

                    /*if(notQuests.getPacketManager().isModern()){
                        notQuests.getPacketManager().getModernPacketInjector().spawnBeaconBeam(player, location);
                        notQuests.sendMessage(player, "<success>Beacon beam spawned successfully!");
                    }*/


                    final QuestPlayer questPlayer = notQuests.getQuestPlayerManager().getOrCreateQuestPlayer(player.getUniqueId());

                    questPlayer.getLocationsAndBeacons().put(locationName, location);
                    questPlayer.updateBeaconLocations(player);

                    notQuests.sendMessage(context.sender(), "<success>Beacon beam spawned successfully!");
                }));
    }
}
