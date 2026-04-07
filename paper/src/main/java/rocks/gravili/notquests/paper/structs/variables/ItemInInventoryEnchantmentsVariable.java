package rocks.gravili.notquests.paper.structs.variables;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.suggestion.Suggestion;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.arguments.variables.StringVariableValueParser;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ItemInInventoryEnchantmentsVariable extends Variable<String[]> {
    public ItemInInventoryEnchantmentsVariable(NotQuests main) {
        super(main);
        setCanSetValue(false); // TODO: Add that to apply enchantments

        addRequiredString(StringVariableValueParser.of("ItemSlot", null, (context, lastString) -> {
            main.getUtilManager().sendFancyCommandCompletion(context.sender(), lastString.input().split(" "), "[Item Slot ID / Equipment Slot Name]", "[...]");
            ArrayList<Suggestion> suggestions = new ArrayList<>();
            for (final EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                suggestions.add(Suggestion.suggestion(equipmentSlot.name()));
            }
            for (int i = 0; i <= 35; i++) {
                suggestions.add(Suggestion.suggestion(String.valueOf(i)));
            }
            return CompletableFuture.completedFuture(suggestions);
        }));
    }

    @Override
    public String[] getValueInternally(QuestPlayer questPlayer, Object... objects) {
        if (questPlayer == null) {
            return null;
        }
        questPlayer.sendDebugMessage("ItemInInventoryEnchantmentsVariable.getValue() stage 1");

        final Player player = questPlayer.getPlayer();
        if (player == null) {
            return null;
        }

        final String itemSlot = getRequiredStringValue("ItemSlot");
        ItemStack itemStackToCheck;

        questPlayer.sendDebugMessage("ItemInInventoryEnchantmentsVariable.getValue() stage 2. ItemSlot: " + itemSlot);


        try {
            final EquipmentSlot equipmentSlot = EquipmentSlot.valueOf(itemSlot);
            itemStackToCheck = player.getEquipment().getItem(equipmentSlot);
        } catch (Exception ignored) {
            try {
                itemStackToCheck = player.getInventory().getItem(Integer.parseInt(itemSlot));
            } catch (Exception ignored2) {
                return null;
            }
        }
        if (itemStackToCheck == null) {
            return new String[]{};
        }
        questPlayer.sendDebugMessage("ItemInInventoryEnchantmentsVariable.getValue() stage 3");

        final List<String> enchantmentsList = new ArrayList<>();
        for (final Enchantment enchantment : itemStackToCheck.getEnchantments().keySet()) {
            enchantmentsList.add(enchantment.getKey().asString());
        }
        questPlayer.sendDebugMessage("ItemStackToCheck: " + itemStackToCheck.toString());

        questPlayer.sendDebugMessage("Enchantments: " + enchantmentsList);

        return enchantmentsList.toArray(new String[0]);
    }

    @Override
    public boolean setValueInternally(String[] newValue, QuestPlayer questPlayer, Object... objects) {
        return false; // TODO: This can be added
    }

    @Override
    public List<Suggestion> getPossibleValues(QuestPlayer questPlayer, Object... objects) {
        return Arrays.stream(Enchantment.values())
                .map(Enchantment::getKey)
                .map(namespacedKey -> Suggestion.suggestion(namespacedKey.asString()))
                .toList();
    }

    @Override
    public String getPlural() {
        return "Enchantments for specific item in inventory";
    }

    @Override
    public String getSingular() {
        return "Enchantment for specific item in inventory";
    }
}
