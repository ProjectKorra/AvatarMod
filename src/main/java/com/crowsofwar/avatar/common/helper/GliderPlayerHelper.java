package com.crowsofwar.avatar.common.helper;

import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.api.helper.GliderHelper;
import com.crowsofwar.avatar.api.item.IGlider;
import com.crowsofwar.avatar.common.wind.WindHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Arrays;

import static com.crowsofwar.avatar.common.config.ConfigGlider.GLIDER_CONFIG;
import static com.crowsofwar.avatar.common.helper.MathHelper.*;

public class GliderPlayerHelper {

    private static final float FALL_REDUCTION = 0.9F;

    /**
     * Updates the position of the player when gliding.
     * Glider is assumed to be deployed already.
     *
     * @param player - the player gliding
     */
    public static void updatePosition(EntityPlayer player){
        boolean isAirbender = BendingData.get(player).getActiveBending() instanceof Airbending;
        ItemStack glider = GliderHelper.getGlider(player);
        if(isValidGlider(glider)) {
            IGlider iGlider = (IGlider) glider.getItem();
            if (player.isServerWorld()) {
                if (shouldBeGliding(player)) {
                    applyHeatUplift(player, iGlider);
                    WindHelper.applyWind(player, glider);
                    if(isAirbender) {
//                            Vec3d vec3d = player.getLookVec();
//                            double d0 = 0.5D;
//                            double d1 = 0.1D;
//                            player.motionX += vec3d.x * d1 + (vec3d.x * d0 - player.motionX) * 0.5D;
//                            player.motionY += vec3d.y * d1 + (vec3d.y * d0 - player.motionY) * 0.5D;
//                            player.motionZ += vec3d.z * d1 + (vec3d.z * d0 - player.motionZ) * 0.5D;
//                            player.velocityChanged = true;
                        final float speed = (float) MathHelper.clampedLerp(iGlider.getMinSpeed(), iGlider.getMaxSpeed(), -player.moveForward);
                        final float elevationBoost = transform(
                                Math.abs(player.rotationPitch),
                                45.0F, 90.0F,
                                1.0F, 0.0F);
                        final float pitch = -toRadians(player.rotationPitch - iGlider.getPitchOffset() * elevationBoost);
                        final float yaw = -toRadians(player.rotationYaw) - (float)Math.PI;
                        final float vxz = -MathHelper.cos(pitch);
                        final float vy = MathHelper.sin(pitch);
                        final float vz = MathHelper.cos(yaw);
                        final float vx = MathHelper.sin(yaw);
                        player.motionX += vx * vxz * speed;
                        player.motionY += vy * speed + iGlider.getYBoost() * (player.rotationPitch > 0.0F ? elevationBoost : 1.0D);
                        player.motionZ += vz * vxz * speed;
                    } else {
                        if (player.motionY < 0.0D) {
                            Vec3d vec3d = player.getLookVec();
                            double d0 = 0.5D;
                            double d1 = 0.1D;
                            player.motionX += vec3d.x * d1 + (vec3d.x * d0 - player.motionX) * 0.5D;
                            player.motionZ += vec3d.z * d1 + (vec3d.z * d0 - player.motionZ) * 0.5D;
                            player.motionY *= FALL_REDUCTION;
                            player.velocityChanged = true;
                        }
                        player.fallDistance = 0.0F;
                    }
                }
                if (player.motionY < 0.0D) {
                    player.motionY *= iGlider.getFallReduction();
                }
                player.fallDistance = 0.0F;
            }
        } else {
            GliderHelper.setIsGliderDeployed(player, false);
        }
    }


