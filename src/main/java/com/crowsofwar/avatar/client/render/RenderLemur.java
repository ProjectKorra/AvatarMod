package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.entity.EntityFlyingLemur;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Korog3
 */

@SideOnly(Side.CLIENT)
public class RenderLemur extends RenderLiving<EntityFlyingLemur>{




	public static final ResourceLocation[] TEXTURES = new ResourceLocation[] { new ResourceLocation(AvatarInfo.MOD_ID+":textures/mob/lemur.png"), new ResourceLocation(AvatarInfo.MOD_ID+":textures/mob/lemur2.png") };

	public RenderLemur(RenderManager manager) {
		super(manager, new ModelLemur(), 0.2F);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityFlyingLemur entity) {
		return TEXTURES[entity.getVariant()];
	}   

	@Override
	protected void applyRotations(EntityFlyingLemur entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
		super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
	}
	
	
	
	public static class RenderFactory implements IRenderFactory<EntityFlyingLemur> {

        @Override
        public Render<? super EntityFlyingLemur> createRenderFor(RenderManager manager) {
            return new RenderLemur(manager);
        }

    }

}
