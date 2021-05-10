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
                    for (int i = 0; i < rings; i++) {
                        //Drawing from the player to the edge of the radius
                        for (double j = 0; j < (radius); j += size / 2F) {
                            //Particles
                            for (int h = 0; h < particles; h++) {
                                //Flow animation
                                double yaw = Math.toRadians(i * (180F / rings));
                                //For some reason, -90 breaks multiple rings (they stack instead of spreading). However,
                                //in order to make a horizontal ring, you need -90.s
                                double pitch = Math.toRadians(i > 0 ? 0 : -90);
                                Vec3d circlePos = Vector.getOrthogonalVector(Vector.toRectangular(yaw, pitch),
                                        //The ternary operator with the radius ensures a shield effect rather than a simple implode effect;
                                        //spherical layers stay out/rings stay out rather than imploding with the horizontal axis
                                        (entity.ticksExisted % 360) * (maxRadius * 20) + h * (360F / particles), i > 0 ? radius : j)
                                        .times(i > 0 ? j : 1).toMinecraft().add(pos);
                                Vec3d targetPos = Vector.getOrthogonalVector(Vector.toRectangular(yaw, pitch),
                                        ((entity.ticksExisted + 1) % 360) * (maxRadius * 20) + h * (360F / particles), i > 0 ? radius : j)
                                        .times(i > 0 ? j : 1).toMinecraft().add(pos);
                                Vec3d vel = new Vec3d(world.rand.nextGaussian() / 240, world.rand.nextGaussian() / 240, world.rand.nextGaussian() / 240);
                                vel = targetPos.subtract(circlePos).normalize().scale(0.10 * (1 / size)).add(vel);
                                ParticleBuilder.create(ParticleBuilder.Type.CUBE).pos(circlePos).spawnEntity(entity).vel(vel)
                                        .clr(0, 102, 255, 145).scale(size)
                                        .time(16).collideParticles(true).element(new Waterbending()).spawn(world);
                            }
                        }
                    }
                } else {
                    AxisAlignedBB box = new AxisAlignedBB(entity.posX + radius, entity.posY + entity.getEyeHeight() / 2 + radius / 4, entity.posZ + radius,
                            entity.posX - radius, entity.posY + entity.getEyeHeight() / 2 - radius / 4, entity.posZ - radius);
                    List<EntityThrowable> projectiles = world.getEntitiesWithinAABB(EntityThrowable.class, box);
                    if (!projectiles.isEmpty()) {
                        for (Entity e : projectiles) {
                            Vector vel = Vector.getVelocity(e).times(-1);
                            e.addVelocity(vel.x(), 0, vel.z());
                        }
                    }
                    if (abilityData.getLevel() >= 2) {
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
                    }
                }
                return false;

            }
        }
        return true;

    }
}
