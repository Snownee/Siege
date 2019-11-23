package snownee.siege.hammer;

import java.util.Collections;

import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import snownee.siege.SiegeConfig;
import snownee.siege.block.BlockModule;

public class HammerItem extends ToolItem {

    public HammerItem(float attackDamageIn, float attackSpeedIn, IItemTier tier, Properties builder, int toolLevel) {
        super(attackDamageIn, attackSpeedIn, tier, Collections.EMPTY_SET, builder.group(ItemGroup.TOOLS).addToolType(BlockModule.hammerToolType, toolLevel).maxDamage(tier.getMaxUses() * 50));
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return SiegeConfig.hammerDurability ? super.getMaxDamage(stack) : 0;
    }

}
