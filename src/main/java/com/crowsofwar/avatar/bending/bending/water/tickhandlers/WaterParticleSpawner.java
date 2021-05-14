package com.crowsofwar.avatar.bending.bending.water.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.water.AbilityWaterBlast;
import com.crowsofwar.avatar.bending.bending.water.Waterbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.util.data.TickHandlerController.WATER_CHARGE;

public class WaterParticleSpawner extends TickHandler {

    public WaterParticleSpawner(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        World world = ctx.getWorld();
        AbilityData abilityData = AbilityData.get(entity, "water_blast");
        AbilityWaterBlast blast = (AbilityWaterBlast) Abilities.get("water_blast");

        if (blast != null) {
            int maxDuration = blast.getProperty(Ability.CHARGE_TIME, abilityData).intValue();//int) -blast.powerModify(blast.getProperty(Ability.CHARGE_TIME, abilityData).floatValue(), abilityData);
            int duration = data.getTickHandlerDuration(this);
            double radius = ((float) maxDuration - duration) / 10F;
            double maxRadius = maxDuration / 10F;

            if (data.hasTickHandler(WATER_CHARGE) && radius >= 0) {
                if (world.isRemote) {
                    //TODO: Swirl animation when charging
                    //TODO: Also make it 3d at level 3
//                    for (int j = 1; j < 4; j++) {
//                        for (int i = 0; i < 90; i++) {
//                            //Creates le swirl
//                            double rScale = (radius / 90) * (90 - i);
//                            float pSize = (float) (1F / (radius / 2F));
//                            Vector lookpos = Vector.toRectangular(Math.toRadians(entity.rotationYaw + j * i * 4), 0).times(rScale).withY(entity.getEyeHeight() / 2);
//                            ParticleBuilder.create(ParticleBuilder.Type.CUBE).element(new Waterbending())
//                                    .pos(lookpos.toMinecraft().add(AvatarEntityUtils.getBottomMiddleOfEntity(entity)))
//                                    .ability(blast).spawnEntity(entity).clr(0, 102, 255, 45).
//                                    scale(Math.min(pSize, 1.5F))
//                                    .time(8 + AvatarUtils.getRandomNumberInRange(0, 4))
//                                    .element(new Waterbending()).vel(world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 30,
//                                    world.rand.nextGaussian() / 45).spin(rScale / 4, 0.00125).spawn(world);
//                        }
//                    }
                    //NOTE: Remember to add the look vec for burst/spheres in front!
                    Vec3d pos = AvatarEntityUtils.getBottomMiddleOfEntity(entity).add(0, entity.getEyeHeight() / 2, 0);

                    //Size starts small gets big
                    float size = Math.min((float) (1F / (radius / 2F)), 1.5F);
                    //In case you forgot year 7 maths, radius * 2 * pi = circumference
                    int particles = (int) (maxRadius * Math.PI);
                    int rings = blast.getBooleanProperty(AbilityWaterBlast.SHIELD, abilityData) ? 6 : 1;
                    //Rings around the player (not around your finger; the police want you)
                    // C u l t u r e
                    ParticleBuilder.create(ParticleBuilder.Type.CUBE).spawnEntity(entity).clr(0, 102, 255, 145).scale(size)
                            .time(16).collideParticles(true).element(new Waterbending()).swirl(rings, particles, (float) radius, size / 2F, (float) maxRadius * 20,
                            (1 / size), entity, world, true, pos,
                            ParticleBuilder.SwirlMotionType.IN, true, true);
                } else {
                    double hitRadius = maxRadius / 2 + radius / 2;
                    AxisAlignedBB box = new AxisAlignedBB(entity.posX + hitRadius, entity.posY + entity.getEyeHeight() / 2 + hitRadius / 2, entity.posZ + hitRadius,
                            entity.posX - hitRadius, entity.posY + entity.getEyeHeight() / 2 - radius / 2, entity.posZ - hitRadius);

                    if (blast.getBooleanProperty(AbilityWaterBlast.SHIELD, abilityData)) {
                        List<Entity> bolts = world.getEntitiesWithinAABB(Entity.class, box);
                        if (!bolts.isEmpty()) {
                            for (Entity e : bolts) {
                                if (e instanceof EntityArrow) {
                                    Vector vel = Vector.getVelocity(e).times(-1);
                                    e.addVelocity(vel.x(), 0, vel.z());
                                }
                                if (e instanceof AvatarEntity && ((AvatarEntity) e).isProjectile()) {
                                    ((AvatarEntity) e).onMajorWaterContact();
                                    ((AvatarEntity) e).setVelocity(((AvatarEntity) e).velocity().times(0.5F));
                                }
                            }
                        }
                        List<EntityThrowable> projectiles = world.getEntitiesWithinAABB(EntityThrowable.class, box);
                        if (!projectiles.isEmpty()) {
                            for (Entity e : projectiles) {
                                Vector vel = Vector.getVelocity(e).times(-1);
                                e.addVelocity(vel.x(), 0, vel.z());
                            }
                        }
                    }

                }
                return false;

            }
        }
        return true;

    }
}
