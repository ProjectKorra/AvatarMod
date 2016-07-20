package com.crowsofwar.avatar.client.particles;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class ParticleTest extends EntityFX
{
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod", "textures/particles/flame.png");
	private static final ResourceLocation VANILLA_PARTICLES = new ResourceLocation("textures/particle/particles.png");
	
    float smokeParticleScale;

    public ParticleTest(World p_i1198_1_, double p_i1198_2_, double p_i1198_4_, double p_i1198_6_, double p_i1198_8_, double p_i1198_10_, double p_i1198_12_)
    {
    	super(p_i1198_1_, p_i1198_2_, p_i1198_4_, p_i1198_6_, p_i1198_8_, p_i1198_10_, p_i1198_12_);
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.setParticleTextureIndex(0);
        this.setSize(0.02F, 0.02F);
        this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
        this.motionX = p_i1198_8_ * 0.20000000298023224D + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.02F);
        this.motionY = p_i1198_10_ * 0.20000000298023224D + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.02F);
        this.motionZ = p_i1198_12_ * 0.20000000298023224D + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.02F);
        this.particleMaxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
        this.particleIcon = new IconParticle("ParticleTest", 32, 32, 0, 0, 256, 256, 32);
    }

//    public ParticleTest(World world, double xe, double y, double z, double motionX, double motionY, double motionZ, float ageScale)
//    {
//        super(world, xe, y, z, 0.0D, 0.0D, 0.0D);
//        setParticleTextureIndex(32);
//        setSize(.02f, .02f);
//        this.motionX *= 0.10000000149011612D;
//        this.motionY *= 0.10000000149011612D;
//        this.motionZ *= 0.10000000149011612D;
//        this.motionX += motionX;
//        this.motionY += motionY;
//        this.motionZ += motionZ;
//        this.particleRed = this.particleGreen = this.particleBlue = (float)(Math.random() * 0.30000001192092896D);
//        this.particleScale *= 0.75F;
//        this.particleScale *= ageScale;
//        this.smokeParticleScale = this.particleScale;
//        this.particleMaxAge = (int) (8.0 / (Math.random() * 0.8 + 0.2));
//        this.particleMaxAge = (int) (1.0 * particleMaxAge * ageScale);
//        this.noClip = false;
//    }

    public void renderParticle(Tessellator t, float p_70539_2_, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_)
    {
    	t.draw();
    	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
    	Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
    	t.setBrightness(200);
    	t.startDrawingQuads();
//    	System.out.println((int) ((1.0 * this.particleAge / this.particleMaxAge) * 4));
//    	System.out.println(1.0 * this.particleAge / this.particleMaxAge);
    	getIcon().setAnimation((int) ((1.0 * this.particleAge / this.particleMaxAge) * 7));
    	this.particleScale = 4;
    	super.renderParticle(t, p_70539_2_, p_70539_3_, p_70539_4_, p_70539_5_, p_70539_6_, p_70539_7_);
    	t.draw();
    	Minecraft.getMinecraft().renderEngine.bindTexture(VANILLA_PARTICLES);
    	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    	t.startDrawingQuads();
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
    	super.onUpdate();
    	if(1==0){
    	this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY += 0.002D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.8500000238418579D;
        this.motionY *= 0.8500000238418579D;
        this.motionZ *= 0.8500000238418579D;
//        particleAge++;
//        motionX = motionY = motionZ = 0;

        if (this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)).getMaterial() != Material.water)
        {
//            this.setDead();
        }

        if (this.particleMaxAge-- <= 0)
        {
            this.setDead();
        }
//        this.prevPosX = this.posX;
//        this.prevPosY = this.posY;
//        this.prevPosZ = this.posZ;
//
//        if (this.particleAge++ >= this.particleMaxAge)
//        {
//            this.setDead();
//        }
//
//        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
//        this.motionY += 0.004D;
//        this.moveEntity(this.motionX, this.motionY, this.motionZ);
//
//        if (this.posY == this.prevPosY)
//        {
//            this.motionX *= 1.1D;
//            this.motionZ *= 1.1D;
//        }
//
//        this.motionX *= 0.9599999785423279D;
//        this.motionY *= 0.9599999785423279D;
//        this.motionZ *= 0.9599999785423279D;
//
//        if (this.onGround)
//        {
//            this.motionX *= 0.699999988079071D;
//            this.motionZ *= 0.699999988079071D;
//        }
    	}
    }
    
    private IconParticle getIcon() {
    	return (IconParticle) particleIcon;
    }
    
}