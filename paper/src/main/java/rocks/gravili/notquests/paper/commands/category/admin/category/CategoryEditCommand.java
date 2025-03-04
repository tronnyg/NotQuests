package rocks.gravili.notquests.paper.commands.category.admin.category;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.suggestion.Suggestion;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.BaseCommand;
import rocks.gravili.notquests.paper.commands.arguments.wrappers.ItemStackSelection;
import rocks.gravili.notquests.paper.managers.data.Category;
import rocks.gravili.notquests.paper.structs.PredefinedProgressOrder;
import rocks.gravili.notquests.paper.structs.Quest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.incendo.cloud.parser.standard.StringParser.stringParser;
import static rocks.gravili.notquests.paper.commands.arguments.ItemStackSelectionParser.itemStackSelectionParser;

public class CategoryEditCommand extends BaseCommand {
    public CategoryEditCommand(NotQuests notQuests, Command.Builder<CommandSender> builder) {
        super(notQuests, builder);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        builder = builder.literal("edit");

        commandManager.command(builder.literal("predefinedProgressOrder")
                .literal("show", Description.of("Shows the current predefined order in which the quests inside this category need to be progressed for your quest."))
                .handler((context) -> {
                    final Category category = context.get("category");
                    context.sender().sendMessage(Component.empty());

                    final String predefinedProgressOrderString = category.getPredefinedProgressOrder() != null ? (category.getPredefinedProgressOrder().getReadableString())
                            : "None";

                    context.sender().sendMessage(notQuests.parse(
                            "<success>Current predefined progress order of category <highlight>" + category.getCategoryFullName()
                                    + "</highlight>: <highlight2>" + predefinedProgressOrderString
                    ));
                }));

        commandManager.command(builder.literal("predefinedProgressOrder")
                .literal("set")
                .literal("none", Description.of("Removes predefined order in which the quests inside this category need to be progressed for your quest."))
                .handler((context) -> {
                    final Category category = context.get("category");
                    category.setPredefinedProgressOrder(null, true);
                    context.sender().sendMessage(Component.empty());
                    context.sender().sendMessage(notQuests.parse(
                            "<success>Predefined progress order of category <highlight>" + category.getCategoryFullName()
                                    + "</highlight> have been removed!"
                    ));
                }));

        commandManager.command(builder.literal("predefinedProgressOrder")
                .literal("set")
                .literal("firstToLast", Description.of("Sets a predefined order in which the quests inside this category need to be progressed for your quest."))
                .handler((context) -> {
                    final Category category = context.get("category");
                    category.setPredefinedProgressOrder(PredefinedProgressOrder.firstToLast(), true);
                    context.sender().sendMessage(Component.empty());
                    context.sender().sendMessage(notQuests.parse(
                            "<success>Predefined progress order of category <highlight>" + category.getCategoryFullName()
                                    + "</highlight> have been set to first to last!"
                    ));
                }));

        commandManager.command(builder.literal("predefinedProgressOrder")
                .literal("set")
                .literal("lastToFirst", Description.of("Sets a predefined order in which the quests inside this category need to be progressed for your quest."))
                .handler((context) -> {
                    final Category category = context.get("category");
                    category.setPredefinedProgressOrder(PredefinedProgressOrder.lastToFirst(), true);
                    context.sender().sendMessage(Component.empty());
                    context.sender().sendMessage(notQuests.parse(
                            "<success>Predefined progress order of category <highlight>" + category.getCategoryFullName()
                                    + "</highlight> have been set to last to first!"
                    ));
                }));

        commandManager.command(builder.literal("predefinedProgressOrder")
                .literal("set")
                .literal("custom", Description.description("Sets a predefined order in which the quests need to be progressed in this category."))
                .required("order", stringParser(),
                        (context, input) -> {
                            notQuests.getUtilManager().sendFancyCommandCompletion(context.sender(), input.input().split(" "), "<Enter custom order (numbers of objective IDs separated by space)>", "");
                            ArrayList<Suggestion> completions = new ArrayList<>();
                            final Category category = context.get("category");

                            for (final Quest quest : category.getQuests()) {
                                completions.add(Suggestion.suggestion(quest.getIdentifier()));
                            }

                            return CompletableFuture.completedFuture(completions);
                        })
                .handler((context) -> {
                    final Category category = context.get("category");
                    final String[] order = context.get("order");
                    final String orderString = String.join(" ", order);
                    final ArrayList<String> orderParsed = new ArrayList<>();
                    Collections.addAll(orderParsed, order);

                    category.setPredefinedProgressOrder(PredefinedProgressOrder.custom(orderParsed), true);
                    context.sender().sendMessage(Component.empty());
                    context.sender().sendMessage(notQuests.parse(
                            "<success>Predefined progress order of category <highlight>" + category.getCategoryFullName()
                                    + "</highlight> have been set to custom with this order: " + orderString
                    ));
                }));


        commandManager.command(builder.literal("displayName")
                .literal("show", Description.of("Shows current Category display name."))
                .handler((context) -> {
                    final Category category = context.get("category");

                    context.sender().sendMessage(notQuests.parse(
                            "<notQuests>Current display name of Category <highlight>" + category.getCategoryFullName() + "</highlight>: <highlight2>"
                                    + category.getDisplayName()
                    ));
                }));
        commandManager.command(builder.literal("displayName")
                .literal("remove", Description.of("Removes current Category display name."))
                .handler((context) -> {
                    final Category category = context.get("category");

                    category.removeDisplayName(true);
                    context.sender().sendMessage(notQuests.parse("<success>Display name successfully removed from Category <highlight>"
                            + category.getCategoryFullName() + "</highlight>!"
                    ));
                }));

        commandManager.command(builder.commandDescription(Description.of("Sets the new display name of the Category."))
                .literal("displayName")
                .literal("set")
                .required("display-name", stringParser(),
                        (context, input) -> {
                            notQuests.getUtilManager().sendFancyCommandCompletion(context.sender(), input.input().split(" "), "<Enter new Category display name>", "");
                            ArrayList<Suggestion> completions = new ArrayList<>();

                            String rawInput = input.input();
                            String lastString = input.input().split(" ")[input.input().split(" ").length - 1];
                            if (lastString.startsWith("{")) {
                                notQuests.getCommandManager().getAdminCommands().placeholders.forEach(entry -> completions.add(Suggestion.suggestion(entry)));
                            } else {
                                if (lastString.startsWith("<")) {
                                    for (String color : notQuests.getUtilManager().getMiniMessageTokens()) {
                                        completions.add(Suggestion.suggestion("<" + color + ">"));
                                        //Now the closings. First we search IF it contains an opening and IF it doesnt contain more closings than the opening
                                        if (input.input().contains("<" + color + ">")) {
                                            if (StringUtils.countMatches(rawInput, "<" + color + ">") > StringUtils.countMatches(rawInput, "</" + color + ">")) {
                                                completions.add(Suggestion.suggestion("</" + color + ">"));
                                            }
                                        }
                                    }
                                } else {
                                    completions.add(Suggestion.suggestion("<Enter new Category display name>"));
                                }
                            }
                            return CompletableFuture.completedFuture(completions);
                        }
                )
                .handler((context) -> {
                    final Category category = context.get("category");

                    final String displayName = String.join(" ", (String[]) context.get("display-name"));

                    category.setDisplayName(displayName, true);
                    context.sender().sendMessage(notQuests.parse("<success>Display name successfully added to category <highlight>"
                            + category.getCategoryFullName() + "</highlight>! New display name: <highlight2>"
                            + category.getDisplayName()
                    ));
                }));

        commandManager.command(builder.commandDescription(Description.of("Sets the item displayed in the category GUI (default: book)."))
                .literal("guiItem")
                .required("material", itemStackSelectionParser(notQuests), Description.of("Material of item displayed in the category GUI."))
                .flag(commandManager.flagBuilder("glow").withDescription(Description.of("Makes the item have the enchanted glow.")))
                .handler((context) -> {
                    final Category category = context.get("category");
                    final boolean glow = context.flags().isPresent("glow");

                    final ItemStackSelection itemStackSelection = context.get("material");
                    ItemStack guiItem = itemStackSelection.toFirstItemStack();
                    if (guiItem == null) {
                        guiItem = new ItemStack(Material.BOOK, 1);
                    }

                    if (glow) {
                        guiItem.addUnsafeEnchantment(Enchantment.SHARPNESS, 1);
                        ItemMeta meta = guiItem.getItemMeta();
                        if (meta == null) {
                            meta = Bukkit.getItemFactory().getItemMeta(guiItem.getType());
                        }
                        if (meta != null) {
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            guiItem.setItemMeta(meta);
                        }

                    }

                    category.setGuiItem(guiItem, true);
                    context.sender().sendMessage(notQuests.parse(
                            "<success>GUI Item for Category <highlight>" + category.getCategoryFullName()
                                    + "</highlight> has been set to <highlight2>" + guiItem.getType().name() + "</highlight2>!"
                    ));
                }));
    }
}
