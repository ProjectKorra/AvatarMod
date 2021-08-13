package com.crowsofwar.avatar.bending.bending.ice.tickhandlers;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.ice.AbilityFrostClaws;
import com.crowsofwar.avatar.bending.bending.ice.Icebending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.PlayerViewRegistry;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FrostClawHandler extends TickHandler {

    //So it spawns particles at main and off hand respectively
    private final EnumHand hand;

    public FrostClawHandler(int id, EnumHand hand) {
        super(id);
        this.hand = hand;
    }

    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        AbilityFrostClaws claws = (AbilityFrostClaws) Abilities.get("frost_claws");

        if (claws != null) {
            AbilityData abilityData = ctx.getData().getAbilityData(claws);
            //This logic is simply used for determining when the tick handler dies
            int duration = data.getTickHandlerDuration(this);
            int maxDuration = claws.getProperty(AbilityFrostClaws.FADE_DURATION, abilityData).intValue();
            World world = ctx.getWorld();
            //Only used for spawning particles

            //Use number is set after a swipe. For example, after punching with your main hand,
            //it's set to 1, even though to execute main hand attacks it has to be 0.
            boolean mainHand = hand == EnumHand.MAIN_HAND;
            Vec3d height, rightSide;
            if (entity instanceof EntityPlayer) {
                if (!AvatarMod.realFirstPersonRender2Compat && !AvatarMod.shoulderSurfingCompat && (PlayerViewRegistry.getPlayerViewMode(entity.getUniqueID()) >= 2 || PlayerViewRegistry.getPlayerViewMode(entity.getUniqueID()) <= -1)) {
                    height = entity.getPositionVector().add(0, 1.5, 0);
                    height = height.add(entity.getLookVec().scale(0.8));
                    //Right
                    if (entity.getPrimaryHand() == EnumHandSide.RIGHT && mainHand
                            || entity.getPrimaryHand() == EnumHandSide.LEFT && !mainHand) {
                        rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw + 90), 0).times(0.5).withY(0).toMinecraft();
                    }
                    //Left
                    else {
                        rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw - 90), 0).times(0.5).withY(0).toMinecraft();
                    }
                } else {
                    height = entity.getPositionVector().add(0, 0.84, 0);
                    if (entity.getPrimaryHand() == EnumHandSide.RIGHT && mainHand
                            || entity.getPrimaryHand() == EnumHandSide.LEFT && !mainHand) {
                        rightSide = Vector.toRectangular(Math.toRadians(entity.renderYawOffset + 90), 0).times(0.385).withY(0).toMinecraft();
                    } else {
                        rightSide = Vector.toRectangular(Math.toRadians(entity.renderYawOffset - 90), 0).times(0.385).withY(0).toMinecraft();
                    }
                }
            } else {
                height = entity.getPositionVector().add(0, 0.84, 0);
                if (entity.getPrimaryHand() == EnumHandSide.RIGHT && mainHand
                        || entity.getPrimaryHand() == EnumHandSide.LEFT && !mainHand) {
                    rightSide = Vector.toRectangular(Math.toRadians(entity.renderYawOffset + 90), 0).times(0.385).withY(0).toMinecraft();
                } else {
                    rightSide = Vector.toRectangular(Math.toRadians(entity.renderYawOffset - 90), 0).times(0.385).withY(0).toMinecraft();
                }

            }
            rightSide = rightSide.add(height);
            if (world.isRemote) {
                for (int i = 0; i < 10; i++) {
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(rightSide).time(6 + AvatarUtils.getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
                            world.rand.nextGaussian() / 40).clr(160, 235, 255, 30).collide(true).
                            scale(1 / 2F).element(BendingStyles.get(Icebending.ID)).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 50)
                            .spawnEntity(entity).spawn(world);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(rightSide).time(6 + AvatarUtils.getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() / 40, world.rand.nextDouble() / 40,
                            world.rand.nextGaussian() / 40).clr(140, 255, 255, 60).collide(true).
                            scale(1 / 2F).element(BendingStyles.get(Icebending.ID)).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 75)
                            .spawnEntity(entity).spawn(world);
                }
            }

            return duration >= maxDuration;
        }
        return true;
    }
}
