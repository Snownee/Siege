package snownee.siege.hammer;

import java.util.Collections;

import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolItem;
import snownee.siege.SiegeConfig;

public class HammerItem extends ToolItem {

    public HammerItem(float attackDamageIn, float attackSpeedIn, IItemTier tier, Properties builder, int toolLevel) {
        super(attackDamageIn, attackSpeedIn, tier, Collections.EMPTY_SET, builder.group(ItemGroup.TOOLS).addToolType(SiegeConfig.hammerToolType, toolLevel));
    }
}
