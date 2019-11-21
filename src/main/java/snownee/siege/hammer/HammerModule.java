package snownee.siege.hammer;

import net.minecraft.item.ItemTier;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;

@KiwiModule(name = "hammer", dependencies = "@block")
@KiwiModule.Optional
public class HammerModule extends AbstractModule {
    public static final HammerItem STONE_HAMMER = new HammerItem(7.0F, -3.2F, ItemTier.STONE, itemProp(), 1);
    public static final HammerItem IRON_HAMMER = new HammerItem(6.0F, -3.1F, ItemTier.IRON, itemProp(), 2);
    public static final HammerItem DIAMOND_HAMMER = new HammerItem(5.0F, -3.0F, ItemTier.DIAMOND, itemProp(), 3);
}
