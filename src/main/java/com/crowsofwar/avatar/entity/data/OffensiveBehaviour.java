package com.crowsofwar.avatar.entity.data;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.bending.bending.air.AbilityAirGust;
import com.crowsofwar.avatar.bending.bending.air.AbilityAirblade;
import com.crowsofwar.avatar.bending.bending.air.tickhandlers.AirBurstHandler;
import com.crowsofwar.avatar.bending.bending.air.tickhandlers.ShootAirBurstHandler;
import com.crowsofwar.avatar.bending.bending.air.tickhandlers.SmashGroundHandler;
import com.crowsofwar.avatar.bending.bending.custom.dark.tickhandlers.DeathDescentHandler;
import com.crowsofwar.avatar.bending.bending.custom.demonic.AbilityChaosLash;
import com.crowsofwar.avatar.bending.bending.custom.demonic.tickhandlers.DemonWingsHandler;
import com.crowsofwar.avatar.bending.bending.custom.ki.tickhandlers.KiFlightHandler;
import com.crowsofwar.avatar.bending.bending.custom.light.tickhandlers.HeavenlyFlightHandler;
import com.crowsofwar.avatar.bending.bending.earth.AbilityRavine;
import com.crowsofwar.avatar.bending.bending.earth.tickhandlers.EarthSpikeHandler;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFireRedirect;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFireShot;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFlamethrower;
import com.crowsofwar.avatar.bending.bending.fire.statctrls.StatCtrlFlameStrike;
import com.crowsofwar.avatar.bending.bending.fire.tickhandlers.FlameGlideHandler;
import com.crowsofwar.avatar.bending.bending.fire.tickhandlers.FlamethrowerUpdateTick;
import com.crowsofwar.avatar.bending.bending.ice.AbilityFrostClaws;
import com.crowsofwar.avatar.bending.bending.ice.tickhandlers.GlacialGlideHandler;
import com.crowsofwar.avatar.bending.bending.ice.tickhandlers.IceLanceHandler;
import com.crowsofwar.avatar.bending.bending.ice.tickhandlers.IceRazeHandler;
import com.crowsofwar.avatar.entity.EntityFireball;
import com.crowsofwar.avatar.entity.EntityFlames;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.util.PlayerViewRegistry;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.Vec3d;

import static com.crowsofwar.avatar.util.data.StatusControlController.THROW_FIREBALL;

public abstract class OffensiveBehaviour extends Behavior<EntityOffensive> {
    public static final DataSerializer<OffensiveBehaviour> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();


    public static void register() {
        DataSerializers.registerSerializer(DATA_SERIALIZER);
        registerBehavior(Idle.class);
        registerBehavior(AirBurstHandler.AirburstShockwave.class);
        registerBehavior(AbilityFireShot.FireShockwaveBehaviour.class);
        registerBehavior(SmashGroundHandler.AirGroundPoundShockwave.class);
        registerBehavior(ShootAirBurstHandler.AirBurstBeamBehaviour.class);
        registerBehavior(AbilityAirGust.AirGustBehaviour.class);
        registerBehavior(AbilityAirblade.AirBladeBehaviour.class);
        registerBehavior(FlamethrowerUpdateTick.FlamethrowerBehaviour.class);
        registerBehavior(StatCtrlFlameStrike.FlameStrikeBehaviour.class);
        registerBehavior(FlameGlideHandler.FireJumpShockwave.class);
        registerBehavior(AbilityRavine.RavineBehaviour.class);
        registerBehavior(EarthSpikeHandler.EarthspikeRavineBehaviour.class);
        FireballBehavior.register();
        CloudburstBehavior.register();
        registerBehavior(Redirect.class);
        registerBehavior(AbilityFireRedirect.AbsorbBehaviour.class);
        registerBehavior(DemonWingsHandler.DemonWingsShockwave.class);
        registerBehavior(HeavenlyFlightHandler.HeavenlyFlightShockwave.class);
        registerBehavior(KiFlightHandler.KiFlightShockwave.class);
        registerBehavior(DeathDescentHandler.DeathDescentShockwave.class);
        registerBehavior(GlacialGlideHandler.GlacialGlideShockwave.class);
        registerBehavior(IceRazeHandler.FlamethrowerBehaviour.class);
        registerBehavior(IceLanceHandler.IceLanceBehaviour.class);
        registerBehavior(AbilityFrostClaws.IceClawBehaviour.class);
        registerBehavior(AbilityChaosLash.WhipBehaviour.class);
    }

