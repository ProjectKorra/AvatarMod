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
import com.crowsofwar.avatar.util.AvatarEntityUtils;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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

            List<AxisAlignedBB> positions = world.getCollisionBoxes(entity, entity.getEntityBoundingBox().grow(radius, radius / 2, radius));

            if (!positions.isEmpty() && redirect.getBooleanProperty(ABSORB_FIRE)) {
                for (AxisAlignedBB blockBox : positions) {
                    BlockPos pos = new BlockPos(AvatarEntityUtils.getMiddleOfAABB(blockBox));
                    if (world.getBlockState(pos).getBlock() instanceof BlockFire) {
                        FireRedirectPowerModifier powerMod = new FireRedirectPowerModifier();
                        powerMod.setTicks(100);
                        powerMod.setPowerRating(5);
                        Objects.requireNonNull(data.getPowerRatingManager(Firebending.ID)).addModifier(powerMod, ctx);
                        if (world.isRemote) {
                            for (int i = 0; i < 3; i++) {
                                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(AvatarEntityUtils.getMiddleOfAABB(blockBox))
                                        .target(Vector.getEyePos(entity).toMinecraft()).scale(0.25F + world.rand.nextFloat() / 2)
                                        .clr(235 + AvatarUtils.getRandomNumberInRange(0, 20), 10 + AvatarUtils.getRandomNumberInRange(0, 20),
                                                5 + AvatarUtils.getRandomNumberInRange(0, 10), 170 + AvatarUtils.getRandomNumberInRange(0, 40))
                                        .spawn(world);
                                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(AvatarEntityUtils.getMiddleOfAABB(blockBox))
                                        .target(Vector.getEyePos(entity).toMinecraft()).scale(0.25F + world.rand.nextFloat() / 2)
                                        .clr(235 + AvatarUtils.getRandomNumberInRange(0, 20), 60 + AvatarUtils.getRandomNumberInRange(10, 40),
                                                25 + AvatarUtils.getRandomNumberInRange(0, 20), 215 + AvatarUtils.getRandomNumberInRange(0, 40))
                                        .spawn(world);
                            }
                        }
                        applyInhibitors = true;
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
}
