/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.bending.bending.custom.demonic.statctrls;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.custom.demonic.AbilityHellBastion;
import com.crowsofwar.avatar.bending.bending.custom.demonic.Demonbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityInfernalBall;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.bending.bending.custom.demonic.tickhandlers.HellBastionHandler.HELL_BASTION_MOVEMENT_MOD_ID;
import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_RIGHT_CLICK_DOWN;
import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_RIGHT_CLICK_UP;
import static com.crowsofwar.avatar.util.data.StatusControl.CrosshairPosition.RIGHT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_HELL_BASTION;
import static com.crowsofwar.avatar.util.data.TickHandlerController.HELL_BASTION_HANDLER;

/**
 * @author CrowsOfWar
 */
public class StatCtrlHellBastion extends StatusControl {

    private final boolean setting;

    public StatCtrlHellBastion(boolean setting) {
        super(setting ? 11 : 12, setting ? CONTROL_RIGHT_CLICK_DOWN : CONTROL_RIGHT_CLICK_UP,
                RIGHT_OF_CROSSHAIR);
        this.setting = setting;
    }

    @Override
    public boolean execute(BendingContext ctx) {

        BendingData data = ctx.getData();
        EntityLivingBase bender = ctx.getBenderEntity();
        World world = ctx.getWorld();
        AbilityData abilityData = ctx.getData().getAbilityData("hell_bastion");
        AbilityHellBastion bastion = (AbilityHellBastion) Abilities.get("hell_bastion");

        if (data.hasBendingId(Demonbending.ID) && bastion != null) {
            if (setting) {
                //Adds the entity here so the player can charge it in the tickhandler.
                //Really basic attributes are set here, overriden later in the tickhandler.

                EntityInfernalBall ball = new EntityInfernalBall(world);
                ball.setAbility(bastion);
                ball.setOwner(bender);
                ball.setElement(Demonbending.ID);
                ball.setPosition(Vector.getEyePos(bender).plusY(10));
                ball.setBehaviour(new InfernalPlayerControlled());
                ball.setVelocity(Vector.ZERO);
                ball.setLifeTime(-1);
                //Other attributes set later
                if (!world.isRemote)
                    world.spawnEntity(ball);

                data.addStatusControl(RELEASE_HELL_BASTION);
                data.addTickHandler(HELL_BASTION_HANDLER, ctx);
            } else {
                if (bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(HELL_BASTION_MOVEMENT_MOD_ID) != null)
                    bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(HELL_BASTION_MOVEMENT_MOD_ID);
            }
        }

        return true;
    }

    //Player controlled behaviour for the ball
    public static class InfernalPlayerControlled extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            EntityLivingBase owner = entity.getOwner();
            World world = entity.world;

            if (owner == null || !(entity instanceof EntityInfernalBall)) return this;

            Vector eye = Vector.getEyePos(owner).plusY(10);
            Vec3d motion = eye.minus(Vector.getEntityPos(entity)).times(0.5).toMinecraft();

            entity.setVelocity(motion);

            if (world.isRemote) {
                Vec3d centre = AvatarEntityUtils.getMiddleOfEntity(entity);
                float size = 0.75F * entity.getAvgSize() * (1 / entity.getAvgSize());
                int rings = (int) (entity.getAvgSize() * 4);
                int particles = (int) (entity.getAvgSize() * Math.PI);

                ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(size).time(8 + AvatarUtils.getRandomNumberInRange(0, 4))
                        .element(BendingStyles.get(entity.getElement())).clr(120, 40, 40).spawnEntity(entity).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 30)
                        .swirl(rings, particles, entity.getAvgSize() * 1.1F, size * 15, entity.getAvgSize() * 10, (-1 / size),
                                entity, world, false, centre, ParticleBuilder.SwirlMotionType.OUT, false, true);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(size).time(8 + AvatarUtils.getRandomNumberInRange(0, 4))
                        .element(BendingStyles.get(entity.getElement())).clr(10, 10, 10).spawnEntity(entity).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 60)
                        .swirl(rings, particles, entity.getAvgSize() * 1.1F, size * 15, entity.getAvgSize() * 10, (-1 / size),
                                entity, world, false, centre, ParticleBuilder.SwirlMotionType.OUT, false, true);


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
