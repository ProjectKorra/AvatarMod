package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.entity.data.FireArcBehavior;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Info;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityFireArc extends FireAbility {
	
	private final Raytrace.Info raytrace;
	
	/**
	 * @param controller
	 */
	public AbilityFireArc() {
		super("fire_arc");
		this.raytrace = new Raytrace.Info();
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		EntityPlayer player = ctx.getPlayerEntity();
		World world = ctx.getWorld();
		FirebendingState fs = (FirebendingState) ctx.getData().getBendingState(controller());
		
		Vector look = Vector.fromEntityLook(player);
		Vector lookPos = Vector.getEyePos(player).plus(look.times(3));
		EntityFireArc fire = new EntityFireArc(world);
		fire.setPosition(lookPos.x(), lookPos.y(), lookPos.z());
		fire.setBehavior(new FireArcBehavior.PlayerControlled(fire, player));
		fire.setOwner(player);
		fire.setDamageMult(0.75f + ctx.getData().getAbilityData(this).getXp() / 100);
		
		world.spawnEntityInWorld(fire);
		
		fs.setFireArc(fire);
		ctx.addStatusControl(StatusControl.THROW_FIRE);
		ctx.getData().sendBendingState(fs);
		
	}
	
	@Override
	public int getIconIndex() {
		return 3;
	}
	
	@Override
	public Info getRaytrace() {
		return raytrace;
	}
	
}
