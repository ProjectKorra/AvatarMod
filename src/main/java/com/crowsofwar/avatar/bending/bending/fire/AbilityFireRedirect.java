package com.crowsofwar.avatar.bending.bending.fire;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.fire.powermods.FireRedirectPowerModifier;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class AbilityFireRedirect extends Ability {

    public AbilityFireRedirect() {
        super(Firebending.ID, "fire_redirect");
    }

    @Override
    public void init() {
        super.init();
        addProperties(DESTROY_TIER, REDIRECT_TIER, ABSORB_TIER, RADIUS, AIM_ASSIST, RANGE, POWER_BOOST, POWER_DURATION);
        addBooleanProperties(ABSORB_FIRE);
    }

    @Override
    public int getBaseTier() {
        return 3;
    }

    @Override
    public boolean isVisibleInRadial() {
        return false;
    }

    @Override
    public boolean isUtility() {
        return true;
    }

    @Override
    public int getCooldown(AbilityContext ctx) {
        return 0;
    }

    @Override
    public float getBurnOut(AbilityContext ctx) {
        return 0;
    }

    @Override
    public float getExhaustion(AbilityContext ctx) {
        return 0;
    }

    public static class AbsorbBehaviour extends OffensiveBehaviour {

        //TODO: Affect DragonBreath: AreaEffectCloud
        //TODO: Affect DragonFireball
        //TODO: Affect Fireball

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            if (entity.getOwner() != null) {
                Vector pos = entity.position();
                Vector targetPos = Vector.getEntityPos(entity.getOwner());
                entity.setVelocity(targetPos.minus(pos).times(0.025));

                if (entity.getDistance(entity.getOwner()) < 0.5) {
                    entity.Dissipate();

                    BendingData data = BendingData.getFromEntity(entity.getOwner());
                    if (data != null) {
                        BendingContext ctx = new BendingContext(data, entity.getOwner(), new Raytrace.Result());
                        FireRedirectPowerModifier powerMod = new FireRedirectPowerModifier();
                        powerMod.setTicks(50 * entity.getTier());
                        powerMod.setPowerRating(5 * entity.getTier());
                        data.getPowerRatingManager(Firebending.ID).addModifier(powerMod, ctx);
                    }
                }
            }
            return this;
        }

        @Override
        public void fromBytes(PacketBuffer buf) {

        }

        @Override
        public void toBytes(PacketBuffer buf) {

        }

        @Override
        public void load(NBTTagCompound nbt) {

        }

        @Override
        public void save(NBTTagCompound nbt) {

        }
    }
}
