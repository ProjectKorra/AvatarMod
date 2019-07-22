//package gr8pefish.openglider.common.recipe;
//
//import gr8pefish.openglider.common.lib.ModInfo;
//import gr8pefish.openglider.common.util.OpenGliderHelper;
//import net.minecraft.inventory.InventoryCrafting;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.nbt.NBTTagList;
//import net.minecraftforge.oredict.ShapelessOreRecipe;
//
//import java.util.ArrayList;
//
//import static gr8pefish.openglider.common.recipe.RecipeHelper.getFirstUpgradableGlider;
//import static gr8pefish.openglider.common.recipe.RecipeHelper.getFirstUpgrade;
//
///**
// * Allows an upgrade to be added to the glider.
// *
// * The current upgrade are:
// * Compass - adds a directional GUI while deployed
// */
////ToDo: Add "Upgrades" to API somehow
//public class AddUpgradeToGliderRecipe extends ShapelessOreRecipe {
//
//    private final ItemStack recipeOutput; //The outputted items after recipes
//
//    public AddUpgradeToGliderRecipe(ItemStack recipeOutput, Object... items){
//        super(recipeOutput, items);
//        this.recipeOutput = recipeOutput;
//    }
//
//    //ToDo: Description
//    /**
//     * Crafts the backpack with the upgrade, with some special cases recognized.
//     * First it checks if the backpack has enough upgrade points available to apply said upgrade to the backpack.
//     * If it has enough points available it progresses, otherwise it returns null;
//     *
//     * Then it checks for special cases, listed below:
//     * You can't have more than the config amount of 'additional upgrade points' upgrade applied.
//     * You can't have conflicting upgrade (as defined by each IConflictingUpgrade).
//     * You can only have a certain amount of alternate gui upgrade. Currently 'hardcoded' as 4, see IronBackpacksConstants.Upgrades.ALT_GUI_UPGRADES_ALLOWED
//     *
//     * @param inventoryCrafting - the inventory recipes to check
//     * @return - the resulting itemstack
//     */
//    @Override
//    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
//
//        ItemStack glider = getFirstUpgradableGlider(inventoryCrafting); //get the upgradable glider in the recipes grid
//        if (glider == null || glider.isEmpty()) return null; //if no valid glider return nothing
//        ItemStack result = glider.copy(); //the resulting glider, copied so it's data can be more easily manipulated
//
//        ArrayList<ItemStack> upgrades = OpenGliderHelper.getUpgradesFromNBT(result); //get the upgrade
//
//        ItemStack upgradeToApply = getFirstUpgrade(inventoryCrafting); //get the upgrade the player is attempting to apply to the glider
//
//        //initialize if it isn't present
//        NBTTagCompound nbtTagCompound = result.getTagCompound();
//        if (nbtTagCompound == null){
//            nbtTagCompound = new NBTTagCompound();
//            nbtTagCompound.setTag(ModInfo.NBT_KEYS.UPGRADES, new NBTTagList());
//            result.setTagCompound(nbtTagCompound);
//        }
//
//        boolean upgradeFound = false; //too determine if you need to return a new glider in the output slot
//        NBTTagList tagList = new NBTTagList(); //the upgrade data base tag
//
//        if (upgradeToApply != null && !upgradeToApply.isEmpty()) { //if have more than zero upgrade slots
//            if (upgrades.isEmpty()) { //no upgrades
//                tagList.appendTag(upgradeToApply.writeToNBT(new NBTTagCompound())); //save the new upgrade
//                upgradeFound = true; //you applied an upgrade, congratulations
//            } else {
//                if (!RecipeHelper.containsStack(upgrades, upgradeToApply)) { //if not already present //ToDo test
//                    for (ItemStack upgrade : upgrades) { //for each upgrade in possible upgrade
//                        tagList.appendTag(upgrade.writeToNBT(new NBTTagCompound())); //save old contents to new tag (transfer over the data, essentially)
//                    }
//                    tagList.appendTag(upgradeToApply.writeToNBT(new NBTTagCompound())); //save the new upgrade
//                    upgradeFound = true; //you applied an upgrade, congratulations
//                }
//            }
//        }
//
//        nbtTagCompound.setTag(ModInfo.NBT_KEYS.UPGRADES, tagList); //set the tag with all the upgrade that were just updated
//        if (upgradeFound) { //if you applied an upgrade
//            return result; //return the new glider
//        } else { //otherwise
//            return ItemStack.EMPTY; //return nothing
//        }
//
//    }
//
//    @Override
//    public ItemStack getRecipeOutput() {
//        return recipeOutput;
//    }
//
//}
