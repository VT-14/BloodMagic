package wayoftime.bloodmagic.common.item;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import wayoftime.bloodmagic.core.data.Binding;
import wayoftime.bloodmagic.tile.TileTeleposer;
import wayoftime.bloodmagic.util.Constants;
import wayoftime.bloodmagic.util.helper.NBTHelper;

public class ItemTelepositionFocus extends ItemBindableBase
{
	private final Supplier<TeleposerFocus> sup;

	public ItemTelepositionFocus(Supplier<TeleposerFocus> sup)
	{
		this.sup = sup;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
		if (player.isSneaking())
		{
			RayTraceResult mop = rayTrace(world, player, FluidMode.NONE);

			if (mop != null && mop.getType() == RayTraceResult.Type.BLOCK)
			{
				setBlockPos(player.getHeldItem(hand), world, new BlockPos(mop.getHitVec()));
			}
		}

		return super.onItemRightClick(world, player, hand);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		if (!stack.hasTag())
			return;

		Binding binding = getBinding(stack);
		if (binding != null)
			tooltip.add(new TranslationTextComponent("tooltip.bloodmagic.currentOwner", binding.getOwnerName()).mergeStyle(TextFormatting.GRAY));
		BlockPos pos = getBlockPos(stack);
		CompoundNBT nbt = getNBT(stack);
		String dimensionID = nbt.getString(Constants.NBT.DIMENSION_ID);
		if (dimensionID.startsWith("minecraft:"))
		{
			dimensionID = dimensionID.substring(10); // remove "minecraft:" from displayed text.
		}

		// TODO: translations!
		tooltip.add(new TranslationTextComponent("Name: %s", nbt.getString(Constants.NBT.CUSTOM_NAME)).mergeStyle(TextFormatting.GRAY));
		tooltip.add(new TranslationTextComponent("Coordinates: %s, %s, %s", pos.getX(), pos.getY(), pos.getZ()).mergeStyle(TextFormatting.GRAY));
		tooltip.add(new TranslationTextComponent("Dimension: %s", dimensionID).mergeStyle(TextFormatting.GRAY));
	}

	public ItemStack setBlockPos(ItemStack stack, World world, BlockPos pos)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileTeleposer)
		{
			CompoundNBT nbt = getNBT(stack);
			nbt.putInt(Constants.NBT.X_COORD, pos.getX());
			nbt.putInt(Constants.NBT.Y_COORD, pos.getY());
			nbt.putInt(Constants.NBT.Z_COORD, pos.getZ());
			nbt.putString(Constants.NBT.CUSTOM_NAME, ((TileTeleposer) tile).getDisplayName().getString());
			ResourceLocation dimensionRL = world.getDimensionKey().getLocation();
			nbt.putString(Constants.NBT.DIMENSION_ID, dimensionRL.toString());
		}
		return stack;
	}

	public BlockPos getBlockPos(ItemStack stack)
	{
		CompoundNBT nbt = getNBT(stack);
		if (nbt.contains(Constants.NBT.X_COORD) && nbt.contains(Constants.NBT.Y_COORD) && nbt.contains(Constants.NBT.Z_COORD))
		{
			return new BlockPos(nbt.getInt(Constants.NBT.X_COORD), nbt.getInt(Constants.NBT.Y_COORD), nbt.getInt(Constants.NBT.Z_COORD));
		} else
			return null;
	}

	public World getWorld(ItemStack stack)
	{
		CompoundNBT nbt = getNBT(stack);
		String dimesionID = nbt.getString(Constants.NBT.DIMENSION_ID);
		ResourceLocation dimensionRL = ResourceLocation.tryCreate(dimesionID);
		World test;
		return null;
	}

	public CompoundNBT getNBT(ItemStack stack)
	{
		stack = NBTHelper.checkNBT(stack);
		return stack.getTag();
	}
}
