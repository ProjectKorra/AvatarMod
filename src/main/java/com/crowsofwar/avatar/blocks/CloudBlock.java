package com.crowsofwar.avatar.blocks;

import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.registry.AvatarItems;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class CloudBlock extends BlockBreakable {

    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);

    public CloudBlock() {
        super(Material.CLOTH, false);
        this.setCreativeTab(AvatarItems.tabItems);
        this.setTranslationKey("avatarmod:cloudblock");
        this.setHardness(1f);
        this.setLightOpacity(0);
        this.setLightLevel(15F);
        this.setResistance(10F);
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
            items.add(new ItemStack(this, 1, enumdyecolor.getMetadata()));
        }
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
        return this.getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(meta));
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
            ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Airbending()).clr(0.95F, 0.95F, 0.95F, 0.075F)
                    .pos(entityIn.posX, pos.getY() + 1, entityIn.posZ).scale(1.25F).time(25).spin(0.25F, 0.125).spawn(worldIn);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        Vec3d centre = AvatarEntityUtils.getMiddleOfBlock(state, worldIn, pos);
        if (worldIn.isRemote && centre != null)
            ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Airbending()).clr(0.95F, 0.95F, 0.95F, 0.075F)
                    .pos(centre).scale(1.5F).time(25).spin(0.25F, 0.125 * worldIn.rand.nextGaussian()).spawn(worldIn);
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

}

