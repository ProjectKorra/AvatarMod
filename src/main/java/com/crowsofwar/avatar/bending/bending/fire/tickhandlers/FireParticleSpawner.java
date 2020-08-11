package com.crowsofwar.avatar.bending.bending.fire.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFireJump;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.client.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.client.particle.ParticleSpawner;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.EntityShockwave;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.bending.bending.Ability.*;

public class FireParticleSpawner extends TickHandler {
    private static final ParticleSpawner particles = new NetworkParticleSpawner();

    public FireParticleSpawner(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase target = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityData data = ctx.getData().getAbilityData(new AbilityFireJump());
        AbilityFireJump jump = (AbilityFireJump) Abilities.get(new AbilityFireJump().getName());
        Vector pos = Vector.getEntityPos(target).minusY(0.05);

        if (world.isRemote)
            for (int i = 0; i < 2 + AvatarUtils.getRandomNumberInRange(0, 4); i++) {
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(255, 10,
                        5).pos(pos.toMinecraft()).vel(world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40).scale(1F +
                        Math.max(data.getLevel(), 0) / 2F).time(6 + AvatarUtils.getRandomNumberInRange(0, 6)).element(new Firebending()).collide(true)
                        .ability(jump).spawnEntity(target).spawn(world);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(255, 40 + AvatarUtils.getRandomNumberInRange(0, 60),
                        10).pos(pos.toMinecraft()).vel(world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40)
                        .scale(1F + Math.max(data.getLevel(), 0) / 2F).time(6 + AvatarUtils.getRandomNumberInRange(0, 6)).element(new Firebending()).collide(true)
                        .ability(jump).spawnEntity(target).spawn(world);
            }

        //particles.spawnParticles(world, world.rand.nextBoolean() ? AvatarParticles.getParticleFlames() : AvatarParticles.getParticleFire(),
        //		4, 16, pos, new Vector(0.7, 0.2, 0.7), true);

        return target.isInWater() || target.onGround || bender.isFlying();

    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);

        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        AbilityData abilityData = ctx.getData().getAbilityData("fire_jump");
        AbilityFireJump jump = (AbilityFireJump) Abilities.get("fire_jump");

