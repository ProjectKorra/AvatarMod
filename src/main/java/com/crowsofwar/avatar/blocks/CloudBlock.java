package com.crowsofwar.avatar.blocks;

import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.registry.AvatarItems;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@SuppressWarnings("ALL")
public class CloudBlock extends BlockBreakable {

    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);

    public CloudBlock() {
        super(Material.GLASS, false);
        this.setCreativeTab(AvatarItems.tabItems);
        //Placeholder
        this.setTranslationKey("avatarmod:cloudblock");
        this.setSoundType(SoundType.CLOTH);
        this.setDefaultState(this.blockState.getBaseState().withProperty(COLOR, EnumDyeColor.WHITE));
    }

    /**
     * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
     * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
     */
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(IBlockState state) {
        return state.getValue(COLOR).getMetadata();
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumDyeColor enumdyecolor : EnumDyeColor.values()) {
            items.add(new ItemStack(this, 1, enumdyecolor.getMetadata()).setStackDisplayName(getNameFromDye(enumdyecolor)));
        }
    }

    public String getNameFromDye(EnumDyeColor dyeColor) {
        String name;
        if (dyeColor == EnumDyeColor.LIGHT_BLUE) {
            name = "Light Blue Cloud Block";
        }
        else {
            StringBuilder dyeName = new StringBuilder(dyeColor.getTranslationKey());
            char letter = dyeName.charAt(0);
            String firstLetter = String.valueOf(letter).toUpperCase();
            dyeName.deleteCharAt(0);
            dyeName.replace(0, 0, firstLetter);
            name = dyeName.toString() + " Cloud Block";
        }
        return name;
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     *
     * @deprecated call via {@link IBlockState#getMapColor(IBlockAccess, BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return MapColor.getBlockColor(state.getValue(COLOR));
    }


    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random) {
        return 0;
    }

    protected boolean canSilkHarvest() {
        return true;
    }

    /**
     * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
     */
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        return this.blockState.getBaseState().withProperty(COLOR, EnumDyeColor.byMetadata(meta));
    }

    /**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            BlockBeacon.updateColorAsync(worldIn, pos);
        }
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            BlockBeacon.updateColorAsync(worldIn, pos);
        }
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return state.getValue(COLOR).getMetadata();
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, COLOR);
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        super.onEntityWalk(worldIn, pos, entityIn);
        if (worldIn.isRemote)
            for (int i = 0; i < 3; i++) {
                float[] colours = worldIn.getBlockState(pos).getValue(COLOR).getColorComponentValues();
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(BendingStyles.get(Airbending.ID)).clr(0.5F + 0.5F * colours[0],
                        0.5F + 0.5F * colours[1], 0.5F + 0.5F * colours[2], 0.075F)
                        .pos(entityIn.posX, pos.getY() + 1, entityIn.posZ).time(15).vel(worldIn.rand.nextGaussian() / 20,
                        worldIn.rand.nextDouble() / 100, worldIn.rand.nextGaussian() / 20).spin(0.25F, 0.125).spawn(worldIn);
            }
    }


    @Override
    public int tickRate(World worldIn) {
        return 3;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return false;
    }

    //Frick MC
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return blockState.getBaseState().withProperty(COLOR, EnumDyeColor.byMetadata(placer.getHeldItemMainhand().getMetadata()));
    }
}

