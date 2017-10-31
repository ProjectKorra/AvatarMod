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
package com.crowsofwar.avatar.client;

import com.crowsofwar.avatar.common.item.AvatarItem;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.item.ItemScroll.ScrollType;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarItemRenderRegister {
	
	private static ModelResourceLocation[] locationsRegular, locationsGlow;
	
	public static void register() {
		
		MinecraftForge.EVENT_BUS.register(new AvatarItemRenderRegister());
		
		// Setup scrolls
		locationsRegular = new ModelResourceLocation[ScrollType.amount()];
		locationsGlow = new ModelResourceLocation[ScrollType.amount()];
		
		for (int i = 0; i < ScrollType.amount(); i++) {

			ScrollType type = ScrollType.get(i);

			locationsRegular[i] = new ModelResourceLocation("avatarmod:scroll_" + type.displayName(),
					"inventory");
			locationsGlow[i] = new ModelResourceLocation("avatarmod:scroll_" + type.displayName() + "_glow",
					"inventory");

			setCustomModelResourceLocation(AvatarItems.itemScroll, i, locationsGlow[i]);
			setCustomModelResourceLocation(AvatarItems.itemScroll, i, locationsRegular[i]);

		}
		
		for (int i = 0; i <= 5; i++) {
			register(AvatarItems.itemWaterPouch, i);
		}
		register(AvatarItems.itemBisonWhistle);
		for (int i = 0; i <= 3; i++) {
			register(AvatarItems.itemBisonArmor, i);
			register(AvatarItems.itemBisonSaddle, i);
		}
		
	}

	/**
	 * Registers the specified item with the given metadata(s). Maps it to
	 * {unlocalizedName}.json. Note that if no metadata is specified, the item
	 * will not be registered.
	 */
	private static void register(AvatarItem item, int... metadata) {

		if (metadata.length == 0) {
			metadata = new int[1];
		}
		
		for (int meta : metadata) {
			ModelResourceLocation mrl = new ModelResourceLocation("avatarmod:" + item.getModelName(meta),
					"inventory");
			
			ModelLoader.setCustomModelResourceLocation(item.item(), meta, mrl);

		}
		
	}
	
	@SubscribeEvent
	public void modelBake(ModelBakeEvent e) {
		
		for (int i = 0; i < ScrollType.amount(); i++) {
			
			ModelResourceLocation mrlRegular = locationsRegular[i];
			ModelResourceLocation mrlGlow = locationsGlow[i];

			IBakedModel currentModel = e.getModelRegistry().getObject(mrlRegular);
			ScrollsPerspectiveModel customModel = new ScrollsPerspectiveModel(mrlRegular, mrlGlow,
					currentModel, e.getModelRegistry().getObject(mrlGlow));
			e.getModelRegistry().putObject(mrlRegular, customModel);

		}
		
	}
	
}
