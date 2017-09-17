package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityEarthSpike;
import com.crowsofwar.avatar.common.entity.EntityRavine;
import com.crowsofwar.avatar.common.entity.EntitySandPrison;
import com.crowsofwar.avatar.common.entity.mob.EntityOtterPenguin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.crowsofwar.avatar.common.bending.BendingStyle.random;

/*@SideOnly(Side.CLIENT)
public class RenderEarthSpikes extends Render<EntityEarthSpike> {



    private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
            "textures/entity/earthspike.png");

    private ModelBase model;

    /**
     * @param renderManager
     */
    /*public RenderEarthSpikes(RenderManager renderManager) {
        super(renderManager);
        this.model = new ModelEarthSpikes();
    }
    @Override
    public void doRender(EntityEarthSpike entity, double x, double y, double z, float entityYaw,
                         float partialTicks) {
        World world = entity.getEntityWorld();
        IBlockState blockState = world.getBlockState(entity.getPosition().offset(EnumFacing.DOWN));
        Block block = blockState.getBlock();
        world.spawnEntity(entity.posX, entity.posY + 0.3, entity.posZ,
                random.nextGaussian() - 0.5, random.nextGaussian() * 0.4, random.nextGaussian() - 0.5,
                Block.getStateId(blockState));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityEarthSpike entity) {
        return TEXTURE;
    }

}
**/
