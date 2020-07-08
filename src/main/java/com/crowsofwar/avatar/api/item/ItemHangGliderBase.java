package com.crowsofwar.avatar.api.item;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.air.AbilityAirGust;
import com.crowsofwar.avatar.common.bending.air.AbilityAirblade;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.Chi;
import com.crowsofwar.avatar.common.entity.EntityAirGust;
import com.crowsofwar.avatar.common.entity.EntityAirblade;
import com.crowsofwar.avatar.common.event.StaffUseEvent;
import com.crowsofwar.avatar.common.helper.GliderPlayerHelper;
import com.crowsofwar.avatar.common.item.AvatarItem;
import com.crowsofwar.avatar.common.network.packets.glider.PacketCUpdateClientTarget;
import com.crowsofwar.avatar.common.util.GliderHelper;
import com.crowsofwar.gorecore.util.Vector;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.crowsofwar.avatar.AvatarInfo.MOD_ID;
import static com.crowsofwar.avatar.api.helper.GliderHelper.getIsGliderDeployed;
import static com.crowsofwar.avatar.api.helper.GliderHelper.setIsGliderDeployed;
import static com.crowsofwar.avatar.common.AvatarChatMessages.MSG_AIR_STAFF_COOLDOWN;
import static com.crowsofwar.avatar.common.data.TickHandlerController.STAFF_GUST_HANDLER;

public class ItemHangGliderBase extends ItemSword implements IGlider, AvatarItem {

    public static final ResourceLocation MODEL_GLIDER_BASIC_TEXTURE_RL = new ResourceLocation(MOD_ID, "textures/models/orangestaff.png");
    public static final ResourceLocation MODEL_GLIDER_ADVANCED_TEXTURE_RL = new ResourceLocation(MOD_ID, "textures/models/blackstaff.png");

    //ToDo: NBT saving tags of upgrade (need IRecipe for them)

    private float minSpeed;
    private float maxSpeed;
    private float pitchOffset;
    private float yBoost;
    private float fallReduction;
    private double windMultiplier;
    private double airResistance;
    private int totalDurability;
    private ResourceLocation modelRL;
    private boolean spawnGust;

