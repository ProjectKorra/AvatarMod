package com.crowsofwar.avatar.common.bending.combustion;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AbilityExplosion extends Ability {

    public AbilityExplosion() {
        super(Combustionbending.ID, "explosion");

    }

    @Override
    public void execute(AbilityContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();

        if (bender.consumeChi(STATS_CONFIG.chiCloudburst)) {
            Raytrace.Result hit = Raytrace.getTargetBlock(entity, 20);
            if (hit.hitSomething()) {
               Vector hitAt = hit.getPosPrecise();
                float explosionSize = STATS_CONFIG.explosionSettings.explosionSize;
                explosionSize += ctx.getPowerRating() * 2.0 / 100;

               Explosion explosion = new Explosion(world, entity, hitAt.x(), hitAt.y(), hitAt.z(), explosionSize,
                       !world.isRemote, STATS_CONFIG.explosionSettings.damageBlocks);
                if (!ForgeEventFactory.onExplosionStart(world, explosion)) {

                     explosion.doExplosionA();
                     explosion.doExplosionB(true);

                }
            }
        }
    }
}
