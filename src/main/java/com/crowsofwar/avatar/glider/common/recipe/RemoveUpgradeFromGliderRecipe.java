//package gr8pefish.openglider.common.recipe;
//
//import gr8pefish.openglider.common.lib.ModInfo;
//import gr8pefish.openglider.common.util.OpenGliderHelper;
//import net.minecraft.inventory.InventoryCrafting;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.nbt.NBTTagList;
//import net.minecraft.util.NonNullList;
//import net.minecraft.world.World;
//import net.minecraftforge.oredict.OreDictionary;
//import net.minecraftforge.oredict.ShapelessOreRecipe;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//import static gr8pefish.openglider.common.recipe.RecipeHelper.getFirstGliderInGridSlotNumber;
//
//public class RemoveUpgradeFromGliderRecipe extends ShapelessOreRecipe {
//
//    private ItemStack recipeOutput; //The outputted items after recipes
//
//    private ItemStack upgradeRemovedStack;
//
//    public RemoveUpgradeFromGliderRecipe(ItemStack recipeOutput, Object... items) {
//        super(recipeOutput, items);
//        this.recipeOutput = recipeOutput;
//    }
//
//    /**
//     * Crafts the glider by itself to remove an upgrade;
//     * First it checks if the glider has any upgrade.
//     * If it does it progresses, otherwise it returns null;
//     * <p/>
//     * Then it checks for where the glider is located in the recipes grid.
//     * It then removes the upgrade in said slot. So if it is in the 2nd slot then it removes the 2nd upgrade on the glider.
//     *
//     * @param inventoryCrafting - the inventory recipes to check
//     * @return - the resulting itemstack
//     */
//    @Override
//    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
//
//        int slotOfGlider = getFirstGliderInGridSlotNumber(inventoryCrafting);
//        if (slotOfGlider == -1) //if no glider
//            return null; //return no output
//
//        //get the glider
//        ItemStack glider = inventoryCrafting.getStackInSlot(slotOfGlider);
//        ItemStack result = glider.copy();
//
//        //get the upgrade
//        ArrayList<ItemStack> upgrades = OpenGliderHelper.getUpgradesFromNBT(result);
//        if (upgrades.isEmpty()) //no upgrade
//            return null; //no output itemStack, i.e. no recipes result
//
//        //get the old tag compound
//        NBTTagCompound nbtTagCompound = result.getTagCompound();
//        if (nbtTagCompound == null) {
//            nbtTagCompound = new NBTTagCompound();
//            nbtTagCompound.setTag(ModInfo.NBT_KEYS.UPGRADES, new NBTTagList());
//            result.setTagCompound(nbtTagCompound);
//        }
//
//        //make sure that we can check for an upgrade to remove
//        boolean nullChecksPassed = false;
//        ItemStack upgradeInQuestion = null;
//        if ((slotOfGlider <= (upgrades.size() - 1)) && (slotOfGlider >= 0) && (upgrades.get(slotOfGlider) != null)) {
//            upgradeInQuestion = upgrades.get(slotOfGlider);
//            nullChecksPassed = true;
//        }
//
//        //init variables for the return stack
//        boolean upgradeRemoved = false;
//        NBTTagList tagList = new NBTTagList();
//
//        for (ItemStack upgrade : upgrades) { //for each slot in possible upgrade
//            if (nullChecksPassed && (ItemStack.areItemStacksEqual(upgrade, upgradeInQuestion))) { //same upgrade, remove it
//                upgradeRemoved = true;
//                //not adding the old recipe is the same outcome as removing the recipe, so no code needed here
//            } else { //save old contents to new tag
//                tagList.appendTag(upgrade.writeToNBT(new NBTTagCompound()));
//            }
//        }
//
//        //set the new tag compound and return the new stack if it has changed
//        nbtTagCompound.setTag(ModInfo.NBT_KEYS.UPGRADES, tagList);
//        if (upgradeRemoved) {
//            upgradeRemovedStack = upgradeInQuestion;
//            return result;
//        } else {
//            upgradeRemovedStack = null;
//            return null;
//        }
//    }
//
//    @Override //copied directly from ShapelessOreRecipe
//    public boolean matches(InventoryCrafting var1, World world)
//    {
//        ArrayList<Object> required = new ArrayList<Object>(input);
//
//        for (int x = 0; x < var1.getSizeInventory(); x++)
//        {
//            ItemStack slot = var1.getStackInSlot(x);
//
//            if (slot != null)
//            {
//                boolean inRecipe = false;
//                Iterator<Object> req = required.iterator();
//
//                while (req.hasNext())
//                {
//                    boolean match = false;
//
//                    Object next = req.next();
//
//                    if (next instanceof ItemStack)
//                    {
//                        match = OreDictionary.itemMatches((ItemStack)next, slot, false);
//                    }
//                    else if (next instanceof List)
//                    {
//                        Iterator<ItemStack> itr = ((List<ItemStack>)next).iterator();
//                        while (itr.hasNext() && !match)
//                        {
//                            match = OreDictionary.itemMatches(itr.next(), slot, false);
//                        }
//                    }
//
//                    if (match)
//                    {
//                        inRecipe = true;
//                        required.remove(next);
//                        break;
//                    }
//                }
//
//                if (!inRecipe)
//                {
//                    return false;
//                }
//            }
//        }
//        return required.isEmpty();
//    }
//
//    @Override
//    public ItemStack getRecipeOutput() {
//        return recipeOutput;
//    }
//
////    @Override
////    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv){ //needs matches overridden due to (Forge?) bug
////        if (upgradeRemovedStack != null){
////            NonNullList<ItemStack> ret = new NonNullList<ItemStack>();
////            ret[0] = upgradeRemovedStack.copy();
////            for (int i = 1; i < ret.length; i++) {
////                ret[i] = null; //remove everything else (i.e can't leave glider)
////            }
////            return ret;
////        }else{
////            return super.getRemainingItems(inv);
////        }
////    }
//
//}
