package com.crowsofwar.avatar.client.render.lightning.render;

import com.crowsofwar.avatar.client.render.lightning.math.BobMathUtil;
import com.crowsofwar.avatar.client.render.lightning.main.ClientProxy;
import com.crowsofwar.avatar.client.render.lightning.main.MainRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.Project;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@SideOnly(Side.CLIENT)
public class RenderHelper {

    public static Field r_setTileEntities;
    public static Field r_viewFrustum;
    public static Method r_getRenderChunk;

    private static FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
    private static FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);
    private static IntBuffer VIEWPORT = GLAllocation.createDirectIntBuffer(16);
    private static FloatBuffer POSITION = GLAllocation.createDirectFloatBuffer(4);

    public static boolean useFullPost = true;
    public static boolean flashlightInit = false;
    public static int shadowFbo;
    public static int shadowFboTex;

    public static int height = 0;
    public static int width = 0;

    public static int deferredFbo;
    public static int deferredColorTex = -1;
    //TODO condense into one texture?
    public static int deferredPositionTex = -1;
    public static int deferredProjCoordTex = -1;
    //Actually it might be possible to reconstruct both position and normal data from the depth buffer, which would't require a shader at all
    //TODO test this?
    public static int deferredNormalTex = -1;
    //Only used for full post processing
    public static int deferredDepthTex = -1;

    public static float[] inv_ViewProjectionMatrix = new float[16];

    //Flashlights should all be rendered at the end of the render world for their deferred rendering to work.
    //If we're not at the end of render world, add it to a list to be rendered later.
    public static boolean renderingFlashlights = false;
    //If true, no flashlights should be added to the render list. This prevents entities that add flashlights from adding them more than once when we
    //Render them here
    private static boolean flashlightLock = false;
    //List of future flashlights to render;
    private static List<Runnable> flashlightQueue = new ArrayList<>();

    /**
     *
     * @param lb
     * @param rb
     * @param rt
     * @param lt
     * @return left-bottom-right-top
     */
    public static float[] getScreenAreaFromQuad(Vec3d lb, Vec3d rb, Vec3d rt, Vec3d lt){
        FloatBuffer mmatrix = GLAllocation.createDirectFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, mmatrix);
        FloatBuffer pmatrix = GLAllocation.createDirectFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, pmatrix);
        IntBuffer vport = GLAllocation.createDirectIntBuffer(16);
        GL11.glGetInteger(GL11.GL_VIEWPORT, vport);

        FloatBuffer[] points = new FloatBuffer[4];
        FloatBuffer buf0 = GLAllocation.createDirectFloatBuffer(3);
        Project.gluProject((float)lb.x, (float)lb.y, (float)lb.z, mmatrix, pmatrix, vport, buf0);
        points[0] = buf0;
        FloatBuffer buf1 = GLAllocation.createDirectFloatBuffer(3);
        Project.gluProject((float)rb.x, (float)rb.y, (float)rb.z, mmatrix, pmatrix, vport, buf1);
        points[1] = buf1;
        FloatBuffer buf2 = GLAllocation.createDirectFloatBuffer(3);
        Project.gluProject((float)rt.x, (float)rt.y, (float)rt.z, mmatrix, pmatrix, vport, buf2);
        points[2] = buf2;
        FloatBuffer buf3 = GLAllocation.createDirectFloatBuffer(3);
        Project.gluProject((float)lt.x, (float)lt.y, (float)lt.z, mmatrix, pmatrix, vport, buf3);
        points[3] = buf3;

        float top = buf0.get(1);
        float bottom = buf0.get(1);
        float left = buf0.get(0);
        float right = buf0.get(0);

        for(FloatBuffer buf : points){
            if(buf.get(0) > right){
                right = buf.get(0);
            }
            if(buf.get(0) < left){
                left = buf.get(0);
            }
            if(buf.get(1) > top){
                top = buf.get(1);
            }
            if(buf.get(1) < bottom){
                bottom = buf.get(1);
            }
        }
        //System.out.println(top);
        if(bottom < 0)
            bottom = 0;
        if(top > Minecraft.getMinecraft().displayHeight)
            top = Minecraft.getMinecraft().displayHeight;
        if(left < 0)
            left = 0;
        if(right > Minecraft.getMinecraft().displayWidth)
            right = Minecraft.getMinecraft().displayWidth;

        if(right <= 0 || top <= 0 || bottom >= Minecraft.getMinecraft().displayHeight || left >= Minecraft.getMinecraft().displayWidth)
            return null;
        //System.out.println(right);
        return new float[]{left, bottom, right, top};
    }


    public static TextureAtlasSprite getItemTexture(Item item, int meta){
        return getItemTexture(new ItemStack(item, 1, meta));
    }

    public static TextureAtlasSprite getItemTexture(Item item){
        return getItemTexture(item, 0);
    }

    public static TextureAtlasSprite getItemTexture(ItemStack item){
        return Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(item, null, null).getParticleTexture();
    }

    public static void addVertexWithUV(double x, double y, double z, double u, double v){
        addVertexWithUV(x, y, z, u, v, Tessellator.getInstance());
    }
    public static void addVertex(double x, double y, double z){
        Tessellator.getBuffer().pos(x, y, z).endVertex();
    }

    public static void addVertexWithUV(double x, double y, double z, double u, double v, Tessellator tes){
        BufferBuilder buf = tes.getBuffer();
        buf.pos(x, y, z).tex(u, v).endVertex();
    }
    public static void startDrawingTexturedQuads(){
        startDrawingTexturedQuads(Tessellator.getInstance());
    }
    public static void startDrawingQuads(){
        Tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
    }
    public static void startDrawingTexturedQuads(Tessellator tes){
        tes.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
    }
    public static void draw(){
        draw(Tessellator.getInstance());
    }
    public static void draw(Tessellator tes){
        tes.draw();
    }

    public static void bindTexture(ResourceLocation resource){
        Minecraft.getMinecraft().renderEngine.bindTexture(resource);
    }
    public static void bindBlockTexture(){
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }

    //Drillgon200: using GLStateManager for this because it caches color values
    public static void setColor(int color) {

        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        GlStateManager.color(red, green, blue, 1.0F);
    }

    public static void unpackColor(int color, float[] col){
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        col[0] = red;
        col[1] = green;
        col[2] = blue;
    }

    public static void resetColor(){
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void startDrawingColored(int i) {
        Tessellator.getBuffer().begin(i, DefaultVertexFormats.POSITION_COLOR);
    }

    public static void addVertexColor(double x, double y, double z, int red, int green, int blue, int alpha){
        Tessellator.getBuffer().pos(x, y, z).color(red, green, blue, alpha).endVertex();;
    }

    public static void addVertexColor(double x, double y, double z, float red, float green, float blue, float alpha){
        Tessellator.getBuffer().pos(x, y, z).color(red, green, blue, alpha).endVertex();;
    }

    public static void renderAll(IBakedModel boxcar) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
        for(BakedQuad quad : boxcar.getQuads(null, null, 0)){
            buf.addVertexData(quad.getVertexData());
        }
        tes.draw();
    }

    public static void renderConeMesh(Vec3d start, Vec3d normal, float height, float radius, int sides){
        float[] vertices = new float[(1+sides)*3];
        vertices[0] = 0;
        vertices[1] = 0;
        vertices[2] = 0;

        Vec3d vertex = new Vec3d(radius, 0, 0);
        for(int i = 0; i < sides; i ++){
            vertex = vertex.rotateYaw((float) (2*Math.PI*(1F/(float)sides)));
            vertices[(i+1)*3] = (float) vertex.x;
            vertices[(i+1)*3+1] = (float) vertex.y-height;
            vertices[(i+1)*3+2] = (float) vertex.z;
        }

        GL11.glPushMatrix();
        Vec3d angles = BobMathUtil.getEulerAngles(normal);
        GL11.glTranslated(start.x, start.y, start.z);
        GL11.glRotated(angles.x+180, 0, 1, 0);
        GL11.glRotated(angles.y+180, 1, 0, 0);


        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.getBuffer();
        buf.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);
        for(int i = 2; i <= sides; i ++){
            buf.pos(0, 0, 0).endVertex();
            buf.pos(vertices[(i-1)*3], vertices[(i-1)*3+1], vertices[(i-1)*3+2]).endVertex();
            buf.pos(vertices[i*3], vertices[i*3+1], vertices[i*3+2]).endVertex();
        }
        buf.pos(0, 0, 0).endVertex();
        buf.pos(vertices[sides*3], vertices[sides*3+1], vertices[sides*3+2]).endVertex();
        buf.pos(vertices[1*3], vertices[1*3+1], vertices[1*3+2]).endVertex();

        for(int i = 1; i < sides-1; i ++){
            buf.pos(vertices[3], vertices[3+1], vertices[3+2]).endVertex();
            buf.pos(vertices[(i+2)*3], vertices[(i+2)*3+1], vertices[(i+2)*3+2]).endVertex();
            buf.pos(vertices[(i+1)*3], vertices[(i+1)*3+1], vertices[(i+1)*3+2]).endVertex();
        }
        tes.draw();

        GL11.glPopMatrix();
    }

    public static void enableBlockVBOs(){
        GlStateManager.glEnableClientState(32884);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.glEnableClientState(32888);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.glEnableClientState(32888);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.glEnableClientState(32886);
    }

    public static void disableBlockVBOs(){
        for (VertexFormatElement vertexformatelement : DefaultVertexFormats.BLOCK.getElements())
        {
            VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();
            int k1 = vertexformatelement.getIndex();

            switch (vertexformatelement$enumusage)
            {
                case POSITION:
                    GlStateManager.glDisableClientState(32884);
                    break;
                case UV:
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + k1);
                    GlStateManager.glDisableClientState(32888);
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                    break;
                case COLOR:
                    GlStateManager.glDisableClientState(32886);
                    GlStateManager.resetColor();
            }
        }
    }

    public static void renderChunks(Collection<RenderChunk> toRender, double posX, double posY, double posZ){
        for(RenderChunk chunk : toRender){
            GL11.glPushMatrix();
            BlockPos chunkPos = chunk.getPosition();
            GL11.glTranslated(chunkPos.getX() - posX, chunkPos.getY() - posY, chunkPos.getZ() - posZ);
            chunk.multModelviewMatrix();
            for(int i = 0; i < 3; i ++){
                if(chunk.getCompiledChunk().isLayerEmpty(BlockRenderLayer.values()[i]) || chunk.getVertexBufferByLayer(i) == null)
                    continue;
                if(i == 2){
                    Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
                }
                VertexBuffer buf = chunk.getVertexBufferByLayer(i);
                buf.bindBuffer();
                GlStateManager.glVertexPointer(3, 5126, 28, 0);
                GlStateManager.glColorPointer(4, 5121, 28, 12);
                GlStateManager.glTexCoordPointer(2, 5126, 28, 16);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                GlStateManager.glTexCoordPointer(2, 5122, 28, 24);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                buf.drawArrays(GL11.GL_QUADS);
                if(i == 2){
                    Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
                }
            }
            GL11.glPopMatrix();
        }
    }

    @Deprecated
    private static void sendFlashlightUniforms(int shader, Vec3d playerPos, Vec3d pos, Vec3d normal, float height, float degrees, ResourceLocation flashlight_tex){
        pos = pos.subtract(playerPos);
        pos = BobMathUtil.viewFromLocal(new Vector4f((float)pos.x, (float)pos.y, (float)pos.z, 1))[0];
        normal = BobMathUtil.viewFromLocal(new Vector4f((float)normal.x, (float)normal.y, (float)normal.z, 0))[0];
        GL20.glUniform1f(GL20.glGetUniformLocation(shader, "angle"), (float) Math.cos(Math.toRadians(degrees)));
        GL20.glUniform1f(GL20.glGetUniformLocation(shader, "height"), height);
        GL20.glUniform3f(GL20.glGetUniformLocation(shader, "pos"), (float)pos.x, (float)pos.y, (float)pos.z);
        GL20.glUniform3f(GL20.glGetUniformLocation(shader, "direction"), (float)normal.x, (float)normal.y, (float)normal.z);
        //GL20.glUniform2f(GL20.glGetUniformLocation(shader, "screenSize"), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);

        GlStateManager.setActiveTexture(GL13.GL_TEXTURE3);
        GlStateManager.bindTexture(shadowFboTex);
        GL20.glUniform1i(GL20.glGetUniformLocation(shader, "shadowTex"), 3);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE4);
        bindTexture(flashlight_tex);
        GL20.glUniform1i(GL20.glGetUniformLocation(shader, "flashlightTex"), 4);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
    }

    @Deprecated
    private static void sendFlashLightPostUniforms(int shader, Vec3d playerPos, Vec3d pos, float height, float brightness, float[] shadowView, float[] shadowProjection, ResourceLocation flashlight_tex){
        pos = pos.subtract(playerPos);
        //pos = BobMathUtil.viewFromLocal(new Vector4f((float)pos.x, (float)pos.y, (float)pos.z, 1))[0];
        GL20.glUniform1f(GL20.glGetUniformLocation(shader, "height"), height);
        GL20.glUniform3f(GL20.glGetUniformLocation(shader, "fs_Pos"), (float)pos.x, (float)pos.y, (float)pos.z);
        GL20.glUniform2f(GL20.glGetUniformLocation(shader, "zNearFar"), 0.05F, Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16 * MathHelper.SQRT_2);
        GL20.glUniform1f(GL20.glGetUniformLocation(shader, "eyeHeight"), Minecraft.getMinecraft().player.getEyeHeight());
        GL20.glUniform1f(GL20.glGetUniformLocation(shader, "brightness"), brightness);

        GlStateManager.setActiveTexture(GL13.GL_TEXTURE3);
        GlStateManager.bindTexture(Minecraft.getMinecraft().getFramebuffer().framebufferTexture);
        GL20.glUniform1i(GL20.glGetUniformLocation(shader, "mc_tex"), 3);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE4);
        GlStateManager.bindTexture(deferredDepthTex);
        GL20.glUniform1i(GL20.glGetUniformLocation(shader, "depthBuffer"), 4);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE5);
        bindTexture(flashlight_tex);
        GL20.glUniform1i(GL20.glGetUniformLocation(shader, "flashlightTex"), 5);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE6);
        GlStateManager.bindTexture(shadowFboTex);
        GL20.glUniform1i(GL20.glGetUniformLocation(shader, "shadowTex"), 6);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);

        Matrix4f view = new Matrix4f();
        Matrix4f proj = new Matrix4f();

        ClientProxy.AUX_GL_BUFFER.put(shadowView);
        ClientProxy.AUX_GL_BUFFER2.put(shadowProjection);
        ClientProxy.AUX_GL_BUFFER.rewind();
        ClientProxy.AUX_GL_BUFFER2.rewind();
        view.load(ClientProxy.AUX_GL_BUFFER);
        proj.load(ClientProxy.AUX_GL_BUFFER2);
        ClientProxy.AUX_GL_BUFFER.rewind();
        ClientProxy.AUX_GL_BUFFER2.rewind();
        Matrix4f.mul(proj, view, view);
        view.store(ClientProxy.AUX_GL_BUFFER);
        ClientProxy.AUX_GL_BUFFER.rewind();
        GL20.glUniformMatrix4(GL20.glGetUniformLocation(shader, "flashlight_ViewProjectionMatrix"), false, ClientProxy.AUX_GL_BUFFER);

        ClientProxy.AUX_GL_BUFFER.put(inv_ViewProjectionMatrix);
        ClientProxy.AUX_GL_BUFFER.rewind();
        GL20.glUniformMatrix4(GL20.glGetUniformLocation(shader, "inv_ViewProjectionMatrix"), false, ClientProxy.AUX_GL_BUFFER);

    }

    @SuppressWarnings("deprecation")
    public static RenderChunk getRenderChunk(BlockPos pos){
        try {
            if(r_viewFrustum == null)
                r_viewFrustum = ReflectionHelper.findField(RenderGlobal.class, "viewFrustum", "field_175008_n");
            if(r_getRenderChunk == null)
                r_getRenderChunk = ReflectionHelper.findMethod(ViewFrustum.class, "getRenderChunk", "func_178161_a", BlockPos.class);
            ViewFrustum v = (ViewFrustum) r_viewFrustum.get(Minecraft.getMinecraft().renderGlobal);
            RenderChunk r = (RenderChunk) r_getRenderChunk.invoke(v, pos);
            return r;
        } catch(IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void renderFullscreenTriangle(){
        renderFullscreenTriangle(false);
    }

    public static void renderFullscreenTriangle(boolean alpha){
        GlStateManager.colorMask(true, true, true, alpha);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();

        GlStateManager.enableColorMaterial();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(-1, -1, 0.0D).tex(0, 0).endVertex();
        bufferbuilder.pos(3, -1, 0.0D).tex(2, 0).endVertex();
        bufferbuilder.pos(-1, 3, 0.0D).tex(0, 2).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.enableLighting();
        GlStateManager.colorMask(true, true, true, true);
    }

    public static void resetParticleInterpPos(Entity entityIn, float partialTicks){
        double entPosX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX)*partialTicks;
        double entPosY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY)*partialTicks;
        double entPosZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ)*partialTicks;

        Particle.interpPosX = entPosX;
        Particle.interpPosY = entPosY;
        Particle.interpPosZ = entPosZ;
    }

    public static float[] project(float x, float y, float z){
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION);
        GL11.glGetInteger(GL11.GL_VIEWPORT, VIEWPORT);

        Project.gluProject(x, y, z, MODELVIEW, PROJECTION, VIEWPORT, POSITION);
        return new float[]{POSITION.get(0), POSITION.get(1), POSITION.get(2)};
    }

    public static float[] unproject(float x, float y, float z){
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION);
        GL11.glGetInteger(GL11.GL_VIEWPORT, VIEWPORT);

        Project.gluUnProject(x, y, z, MODELVIEW, PROJECTION, VIEWPORT, POSITION);
        return new float[]{POSITION.get(0), POSITION.get(1), POSITION.get(2)};
    }

    public static Vec3d unproject_world(float[] inv_mvp, float x, float y, float z){
        Matrix4f mat = new Matrix4f();
        ClientProxy.AUX_GL_BUFFER.put(inv_mvp);
        ClientProxy.AUX_GL_BUFFER.rewind();
        mat.load(ClientProxy.AUX_GL_BUFFER);
        ClientProxy.AUX_GL_BUFFER.rewind();

        Vector4f ndcPos = new Vector4f();
        ndcPos.x = (2F*x)/(Minecraft.getMinecraft().displayWidth) - 1;
        ndcPos.y = (2F*y)/(Minecraft.getMinecraft().displayHeight) - 1;
        float near = 0;
        float far = 1;
        ndcPos.z = (2*z - near - far)/(far-near);
        ndcPos.w = 1;

        Matrix4f.transform(mat, ndcPos, ndcPos);
        float invW = 1F/ndcPos.w;
        Vector3f worldPos = new Vector3f(ndcPos.x*invW, ndcPos.y*invW, ndcPos.z*invW);

        Entity ent = Minecraft.getMinecraft().getRenderViewEntity();
        float partialTicks = MainRegistry.proxy.partialTicks();
        double rPosX = ent.prevPosX + (ent.posX-ent.prevPosX)*partialTicks;
        double rPosY = ent.prevPosY + (ent.posY-ent.prevPosY)*partialTicks;
        double rPosZ = ent.prevPosZ + (ent.posZ-ent.prevPosZ)*partialTicks;
        //Vec3d eyePos = ActiveRenderInfo.getCameraPosition();

        return new Vec3d(worldPos.x + rPosX, worldPos.y + rPosY, worldPos.z + rPosZ);
    }

    public static boolean intersects2DBox(float x, float y, float[] box){
        return x > box[0] && x < box[2] && y > box[1] && y < box[3];
    }

    public static boolean boxesOverlap(float[] box1, float[] box2){
        return box1[0] < box2[2] && box1[2] > box2[0] && box1[1] < box2[3] && box1[3] > box2[1];
    }

    public static boolean boxContainsOther(float[] box, float[] other){
        return box[0] <= other[0] && box[1] <= other[1] && box[2] >= other[2] && box[3] >= other[3];
    }

    public static float[] getBoxCenter(float[] box){
        return new float[]{box[0]+(box[2]-box[0])*0.5F, box[1]+(box[3]-box[1])*0.5F};
    }

}
