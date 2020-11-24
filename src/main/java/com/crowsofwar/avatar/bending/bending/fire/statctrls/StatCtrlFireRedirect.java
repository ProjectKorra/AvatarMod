package com.crowsofwar.avatar.bending.bending.fire.statctrls;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFireRedirect;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.bending.bending.fire.powermods.FireRedirectPowerModifier;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.BlockFire;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.bending.bending.fire.AbilityFireRedirect.*;

public class StatCtrlFireRedirect extends StatusControl {

    public StatCtrlFireRedirect() {
        super(100, AvatarControl.CONTROL_SHIFT, CrosshairPosition.BELOW_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        BendingData data = ctx.getData();
        Bender bender = ctx.getBender();
        AbilityData abilityData = data.getAbilityData(new AbilityFireRedirect());
        AbilityFireRedirect redirect = (AbilityFireRedirect) Abilities.get("fire_redirect");

        if (abilityData.getAbilityCooldown(entity) > 0)
            return true;

        if (redirect != null) {
            int cooldown = redirect.getCooldown(abilityData);
            float exhaustion = redirect.getExhaustion(abilityData);
            float burnout = redirect.getBurnOut(abilityData);
            float chiCost = redirect.getChiCost(abilityData);
            float xp = redirect.getProperty(Ability.XP_USE, abilityData).floatValue();
            float radius = redirect.getProperty(Ability.RADIUS, abilityData).floatValue();
            int redirectTier = redirect.getProperty(REDIRECT_TIER, abilityData).intValue();
            boolean applyInhibitors = false;

            xp *= abilityData.getDamageMult() * abilityData.getXpModifier();

            if (entity instanceof EntityBender || entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
                chiCost = exhaustion = burnout = cooldown = 0;

            List<EntityOffensive> redirectables = world.getEntitiesWithinAABB(EntityOffensive.class, entity.getEntityBoundingBox().grow(radius));
            redirectables = redirectables.stream().filter(entityOffensive -> entityOffensive.canCollideWith(entity) && entityOffensive.isRedirectable()
                    && entityOffensive.getElement() instanceof Firebending).collect(Collectors.toList());


            if (!redirectables.isEmpty()) {
                for (EntityOffensive e : redirectables) {
                    if (e.getTier() <= redirectTier) {
                        if (bender.consumeChi(chiCost)) {
                            e.setOwner(entity);
                            if (redirect.getBooleanProperty(ABSORB_FIRE) &&
                                    e.getTier() <= redirect.getProperty(ABSORB_TIER).intValue()) {
                                e.setBehaviour(new AbsorbBehaviour());
                            } else e.setBehaviour(new OffensiveBehaviour.Redirect());
                            abilityData.addXp(xp);
                            applyInhibitors = true;
                        }
                    }
                }
            }

            if (redirect.getBooleanProperty(ABSORB_FIRE, abilityData)) {
                for (int x = 0; x <= radius; x++) {
                    for (int z = 0; z <= radius; z++) {
                        for (int y = 0; y <= radius; y++) {
                            BlockPos pos = new BlockPos(entity.posX + x, entity.posY + y, entity.posZ + z);
                            applyInhibitors = handleAbsorption(pos, world, redirect, abilityData, ctx, data, entity);
                        }
                    }
                }
                for (int x = 0; x >= -radius; x--) {
                    for (int z = 0; z >= -radius; z--) {
                        for (int y = 0; y >= -radius; y--) {
                            BlockPos pos = new BlockPos(entity.posX + x, entity.posY + y, entity.posZ + z);
                            applyInhibitors = handleAbsorption(pos, world, redirect, abilityData, ctx, data, entity);
                        }
                    }
                }
            }


            if (applyInhibitors) {
                abilityData.setAbilityCooldown(cooldown);
                abilityData.addBurnout(burnout);
                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).addExhaustion(exhaustion);
            }

        }
        return true;
    }


    public boolean handleAbsorption(BlockPos pos, World world, AbilityFireRedirect redirect, AbilityData abilityData,
                                    BendingContext ctx, BendingData data, EntityLivingBase entity) {
        if (world.getBlockState(pos).getBlock() instanceof BlockFire
                || world.getBlockState(pos).getBlock() == Blocks.FIRE) {
            if (world.isRemote) {
                for (int h = 0; h < 12; h++) {
                    Vec3d spawnPos = new Vec3d(pos.getX(), pos.getY() + 0.5, pos.getZ());
                    Vec3d endPos = Vector.getEyePos(entity).toMinecraft();
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(pos.getX() + world.rand.nextGaussian() / 5,
                            pos.getY() + 0.5 + world.rand.nextGaussian() / 5, pos.getZ() + world.rand.nextGaussian() / 5)
                            .vel(endPos.subtract(spawnPos).scale(0.15).add(world.rand.nextGaussian() / 240,
                                    world.rand.nextGaussian() / 240, world.rand.nextGaussian() / 240)).scale(0.5F + world.rand.nextFloat() / 10)
                            .clr(235 + AvatarUtils.getRandomNumberInRange(0, 20), 10 + AvatarUtils.getRandomNumberInRange(0, 20),
                                    5 + AvatarUtils.getRandomNumberInRange(0, 10), 170 + AvatarUtils.getRandomNumberInRange(0, 40))
                            .time(4 + AvatarUtils.getRandomNumberInRange(0, 4)).glow(true).spawn(world);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(pos.getX() + world.rand.nextGaussian() / 5,
                            pos.getY() + 0.5 + world.rand.nextGaussian() / 5, pos.getZ() + world.rand.nextGaussian() / 5)
                            .vel(endPos.subtract(spawnPos).scale(0.15).add(world.rand.nextGaussian() / 240,
                                    world.rand.nextGaussian() / 240, world.rand.nextGaussian() / 240)).scale(0.5F + world.rand.nextFloat() / 10)
                            .clr(235 + AvatarUtils.getRandomNumberInRange(0, 20), 80 + AvatarUtils.getRandomNumberInRange(10, 40),
                                    25 + AvatarUtils.getRandomNumberInRange(0, 20), 215 + AvatarUtils.getRandomNumberInRange(0, 40))
                            .time(4 + AvatarUtils.getRandomNumberInRange(0, 4)).glow(true).spawn(world);
                }
            } else {
                world.setBlockToAir(pos);

                FireRedirectPowerModifier powerMod = new FireRedirectPowerModifier();
                powerMod.setTicks(redirect.getProperty(POWER_DURATION, abilityData).intValue());
                powerMod.setPowerRating(redirect.getProperty(POWER_BOOST, abilityData).intValue());
                Objects.requireNonNull(data.getPowerRatingManager(Firebending.ID)).addModifier(powerMod, ctx);


            }
            return true;
        }
        return false;

    }
}
