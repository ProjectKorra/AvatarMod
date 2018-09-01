package com.crowsofwar.avatar.common.item;

import com.crowsofwar.avatar.common.bending.air.AbilityAirGust;
import com.crowsofwar.avatar.common.bending.air.AbilityAirblade;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.bending.air.StaffPowerModifier;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.Chi;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.EntityAirGust;
import com.crowsofwar.avatar.common.entity.EntityAirblade;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Random;

import static com.crowsofwar.avatar.common.data.TickHandler.STAFF_GUST_HANDLER;

public class ItemAirbenderStaff extends ItemSword implements AvatarItem {

	private boolean spawnGust;

	public ItemAirbenderStaff(Item.ToolMaterial material) {
		super(material);
		setUnlocalizedName("airbender_staff");
		setCreativeTab(AvatarItems.tabItems);
		setMaxStackSize(1);
		setMaxDamage(2);
		this.spawnGust = new Random().nextBoolean();

	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}


	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		Vector velocity = Vector.getLookRectangular(attacker);
		target.motionX += velocity.x();
		target.motionY += velocity.y() > 0 ? velocity.y() + 0.2 : 0.3;
		target.motionZ += velocity.z();
		return true;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	public Item item() {
		return this;
	}


	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		BendingData data = BendingData.get(entityLiving);
		if (!data.hasTickHandler(STAFF_GUST_HANDLER) && !entityLiving.world.isRemote) {
			if (spawnGust) {
				EntityAirGust gust = new EntityAirGust(entityLiving.world);
				gust.setPosition(Vector.getLookRectangular(entityLiving).plus(Vector.getEntityPos(entityLiving)).withY(entityLiving.getEyeHeight() + entityLiving.getEntityBoundingBox().minY));
				gust.setAbility(new AbilityAirGust());
				gust.setOwner(entityLiving);
				gust.setVelocity(Vector.getLookRectangular(entityLiving).times(30));
				entityLiving.world.spawnEntity(gust);
				data.addTickHandler(STAFF_GUST_HANDLER);
				return true;
			}
			else {
				EntityAirblade blade = new EntityAirblade(entityLiving.world);
				blade.setPosition(Vector.getLookRectangular(entityLiving).plus(Vector.getEntityPos(entityLiving)));
				blade.setAbility(new AbilityAirblade());
				blade.setOwner(entityLiving);
				blade.setVelocity(Vector.getLookRectangular(entityLiving).times(30).withY(entityLiving.getEyeHeight()));
				blade.setDamage(2);
				entityLiving.world.spawnEntity(blade);
				data.addTickHandler(STAFF_GUST_HANDLER);
				return true;
			}

		}
		return false;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {

		if (isSelected && entityIn instanceof EntityLivingBase) {
			if (!worldIn.isRemote && worldIn instanceof WorldServer) {
				WorldServer world = (WorldServer) worldIn;
				if (entityIn.ticksExisted % 40 == 0) {
					world.spawnParticle(EnumParticleTypes.CLOUD, entityIn.posX, entityIn.posY + entityIn.getEyeHeight(),
							entityIn.posZ, 2, 0, 0, 0, 0.08 );
				}
			}
			BendingData data = BendingData.get((EntityLivingBase) entityIn);
			Chi chi = data.chi();
			if (entityIn.ticksExisted % 40 == 0 && chi != null && data.getActiveBendingId() == Airbending.ID) {
				if (item().isDamaged(stack)) {
					float availableChi = chi.getAvailableChi();
					if (availableChi > 0) {
						chi.setTotalChi(chi.getTotalChi() - 1);
						item().setDamage(stack, getDamage(stack) - 1);
					}
				}
			}
		}

	}

	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot)
	{
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

		if (equipmentSlot == EntityEquipmentSlot.MAINHAND)
		{
			spawnGust = new Random().nextBoolean();
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 1, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 0, 0));
		}

		return multimap;
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	public String getModelName(int meta) {
		return "airbender_staff";
	}

	//Add power rating modifier
}
