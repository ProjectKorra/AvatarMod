/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/
package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.client.render.ostrich.ModelOstrichHorseTier1;
import com.crowsofwar.avatar.client.render.ostrich.ModelOstrichHorseTier2;
import com.crowsofwar.avatar.client.render.ostrich.ModelOstrichHorseTier3;
import com.crowsofwar.avatar.client.render.ostrich.ModelOstrichHorseWild;
import com.crowsofwar.avatar.common.entity.mob.EntityOstrichHorse;

import com.crowsofwar.avatar.common.item.ItemOstrichEquipment;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

/**
 * @author CrowsOfWar
 */
public class RenderOstrichHorse extends RenderLiving<EntityOstrichHorse> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/mob/ostrich.png");

	private final ModelBase[] models;
	private final ResourceLocation[] textures;

	public RenderOstrichHorse(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelOstrichHorseWild(), 0.5f); // pass in a dummy model ot
		// avoid NPEs

		models = new ModelBase[] {
				new ModelOstrichHorseWild(),
				new ModelOstrichHorseTier1(),
				new ModelOstrichHorseTier2(),
				new ModelOstrichHorseTier3()
		};
		textures = new ResourceLocation[models.length];
		for (int i = 0; i < textures.length; i++) {
			textures[i] = new ResourceLocation("avatarmod", "textures/mob/ostrich_tier" + i +
					".png");
		}

	}

	/**
	 * For retrieving a model or texture based on the ostrich's equipment. Gets the index of the
	 * ostrich assets to be used in either {@link #models} or {@link #textures}.
	 */
	private int getAssetIndex(EntityOstrichHorse entity) {
		ItemOstrichEquipment.EquipmentTier equipmentTier = entity.getEquipment();
		return equipmentTier == null ? 0 : equipmentTier.ordinal() + 1;
	}

	@Override
	public void doRender(EntityOstrichHorse entity, double x, double y, double z, float entityYaw, float
			partialTicks) {

		mainModel = models[getAssetIndex(entity)];
		super.doRender(entity, x, y, z, entityYaw, partialTicks);

	}

	@Override
	protected ResourceLocation getEntityTexture(EntityOstrichHorse entity) {
		return textures[getAssetIndex(entity)];
	}

}