    public ItemHangGliderBase(float minSpeed, float maxSpeed, float pitchOffset, float yBoost, float fallReduction, double windMultiplier, double airResistance, int totalDurability, ResourceLocation modelRL) {
        super(Item.ToolMaterial.WOOD);
        this.windMultiplier = windMultiplier;
        this.airResistance = airResistance;
        this.totalDurability = totalDurability;
        this.modelRL = modelRL;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.pitchOffset = pitchOffset;
        this.yBoost = yBoost;
        this.fallReduction = fallReduction;

        setMaxDamage(totalDurability);
        setMaxStackSize(1);

        //Add different icons for if the glider is deployed or not
        this.addPropertyOverride(new ResourceLocation("status"), new IItemPropertyGetter() {

            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                return isGlidingGlider(entityIn, stack) ? 1.0F : isBroken(stack) ? 2.0F : 0.0F; //0 = undeployed, 1 = deployed, 2 = broken
            }

            private boolean isGlidingGlider(EntityLivingBase entityIn, ItemStack stack) {
                return entityIn != null && entityIn instanceof EntityPlayer && getIsGliderDeployed((EntityPlayer) entityIn) && GliderPlayerHelper.getGlider((EntityPlayer) entityIn) == stack;
            }

        });

    }

    @Override
    public float getAttackDamage() {
        return 1F;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return false;
    }


    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        boolean isCreative =
                attacker instanceof EntityPlayer && ((EntityPlayer) attacker).isCreative();
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
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        boolean isCreative = entityLiving instanceof EntityPlayer && ((EntityPlayer) entityLiving)
                .isCreative();
        BendingData data = BendingData.get(entityLiving);
        if(entityLiving.isSneaking()) {
            if (entityLiving.getHeldItemOffhand() == stack) {
                if (!data.hasTickHandler(STAFF_GUST_HANDLER) && !entityLiving.world.isRemote) {
                    if (!MinecraftForge.EVENT_BUS.post(new StaffUseEvent(entityLiving, spawnGust))) {
                        if (spawnGust) {
                            EntityAirGust gust = new EntityAirGust(entityLiving.world);
                            gust.setPosition(Vector.getLookRectangular(entityLiving)
                                    .plus(Vector.getEntityPos(entityLiving))
                                    .withY(entityLiving.getEyeHeight() +
                                            entityLiving.getEntityBoundingBox().minY));
                            gust.setAbility(new AbilityAirGust());
                            gust.setOwner(entityLiving);
                            gust.setVelocity(Vector.getLookRectangular(entityLiving).times(30));
                            entityLiving.world.spawnEntity(gust);
                            if (!isCreative) {
                                data.addTickHandler(STAFF_GUST_HANDLER);
                                stack.damageItem(2, entityLiving);
                            }
                            return true;
                        } else {
                            EntityAirblade blade = new EntityAirblade(entityLiving.world);
                            blade.setPosition(Vector.getLookRectangular(entityLiving)
                                    .plus(Vector.getEntityPos(entityLiving))
                                    .withY(entityLiving.getEyeHeight() + entityLiving
                                            .getEntityBoundingBox().minY));
                            blade.setAbility(new AbilityAirblade());
                            blade.rotationYaw = entityLiving.rotationYaw;
                            blade.rotationPitch = entityLiving.rotationPitch;
                            blade.setOwner(entityLiving);
                            blade.setVelocity(Vector.getLookRectangular(entityLiving).times(30));
                            blade.setDamage(2);
                            entityLiving.world.spawnEntity(blade);
                            if (!isCreative) {
                                data.addTickHandler(STAFF_GUST_HANDLER);
                                stack.damageItem(2, entityLiving);
                            }
                            return true;
                        }
                    }
                }
                if (data.hasTickHandler(STAFF_GUST_HANDLER) && !entityLiving.world.isRemote) {
                    MSG_AIR_STAFF_COOLDOWN.send(entityLiving);
                }
                return true;
            }
            return false;
        } else {
            if (!data.hasTickHandler(STAFF_GUST_HANDLER) && !entityLiving.world.isRemote) {
                if (!MinecraftForge.EVENT_BUS.post(new StaffUseEvent(entityLiving, spawnGust))) {
                    if (spawnGust) {
                        EntityAirGust gust = new EntityAirGust(entityLiving.world);
                        gust.setPosition(Vector.getLookRectangular(entityLiving)
                                .plus(Vector.getEntityPos(entityLiving))
                                .withY(entityLiving.getEyeHeight() + entityLiving
                                        .getEntityBoundingBox().minY));
                        gust.setAbility(new AbilityAirGust());
                        gust.setOwner(entityLiving);
                        gust.setVelocity(Vector.getLookRectangular(entityLiving).times(30));
                        entityLiving.world.spawnEntity(gust);
                        if (!isCreative) {
                            data.addTickHandler(STAFF_GUST_HANDLER);
                            stack.damageItem(2, entityLiving);
                        }
                        return true;
                    } else {
                        EntityAirblade blade = new EntityAirblade(entityLiving.world);
                        blade.setPosition(Vector.getLookRectangular(entityLiving)
                                .plus(Vector.getEntityPos(entityLiving))
                                .withY(entityLiving.getEyeHeight() + entityLiving
                                        .getEntityBoundingBox().minY));
                        blade.setAbility(new AbilityAirblade());
                        blade.setOwner(entityLiving);
                        blade.rotationYaw = entityLiving.rotationYaw;
                        blade.rotationPitch = entityLiving.rotationPitch;
                        blade.setVelocity(Vector.getLookRectangular(entityLiving).times(30));
                        blade.setDamage(2);
                        entityLiving.world.spawnEntity(blade);
                        if (!isCreative) {
                            data.addTickHandler(STAFF_GUST_HANDLER);
                            stack.damageItem(2, entityLiving);
                        }
                        return true;
                    }

                }
            }
            if (data.hasTickHandler(STAFF_GUST_HANDLER) && entityLiving instanceof EntityPlayer
                    && !entityLiving.world.isRemote) {
                MSG_AIR_STAFF_COOLDOWN.send(entityLiving);
            }
            return false;
        }

    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot,
                         boolean isSelected) {

        if (isSelected && entityIn instanceof EntityLivingBase) {
            spawnGust = !entityIn.isSneaking();
            if (!worldIn.isRemote && worldIn instanceof WorldServer) {
                WorldServer world = (WorldServer) worldIn;
                if (entityIn.ticksExisted % 40 == 0) {
                    world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, entityIn.posX,
                            entityIn.posY + entityIn.getEyeHeight(),
                            entityIn.posZ, 1, 0, 0, 0, 0.04);
                    ((EntityLivingBase) entityIn)
                            .addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 0, false, false));
                    ((EntityLivingBase) entityIn)
                            .addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 40, 0, false, false));
                }
            }
        }
        if (entityIn instanceof EntityLivingBase) {
            if (((EntityLivingBase) entityIn).getHeldItemOffhand().getItem() == this) {
                if (!worldIn.isRemote && worldIn instanceof WorldServer) {
                    WorldServer world = (WorldServer) worldIn;
                    if (entityIn.ticksExisted % 40 == 0) {
                        world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, entityIn.posX,
                                entityIn.posY + entityIn.getEyeHeight(),
                                entityIn.posZ, 1, 0, 0, 0, 0.04);
                        ((EntityLivingBase) entityIn)
                                .addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 0, false, false));
                        ((EntityLivingBase) entityIn).addPotionEffect(
                                new PotionEffect(MobEffects.JUMP_BOOST, 40, 0, false, false));
                    }
                }
            }
        }
        if (entityIn instanceof EntityLivingBase) {
            //Heals the item's durability if you have airbending
            BendingData data = BendingData.get((EntityLivingBase) entityIn);
            Chi chi = data.chi();
            if (entityIn.ticksExisted % 80 == 0 && chi != null && data.hasBendingId(Airbending.ID)
                    && ((new Random().nextInt(2) + 1) >= 2)) {
                if (stack.isItemDamaged()) {
                    float availableChi = chi.getAvailableChi();
                    if (availableChi > 1) {
                        if (!(entityIn instanceof EntityPlayer && (((EntityPlayer) entityIn)
                                .isCreative()))) {
                            chi.setTotalChi(chi.getTotalChi() - 2);
                        }
                        stack.damageItem(-1, (EntityLivingBase) entityIn);
                    }
                }
            }
        }
    }


    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(
            EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = HashMultimap.create();

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            spawnGust = new Random().nextBoolean();
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier",
                            getAttackDamage(), 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                    new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 0, 0));
        }

        return multimap;
    }

    /**
     * Handles a right click of the item attempting to deploy the hang glider.
     *
     * @param world  - the world this occurs in
     * @param player - the player clicking
     * @param hand   - the hand used
     * @return - an ActionResult of the occurrence
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

        //ToDo: test enforce mainhand only
        ItemStack chestItem = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        ItemStack itemStack = player.getHeldItem(hand);

        //if no elytra equipped
        if (!(chestItem != null && !chestItem.isEmpty() && chestItem.getItem() instanceof ItemElytra)) {

            if (this.isBroken(itemStack))
                return ActionResult.newResult(EnumActionResult.PASS, itemStack); //nothing if broken
            if (!hand.equals(EnumHand.MAIN_HAND))
                return ActionResult.newResult(EnumActionResult.PASS, itemStack); //return if not using main hand

            //old deployment state
            boolean isDeployed = getIsGliderDeployed(player);


            //toggle state of glider deployment
            setIsGliderDeployed(player, !isDeployed);


            //client only
            if (!world.isRemote) {
                //send packet to nearby players to update visually
                AvatarMod.network.sendToAllTracking(new PacketCUpdateClientTarget(player, getIsGliderDeployed(player)), player);
            }

        } else {
            if (world.isRemote) { //client
                player.sendMessage(new TextComponentTranslation("avatar.gliderElytra"));
            }
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
    }

    //Helper method for checking if a hang glider is broken (1 durability left)
    @Override
    public boolean isBroken(ItemStack stack) {
        return stack.isItemDamaged() && (stack.getItemDamage() >= stack.getMaxDamage() - 1);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        ArrayList<ItemStack> upgrades = GliderHelper.getUpgradesFromNBT(stack);
        for (ItemStack upgrade : upgrades) {
            tooltip.add(upgrade.getDisplayName() + " " + I18n.format("glider.tooltip.upgrade"));
        }
    }

    /**
     * Return whether this item is repairable in an anvil. Uses leather.
     */
    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        //Does not currently use durability TODO: [AD]: Figure out how and when to lose durability
        /*List<ItemStack> wools = OreDictionary.getOres("wool");
        for (ItemStack stack : wools) {
            if (stack.getItem() == repair.getItem()) return true;
        }*/
        return false;

    }


    //==============================================IGlider========================================

    @Override
    public float getMaxSpeed() {
        return maxSpeed;
    }

    @Override
    public void setMaxSpeed(float speed) {
        maxSpeed = speed;
    }

    @Override
    public float getMinSpeed() {
        return minSpeed;
    }

    @Override
    public void setMinSpeed(float speed) {
        minSpeed = speed;
    }

    @Override
    public float getYBoost() {
        return yBoost;
    }

    @Override
    public void setYBoost(float boost) {
        yBoost = boost;
    }

    @Override
    public float getFallReduction() {
        return fallReduction;
    }

    @Override
    public void setFallReduction(float reduction) {
        fallReduction = reduction;
    }

    @Override
    public float getPitchOffset() {
        return pitchOffset;
    }

    @Override
    public void setPitchOffset(float offset) {
        pitchOffset = offset;
    }

    @Override
    public double getWindMultiplier() {
        return windMultiplier;
    }

    @Override
    public void setWindMultiplier(double windMultiplierToSetTo) {
        windMultiplier = windMultiplierToSetTo;
    }

    @Override
    public double getAirResistance() {
        return airResistance;
    }

    @Override
    public void setAirResistance(double airResistanceToSetTo) {
        airResistance = airResistanceToSetTo;
    }

    @Override
    public int getTotalDurability() {
        return totalDurability;
    }

    @Override
    public void setTotalDurability(int durability) {
        totalDurability = durability;
    }

    @Override
    public int getCurrentDurability(ItemStack stack) {
        return stack.getItemDamage();
    }

    @Override
    public void setCurrentDurability(ItemStack stack, int durability) {
        setDamage(stack, durability);
        if (stack.getItemDamage() < 1)
            stack.setItemDamage(1);
    }

    @Override
    public ArrayList<ItemStack> getUpgrades(ItemStack glider) {
        return GliderHelper.getUpgradesFromNBT(glider); //ToDo: too tightly tied, not API friendly
    }

    @Override
    public void removeUpgrade(ItemStack glider, ItemStack upgrade) {
        //ToDo
    }

    @Override
    public void addUpgrade(ItemStack glider, ItemStack upgrade) {
        //ToDo
    }

    @Override
    public ResourceLocation getModelTexture(ItemStack glider) {
        return modelRL;
    }

    @Override
    public void setModelTexture(ResourceLocation resourceLocation) {
        modelRL = resourceLocation;
    }


    @Override
    public NBTTagCompound serializeNBT() {
        return null; //ToDo
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        //ToDo
    }

    @Override
    public Item item() {
        return this;
    }

    @Override
    public String getModelName(int meta) {
        switch (meta) {
            case 1:
                return "hang_glider_basic_deployed";
            case 3:
                return "hang_glider_basic_broken";
            default:
                return "hang_glider_basic";
        }
    }
}
