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

package com.crowsofwar.avatar.network;

import com.crowsofwar.avatar.client.controls.IControlsHandler;
import com.crowsofwar.avatar.client.controls.KeybindingWrapper;
import com.crowsofwar.avatar.client.render.lightning.math.Vec3;
import com.crowsofwar.avatar.util.data.AvatarPlayerData;
import com.crowsofwar.avatar.client.gui.AvatarGui;
import com.crowsofwar.avatar.capabilities.IAdvancedGliderCapabilityHandler;
import com.crowsofwar.gorecore.data.PlayerDataFetcher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class AvatarServerProxy implements AvatarCommonProxy {

	private AvatarKeybindingServer keys;

	@Override
	public void preInit() {
		keys = new AvatarKeybindingServer();
	}

	@Override
	public IControlsHandler getKeyHandler() {
		return keys;
	}

	@Override
	public IPacketHandler getClientPacketHandler() {
		return null;
	}

	@Override
	public double getPlayerReach() {
		return 0;
	}

	@Override
	public void init() {

	}

	public File getDataDir(){
		return FMLCommonHandler.instance().getMinecraftServerInstance().getDataDirectory();
	}

	@Override
	public AvatarGui createClientGui(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public PlayerDataFetcher<AvatarPlayerData> getClientDataFetcher() {
		return null;
	}

	@Override
	public IThreadListener getClientThreadListener() {
		return null;
	}

	@Override
	public int getParticleAmount() {
		return 0;
	}

	@Override
	public KeybindingWrapper createKeybindWrapper(String keybindName) {
		return new KeybindingWrapper();
	}

	@Override
	public void registerItemModels() {
	}

	@Override
	public boolean isOptifinePresent() {
		return false;
	}

	@Override
	public EntityPlayer getClientPlayer(){
		return null; //nothing on server
	}

	@Override
	public World getClientWorld() {
		return null; //Nothing on server
	}

	@Override
	public IAdvancedGliderCapabilityHandler getClientGliderCapability() {
		return null; //nothing on server
	}

	// From Drillgon
	public void registerRenderInfo() { }
	public void registerTileEntitySpecialRenderer() { }
	public void registerItemRenderer() { }
	public void registerEntityRenderer() { }
	public void registerBlockRenderer() { }

	public void particleControl(double x, double y, double z, int type) { }

	public void spawnParticle(double x, double y, double z, String type, float[] args) { }

	public void spawnSFX(World world, double posX, double posY, double posZ, int type, Vec3 payload) { }

	public void effectNT(NBTTagCompound data) { }

	public void registerMissileItems(IRegistry<ModelResourceLocation, IBakedModel> reg) { }

	public void preInit(FMLPreInitializationEvent evt) {}

	public void checkGLCaps(){};

	public void postInit(FMLPostInitializationEvent e){
	}

	public float partialTicks(){
		return 1;
	};

	public boolean opengl33(){
		return true;//Doesn't matter for servers, and this won't print an error message.
	}

	public EntityPlayer me() {
		return null;
	}

	public void playSound(String sound, Object data) { }

	public void displayTooltip(String msg) { }

	public void setRecoil(float rec){};

	public boolean isVanished(Entity e) {
		return false;
	}

}
