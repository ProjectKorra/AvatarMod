package com.crowsofwar.avatar.client.render.lightning.main;

import com.crowsofwar.avatar.client.render.lightning.math.Vec3;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ServerProxy
{
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

    public File getDataDir(){
        return FMLCommonHandler.instance().getMinecraftServerInstance().getDataDirectory();
    }

    public void postInit(FMLPostInitializationEvent e){
    }

    public boolean opengl33(){
        return true;//Doesn't matter for servers, and this won't print an error message.
    }

    public EntityPlayer me() {
        return null;
    }

    public float partialTicks(){
        return 1;
    };

    public void playSound(String sound, Object data) { }

    public void displayTooltip(String msg) { }

    public void setRecoil(float rec){};

    public boolean isVanished(Entity e) {
        return false;
    }
}