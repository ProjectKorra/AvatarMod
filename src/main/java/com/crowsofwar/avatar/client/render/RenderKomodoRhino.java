package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.client.render.komodorhino.ModelKomodoRhinoPlate;
import com.crowsofwar.avatar.client.render.komodorhino.ModelKomodoRhinoWoven;
import com.crowsofwar.avatar.client.render.komodorhino.ModelKomodoRhinoChain;
import com.crowsofwar.avatar.client.render.komodorhino.ModelKomodoRhinoWild;
import com.crowsofwar.avatar.common.entity.mob.EntityKomodoRhino;
import com.crowsofwar.avatar.common.item.ItemKomodoRhinoEquipment;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

/**
 * @author CrowsOfWar
 */
public class RenderKomodoRhino extends RenderLiving<EntityKomodoRhino> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
            "textures/mob/komodorhino.png");

    private final ModelBase[] models;
    private final ResourceLocation[] textures;

    public RenderKomodoRhino(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelKomodoRhinoWild() {
        }, 0.5f); // pass in a dummy model ot
        // avoid NPEs

        models = new ModelBase[]{
                new ModelKomodoRhinoWild(),
                new ModelKomodoRhinoWoven(),
                new ModelKomodoRhinoChain(),
                new ModelKomodoRhinoPlate()
        };
        textures = new ResourceLocation[models.length];
        for (int i = 0; i < textures.length; i++) {
            String tier = ItemKomodoRhinoEquipment.EquipmentTier.getTierName(i - 1);
            if (tier == null) {
                tier = "wild";
            }
            textures[i] = new ResourceLocation("avatarmod", "textures/mob/komodorhino_" + tier +
                    ".png");
        }

    }

    /**
     * For retrieving a model or texture based on the ostrich's equipment. Gets the index of the
     * ostrich assets to be used in either {@link #models} or {@link #textures}.
     */
    private int getAssetIndex(EntityKomodoRhino entity) {
        ItemKomodoRhinoEquipment.EquipmentTier equipmentTier = entity.getEquipment();
        return equipmentTier == null ? 0 : equipmentTier.ordinal() + 1;
    }

    @Override
    public void doRender(EntityKomodoRhino entity, double x, double y, double z, float entityYaw, float
            partialTicks) {

        mainModel = models[getAssetIndex(entity)];
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

    }

    @Override
    protected ResourceLocation getEntityTexture(EntityKomodoRhino entity) {
        return textures[getAssetIndex(entity)];
    }

}
