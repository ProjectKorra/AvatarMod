package com.crowsofwar.avatar.common.item;

import com.crowsofwar.avatar.common.bending.StatusControl;
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
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
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
		setMaxDamage(200);
		//Max damage is the durability of the item, or the max damage the item can take
		this.spawnGust = new Random().nextBoolean();

	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}


	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		boolean isCreative = attacker instanceof EntityPlayer && ((EntityPlayer) attacker).isCreative();
		if (!isCreative) {
			stack.damageItem(1, attacker);
		}
		Vector velocity = Vector.getLookRectangular(attacker).times(1.1);
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
		boolean isCreative = entityLiving instanceof EntityPlayer && ((EntityPlayer) entityLiving).isCreative();
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
				if (!isCreative) {
					stack.damageItem(2, entityLiving);
				}
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
				if (!isCreative) {
					stack.damageItem(2, entityLiving);
				}
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
							entityIn.posZ, 1, 0, 0, 0, 0.04);
					((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.SPEED, 40));
					((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 40));
					if ((new Random().nextInt(2) + 1) >= 2) {
						((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 20));
					}
				}
			}
		}
		if (entityIn instanceof EntityLivingBase) {
			//Heals the item's durability if you have airbending
			BendingData data = BendingData.get((EntityLivingBase) entityIn);
			Chi chi = data.chi();
			if (entityIn.ticksExisted % 80 == 0 && chi != null && data.hasBendingId(Airbending.ID) && ((new Random().nextInt(2) + 1) >= 2)) {
				if (stack.isItemDamaged()) {
					float availableChi = chi.getAvailableChi();
					if (availableChi > 1) {
						if (!(entityIn instanceof EntityPlayer && (((EntityPlayer) entityIn).isCreative()))) {
							chi.setTotalChi(chi.getTotalChi() - 2);
						}
						stack.damageItem(-1, (EntityLivingBase) entityIn);
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
