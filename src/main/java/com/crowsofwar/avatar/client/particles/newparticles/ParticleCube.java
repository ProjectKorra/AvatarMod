package com.crowsofwar.avatar.client.particles.newparticles;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.particles.newparticles.behaviour.ParticleAvatarBehaviour;
import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.bending.bending.water.Waterbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

import static net.minecraft.util.math.MathHelper.cos;
import static net.minecraft.util.math.MathHelper.sin;

//@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class ParticleCube extends ParticleAvatar {

    private static final ResourceLocation WATER = new ResourceLocation("minecraft",
            "textures/blocks/water_still.png");

    /**
     * Creates a new particle in the given world at the given position. All other parameters are set via the various
     * setter methods ({@link ParticleBuilder ParticleBuilder} deals with all of that anyway).
     *
     * @param world The world in which to create the particle.
     * @param x     The x-coordinate at which to create the particle.
     * @param y     The y-coordinate at which to create the particle.
     * @param z     The z-coordinate at which to create the particle.
     */
    public ParticleCube(World world, double x, double y, double z) {
        super(world, x, y, z);//, WATER);
        this.setRBGColorF(1, 1, 1);
        this.particleAlpha = 1F;
        this.particleMaxAge = 12 + rand.nextInt(4);
        this.canCollide = true;
    }

    public static void drawQuad(int normal, Vector4f pos1, Vector4f pos2, Vector4f pos3, Vector4f
            pos4, double u1, double v1, double u2, double v2, float r, float g, float b, float a, BendingStyle element) {

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        if (element instanceof Waterbending) {
            if (normal == 0 || normal == 2) {
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                buffer.pos(pos1.x, pos1.y, pos1.z).tex(u2, v1).color(r, g, b, a).endVertex();
                buffer.pos(pos2.x, pos2.y, pos2.z).tex(u2, v2).color(r, g, b, a).endVertex();
                buffer.pos(pos3.x, pos3.y, pos3.z).tex(u1, v2).color(r, g, b, a).endVertex();
                buffer.pos(pos4.x, pos4.y, pos4.z).tex(u1, v1).color(r, g, b, a).endVertex();
                tessellator.draw();
            }
            if (normal == 1 || normal == 2) {
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                buffer.pos(pos1.x, pos1.y, pos1.z).tex(u2, v1).color(r, g, b, a).endVertex();
                buffer.pos(pos4.x, pos4.y, pos4.z).tex(u1, v1).color(r, g, b, a).endVertex();
                buffer.pos(pos3.x, pos3.y, pos3.z).tex(u1, v2).color(r, g, b, a).endVertex();
                buffer.pos(pos2.x, pos2.y, pos2.z).tex(u2, v2).color(r, g, b, a).endVertex();
                tessellator.draw();
            }
        } else if (element instanceof Firebending || element instanceof Airbending) {
            if (normal == 0 || normal == 2) {
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                buffer.pos(pos1.x, pos1.y, pos1.z).color(r, g, b, a).endVertex();
                buffer.pos(pos2.x, pos2.y, pos2.z).color(r, g, b, a).endVertex();
                buffer.pos(pos3.x, pos3.y, pos3.z).color(r, g, b, a).endVertex();
                buffer.pos(pos4.x, pos4.y, pos4.z).color(r, g, b, a).endVertex();
                tessellator.draw();
            }
            if (normal == 1 || normal == 2) {
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                buffer.pos(pos1.x, pos1.y, pos1.z).color(r, g, b, a).endVertex();
                buffer.pos(pos4.x, pos4.y, pos4.z).color(r, g, b, a).endVertex();
                buffer.pos(pos3.x, pos3.y, pos3.z).color(r, g, b, a).endVertex();
                buffer.pos(pos2.x, pos2.y, pos2.z).color(r, g, b, a).endVertex();
                tessellator.draw();
            }
        }
    }


    @Override
    public boolean shouldDisableDepth() {
        return true;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity viewer, float partialTicks, float lookZ, float lookY, float lookX, float lookXY, float lookYZ) {

        updateEntityLinking(partialTicks);

        Minecraft mc = Minecraft.getMinecraft();

        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        GlStateManager.translate(x, y, z);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        if (element instanceof Waterbending) {
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            mc.renderEngine.bindTexture(WATER);
        }

        if (element instanceof Firebending)
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        if (element instanceof Airbending)
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        float ticks = this.particleAge + partialTicks;
        float colorEnhancement = 1.5f;
        float size = 1;
        float scale = particleScale / 10;


        GlStateManager.scale(scale, scale, scale);

        Matrix4f mat = new Matrix4f();
        mat = mat.translate(x, y + 0.4F, z);

        //4 = degrees per second
        mat = mat.rotate(ticks / 20 * 0.2F * 40, 1, 0, 0);
        mat = mat.rotate(ticks / 20 * 40, 0, 1, 0);
        mat = mat.rotate(ticks / 20 * -0.4F * 40, 0, 0, 1);


        // @formatter:off
        Vector4f
                //You can't mul using the size because that would mul the w component, which would still make the bubble with a size of 1.
                lbf = new Vector4f(-.5f * size, -.5f * size, -.5f * size, 1).mul(mat),
                rbf = new Vector4f(0.5f * size, -.5f * size, -.5f * size, 1).mul(mat),
                ltf = new Vector4f(-.5f * size, 0.5f * size, -.5f * size, 1).mul(mat),
                rtf = new Vector4f(0.5f * size, 0.5f * size, -.5f * size, 1).mul(mat),
                lbb = new Vector4f(-.5f * size, -.5f * size, 0.5f * size, 1).mul(mat),
                rbb = new Vector4f(0.5f * size, -.5f * size, 0.5f * size, 1).mul(mat),
                ltb = new Vector4f(-.5f * size, 0.5f * size, 0.5f * size, 1).mul(mat),
                rtb = new Vector4f(0.5f * size, 0.5f * size, 0.5f * size, 1).mul(mat);


        if (element instanceof Waterbending) {

            float t1 = ticks * (float) Math.PI / 10f;
            float t2 = t1 + (float) Math.PI / 2f;
            float amt = 0.05f;

            lbf.add(cos(t1) * amt, sin(t2) * amt, cos(t2) * amt, 0);
            rbf.add(sin(t1) * amt, cos(t2) * amt, sin(t2) * amt, 0);
            lbb.add(sin(t2) * amt, cos(t2) * amt, cos(t2) * amt, 0);
            rbb.add(cos(t2) * amt, cos(t1) * amt, cos(t1) * amt, 0);

            ltf.add(cos(t2) * amt, cos(t1) * amt, sin(t1) * amt, 0);
            rtf.add(sin(t2) * amt, sin(t1) * amt, cos(t1) * amt, 0);
            ltb.add(sin(t1) * amt, sin(t2) * amt, cos(t1) * amt, 0);
            rtb.add(cos(t1) * amt, cos(t2) * amt, sin(t1) * amt, 0);
        }
        // @formatter:on


        float existed = ticks;
        int anim = ((int) existed % 16);
        float v1 = anim / 16f, v2 = v1 + 1f / 16;
        if (AvatarMod.proxy.isOptifinePresent()) {
            //if (element instanceof Waterbending)
            //   setRBGColorF(particleRed * 0.75F, particleGreen * 0.75F, particleBlue * 1.25F);
        }

        drawQuad(2, ltb, lbb, lbf, ltf, 0, v1, 1, v2,
                particleRed * colorEnhancement, particleGreen * colorEnhancement, particleBlue * colorEnhancement, particleAlpha * 0.5F, element); // -x
        drawQuad(2, rtb, rbb, rbf, rtf, 0, v1, 1, v2,
                particleRed * colorEnhancement, particleGreen * colorEnhancement, particleBlue * colorEnhancement, particleAlpha * 0.5F, element); // +x
        drawQuad(2, rbb, rbf, lbf, lbb, 0, v1, 1, v2,
                particleRed * colorEnhancement, particleGreen * colorEnhancement, particleBlue * colorEnhancement, particleAlpha * 0.5F, element); // -y
        drawQuad(2, rtb, rtf, ltf, ltb, 0, v1, 1, v2,
                particleRed * colorEnhancement, particleGreen * colorEnhancement, particleBlue * colorEnhancement, particleAlpha * 0.5F, element); // +y
        drawQuad(2, rtf, rbf, lbf, ltf, 0, v1, 1, v2,
                particleRed * colorEnhancement, particleGreen * colorEnhancement, particleBlue * colorEnhancement, particleAlpha * 0.5F, element); // -z
        drawQuad(2, rtb, rbb, lbb, ltb, 0, v1, 1, v2,
                particleRed * colorEnhancement, particleGreen * colorEnhancement, particleBlue * colorEnhancement, particleAlpha * 0.5F, element); // +z

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

    }


    @Override
    public int getFXLayer() {
        return 3;
    }

    @SideOnly(Side.CLIENT)
    public static class WaterParticleBehaviour extends ParticleAvatarBehaviour {

        @Nonnull
        @Override
        public ParticleAvatarBehaviour onUpdate(ParticleAvatar particle) {
            particle.setGravity(true);
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
