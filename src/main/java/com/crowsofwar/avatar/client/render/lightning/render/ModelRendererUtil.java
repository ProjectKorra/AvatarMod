package com.crowsofwar.avatar.client.render.lightning.render;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.render.lightning.main.ResourceManager;
import com.crowsofwar.avatar.client.render.lightning.math.BobMathUtil;
import com.crowsofwar.avatar.network.AvatarClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import javax.annotation.Nullable;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class ModelRendererUtil {

	//Need to call this because things like bats do extra scaling
	public static Method rPrepareScale;
	public static Method rGetEntityTexture;
	public static Method rHandleRotationFloat;
	public static Method rApplyRotations;
	public static Field rQuadList;
	public static Field rCompiled;
	
	public static ResourceLocation getEntityTexture(Entity e){
		Render<Entity> eRenderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(e);
		if(rGetEntityTexture == null){
			rGetEntityTexture = ReflectionHelper.findMethod(Render.class, "getEntityTexture", "func_110775_a", Entity.class);
		}
		ResourceLocation r = null;
		try {
			r = (ResourceLocation) rGetEntityTexture.invoke(eRenderer, e);
		} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
			e1.printStackTrace();
		}
		return r == null ? ResourceManager.turbofan_blades_tex : r;
	}


	@SuppressWarnings("deprecation")
	private static List<Pair<Matrix4f, ModelRenderer>> getBoxesFromMob(EntityLivingBase e, RenderLivingBase<?> render, float partialTicks) {
		ModelBase model = render.getMainModel();
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GlStateManager.disableCull();
		GlStateManager.enableRescaleNormal();
		//So basically we're just going to copy vanialla methods so the 
		model.swingProgress = e.getSwingProgress(partialTicks);
		boolean shouldSit = e.isRiding() && (e.getRidingEntity() != null && e.getRidingEntity().shouldRiderSit());
		model.isRiding = shouldSit;
		model.isChild = e.isChild();
		float f = interpolateRotation(e.prevRenderYawOffset, e.renderYawOffset, partialTicks);
		float f1 = interpolateRotation(e.prevRotationYawHead, e.rotationYawHead, partialTicks);
		float f2 = f1 - f;
		if(shouldSit && e.getRidingEntity() instanceof EntityLivingBase) {
			EntityLivingBase elivingbase = (EntityLivingBase) e.getRidingEntity();
			f = interpolateRotation(elivingbase.prevRenderYawOffset, elivingbase.renderYawOffset, partialTicks);
			f2 = f1 - f;
			float f3 = MathHelper.wrapDegrees(f2);

			if(f3 < -85.0F) {
				f3 = -85.0F;
			}

			if(f3 >= 85.0F) {
				f3 = 85.0F;
			}

			f = f1 - f3;

			if(f3 * f3 > 2500.0F) {
				f += f3 * 0.2F;
			}

			f2 = f1 - f;
		}

		float f7 = e.prevRotationPitch + (e.rotationPitch - e.prevRotationPitch) * partialTicks;
		//renderLivingAt(e, x, y, z);
		//float f8 = e.ticksExisted + partialTicks;
		//GlStateManager.rotate(180.0F - f, 0.0F, 1.0F, 0.0F);
		//if(rPreRenderCallback == null){
		//	rPreRenderCallback = ReflectionHelper.findMethod(RenderLivingBase.class, "preRenderCallback", "func_77041_b", EntityLivingBase.class, float.class);
		//}
		if(rPrepareScale == null){
			rPrepareScale = ReflectionHelper.findMethod(RenderLivingBase.class, "prepareScale", "func_188322_c", EntityLivingBase.class, float.class);
		}
		//float f4 = prepareScale(e, partialTicks, render);
		if(rHandleRotationFloat == null){
			rHandleRotationFloat = ReflectionHelper.findMethod(RenderLivingBase.class, "handleRotationFloat", "func_77044_a", EntityLivingBase.class, float.class);
			rApplyRotations = ReflectionHelper.findMethod(RenderLivingBase.class, "applyRotations", "func_77043_a", EntityLivingBase.class, float.class, float.class, float.class);
		}
		
		float f8 = 0;
		try {
			f8 = (Float)rHandleRotationFloat.invoke(render, e, partialTicks);
			rApplyRotations.invoke(render, e, f8, f, partialTicks);
		} catch(Exception x){
		}
		
        //this.applyRotations(entity, f8, f, partialTicks);
		
		float f4 = 0.0625F;
		try {
			f4 = (float) rPrepareScale.invoke(render, e, partialTicks);
		} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e2) {
			e2.printStackTrace();
		}
		float f5 = 0.0F;
		float f6 = 0.0F;
		if(!e.isRiding()) {
			f5 = e.prevLimbSwingAmount + (e.limbSwingAmount - e.prevLimbSwingAmount) * partialTicks;
			f6 = e.limbSwing - e.limbSwingAmount * (1.0F - partialTicks);

			if(e.isChild()) {
				f6 *= 3.0F;
			}

			if(f5 > 1.0F) {
				f5 = 1.0F;
			}
			f2 = f1 - f; // Forge: Fix MC-1207
		}
		model.setLivingAnimations(e, f6, f5, partialTicks);
		model.setRotationAngles(f6, f5, f8, f2, f7, f4, e);

		if(rGetEntityTexture == null){
			rGetEntityTexture = ReflectionHelper.findMethod(Render.class, "getEntityTexture", "func_110775_a", Entity.class);
		}
		ResourceLocation r = ResourceManager.turbofan_blades_tex;
		try {
			r = (ResourceLocation) rGetEntityTexture.invoke(render, e);
			if(r == null)
				r = ResourceManager.turbofan_blades_tex;
		} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
			e1.printStackTrace();
		}
		if(rCompiled == null){
			rCompiled = ReflectionHelper.findField(ModelRenderer.class, "compiled", "field_78812_q");
		}
		List<Pair<Matrix4f, ModelRenderer>> list = new ArrayList<>();
		for(ModelRenderer renderer : model.boxList) {
			if(!isChild(renderer, model.boxList))
				generateList(e.world, e, f4, list, renderer, r);
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.enableCull();
		GL11.glPopMatrix();
		return list;
	}
	
	public static boolean isChild(ModelRenderer r, List<ModelRenderer> list){
		for(ModelRenderer r2 : list){
			if(r2.childModels != null && r2.childModels.contains(r))
				return true;
		}
		return false;
	}
	
	protected static void generateList(World world, EntityLivingBase ent, float scale, List<Pair<Matrix4f, ModelRenderer>> list, ModelRenderer render, ResourceLocation tex){
		boolean compiled = false;
		try {
			//A lot of mobs weirdly replace model renderers and end up with extra ones in the list that aren't ever rendered.
			//Since they're not rendered, they should never be compiled, so this hack tries to detect that.
			//Not the greatest method ever, but it appears to work.
			compiled = rCompiled.getBoolean(render);
		} catch(Exception x){
		}
		if(render.isHidden || !render.showModel || !compiled)
			return;
		GL11.glPushMatrix();
		doTransforms(render, scale);
		if(render.childModels != null)
			for(ModelRenderer renderer : render.childModels) {
				generateList(world, ent, scale, list, renderer, tex);
			}
		GL11.glScaled(scale, scale, scale);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, AvatarClientProxy.AUX_GL_BUFFER);
		Matrix4f mat = new Matrix4f();
		mat.load(AvatarClientProxy.AUX_GL_BUFFER);
		AvatarClientProxy.AUX_GL_BUFFER.rewind();
		list.add(Pair.of(mat, render));
		GL11.glPopMatrix();
	}
	
	public static void doTransforms(ModelRenderer m, float scale) {
		GlStateManager.translate(m.offsetX, m.offsetY, m.offsetZ);
		if(m.rotateAngleX == 0.0F && m.rotateAngleY == 0.0F && m.rotateAngleZ == 0.0F) {
			if(m.rotationPointX == 0.0F && m.rotationPointY == 0.0F && m.rotationPointZ == 0.0F) {
			} else {
				GlStateManager.translate(m.rotationPointX * scale, m.rotationPointY * scale, m.rotationPointZ * scale);
			}
		} else {
			GlStateManager.translate(m.rotationPointX * scale, m.rotationPointY * scale, m.rotationPointZ * scale);
			if(m.rotateAngleZ != 0.0F) {
				GlStateManager.rotate(m.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
			}
			if(m.rotateAngleY != 0.0F) {
				GlStateManager.rotate(m.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
			}
			if(m.rotateAngleX != 0.0F) {
				GlStateManager.rotate(m.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
			}
		}
	}
	
	protected static float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
		float f;
		
		for(f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F) {
			;
		}

		while(f >= 180.0F) {
			f -= 360.0F;
		}

		return prevYawOffset + partialTicks * f;
	}
	
	public static class VertexData {
		public Vec3d[] positions;
		public int[] positionIndices;
		public float[] texCoords;
		
		public void tessellate(BufferBuilder buf, boolean normal){
			tessellate(buf, false, normal);
		}
		
		public void tessellate(BufferBuilder buf, boolean flip, boolean normal){
			if(positionIndices != null)
				for(int i = 0; i < positionIndices.length; i += 3){
					Vec3d a = positions[positionIndices[i]];
					Vec3d b = positions[positionIndices[i+1]];
					Vec3d c = positions[positionIndices[i+2]];
					//Offset into texcoord array
					int tOB = 1;
					int tOC = 2;
					if(flip){
						Vec3d tmp = b;
						b = c;
						c = tmp;
						tOB = 2;
						tOC = 1;
					}
					if(normal){
						Vec3d norm = b.subtract(a).crossProduct(c.subtract(a)).normalize();
						buf.pos(a.x, a.y, a.z).tex(texCoords[i*2+0], texCoords[i*2+1]).normal((float)norm.x, (float)norm.y, (float)norm.z).endVertex();
						buf.pos(b.x, b.y, b.z).tex(texCoords[(i+tOB)*2+0], texCoords[(i+tOB)*2+1]).normal((float)norm.x, (float)norm.y, (float)norm.z).endVertex();
						buf.pos(c.x, c.y, c.z).tex(texCoords[(i+tOC)*2+0], texCoords[(i+tOC)*2+1]).normal((float)norm.x, (float)norm.y, (float)norm.z).endVertex();
					} else {
						buf.pos(a.x, a.y, a.z).tex(texCoords[i*2+0], texCoords[i*2+1]).endVertex();
						buf.pos(b.x, b.y, b.z).tex(texCoords[(i+tOB)*2+0], texCoords[(i+tOB)*2+1]).endVertex();
						buf.pos(c.x, c.y, c.z).tex(texCoords[(i+tOC)*2+0], texCoords[(i+tOC)*2+1]).endVertex();
					}
					
				}
		}

		public float[] vertexArray() {
			float[] verts = new float[positions.length*3];
			for(int i = 0; i < positions.length; i ++){
				Vec3d pos = positions[i];
				verts[i*3] = (float) pos.x;
				verts[i*3+1] = (float) pos.y;
				verts[i*3+2] = (float) pos.z;
			}
			return verts;
		}
	}
	
}
