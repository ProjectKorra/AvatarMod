package com.crowsofwar.avatar.client.particles.newparticles;

import java.util.ArrayList;
import java.util.List;

import com.crowsofwar.avatar.client.render.lightning.handler.HbmShaderManager2;
import com.crowsofwar.avatar.client.render.lightning.main.ResourceManager;
import com.crowsofwar.avatar.client.render.lightning.math.BobMathUtil;
import com.crowsofwar.avatar.client.render.lightning.render.TrailRenderer2;
import com.crowsofwar.avatar.client.render.lightning.handler.LightningGenerator;
import com.crowsofwar.avatar.client.render.lightning.handler.LightningGenerator.LightningGenInfo;
import com.crowsofwar.avatar.client.render.lightning.handler.LightningGenerator.LightningNode;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleLightningStrip extends ParticleTargeted {

    /**
     * Half the width of the outermost layer.
     */
    private static final float THICKNESS = 0.04f;
    /**
     * Maximum length of a segment.
     */
    private static final double MAX_SEGMENT_LENGTH = 0.6;
    /**
     * Minimum length of a segment.
     */
    private static final double MIN_SEGMENT_LENGTH = 0.2;
    /**
     * Maximum deviation (in x or y, as drawn before transformations) from the centreline.
     */
    private static final double VERTEX_JITTER = 0.15;
    /**
     * Maximum number of segments a fork can have before ending.
     */
    private static final int MAX_FORK_SEGMENTS = 3;
    /**
     * Probability (as a fraction) that a vertex will have a fork.
     */
    private static final float FORK_CHANCE = 0.3f;
    /**
     * Number of ticks to wait before the arc changes shape again.
     */
    private static final int UPDATE_PERIOD = 1;

    public List<LightningPoint> points = new ArrayList<>();
    public float forkChance = 0.2F;
    public float minNewPointDist = 0.1F;
    public float motionScaleTan = 0.03F;
    public float motionScaleNorm = 0.01F;
    public float width = 0.004F;
    public boolean doTransform = false;

    public ParticleLightningStrip(World worldIn, double posXIn, double posYIn, double posZIn) {
        super(worldIn, posXIn, posYIn, posZIn);
        this.particleMaxAge = 122;
    }

    public void setNewPoint(Vec3d point){
        float scale = 0.01F;
        float scale2 = 0.002F;
        Vec3d pos = point.add((world.rand.nextFloat()*2-1)*scale, (world.rand.nextFloat()*2-1)*scale, (world.rand.nextFloat()*2-1)*scale);
        Vec3d motion = new Vec3d((world.rand.nextFloat()*2-1)*scale2, (world.rand.nextFloat()*2-1)*scale2, (world.rand.nextFloat()*2-1)*scale2);
        LightningNode fork = null;
        if(points.size() >= 1){
            Vec3d direction = point.subtract(points.get(points.size()-1).ogPos);
            double dot = direction.dotProduct(pos.subtract(points.get(points.size()-1).ogPos));
            Vec3d project = direction.scale(dot/direction.lengthSquared());
            direction = direction.normalize();
            motion = motion.add(pos.subtract(project).normalize().scale(motionScaleTan)).add(direction.scale(motionScaleNorm));
            if(world.rand.nextFloat() < forkChance){
                LightningGenInfo i = new LightningGenInfo();
                i.randAmount = 0.03F;
                i.subdivisions = 3;
                i.subdivRecurse = 1;
                i.forkChance = 0.1F;
                fork = LightningGenerator.generateLightning(new Vec3d(0, 0, 0), BobMathUtil.randVecInCone(direction, 20).scale(-0.3F), i);
            }
        }
        LightningPoint lPoint = new LightningPoint(point, pos, motion);

        lPoint.fork = fork;
        points.add(lPoint);
        if(points.size() >= 3 && points.get(points.size()-3).pos.squareDistanceTo(point) < minNewPointDist*minNewPointDist){
            points.remove(points.size()-2);
        }
    }

    @Override
    public void onUpdate() {
        this.particleAge ++;
        if(this.particleAge > this.particleMaxAge){
            this.setExpired();
        }
        for(LightningPoint p : points){
            p.prevPos = p.pos;
            p.pos = p.pos.add(p.motion);
            p.motion = p.motion.scale(0.96);
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if(points.size() >= 2){
            if(doTransform){
                GL11.glPushMatrix();
                GL11.glTranslated(-interpPosX, -interpPosY, -interpPosZ);
            }
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
            ResourceManager.lightning.use();
            ResourceManager.lightning.uniform4f("duck", 1F, 1F, 1F, 1F);
            ResourceManager.lightning.uniform1f("age", this.particleAge+partialTicks);
            int list = GL11.glGenLists(1);
            GL11.glNewList(list, GL11.GL_COMPILE);
            float time = (this.particleAge+partialTicks)*0.012F;
            List<Vec3d> currentPoints = new ArrayList<>(points.size());
            for(int i = 0; i < points.size(); i++){
                LightningPoint p = points.get(i);
                Vec3d pos = BobMathUtil.lerp(p.prevPos, p.pos, partialTicks);
                float override = (float)(i)/(float)points.size();

                override = 1-MathHelper.clamp(override-time*time*time, 0.001F, 1F);
                ResourceManager.lightning.uniform1f("fadeoverride", override);
                if(p.fork != null){
                    if(doTransform){
                        LightningGenerator.render(p.fork, new Vec3d(interpPosX, interpPosY+entityIn.getEyeHeight(), interpPosZ), width*0.5F, (float)pos.x, (float)pos.y, (float)pos.z, false, null);
                    } else {
                        LightningGenerator.render(p.fork, new Vec3d(0, 0, 0), width*0.5F, (float)pos.x, (float)pos.y, (float)pos.z, false, null);
                    }
                }
                currentPoints.add(pos);
            }
            ResourceManager.lightning.uniform1f("fadeoverride", 1F);
            ResourceManager.lightning.uniform1i("vertices", currentPoints.size()*3+2);
            if(doTransform){
                TrailRenderer2.draw(new Vec3d(interpPosX, interpPosY+entityIn.getEyeHeight(), interpPosZ), currentPoints, width);
            } else {
                TrailRenderer2.draw(new Vec3d(0, 0, 0), currentPoints, width);
            }
            GL11.glEndList();
            GL11.glCallList(list);
            HbmShaderManager2.bloomData.bindFramebuffer(false);
            ResourceManager.lightning.uniform4f("duck", 0.6F, 0.8F, 1F, 1F);
            GL11.glCallList(list);
            GL11.glCallList(list);
            Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
            GL11.glDeleteLists(list, 1);
            HbmShaderManager2.releaseShader();
            GlStateManager.disableBlend();
            if(doTransform){
                GL11.glPopMatrix();
            }
        }
    }

    @Override
    protected void draw(Tessellator tessellator, double length, float partialTicks) {
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        // The direction of the arc drawn by the tessellator is always along the z axis and is rotated to the
        // correct orientation, that way there isn't a ton of trigonometry and the code is way neater.

        boolean freeEnd = this.target == null;

        int numberOfSegments = (int) Math.round(length / MAX_SEGMENT_LENGTH); // Number of segments

        for (int layer = 0; layer < 3; layer++) {

            double px = 0, py = 0, pz = 0;
            // Creates a random from the arc's seed field + the number of ticks it has existed/the update period.
            // By using a seed, we can ensure the vertex positions and forks are identical a) for each layer, even
            // though they are rendered sequentially, and b) across many frames (and ticks, if updateTime > 1).
            random.setSeed(this.seed + this.particleAge / UPDATE_PERIOD);

            // numberOfSegments-1 because the last segment is handled separately.
            for (int i = 0; i < numberOfSegments - 1; i++) {

                double px2 = (random.nextDouble() * 2 - 1) * VERTEX_JITTER * particleScale;
                double py2 = (random.nextDouble() * 2 - 1) * VERTEX_JITTER * particleScale;
                double pz2 = pz + length / (double) numberOfSegments; // For now they are all the same length

                drawSegment(tessellator, layer, px, py, pz, px2, py2, pz2, THICKNESS * particleScale);

                // Forks
                if (random.nextFloat() < FORK_CHANCE) {

                    double px3 = px, py3 = py, pz3 = pz;

                    for (int j = 0; j < random.nextInt(MAX_FORK_SEGMENTS - 1) + 1; j++) {
                        // Forks set their centreline to the x/y coordinates of the vertex they originate from
                        double px4 = px3 + (random.nextDouble() * 2 - 1) * VERTEX_JITTER * particleScale;
                        double py4 = py3 + (random.nextDouble() * 2 - 1) * VERTEX_JITTER * particleScale;
                        double pz4 = pz3 + MIN_SEGMENT_LENGTH + random.nextDouble() * (MAX_SEGMENT_LENGTH - MIN_SEGMENT_LENGTH);

                        drawSegment(tessellator, layer, px3, py3, pz3, px4, py4, pz4, THICKNESS * 0.8f * particleScale);

                        // Forks of forks
                        if (random.nextFloat() < FORK_CHANCE) {

                            double px5 = px3 + (random.nextDouble() * 2 - 1) * VERTEX_JITTER * particleScale;
                            double py5 = py3 + (random.nextDouble() * 2 - 1) * VERTEX_JITTER * particleScale;
                            double pz5 = pz3 + MIN_SEGMENT_LENGTH + random.nextDouble() * (MAX_SEGMENT_LENGTH - MIN_SEGMENT_LENGTH);

                            drawSegment(tessellator, layer, px3, py3, pz3, px5, py5, pz5, THICKNESS * 0.6f * particleScale);
                        }

                        px3 = px4;
                        py3 = py4;
                        pz3 = pz4;
                    }
                }

                px = px2;
                py = py2;
                pz = pz2;
            }

            // Last segment has a specific end position and cannot fork.
            double px2 = freeEnd ? (random.nextDouble() * 2 - 1) * VERTEX_JITTER * particleScale : 0;
            double py2 = freeEnd ? (random.nextDouble() * 2 - 1) * VERTEX_JITTER * particleScale : 0;
            drawSegment(tessellator, layer, px, py, pz, px2, py2, length, THICKNESS * particleScale);

        }

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
    }

    private void drawSegment(Tessellator tessellator, int layer, double x1, double y1, double z1, double x2, double y2, double z2, float thickness) {

        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        switch (layer) {

            case 0:
                drawShearedBox(buffer, x1, y1, z1, x2, y2, z2, 0.25f * thickness, 1, 1, 1, 1);
                break;

            case 1:
                drawShearedBox(buffer, x1, y1, z1, x2, y2, z2, 0.6f * thickness, (particleRed + 1) / 2, (particleGreen + 1) / 2,
                        (particleBlue + 1) / 2, 0.65f);
                break;

            case 2:
                drawShearedBox(buffer, x1, y1, z1, x2, y2, z2, thickness, particleRed, particleGreen, particleBlue, 0.3f);
                break;
        }

        tessellator.draw();
    }

    /**
     * Draws a single box for one segment of the arc, from the point (x1, y1, z1) to the point (x2, y2, z2), with given width and colour.
     */
    private void drawShearedBox(BufferBuilder buffer, double x1, double y1, double z1, double x2, double y2, double z2, float width, float r, float g, float b, float a) {

        buffer.pos(x1 - width, y1 - width, z1).color(r, g, b, a).endVertex();
        buffer.pos(x2 - width, y2 - width, z2).color(r, g, b, a).endVertex();
        buffer.pos(x1 - width, y1 + width, z1).color(r, g, b, a).endVertex();
        buffer.pos(x2 - width, y2 + width, z2).color(r, g, b, a).endVertex();
        buffer.pos(x1 + width, y1 + width, z1).color(r, g, b, a).endVertex();
        buffer.pos(x2 + width, y2 + width, z2).color(r, g, b, a).endVertex();
        buffer.pos(x1 + width, y1 - width, z1).color(r, g, b, a).endVertex();
        buffer.pos(x2 + width, y2 - width, z2).color(r, g, b, a).endVertex();
        buffer.pos(x1 - width, y1 - width, z1).color(r, g, b, a).endVertex();
        buffer.pos(x2 - width, y2 - width, z2).color(r, g, b, a).endVertex();
    }


    public static class LightningPoint {
        Vec3d ogPos;
        Vec3d pos;
        Vec3d prevPos;
        Vec3d motion;
        LightningNode fork = null;

        public LightningPoint(Vec3d ogPos, Vec3d pos, Vec3d motion) {
            this.ogPos = ogPos;
            this.pos = pos;
            this.prevPos = pos;
            this.motion = motion;
        }
    }

}
