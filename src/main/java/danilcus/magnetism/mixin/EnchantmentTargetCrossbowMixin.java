package danilcus.magnetism.mixin;

import danilcus.magnetism.item.SawbladeLauncherItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/enchantment/EnchantmentTarget$5")
public class EnchantmentTargetCrossbowMixin {
    @Inject(method = "isAcceptableItem(Lnet/minecraft/item/Item;)Z", at = @At("HEAD"), cancellable = true)
    public void ExcludeCrossbowEnchantmentsOnLauncher(Item item, CallbackInfoReturnable<Boolean> info) {
        if(item instanceof SawbladeLauncherItem) {
            info.setReturnValue(false);
        }
    }
}
