package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.network.packets.PacketCSyncEntityNBT;

import elucent.albedo.event.GatherLightsEvent;
import elucent.albedo.lighting.ILightProvider;
import elucent.albedo.lighting.Light;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Aang23
 */
@Optional.Interface(iface = "elucent.albedo.lighting.ILightProvider", modid = "albedo")
public class EntityLightOrb extends AvatarEntity implements ILightProvider {

    private double size = 2;
    private int radius = 20;
    public float colorR = 1F, colorG = 1F, colorB = 1F, colorA = 1F;

    public EntityLightOrb(World world) {
        super(world);
        setSize(0.1F, 0.1F);
    }

    /**
     * Sets the Orb's color. RGBA format
     */
    public void setColor(float r, float g, float b, float a) {
        colorR = r;
        colorG = g;
        colorB = b;
        colorA = a;
    }

    /**
     * Sets the light's radius
     * 
     * @param radius
     */
    public void setLightRadius(int radius) {
        this.radius = radius;
    }

    /**
     * Sets the orb size
     * 
     * @param size
     */
    public void setOrbSize(double size) {
        this.size = size;
    }

    public int getLightRadius() {
        return radius;
    }

    public double getOrbSize() {
        return size;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote)
            sendUpdateToClients();
    }

    private void sendUpdateToClients() {
        EntityTracker et = ((WorldServer) this.world).getEntityTracker();
        et.sendToTracking(this,
                AvatarMod.network.getPacketFrom(new PacketCSyncEntityNBT(this.getEntityId(), this.serializeNBT())));
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    @Optional.Method(modid = "albedo")
    public Light provideLight() {
        return Light.builder().pos(this).color(colorR, colorG, colorB).radius(radius).build();
    }

    @Override
    @Optional.Method(modid = "albedo")
    public void gatherLights(GatherLightsEvent arg0, Entity arg1) {

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        size = nbt.getDouble("OrbSize");
        radius = nbt.getInteger("OrbRadius");
        colorR = nbt.getFloat("OrbColorR");
        colorG = nbt.getFloat("OrbColorG");
        colorB = nbt.getFloat("OrbColorB");
        colorA = nbt.getFloat("OrbColorA");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setDouble("OrbSize", size);
        nbt.setInteger("OrbRadius", radius);
        nbt.setFloat("OrbColorR", colorR);
        nbt.setFloat("OrbColorG", colorG);
        nbt.setFloat("OrbColorB", colorB);
        nbt.setFloat("OrbColorA", colorA);
    }
}