package com.crowsofwar.avatar.common.bending;

import java.util.function.Function;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.earth.EarthbendingState;
import com.crowsofwar.avatar.common.bending.earth.FloatingBlockEvent;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.FloatingBlockBehavior;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Describes a temporary effect where a callback listener is added to a control event. The listener
 * then will perform certain actions associated with that control.
 * <p>
 * For example, the player receives a place-block Status Control, which subscribes to right-click.
 * The status control receives a callback whenever the player uses the right-click control. Then,
 * the status control is removed.
 * <p>
 * Status controls are stored in player-data, but are also sent to the client via packets, which
 * render over the crosshair.
 * 
 * @author CrowsOfWar
 */
public enum StatusControl {
	
	AIR_JUMP(ctx -> {
		EntityPlayer player = ctx.getPlayerEntity();
		
		Vector rotations = new Vector(Math.toRadians((player.rotationPitch - 15) / 2),
				Math.toRadians(player.rotationYaw), 0);
		
		Vector velocity = rotations.toRectangular();
		velocity.mul(4);
		velocity.setY(velocity.y() * 0.8);
		player.addVelocity(velocity.x(), velocity.y(), velocity.z());
		((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityVelocity(player));
		
		return true;
		
	}, 0, AvatarControl.CONTROL_SPACE),
	
	PLACE_BLOCK(ctx -> {
		
		BendingController<EarthbendingState> controller = (BendingController<EarthbendingState>) BendingManager
				.getBending(BendingType.EARTHBENDING);
		
		AvatarPlayerData data = ctx.getData();
		EarthbendingState ebs = (EarthbendingState) data.getBendingState(controller);
		
		EntityFloatingBlock floating = ebs.getPickupBlock();
		if (floating != null) {
			// TODO Verify look at block
			VectorI looking = ctx.getClientLookBlock();
			EnumFacing lookingSide = ctx.getLookSide();
			if (looking != null && lookingSide != null) {
				looking.offset(lookingSide);
				
				floating.setBehavior(new FloatingBlockBehavior.Place(looking.toBlockPos()));
				Vector force = looking.precision().minus(new Vector(floating));
				force.normalize();
				floating.velocity().add(force);
				ebs.dropBlock();
				
				controller.post(new FloatingBlockEvent.BlockPlaced(floating, ctx.getPlayerEntity()));
				
			}
		}
		
		return true;
		
	}, 1, AvatarControl.CONTROL_RIGHT_CLICK_DOWN),
	
	THROW_BLOCK(ctx -> {
		
		BendingController<EarthbendingState> controller = (BendingController<EarthbendingState>) BendingManager
				.getBending(BendingType.EARTHBENDING);
		
		EarthbendingState ebs = (EarthbendingState) ctx.getData().getBendingState(controller);
		EntityPlayer player = ctx.getPlayerEntity();
		World world = player.worldObj;
		EntityFloatingBlock floating = ebs.getPickupBlock();
		
		if (floating != null) {
			floating.setOwner(null);
			
			float yaw = (float) Math.toRadians(player.rotationYaw);
			float pitch = (float) Math.toRadians(player.rotationPitch);
			
			// Calculate force and everything
			Vector lookDir = Vector.fromYawPitch(yaw, pitch);
			floating.velocity().add(lookDir.times(20));
			floating.setBehavior(new FloatingBlockBehavior.Thrown(floating));
			ebs.setPickupBlock(null);
			AvatarMod.network.sendTo(new PacketCPlayerData(ctx.getData()), (EntityPlayerMP) player);
			
			controller.post(new FloatingBlockEvent.BlockThrown(floating, player));
			
		}
		
		return true;
		
	}, 2, AvatarControl.CONTROL_LEFT_CLICK_DOWN);
	
	private final int texture;
	private final Function<AbilityContext, Boolean> callback;
	private final AvatarControl control;
	private final Raytrace.Info raytrace;
	
	private StatusControl(Function<AbilityContext, Boolean> callback, int texture,
			AvatarControl subscribeTo) {
		this(callback, texture, subscribeTo, new Raytrace.Info());
	}
	
	private StatusControl(Function<AbilityContext, Boolean> callback, int texture, AvatarControl subscribeTo,
			Raytrace.Info raytrace) {
		this.texture = texture;
		this.callback = callback;
		this.control = subscribeTo;
		this.raytrace = raytrace;
	}
	
	public int id() {
		return ordinal() + 1;
	}
	
	public AvatarControl getSubscribedControl() {
		return control;
	}
	
	/**
	 * Execute this status control in the given context.
	 * 
	 * @param ctx
	 *            Information for status control
	 * @return Whether to remove it
	 */
	public boolean execute(AbilityContext ctx) {
		return callback.apply(ctx);
	}
	
	public Raytrace.Info getRaytrace() {
		return raytrace;
	}
	
	public int getTextureU() {
		return (texture * 16) % 256;
	}
	
	public int getTextureV() {
		return (texture / 16) * 16;
	}
	
	public static StatusControl lookup(int id) {
		return values()[id - 1];
	}
	
}
