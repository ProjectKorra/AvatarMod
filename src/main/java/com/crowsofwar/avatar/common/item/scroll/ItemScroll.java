package com.crowsofwar.avatar.common.item.scroll;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.AvatarChatMessages;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.BendingStyles;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.gui.AvatarGuiHandler;
import com.crowsofwar.avatar.common.item.AvatarItem;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.item.scroll.Scrolls.ScrollType;
import com.crowsofwar.gorecore.format.FormattedMessageProcessor;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

import static com.crowsofwar.avatar.common.AvatarChatMessages.MSG_SPECIALTY_SCROLL_TOOLTIP;

/**
 * Base class for scrolls
 *
 * @author Aang23
 */
public class ItemScroll extends Item implements AvatarItem {
	private final Scrolls.ScrollType type;

	public ItemScroll(Scrolls.ScrollType type) {
		this.type = type;
		setMaxStackSize(1);
		setMaxDamage(0);
		setCreativeTab(AvatarItems.tabItems);
		setHasSubtypes(true);
		setTranslationKey("scroll_" + type.displayName());
	}



	public Scrolls.ScrollType getScrollType() {
		return type;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		switch (stack.getMetadata()) {
			case 0:
			case 1:
				return EnumRarity.COMMON;
			case 2:
				return EnumRarity.UNCOMMON;
			case 3:
			case 4:
				return EnumRarity.RARE;
			case 5:
			case 6:
				return EnumRarity.EPIC;
		}
		return EnumRarity.RARE;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		//Mothertrucking minecraft
	    if (!this.isInCreativeTab(tab)) {
			return;
		}
		for (int meta = 0; meta < 7; meta++) {
			items.add(new ItemStack(this, 1, meta));
		}
	}

	@Override
	public Item item() {
		return this;
	}

	@Override
	public String getModelName(int meta) {
		return "scroll_" + type.displayName();
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return "item.avatarmod:scroll." + type.displayName() + "." + (stack.getMetadata() + 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltips, ITooltipFlag advanced) {

		String tooltip = I18n.format("avatar." + Scrolls.getTypeForStack(stack).getBendingName());
		tooltips.add(tooltip);

		if (Scrolls.getTypeForStack(stack).isSpecialtyType()) {
			String translated = I18n.format("avatar.specialtyScroll.tooltip");
			String bendingName = Scrolls.getTypeForStack(stack).getBendingName();
			String formatted = FormattedMessageProcessor.formatText(MSG_SPECIALTY_SCROLL_TOOLTIP, translated,
					bendingName);
			tooltips.add(formatted);
		}

	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {

		ScrollType type = Scrolls.getTypeForStack(player.getHeldItem(hand));
		assert type != null;
		if (type.isSpecialtyType()) {
			handleSpecialtyScrollUse(world, player, player.getHeldItem(hand));
		} else {
			handleMainScrollUse(world, player);
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	/**
	 * Fired for right-clicking on a main bending style scroll (e.g. firebending
	 * scroll)
	 */
	private void handleMainScrollUse(World world, EntityPlayer player) {

		BendingData data = BendingData.get(player);
		if (data.getAllBending().isEmpty()) {
			player.openGui(AvatarMod.instance, AvatarGuiHandler.GUI_ID_GET_BENDING, world, 0, 0, 0);
		} else {
			BendingStyle controller = data.getAllBending().get(0);
			int guiId = AvatarGuiHandler.getGuiId(controller.getId());
			player.openGui(AvatarMod.instance, guiId, world, 0, 0, 0);
		}

	}

	/**
	 * Fired for right-clicking on a specialty bending scroll (e.g. lightningbending
	 * scroll)
	 */
	private void handleSpecialtyScrollUse(World world, EntityPlayer player, ItemStack stack) {

		if (world.isRemote) {
			return;
		}

		ScrollType type = Scrolls.getTypeForStack(stack);
		if (Bender.isBenderSupported(player)) {
			BendingData data = BendingData.get(player);
			assert type != null;
			BendingStyle specialtyStyle = BendingStyles.get(type.getBendingId());

			// Fail if player already has the scroll
			assert specialtyStyle != null;
			if (data.hasBending(specialtyStyle)) {

				String specialtyName = specialtyStyle.getName();
				AvatarChatMessages.MSG_SPECIALTY_SCROLL_ALREADY_HAVE.send(player, specialtyName);
				return;

			}

			// noinspection ConstantConditions - we already know this is a specialty bending
			// style
			UUID requiredMainBending = specialtyStyle.getParentBendingId();
			if (data.hasBendingId(requiredMainBending)) {
				data.addBending(specialtyStyle);
				if (!player.isCreative()) {
					stack.shrink(1);
				}
				String specialtyName = specialtyStyle.getName();
				AvatarChatMessages.MSG_SPECIALTY_SCROLL_SUCCESS.send(player, specialtyName);
			} else {
				String specialtyName = specialtyStyle.getName();
				String mainName = BendingStyles.getName(requiredMainBending);
				AvatarChatMessages.MSG_SPECIALTY_SCROLL_FAIL.send(player, specialtyName, mainName);
			}
		}
	}


}