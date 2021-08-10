package com.crowsofwar.avatar.bending.bending.fire.statctrls;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFireRedirect;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.bending.bending.fire.AbilityFireRedirect.DESTROY_TIER;

public class StatCtrlFireSplit extends StatusControl {

    public StatCtrlFireSplit() {
        //Automatically disables itself while status controls with similar controls are in use.
        super(100, AvatarControl.CONTROL_RIGHT_CLICK, CrosshairPosition.RIGHT_OF_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        BendingData data = ctx.getData();
        Bender bender = ctx.getBender();
        AbilityData abilityData = data.getAbilityData(new AbilityFireRedirect());
        AbilityFireRedirect redirect = (AbilityFireRedirect) Abilities.get("fire_redirect");

        if (redirect != null) {
            int cooldown = redirect.getCooldown(abilityData);
            float exhaustion = redirect.getExhaustion(abilityData);
            float burnout = redirect.getBurnOut(abilityData);
            float chiCost = redirect.getChiCost(abilityData);
            float xp = redirect.getProperty(Ability.XP_USE, abilityData).floatValue();
            float aimAssist = redirect.getProperty(Ability.AIM_ASSIST, abilityData).floatValue();
            float range = redirect.getProperty(Ability.RANGE, abilityData).floatValue();
            int destroyTier = redirect.getProperty(DESTROY_TIER, abilityData).intValue();

            xp *= abilityData.getDamageMult() * abilityData.getXpModifier();

            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
                chiCost = exhaustion = burnout = cooldown = 0;
            if (entity instanceof EntityBender)
                chiCost = 0;

            List<Entity> destructables = Raytrace.entityRaytrace(world, Vector.getEyePos(entity), Vector.getLookRectangular(entity),
                     range, aimAssist, entity1 -> entity1 instanceof EntityOffensive && ((EntityOffensive) entity1).canCollideWith(entity));

            if (!destructables.isEmpty()) {
                for (Entity e : destructables) {
                    if (e instanceof EntityOffensive) {
                        if (((EntityOffensive) e).getTier() <= destroyTier) {
                            if (world.isRemote) {
                                for (int i = 0; i < 12; i++) {
                                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).vel(entity.getLookVec().scale(range / 7.5)
                                    .add(world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20))
                                            .time((int) range * 4 + AvatarUtils.getRandomNumberInRange(0, 4)).scale(0.25F + range / 10)
                                            .pos(Vector.getEyePos(entity).toMinecraft()).clr(235 + AvatarUtils.getRandomNumberInRange(0, 20),
                                            60 + AvatarUtils.getRandomNumberInRange(10, 40), 25 + AvatarUtils.getRandomNumberInRange(0, 10),
                                            170 + AvatarUtils.getRandomNumberInRange(0, 20)).collide(true).collideParticles(true).element(BendingStyles.get(Firebending.ID)).spawn(world);
                                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).vel(entity.getLookVec().scale(range / 7.5)
                                            .add(world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20))
                                            .time((int) range * 4 + AvatarUtils.getRandomNumberInRange(0, 4)).scale(0.25F + range / 10)
                                            .pos(Vector.getEyePos(entity).toMinecraft()).clr(255,
                                            20 + AvatarUtils.getRandomNumberInRange(0, 20), 5 + AvatarUtils.getRandomNumberInRange(0, 10),
                                            120 + AvatarUtils.getRandomNumberInRange(0, 20)).collide(true).collideParticles(true).element(BendingStyles.get(Firebending.ID)).spawn(world);
                                }
                            }
                            if (bender.consumeChi(chiCost)) {
                                ((EntityOffensive) e).Dissipate();
                                abilityData.setAbilityCooldown(cooldown);
                                abilityData.addBurnout(burnout);
                                if (entity instanceof EntityPlayer)
                                    ((EntityPlayer) entity).addExhaustion(exhaustion);
                                abilityData.addXp(xp);
                                entity.swingArm(EnumHand.MAIN_HAND);
                            }
                        }
                    }
                }

            }

        }
        return true;
    }
}
