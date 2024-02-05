package danilcus.magnetism.item;

import danilcus.magnetism.Magnetism;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class SawbladeLauncherItem extends CrossbowItem {
    public static final Predicate<ItemStack> SAWBLADES = stack -> stack.isOf(Magnetism.SAW_BLADE);

    public SawbladeLauncherItem(Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getHeldProjectiles() {
        return SAWBLADES;
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return SAWBLADES;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack) - remainingUseTicks;
        float f = getPullProgress(i, stack);
        if (f >= 1.0F && !isCharged(stack) && loadProjectiles(user, stack)) {
            setCharged(stack, true);
            SoundCategory soundCategory = user instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
            world.playSound(
                    null,
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    SoundEvents.ITEM_CROSSBOW_LOADING_END,
                    soundCategory,
                    1.0F,
                    1.0F / (world.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
            );
        }
    }

    protected float getPullProgress(int useTicks, ItemStack stack) {
        float f = (float)useTicks / (float)getPullTime(stack);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    protected boolean loadProjectiles(LivingEntity shooter, ItemStack projectile) {
        int i = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, projectile);
        int j = i == 0 ? 1 : 3;
        boolean bl = shooter instanceof PlayerEntity && ((PlayerEntity)shooter).getAbilities().creativeMode;
        ItemStack itemStack = shooter.getArrowType(projectile);
        ItemStack itemStack2 = itemStack.copy();

        for(int k = 0; k < j; ++k) {
            if (k > 0) {
                itemStack = itemStack2.copy();
            }

            if (itemStack.isEmpty() && bl) {
                itemStack = new ItemStack(Magnetism.SAW_BLADE);
                itemStack2 = itemStack.copy();
            }

            if (!loadProjectile(shooter, projectile, itemStack, k > 0, bl)) {
                return false;
            }
        }

        return true;
    }

    protected boolean loadProjectile(LivingEntity shooter, ItemStack crossbow, ItemStack projectile, boolean simulated, boolean creative) {
        if (projectile.isEmpty()) {
            return false;
        } else {
            boolean bl = creative && projectile.getItem() instanceof SawBladeItem;
            ItemStack itemStack;
            if (!bl && !creative && !simulated) {
                itemStack = projectile.split(1);
                if (projectile.isEmpty() && shooter instanceof PlayerEntity) {
                    ((PlayerEntity)shooter).getInventory().removeOne(projectile);
                }
            } else {
                itemStack = projectile.copy();
            }

            putProjectile(crossbow, itemStack);
            return true;
        }
    }

    protected void putProjectile(ItemStack crossbow, ItemStack projectile) {
        NbtCompound nbtCompound = crossbow.getOrCreateNbt();
        NbtList nbtList;
        if (nbtCompound.contains("ChargedProjectiles", NbtElement.LIST_TYPE)) {
            nbtList = nbtCompound.getList("ChargedProjectiles", NbtElement.COMPOUND_TYPE);
        } else {
            nbtList = new NbtList();
        }

        NbtCompound nbtCompound2 = new NbtCompound();
        projectile.writeNbt(nbtCompound2);
        nbtList.add(nbtCompound2);
        nbtCompound.put("ChargedProjectiles", nbtList);
    }

    public void shoot(
            World world,
            LivingEntity shooter,
            Hand hand,
            ItemStack crossbow,
            ItemStack projectile,
            float soundPitch,
            boolean creative,
            float speed,
            float divergence,
            float simulated
    ) {
        if (!world.isClient) {
            ProjectileEntity projectileEntity;
            projectileEntity = createArrow(world, shooter, crossbow, projectile);
            if (creative || simulated != 0.0F) {
                ((PersistentProjectileEntity)projectileEntity).pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
            }

            if (shooter instanceof CrossbowUser crossbowUser) {
                crossbowUser.shoot(crossbowUser.getTarget(), crossbow, projectileEntity, simulated);
            } else {
                Vec3d vec3d = shooter.getOppositeRotationVector(1.0F);
                Quaternion quaternion = new Quaternion(new Vec3f(vec3d), simulated, true);
                Vec3d vec3d2 = shooter.getRotationVec(1.0F);
                Vec3f vec3f = new Vec3f(vec3d2);
                vec3f.rotate(quaternion);
                projectileEntity.setVelocity((double)vec3f.getX(), (double)vec3f.getY(), (double)vec3f.getZ(), speed, divergence);
            }

            crossbow.damage(1, shooter, e -> e.sendToolBreakStatus(hand));
            world.spawnEntity(projectileEntity);
            world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, soundPitch);
        }
    }

    protected PersistentProjectileEntity createArrow(World world, LivingEntity entity, ItemStack crossbow, ItemStack sawBlade) {
        SawBladeItem sawBladeItem = (SawBladeItem)(sawBlade.getItem() instanceof SawBladeItem ? sawBlade.getItem() : Magnetism.SAW_BLADE);
        PersistentProjectileEntity persistentProjectileEntity = sawBladeItem.createArrow(world, sawBlade, entity);
        if (entity instanceof PlayerEntity) {
            persistentProjectileEntity.setCritical(true);
        }

        persistentProjectileEntity.setSound(SoundEvents.ITEM_CROSSBOW_HIT);
        persistentProjectileEntity.setShotFromCrossbow(true);
        int i = EnchantmentHelper.getLevel(Enchantments.PIERCING, crossbow);
        if (i > 0) {
            persistentProjectileEntity.setPierceLevel((byte)i);
        }

        return persistentProjectileEntity;
    }
}
