package com.crowsofwar.avatar.bending.bending.earth.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.earth.AbilityRestore;
import com.crowsofwar.avatar.bending.bending.earth.Earthbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.Chi;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.config.ConfigChi.CHI_CONFIG;

public class RestoreParticleHandler extends TickHandler {

    public RestoreParticleHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        AbilityData aD = data.getAbilityData("restore");
        World world = ctx.getWorld();

        AbilityRestore restore = (AbilityRestore) Abilities.get("restore");

        if (restore != null) {
            int duration = data.getTickHandlerDuration(this);
            int restoreDuration = (int) (restore.getProperty(Ability.DURATION, aD).intValue() * aD.getXpModifier() * aD.getDamageMult());

            //Chi handling
            float chiBoost = restore.getProperty(Ability.CHI_BOOST, aD).floatValue();
            float chiRegenBoost = restore.getProperty(Ability.CHI_REGEN_BOOST, aD).floatValue();

            chiBoost *= aD.getDamageMult() * aD.getXpModifier();
            chiRegenBoost *= aD.getDamageMult() * aD.getXpModifier();

            if (!world.isRemote) {
                Chi chi = data.chi();
                if (chi != null) {
                    if (chi.getAvailableChi() < chiBoost + CHI_CONFIG.maxAvailableChi)
                        if (!(entity instanceof EntityBender || entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()))
                            chi.changeAvailableChi(chiRegenBoost / 20);
                }
            } else {
                for (int i = 0; i < chiBoost * 2 + AvatarUtils.getRandomNumberInRange(0, 2); i++) {
                    Vec3d random = new Vec3d(world.rand.nextGaussian() * 0.75, 0, world.rand.nextGaussian() * 0.75);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Earthbending())
                            .clr(90, 252, 120, 20).pos(Vector.getEntityPos(entity).toMinecraft().add(random))
                            .vel(world.rand.nextGaussian() / 20, world.rand.nextDouble() / 5, world.rand.nextGaussian() / 20)
                            .time(restoreDuration / 5).spawn(world);
                }
            }
            return duration >= restoreDuration;
        }
        return true;
    }
}