    public static class Idle extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            return this;
        }

        @Override
        public void renderUpdate(EntityOffensive entity) {

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

    public static class Redirect extends FireballBehavior.PlayerControlled {

        int ticks = 0;
        boolean rotateRight = true;

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            if (entity.getOwner() != null) {
                EntityLivingBase owner = entity.getOwner();
                Vec3d height, rightSide, leftSide;
                if (owner instanceof EntityPlayer) {
                    if (!AvatarMod.realFirstPersonRender2Compat && (PlayerViewRegistry.getPlayerViewMode(owner.getUniqueID()) >= 2 || PlayerViewRegistry.getPlayerViewMode(owner.getUniqueID()) <= -1)) {
                        height = owner.getPositionVector().add(0, 1.5, 0);
                        height = height.add(owner.getLookVec().scale(0.8));

                        rightSide = Vector.toRectangular(Math.toRadians(owner.rotationYaw + 90), 0).times(0.5).withY(0).toMinecraft();
                        leftSide = Vector.toRectangular(Math.toRadians(owner.rotationYaw - 90), 0).times(0.5).withY(0).toMinecraft();

                    } else {
                        height = owner.getPositionVector().add(0, 0.84, 0);

                        rightSide = Vector.toRectangular(Math.toRadians(owner.renderYawOffset + 90), 0).times(0.385).withY(0).toMinecraft();
                        leftSide = Vector.toRectangular(Math.toRadians(owner.renderYawOffset - 90), 0).times(0.385).withY(0).toMinecraft();

                    }
                } else {
                    height = owner.getPositionVector().add(0, 0.84, 0);

                    rightSide = Vector.toRectangular(Math.toRadians(owner.renderYawOffset + 90), 0).times(0.385).withY(0).toMinecraft();
                    leftSide = Vector.toRectangular(Math.toRadians(owner.renderYawOffset - 90), 0).times(0.385).withY(0).toMinecraft();
                }
                rightSide = rightSide.add(height);
                leftSide = leftSide.add(height);

                if (entity.getDistance(leftSide.x, leftSide.y, leftSide.z) >= entity.getDistance(rightSide.x, rightSide.y, rightSide.z)
                        && ticks < 1)
                    rotateRight = true;
                else if (ticks < 1)
                    rotateRight = false;


                if (rotateRight) {
                    int angle = (owner.ticksExisted * 10) % 360;
                    double radians = Math.toRadians(angle);
                    double x = 1.75 * Math.cos(radians);
                    double y = height.y;
                    double z = Math.sin(radians);
                    entity.setVelocity(new Vec3d(x + owner.posX, y,
                            z + entity.posZ).subtract(entity.getPositionVector()).scale(0.25));
                } else {
                    int angle = (owner.ticksExisted * -10) % 360;
                    double radians = Math.toRadians(angle);
                    double x = Math.cos(radians);
                    double y = height.y;
                    double z = Math.sin(radians);
                    entity.setVelocity(new Vec3d(x + owner.posX, y,
                            z + entity.posZ).subtract(entity.getPositionVector()).scale(0.25));

                }
                if (entity instanceof EntityFireball) {
                    BendingData data = BendingData.getFromEntity(entity.getOwner());
                    if (data != null)
                        data.addStatusControl(THROW_FIREBALL);
                }
                //280 degrees
                if (ticks >= 28)
                    if (entity instanceof EntityFlames && entity.getAbility() instanceof AbilityFlamethrower) {
                        entity.addVelocity(entity.getLookVec().scale(2));
                        entity.setBehaviour(new FlamethrowerUpdateTick.FlamethrowerBehaviour());
                    }
            }
            if (ticks >= 60)
                entity.setDead();

            ticks++;
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
