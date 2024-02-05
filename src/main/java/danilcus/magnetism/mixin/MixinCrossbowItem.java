package danilcus.magnetism.mixin;

import danilcus.magnetism.item.SawbladeLauncherItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrossbowItem.class)
public class MixinCrossbowItem {
    @Inject(
            at = @At("HEAD"),
            method = "shoot(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;FZFFF)V",
            cancellable = true
    )
    private static void shoot(
            World world,
            LivingEntity shooter,
            Hand hand,
            ItemStack crossbow,
            ItemStack projectile,
            float soundPitch,
            boolean creative,
            float speed,
            float divergence,
            float simulated,
            CallbackInfo cir
    ) {
        if(crossbow.getItem() instanceof SawbladeLauncherItem) {
            ((SawbladeLauncherItem)crossbow.getItem()).shoot(world, shooter, hand, crossbow, projectile, soundPitch, creative, speed, divergence, simulated);

            cir.cancel();
        }
    }
}
