package com.crowsofwar.avatar.bending.bending.ice;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityIceClaws;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandlerController;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AbilityFrostClaws extends Ability {

    //Exists for how long the particles are around your fists
    public static final String FADE_DURATION = "fadeDuration";

    public AbilityFrostClaws() {
        super(Icebending.ID, "frost_claws");
    }

    @Override
    public void execute(AbilityContext ctx) {

        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        AbilityData abilityData = ctx.getAbilityData();
        BendingData data = ctx.getData();

        EnumClawDirection direction;
        EnumHand hand;
        //Main hand is even for combo, off hand is odd
        if (abilityData.getUseNumber() % 2 == 0) {
            data.removeTickHandler(TickHandlerController.FROST_CLAW_MAIN_HAND_HANDLER, ctx);
            data.addTickHandler(TickHandlerController.FROST_CLAW_MAIN_HAND_HANDLER, ctx);
            hand = EnumHand.MAIN_HAND;
        } else {
            data.removeTickHandler(TickHandlerController.FROST_CLAW_OFF_HAND_HANDLER, ctx);
            data.addTickHandler(TickHandlerController.FROST_CLAW_OFF_HAND_HANDLER, ctx);
            hand = EnumHand.OFF_HAND;
        }

        if (world.isRemote)
            entity.swingArm(hand);

        float speed = getProperty(SPEED, ctx).floatValue();
        float size = getProperty(SIZE, ctx).floatValue();
        float damage = getProperty(DAMAGE, ctx).floatValue();
        float knockback = getProperty(KNOCKBACK, ctx).floatValue();
        int lifetime = getProperty(LIFETIME, ctx).intValue();
        int performance = getProperty(PERFORMANCE, ctx).intValue();

        //Controls direction of the claws.
        //Just going to do up and down for now
        switch (abilityData.getUseNumber()) {
//            case 0:
//                direction = EnumClawDirection.TR_BL;
//                break;
//            case 1:
//                direction = EnumClawDirection.L_R;
//                break;
//            case 2:
//                direction = EnumClawDirection.B_T;
//                break;
//            case 3:
//                direction = EnumClawDirection.T_B;
//                break;
            default:
                if (entity.getPrimaryHand() == EnumHandSide.RIGHT &&
                        hand == EnumHand.MAIN_HAND || entity.getPrimaryHand() == EnumHandSide.LEFT
                        && hand == EnumHand.MAIN_HAND)
                    direction = EnumClawDirection.T_B;
                else direction = EnumClawDirection.B_T;
                break;
        }

        for (int i = 0; i < 5; i++) {
            //Creates 5 big claws
            Vec3d right = Vector.toRectangular(Math.toRadians(entity.rotationYaw + 90), 0).toMinecraft();
            right = right.scale(-1 + i * 0.5);
            right = right.add(Vector.getEntityPos(entity).toMinecraft());
            right = right.add(0, entity.getEyeHeight() / 2, 0);
            EntityIceClaws claws = new EntityIceClaws(world);
            //Will be used later
            //Handles positioning and velocity
            orient(right, claws, speed, direction, entity, size, 0.25F);
            claws.setTier(getCurrentTier(ctx));
            claws.setElement(Icebending.ID);
            claws.setOwner(entity);
            claws.setDamage(damage);
            claws.setPiercing(true);
            claws.setEntitySize(size * 2.5F, size);
            claws.setAbility(this);
            claws.setExpandedHeight(size * 2);
            claws.setExpandedWidth(size * 2);
            claws.setLifeTime(lifetime);
            claws.setPerformanceAmount(performance);
            claws.setPush(knockback);
            claws.setBehaviour(new IceClawBehaviour());
            claws.rotationYaw = entity.rotationYaw;
            claws.rotationPitch = entity.rotationPitch;
            if (!world.isRemote) {
                world.spawnEntity(claws);
            }
        }

        //This goes at the end; spawning goes above.
        //Currently the max combo is 2
        if (abilityData.getUseNumber() >=
                getProperty(MAX_COMBO, abilityData).intValue()) {
            //Resets the combo
            abilityData.setUseNumber(0);
        } else abilityData.incrementUseNumber();
        super.execute(ctx);
    }

    @Override
    public void init() {
        super.init();
        addProperties(MAX_COMBO, FADE_DURATION);
    }

    @Override
    public boolean isOffensive() {
        return true;
    }

    @Override
    public boolean isProjectile() {
        return true;
    }

    //Correctly positions the entity and sets its orientation
    public void orient(Vec3d startPos, EntityIceClaws claws, float speed, EnumClawDirection direction,
                       EntityLivingBase entity, float size, float range) {
        //Position and destination of the ice claws
        Vec3d pos = startPos, dest = startPos, look = entity.getLookVec().scale(range);
        Vec3d spd;
        //Only need to cover top bottom and bottom top for now
        if (direction == EnumClawDirection.T_B) {
            pos = pos.add(look).add(0, size * 2, 0);
            dest = dest.add(look).add(0, -size * 2, 0);
        } else if (direction == EnumClawDirection.B_T) {
            pos = pos.add(look).add(0, -size * 2, 0);
            dest = dest.add(look).add(0, size * 2, 0);
        }

        spd = dest.subtract(pos).scale(speed / 20).add(look.scale(4));
        claws.setPosition(pos);
        claws.setVelocity(spd);
    }

    //Not all of these will be used but I'll leave them in just in case.
    //Translation:

    /**
     * T = Top
     * B = Bottom
     * R = Right
     * L = Left.
     * <p>
     * Handle in arcs of 90 degrees.
     * The underscore separates start point and destination.
     * Should probably be a handler method for this so I can just plug it in and have fun.
     */
    public enum EnumClawDirection {
        TR_BL,
        L_R,
        B_T,
        T_B,
        R_L,
        TL_BR,
        BL_TR,
        BR_TL
    }

    public static class IceClawBehaviour extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            World world = entity.world;
            //This only works for vertical for now. TODO: Adjust this.
            entity.motionX *= 0.5;
            entity.motionY *= 0.5;
            entity.motionZ *= 0.5;
            if (entity.velocity().sqrMagnitude() <= 0.5)
                entity.Dissipate();

            if (world.isRemote) {
                if (entity.ticksExisted % 2 == 0 || entity.ticksExisted <= 2) {
                    for (double i = -90; i <= 90; i += 20) {
                        Vec3d pos = AvatarEntityUtils.getMiddleOfEntity(entity);
                        Vec3d newDir = entity.getLookVec().scale(entity.getHeight() / 1.75 * Math.cos(Math.toRadians(i)));
                        pos = pos.add(newDir);
                        pos = new Vec3d(pos.x, pos.y + (entity.getHeight() / 1.75 * Math.sin(Math.toRadians(i))), pos.z);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(pos).vel(world.rand.nextGaussian() / 30, world.rand.nextGaussian() / 30,
                                world.rand.nextGaussian() / 30).collide(true).time(12 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(140, 230, 255, 20)
                                .scale(entity.getWidth() * 1.5F).element(BendingStyles.get(entity.getElement())).spawnEntity(entity).glow(
                                AvatarUtils.getRandomNumberInRange(1, 100) > 40).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(pos).vel(entity.motionX, entity.motionY, entity.motionZ).collide(true)
                                .time(7 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(130, 255, 255, 60)
                                .scale(entity.getWidth() * 2F).spawnEntity(entity).element(BendingStyles.get(entity.getElement())).spawn(world);
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
