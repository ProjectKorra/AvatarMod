package com.crowsofwar.avatar.bending.bending.earth.statctrls;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.earth.AbilityEarthRedirect;
import com.crowsofwar.avatar.bending.bending.earth.Earthbending;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFireRedirect;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.EntityShockwave;
import com.crowsofwar.avatar.entity.data.Behavior;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.entity.data.ShockwaveBehaviour;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.bending.bending.Ability.*;
import static com.crowsofwar.avatar.bending.bending.fire.AbilityFireRedirect.DESTROY_TIER;

public class StatCtrlEarthRedirectWave extends StatusControl {

    public StatCtrlEarthRedirectWave() {
        super(100, AvatarControl.CONTROL_RIGHT_CLICK_DOWN, CrosshairPosition.RIGHT_OF_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        //for now, just return true;
        return true; /*
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        BendingData data = ctx.getData();
        Bender bender = ctx.getBender();
        AbilityData abilityData = data.getAbilityData(new AbilityEarthRedirect());
        AbilityEarthRedirect redirect = (AbilityEarthRedirect) Abilities.get("earth_redirect");

        if (abilityData.getAbilityCooldown(entity) > 0) return true;

        if (redirect != null) {
            int cooldown = redirect.getCooldown(abilityData);
            float exhaustion = redirect.getExhaustion(abilityData);
            float burnout = redirect.getBurnOut(abilityData);
            float chiCost = redirect.getChiCost(abilityData);
            float chiHit = redirect.getProperty(CHI_HIT, abilityData).floatValue();
            float xp = redirect.getProperty(Ability.XP_USE, abilityData).floatValue();
            float radius = redirect.getProperty(Ability.RADIUS, abilityData).floatValue();
            float damage = redirect.getProperty(DAMAGE, abilityData).floatValue();
            float speed = redirect.getProperty(SPEED, abilityData).floatValue();
            int destroyTier = redirect.getProperty(DESTROY_TIER, abilityData).intValue();



            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
                exhaustion = burnout = chiCost = cooldown = 0;
            else if (entity instanceof EntityBender)
                chiCost = 0;

            //nice
            if (bender.consumeChi(chiCost)) {
                EntityShockwave shockwave = new EntityShockwave(world);
                //too many setters pls help
                shockwave.setOwner(entity);
                shockwave.setTier(destroyTier);
                shockwave.setSpeed(speed);
                shockwave.setPush(speed / 4);
                shockwave.setRange(radius);
                shockwave.setXp(xp);
                shockwave.setDamage(damage);
                shockwave.setSphere(false);
                //TODO: Add lang file entry
                shockwave.setDamageSource("avatar_Earth_redirectShockwave");
                shockwave.setChiHit(chiHit);
                shockwave.setRenderNormal(false);
                shockwave.setAbility(redirect);
                shockwave.setElement(new Earthbending());
                shockwave.setBehaviour(new EarthRedirectShockwave());
                if (!world.isRemote)
                    world.spawnEntity(shockwave);

                //Inhibitors
                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).addExhaustion(exhaustion);
                abilityData.addBurnout(burnout);
                abilityData.setAbilityCooldown(cooldown);
            }

        }
        return true;
    **/}


    public static class EarthRedirectShockwave extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            if (entity instanceof EntityShockwave) {

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
