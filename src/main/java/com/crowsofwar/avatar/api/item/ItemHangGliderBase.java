package com.crowsofwar.avatar.api.item;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.helper.GliderPlayerHelper;
import com.crowsofwar.avatar.common.item.AvatarItem;
import com.crowsofwar.avatar.common.network.packets.glider.PacketCUpdateClientTarget;
import com.crowsofwar.avatar.common.util.GliderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.crowsofwar.avatar.api.helper.GliderHelper.getIsGliderDeployed;
import static com.crowsofwar.avatar.api.helper.GliderHelper.setIsGliderDeployed;

public class ItemHangGliderBase extends Item implements IGlider, AvatarItem {

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

    public ItemHangGliderBase(float minSpeed, float maxSpeed, float pitchOffset, float yBoost, float fallReduction, double windMultiplier, double airResistance, int totalDurability, ResourceLocation modelRL) {
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
                AvatarMod.network.sendToServer(new PacketCUpdateClientTarget(player, getIsGliderDeployed(player)));
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
