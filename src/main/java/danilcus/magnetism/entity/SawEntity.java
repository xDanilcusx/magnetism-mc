package danilcus.magnetism.entity;

import com.google.common.collect.Lists;
import danilcus.magnetism.Magnetism;
import danilcus.magnetism.item.SawBladeItem;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class SawEntity extends PersistentProjectileEntity {
    @Nullable
    private BlockState inBlockState;
    private int life;
    private double damage = 8.0;
    private int punch;

    public SawEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public SawEntity(World world, LivingEntity owner) {
        super(Magnetism.SAW, owner, world);
    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        float f = (float)this.getVelocity().length();
        int i = (int) this.damage;

        Entity entity2 = this.getOwner();
        DamageSource damageSource;
        if (entity2 == null) {
            damageSource = DamageSource.arrow(this, this);
        } else {
            damageSource = DamageSource.arrow(this, entity2);
            if (entity2 instanceof LivingEntity) {
                ((LivingEntity)entity2).onAttacking(entity);
            }
        }

        boolean bl = entity.getType() == EntityType.ENDERMAN;
        int j = entity.getFireTicks();
        if (this.isOnFire() && !bl) {
            entity.setOnFireFor(5);
        }

        if (entity.damage(damageSource, (float)i)) {
            if (bl) {
                return;
            }

            if (entity instanceof LivingEntity livingEntity) {
                if (this.punch > 0) {
                    double d = Math.max(0.0, 1.0 - livingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                    Vec3d vec3d = this.getVelocity().multiply(1.0, 0.0, 1.0).normalize().multiply((double)this.punch * 0.6 * d);
                    if (vec3d.lengthSquared() > 0.0) {
                        livingEntity.addVelocity(vec3d.x, 0.1, vec3d.z);
                    }
                }

                if (!this.world.isClient && entity2 instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(livingEntity, entity2);
                    EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity);
                }

                this.onHit(livingEntity);
                if (livingEntity != entity2 && livingEntity instanceof PlayerEntity && entity2 instanceof ServerPlayerEntity && !this.isSilent()) {
                    ((ServerPlayerEntity)entity2)
                            .networkHandler
                            .sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
                }

                if (!this.world.isClient && entity2 instanceof ServerPlayerEntity serverPlayerEntity) {
                    if (!entity.isAlive() && this.isShotFromCrossbow()) {
                        Criteria.KILLED_BY_CROSSBOW.trigger(serverPlayerEntity, Arrays.asList(entity));
                    }
                }
            }

            this.playSound(this.getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        } else {
            entity.setFireTicks(j);
            this.setVelocity(this.getVelocity().multiply(-0.1));
            this.setYaw(this.getYaw() + 180.0F);
            this.prevYaw += 180.0F;
            if (!this.world.isClient && this.getVelocity().lengthSquared() < 1.0E-7) {
                if (this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
                    this.dropStack(this.asItemStack(), 0.1F);
                }

                this.discard();
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        this.inBlockState = this.world.getBlockState(blockHitResult.getBlockPos());
        inBlockState.onProjectileHit(this.world, inBlockState, blockHitResult, this);
        Direction dir = blockHitResult.getSide();
        if(!this.isShotFromCrossbow() || (dir != Direction.UP && dir != Direction.DOWN) || this.getVelocity().horizontalLength() == 0) {
            Vec3d vec3d = blockHitResult.getPos().subtract(this.getX(), this.getY(), this.getZ());
            this.setVelocity(vec3d);
            Vec3d vec3d2 = vec3d.normalize().multiply(0.05F);
            this.setPos(this.getX() - vec3d2.x, this.getY() - vec3d2.y, this.getZ() - vec3d2.z);
            this.playSound(this.getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            this.inGround = true;
            this.shake = 7;
            this.setCritical(false);
            this.setSound(SoundEvents.ITEM_TRIDENT_HIT_GROUND);
            this.setShotFromCrossbow(false);
        } else {
            Vec3d vec3d = this.getVelocity();
            this.setVelocity(vec3d.multiply(0.8d));
            Vec3d vec3d2 = this.getVelocity();
            double bounce = Math.abs(vec3d2.x + vec3d2.z) * 1000d * 0.1d;
            this.setVelocity(vec3d2.x, bounce / 1000d, vec3d2.z);
            this.playSound(SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        }
    }

    protected SoundEvent getHitSound() {
        return SoundEvents.BLOCK_ANVIL_LAND;
    }

    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(Magnetism.SAW_BLADE);
    }
}
