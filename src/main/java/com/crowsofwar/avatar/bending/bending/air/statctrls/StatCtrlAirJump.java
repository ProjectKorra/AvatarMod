/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.bending.bending.air.statctrls;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.air.AbilityAirJump;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.bending.bending.air.powermods.AirJumpPowerModifier;
import com.crowsofwar.avatar.bending.bending.air.tickhandlers.SmashGroundHandler;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityShockwave;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.*;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.util.data.TickHandlerController.AIR_PARTICLE_SPAWNER;
import static com.crowsofwar.avatar.util.data.TickHandlerController.SMASH_GROUND;
import static com.crowsofwar.avatar.util.helper.GliderHelper.getIsGliderDeployed;

/**
 * @author CrowsOfWar
 */
public class StatCtrlAirJump extends StatusControl {

    public StatCtrlAirJump() {
        super(0, AvatarControl.CONTROL_JUMP, CrosshairPosition.BELOW_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {

        Bender bender = ctx.getBender();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        World world = ctx.getWorld();
        AbilityData abilityData = data.getAbilityData("air_jump");
        AbilityAirJump jump = (AbilityAirJump) Abilities.get("air_jump");

        if (jump == null)
            return true;

        boolean allowDoubleJump = abilityData.getJumpNumber() < jump.getProperty(Ability.JUMPS, abilityData).intValue();

        // Figure out whether entity is on ground by finding collisions with
        // ground - if found a collision box, then is not on ground
        List<AxisAlignedBB> collideWithGround = world.getCollisionBoxes(entity, entity.getEntityBoundingBox().grow(0.25, 0.625, 0.25));
        boolean onGround = !collideWithGround.isEmpty() || entity.collidedVertically || world.getBlockState(entity.getPosition()).getBlock() == Blocks.WEB;

        if (onGround || entity instanceof EntityPlayer && getIsGliderDeployed((EntityPlayer) entity) ||
                allowDoubleJump) {

            int lvl = abilityData.getLevel();
            double multiplier = jump.getProperty(Ability.JUMP_HEIGHT, abilityData).floatValue() / 5;
            double powerModifier = jump.getProperty(Ability.POWERRATING, abilityData).doubleValue();
            double powerDuration = jump.getProperty(Ability.DURATION, abilityData).doubleValue();

            float fallAbsorption = jump.getProperty(Ability.FALL_ABSORPTION, abilityData).floatValue();
            float speed = jump.getProperty(Ability.KNOCKBACK, abilityData).floatValue() / 3;
            float size = jump.getProperty(Ability.EFFECT_RADIUS, abilityData).floatValue();
            float chiCost, chiOnHit, exhaustion, burnout;
            int cooldown;
            chiCost = jump.getChiCost(abilityData);
            exhaustion = jump.getExhaustion(abilityData);
            burnout = jump.getBurnOut(abilityData);
            cooldown = jump.getCooldown(abilityData);
            chiOnHit = jump.getProperty(Ability.CHI_HIT, abilityData).floatValue();

            chiOnHit *= abilityData.getDamageMult() * abilityData.getXpModifier();
            speed *= abilityData.getDamageMult() * abilityData.getXpModifier();
            size *= abilityData.getDamageMult() * abilityData.getXpModifier();

            data.getMiscData().setFallAbsorption(fallAbsorption);

            if (entity instanceof EntityBender || entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
                chiCost = 0;
            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
                exhaustion = burnout = cooldown = 0;

            if (bender.consumeChi(chiCost) && abilityData.getAbilityCooldown() == 0) {
                data.addTickHandler(AIR_PARTICLE_SPAWNER, ctx);
                if (jump.getBooleanProperty(Ability.GROUND_POUND, abilityData)) {
                    data.addTickHandler(SMASH_GROUND, ctx);
                }

                Vector velocity = Vector.getLookRectangular(entity);
                velocity = velocity.times(multiplier * 1.25);
                velocity = velocity.withY(Math.max(velocity.y(), 0.15));
                if (!world.isRemote) {
                    if (!onGround) {
                        velocity = velocity.times(0.875);
                        entity.motionX = velocity.x();
                        entity.motionY = velocity.y();
                        entity.motionZ = velocity.z();
                    } else
                        entity.addVelocity(velocity.x(), velocity.y(), velocity.z());
                }
                AvatarUtils.afterVelocityAdded(entity);

                entity.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.PLAYERS, 1, .7f);

                PowerRatingModifier powerRatingModifier = new AirJumpPowerModifier(powerModifier);
                powerRatingModifier.setTicks((int) powerDuration);
                //noinspection ConstantConditions
                data.getPowerRatingManager(Airbending.ID).addModifier(powerRatingModifier, ctx);


                EntityShockwave wave = new EntityShockwave(world);
                wave.setDamage(0);
                wave.setFireTime(0);
                wave.setRange(size);
                wave.setSpeed(speed);
                wave.setParticle(EnumParticleTypes.EXPLOSION_NORMAL);
                wave.setPerformanceAmount(10);
                wave.setKnockbackMult(new Vec3d(0.5, 0.2, 0.5));
                wave.setParticleAmount(2);
                wave.setDamageSource("avatar_Air_shockwave");
                wave.setAbility(jump);
                wave.setChiHit(chiOnHit);
                wave.setElement(new Airbending());
                wave.setParticleSpeed(speed / 60);
                wave.setPosition(entity.getPositionVector().add(0, 0.5, 0));
                wave.setOwner(entity);
                wave.setBehaviour(new SmashGroundHandler.AirGroundPoundShockwave());
                wave.setRenderNormal(false);
                wave.setKnockbackMult(new Vec3d(speed, speed * 2, speed));
                wave.setXp(jump.getProperty(Ability.XP_HIT, abilityData).floatValue());
                if (!world.isRemote)
                    world.spawnEntity(wave);

                abilityData.addXp(jump.getProperty(Ability.XP_USE, abilityData).floatValue());

                abilityData.setJumpNumber(abilityData.getJumpNumber() + 1);
                if (abilityData.getJumpNumber() == jump.getProperty(Ability.JUMPS, abilityData).intValue()) {
                    abilityData.addBurnout(burnout);
                    abilityData.setAbilityCooldown(cooldown);
                    if (entity instanceof EntityPlayer)
                        ((EntityPlayer) entity).addExhaustion(exhaustion);
                }
                if (abilityData.getJumpNumber() > jump.getProperty(Ability.JUMPS, abilityData).intValue())
                    abilityData.setJumpNumber(1);
                abilityData.setRegenBurnout(true);

                int numberOfParticles = (int) (size * 5);
                double particleSpeed = speed / 4;

                if (world.isRemote) {
                    for (int i = 0; i < numberOfParticles; i++)
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).collide(true).pos(AvatarEntityUtils.getBottomMiddleOfEntity(entity).add(0, 0.1, 0))
                                .clr(0.95F, 0.95F, 0.95F, 0.1F).time(14 + AvatarUtils.getRandomNumberInRange(0, 4)).vel(world.rand.nextGaussian() * particleSpeed / 10, world.rand.nextGaussian() * particleSpeed / 20,
                                world.rand.nextGaussian() * particleSpeed / 10).scale(1F + (float) particleSpeed).spawn(world);
                }
            }
            return true;

        }

        return false;

    }

}
