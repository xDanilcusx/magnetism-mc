package danilcus.magnetism.item;

import danilcus.magnetism.entity.SawEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SawBladeItem extends Item {
    public SawBladeItem(Settings settings) {
        super(settings);
    }

    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        return new SawEntity(world, shooter);
    }
}
