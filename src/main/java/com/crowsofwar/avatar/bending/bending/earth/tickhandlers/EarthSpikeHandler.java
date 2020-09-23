package com.crowsofwar.avatar.bending.bending.earth.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.earth.AbilityEarthRedirect;
import com.crowsofwar.avatar.bending.bending.earth.AbilityEarthspikes;
import com.crowsofwar.avatar.entity.EntityEarthspike;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EarthSpikeHandler extends TickHandler {

    public EarthSpikeHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        return false;
    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityEarthspikes ability = (AbilityEarthspikes) Abilities.get("earth_spikes");
        AbilityData abilityData = AbilityData.get(entity, "earth_spikes");

        if (ability != null && abilityData != null) {
            EntityEarthspike earthspike = new EntityEarthspike(world);
            if (!world.isRemote)
                world.spawnEntity(earthspike);
        }
    }
}
