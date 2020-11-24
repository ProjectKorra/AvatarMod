package com.crowsofwar.avatar.util.data;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;

/**
 * Contains all wall jump related logic for a Bender. These methods would normally be in the
 * Bender class itself, but there is a lot of logic so they have been moved here. Wall jump
 * decisions can be accessed like <code>bender.getWallJumpManager.canWallJump()</code>.
 * <p>
 * TODO: Nuke this sad excuse for actual working code
 */
public class WallJumpManager {

    private final Bender bender;

    public WallJumpManager(Bender bender) {
        this.bender = bender;
    }

    public void doWallJump(ResourceLocation particleType) {

        World world = bender.getWorld();
        EntityLivingBase entity = bender.getEntity();

        Vector normal = getHorizontalCollisionNormal();
        Block block = getHorizontalCollisionBlock();

        if (normal != Vector.UP && normal != null) {

            Vector velocity = new Vector(entity.motionX, entity.motionY, entity.motionZ);
            Vector n = velocity.reflect(normal).times(4).minus(normal.times(0.5)).withY(0.5);
            n = n.plus(Vector.getLookRectangular(entity).times(-1.25).withY(1));

            if (n.sqrMagnitude() > 1) {
                n = n.normalize().times(1);
            }

            // can't use setVelocity since that is Client SideOnly
            entity.motionX *= 0;
            entity.motionY *= 0;
            entity.motionZ *= 0;
            entity.motionX += n.x();
            entity.motionY += n.y();
            entity.motionZ += n.z();
            AvatarUtils.afterVelocityAdded(entity);

            world.playSound(null, new BlockPos(entity), block.getSoundType().getBreakSound(),
                    SoundCategory.PLAYERS, 1, 0.6f);

            bender.getData().getMiscData().addFallAbsorption(3);
            if (particleType == ParticleBuilder.Type.FLASH && STATS_CONFIG.allowMultiAirbendingWalljump) {
                bender.getData().getMiscData().setWallJumping(true);
            }

        }

    }

    /**
     * Returns whether the bender can physically wall jump regardless of their bending ability -
     * whether they are at a wall etc.
     */
    public boolean canWallJump() {

        EntityLivingBase entity = bender.getEntity();

        // Detect whether the player is horizontally collided (i.e. touching a wall)
        // Calculation different between client/server b/c client has isCollidedVertically
        // properly setup, while server doesn't and needs trickier calculation

        boolean collidedWithWall;
        if (bender.getWorld().isRemote) {
            collidedWithWall = entity.collidedHorizontally && !entity.collidedVertically;
        } else {
            collidedWithWall = getHorizontalCollisionBlock() != null;
        }

        MiscData md = bender.getData().getMiscData();

        return collidedWithWall && !md.isWallJumping() && md.getTimeInAir() >= STATS_CONFIG
                .wallJumpDelay;

    }

    /**
     * Returns whether the bender has the necessary skills/abilities to actually wall jump.
     * Different from {@link #canWallJump()} since that is whether the bender physically is in a
     * situation where they could theoretically wall jump if they knew how.
     */
    public boolean knowsWallJump() {
        return getWallJumpParticleType() != null;
    }

    /**
     * @return If the bender knows how to wall jump, gives the type of particles spawned when they
     * wall jump. If the bender does not know how to wall jump, returns null.
     * @see #knowsWallJump()
     */
    @Nullable
    public ResourceLocation getWallJumpParticleType() {

        // Airbender?
        if (bender.getData().hasBending(new Airbending())) {
            return ParticleBuilder.Type.FLASH;
        }

        return null;

    }

    @Nullable
    private Vector getHorizontalCollisionNormal() {
        EntityLivingBase entity = bender.getEntity();
        BlockPos pos = new BlockPos(entity);
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {

            BlockPos adjusted = pos.offset(facing);
            if (!bender.getWorld().isAirBlock(adjusted)) {
                return new Vector(facing.getDirectionVec());
            }

        }

        return null;
    }

    @Nullable
    private Block getHorizontalCollisionBlock() {
        EntityLivingBase entity = bender.getEntity();
        BlockPos pos = new BlockPos(entity);
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {

            BlockPos adjusted = pos.offset(facing);
            if (!bender.getWorld().isAirBlock(adjusted)) {
                return bender.getWorld().getBlockState(adjusted).getBlock();
            }

        }

        return null;
    }

}
