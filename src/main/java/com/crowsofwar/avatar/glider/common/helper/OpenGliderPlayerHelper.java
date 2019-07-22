package com.crowsofwar.avatar.glider.common.helper;

import com.crowsofwar.avatar.common.bending.BendingStyles;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.glider.api.helper.GliderHelper;
import com.crowsofwar.avatar.glider.api.item.IGlider;
import com.crowsofwar.avatar.glider.common.config.ConfigHandler;
import com.crowsofwar.avatar.glider.common.network.PacketHandler;
import com.crowsofwar.avatar.glider.common.network.PacketUpdateGliderDamage;
import com.crowsofwar.avatar.glider.common.wind.WindHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class OpenGliderPlayerHelper {

    /**
     * Updates the position of the player when gliding.
     * Glider is assumed to be deployed already.
     *
     * @param player - the player gliding
     */
    public static void updatePosition(EntityPlayer player){
        boolean isAirbender = BendingData.get(player).getAllBending().contains(BendingStyles.get("airbending"));
        if (shouldBeGliding(player)) {
            ItemStack glider = GliderHelper.getGlider(player);
            if (isValidGlider(glider)) {
                if (player.motionY < 0) { //if falling (flying)

                    // Init variables
                    final double horizontalSpeed;
                    final double verticalSpeed;
                    IGlider iGlider = (IGlider) glider.getItem();

                    // Get speed depending on glider and if player is sneaking
                    if (!player.isSneaking()) {
                        horizontalSpeed = iGlider.getHorizontalFlightSpeed();
                        verticalSpeed = iGlider.getVerticalFlightSpeed();
                    } else if(isAirbender && Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown()){
                        horizontalSpeed = iGlider.getSpaceHorizontalFlightSpeed();
                        verticalSpeed = iGlider.getSpaceVerticalFlightSpeed();
                    } else {
                        horizontalSpeed = iGlider.getShiftHorizontalFlightSpeed();
                        verticalSpeed = iGlider.getShiftVerticalFlightSpeed();
                    }

                    // Apply wind effects
                    WindHelper.applyWind(player, glider);

                    // Apply heat uplift
                    if (ConfigHandler.heatUpdraftEnabled) {
                        applyHeatUplift(player, iGlider);
                    }

                    // Apply falling motion
                    player.motionY *= verticalSpeed;

                    // Apply forward motion
                    double x = Math.cos(Math.toRadians(player.rotationYaw + 90)) * horizontalSpeed;
                    double z = Math.sin(Math.toRadians(player.rotationYaw + 90)) * horizontalSpeed;
                    player.motionX += x;
                    player.motionZ += z; //ToDo: Wrong, need multiplication to slow down

                    // Apply air resistance
                    if (ConfigHandler.airResistanceEnabled) {
                        player.motionX *= iGlider.getAirResistance();
                        player.motionZ *= iGlider.getAirResistance();
                    }

                    // Stop fall damage
                    player.fallDistance = 0.0F;

//                    playWindSound(player); //ToDo: sounds
                }

                //no wild arm swinging while flying
                if (player.world.isRemote) {
                    player.limbSwing = 0;
                    player.limbSwingAmount = 0;
                }

                //damage the hang glider
                if (ConfigHandler.durabilityEnabled) { //durability should be taken away
                    if (!player.world.isRemote) { //server
                        if (player.world.rand.nextInt(ConfigHandler.durabilityTimeframe) == 0) { //damage about once per x ticks
                            PacketHandler.HANDLER.sendTo(new PacketUpdateGliderDamage(), (EntityPlayerMP) player); //send to client
                            glider.damageItem(ConfigHandler.durabilityPerUse, player);
                            if (((IGlider)(glider.getItem())).isBroken(glider)) { //broken item
                                GliderHelper.setIsGliderDeployed(player, false);
                            }
                        }
                    }
                }

                //SetPositionAndUpdate on server only

            } else { //Invalid item (likely changed selected item slot, update)
                GliderHelper.setIsGliderDeployed(player, false);
            }
        }

    }

    private static void applyHeatUplift(EntityPlayer player, IGlider glider) {

        BlockPos pos = player.getPosition();
        World worldIn = player.getEntityWorld();

        int maxSearchDown = 5;
        int maxSquared = (maxSearchDown-1) * (maxSearchDown-1);

        int i = 0;
        while (i <= maxSearchDown) {
            BlockPos scanpos = pos.down(i);
            Block scanned = worldIn.getBlockState(scanpos).getBlock();
            if (scanned.equals(Blocks.FIRE) || scanned.equals(Blocks.LAVA) || scanned.equals(Blocks.FLOWING_LAVA)) { //ToDo: configurable

//                get closeness to heat as quadratic (squared)
                double closeness = (maxSearchDown - i) * (maxSearchDown - i);

                //set amount up
                double configMovement = 12.2;
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


//                double boostAmt = 1 + (0.5 * (maxSearchDown - i));
//                double calculated = (player.motionY - (player.motionY * boostAmt));
//                Logger.info(calculated);
//                Logger.info("BEFORE: "+player.motionY);
//                player.motionY += calculated;
//                Logger.info("AFTER: "+player.motionY);


//                Vec3d vec3d = player.getLookVec();
//                double d0 = 1.5D;
//                double d1 = 0.1D;
////                player.motionX += vec3d.x * d1 + (vec3d.x * d0 - player.motionX) * 0.2D;
////                player.motionZ += vec3d.z * d1 + (vec3d.z * d0 - player.motionZ) * 0.2D;
//                double up_boost;
//                if (i > 0) {
//                    up_boost = -0.07 * i + 0.6;
//                } else {
//                    up_boost = 0.07;
//                }
//                if (up_boost > 0) {
//                    player.addVelocity(0, up_boost, 0);
//
//                    if (ConfigHandler.airResistanceEnabled) {
//                        player.motionX *= glider.getAirResistance();
//                        player.motionZ *= glider.getAirResistance();
//                    }
//                }

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
            if (stack.getItem() instanceof IGlider && (!((IGlider)(stack.getItem())).isBroken(stack))) { //hang glider, not broken
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
//        if (ConfigHandler.holdingGliderEnforced)
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