    private static void applyHeatUplift(EntityPlayer player, IGlider glider) {

        BlockPos pos = player.getPosition();
        World worldIn = player.getEntityWorld();

        int maxSearchDown = 15;
        int maxSquared = (maxSearchDown-1) * (maxSearchDown-1);

        int i = 0;
        while (i <= maxSearchDown) {
            BlockPos scanpos = pos.down(i);
            Block scanned = worldIn.getBlockState(scanpos).getBlock();
            if (scanned.equals(Blocks.FIRE) || scanned.equals(Blocks.LAVA) || scanned.equals(Blocks.FLOWING_LAVA)) { //ToDo: configurable

//                get closeness to heat as quadratic (squared)
                double closeness = (maxSearchDown - i) * (maxSearchDown - i);

                //set amount up
                double configMovement = 17.2;
                double upUnnormalized = configMovement * closeness;

//                Logger.info("UN-NORMALIZED: "+upUnnormalized);

                //normalize
                double upNormalized = 1 + (upUnnormalized/(configMovement * maxSquared));

//                Logger.info("NORMALIZED: "+upNormalized);

                //scale amount to player's current motion
                double motion = player.motionY;
                double scaled = motion - (motion * (upNormalized * upNormalized));
//                Logger.info("SCALED: "+scaled);

                //apply final
//                Logger.info("BEFORE: "+player.motionY);
                player.motionY += scaled;
//                Logger.info("AFTER: "+player.motionY);


                double boostAmt = 1 + (0.5 * (maxSearchDown - i));
                double calculated = (player.motionY - (player.motionY * boostAmt));
                player.motionY += calculated;


                Vec3d vec3d = player.getLookVec();
                double d0 = 1.5D;
                double d1 = 0.1D;
                player.motionX += vec3d.x * d1 + (vec3d.x * d0 - player.motionX) * 0.2D;
                player.motionZ += vec3d.z * d1 + (vec3d.z * d0 - player.motionZ) * 0.2D;
                double up_boost;
                if (i > 0) {
                    up_boost = -0.07 * i + 0.6;
                } else {
                    up_boost = 0.07;
                }
                if (up_boost > 0) {
                    player.addVelocity(0, up_boost, 0);

                    if (GLIDER_CONFIG.airResistanceEnabled) {
                        player.motionX *= glider.getAirResistance();
                        player.motionZ *= glider.getAirResistance();
                    }
                }

                break;
            } else if (!scanned.equals(Blocks.AIR)) {
                break;
            }
            i++;
        }

    }

    //Currently sounds awful with crazy reverb, need to fix it somehow (probably custom ElytraSound without the check for isElytraFlying)
    private static void playWindSound(EntityPlayer player) {

        float volume;
        float pitch;

        float f = MathHelper.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ + player.motionY * player.motionY);
        float f1 = f / 2.0F;

        if ((double)f >= 0.01D) {
            volume = MathHelper.clamp(f1 * f1, 0.0F, 1.0F);
        }
        else {
            volume = 0.0F;
        }

        if (volume > 0.8F) {
            pitch = 1.0F + (volume - 0.8F);
        }
        else {
            pitch = 1.0F;
        }

        player.playSound(SoundEvents.ITEM_ELYTRA_FLYING, volume, pitch);

//        Minecraft.getMinecraft().getSoundHandler().playSound(new ElytraSound((EntityPlayerSP) player)); //doesn't work b/c hardcoded to isElytraFlying?
    }

    /**
     * Check if the player should be gliding.
     * Checks if the player is alive, and not on the ground or in water.
     *
     * @param player - the player to check
     * @return - true if the conditions are met, false otherwise
     */
    public static boolean shouldBeGliding(EntityPlayer player){
        if (player == null || player.isDead) return false;
        if (player.onGround || player.isInWater()) return false;
        return true;
    }

    /**
     * Check if the itemStack is an unbroken HangGlider.
     *
     * @param stack - the itemstack to check
     * @return - true if the item is an unbroken glider, false otherwise
     */
    private static boolean isValidGlider(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            if (stack.getItem() instanceof IGlider && (!((IGlider)(stack.getItem())).isBroken(stack))) { //glider, not broken
                return true;
            }
        }
        return false;
    }

    /**
     * Loop through player's inventory to get their hang glider.
     *
     * @param player - the player to search
     * @return - the first glider found (as an itemstack), null otherwise
     */
    public static ItemStack getGlider(EntityPlayer player) {
//        if (ConfigGlider.holdingGliderEnforced)
              return player.getHeldItemMainhand();
//        if (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() instanceof ItemHangglider) {
//            return player.getHeldItemOffhand();
//        }
//        for (ItemStack stack : player.inventory.mainInventory) {
//            if (stack != null) {
//                if (stack.getItem() instanceof ItemHangglider) {
//                    return stack;
//                }
//            }
//        }
//        return null;
    }

}