        if (jump != null && jump.getBooleanProperty(STOP_SHOCKWAVE)) {
            float speed = jump.getProperty(SPEED, abilityData).floatValue() / 10;
            float size = jump.getProperty(SIZE, abilityData).floatValue() * 2;
            int lifetime = (int) (speed / size * 10);
            float knockback = jump.getProperty(KNOCKBACK, abilityData).floatValue();
            float damage = jump.getProperty(DAMAGE, abilityData).floatValue();
            int fireTime = jump.getProperty(FIRE_TIME, abilityData).intValue();
            int performance = jump.getProperty(PERFORMANCE, abilityData).intValue() / 10;
            float chiHit = jump.getProperty(CHI_HIT, abilityData).floatValue() / 4;
            int r, g, b, fadeR, fadeG, fadeB;

            r = jump.getProperty(FIRE_R, abilityData).intValue();
            g = jump.getProperty(FIRE_G, abilityData).intValue();
            b = jump.getProperty(FIRE_B, abilityData).intValue();
            fadeR = jump.getProperty(FADE_R, abilityData).intValue();
            fadeG = jump.getProperty(FADE_G, abilityData).intValue();
            fadeB = jump.getProperty(FADE_B, abilityData).intValue();

            speed *= abilityData.getDamageMult() * abilityData.getXpModifier();
            size *= abilityData.getDamageMult() * abilityData.getXpModifier();
            lifetime *= abilityData.getDamageMult() * abilityData.getXpModifier();
            knockback *= abilityData.getDamageMult() * abilityData.getXpModifier();
            damage *= abilityData.getDamageMult() * abilityData.getXpModifier();
            fireTime *= abilityData.getDamageMult() * abilityData.getXpModifier();
            performance *= abilityData.getDamageMult() * abilityData.getXpModifier();
            chiHit *= abilityData.getDamageMult() * abilityData.getXpModifier();

            EntityShockwave wave = new EntityShockwave(world);
            wave.setOwner(entity);
            wave.setDamageSource("avatar_Fire_shockwave");
            wave.setPosition(AvatarEntityUtils.getBottomMiddleOfEntity(entity));
            wave.setFireTime(fireTime);
            wave.setEntitySize(size / 2);
            wave.setElement(new Firebending());
            wave.setAbility(new AbilityFireJump());
            wave.setDamage(damage);
            wave.setOwner(entity);
            wave.setSphere(false);
            wave.setSpeed(speed);
            wave.setRange(size);
            wave.setLifeTime(lifetime);
            wave.setChiHit(chiHit);
            wave.setPerformanceAmount(performance);
            wave.setPush(knockback);
            wave.setBehaviour(new FireJumpShockwave());
            wave.setParticleWaves(3);
            wave.setParticleSpeed(speed);
            wave.setParticleAmount(4);
            wave.setRGB(r, g, b);
            wave.setFade(fadeR, fadeG, fadeB);
            if (!world.isRemote)
                world.spawnEntity(wave);
        }
    }

    public static class FireJumpShockwave extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            if (entity instanceof EntityShockwave && entity.world.isRemote) {
                World world = entity.world;
                if (entity.getOwner() != null) {
                    EntityLivingBase owner = entity.getOwner();

                    if (entity.ticksExisted <= ((EntityShockwave) entity).getParticleWaves()) {
                        int[] fade = entity.getFade();
                        int[] rgb = entity.getRGB();
                        for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / (((EntityShockwave) entity).getRange() *
                                ((EntityShockwave) entity).getParticleAmount()) * entity.ticksExisted) {
                            int rRandom = fade[0] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[0] * 2) : AvatarUtils.getRandomNumberInRange(fade[0] / 2,
                                    fade[0] * 2);
                            int gRandom = fade[1] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[1] * 2) : AvatarUtils.getRandomNumberInRange(fade[1] / 2,
                                    fade[1] * 2);
                            int bRandom = fade[2] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[2] * 2) : AvatarUtils.getRandomNumberInRange(fade[2] / 2,
                                    fade[2] * 2);

                            //Even though the maths is technically wrong, you use sin if you want a shockwave, and cos if you want a sphere (for x).
                            double x2 = entity.posX + (entity.ticksExisted * ((EntityShockwave) entity).getSpeed()) * Math.cos(angle);
                            double y2 = entity.posY;
                            double z2 = entity.posZ + (entity.ticksExisted * ((EntityShockwave) entity).getSpeed()) * Math.sin(angle);
                            Vector speed = new Vector((entity.ticksExisted * ((EntityShockwave) entity).getSpeed()) * Math.cos(angle) *
                                    (entity.getParticleSpeed() * 10), entity.getParticleSpeed() / 2, (entity.ticksExisted *
                                    ((EntityShockwave) entity).getSpeed()) * Math.sin(angle) * (entity.getParticleSpeed() * 10));

                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Firebending()).vel(speed.toMinecraft())
                                    .spawnEntity(owner).collide(world.rand.nextBoolean()).clr(rgb[0], rgb[1], rgb[2], 180 + AvatarUtils.getRandomNumberInRange(0, 40)).
                                    fade(rRandom, gRandom, bRandom, 100 + AvatarUtils.getRandomNumberInRange(0, 40)).pos(x2, y2, z2).
                                    scale(entity.getAvgSize() / 4).spawn(world);
                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Firebending()).vel(speed.toMinecraft())
                                    .spawnEntity(owner).collide(world.rand.nextBoolean()).clr(250, 80, 20, 180 + AvatarUtils.getRandomNumberInRange(0, 40)).
                                    fade(rRandom, gRandom, bRandom, 100 + AvatarUtils.getRandomNumberInRange(0, 40)).pos(x2, y2, z2).
                                    scale(entity.getAvgSize() / 4).spawn(world);
                        }
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

