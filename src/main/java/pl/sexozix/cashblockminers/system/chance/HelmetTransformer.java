package pl.sexozix.cashblockminers.system.chance;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class HelmetTransformer implements ChanceTransformer {
    @Override
    public boolean applyChance(TransformerContext context) {
        ItemStack helmet = context.getPlayer().getInventory().getHelmet();
        if (helmet != null)
            context.multiplyQuantity(1.0 + (helmet.getEnchantmentLevel(Enchantment.LUCK) * 0.1));
        return true;
    }
}
