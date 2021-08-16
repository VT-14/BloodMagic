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
		CompoundNBT nbt = stack.getTag();
		if (nbt.contains(Constants.NBT.X_COORD)) // Has coordinates set.
		{
			int x = nbt.getInt(Constants.NBT.X_COORD);
			int y = nbt.getInt(Constants.NBT.Y_COORD);
			int z = nbt.getInt(Constants.NBT.Z_COORD);

			// TODO: translations!
			tooltip.add(new TranslationTextComponent("Name: %s", nbt.getString(Constants.NBT.CUSTOM_NAME)).mergeStyle(TextFormatting.GRAY));
			tooltip.add(new TranslationTextComponent("Coordinates: %s, %s, %s", x, y, z).mergeStyle(TextFormatting.GRAY));
			tooltip.add(new TranslationTextComponent("Dimension: %s", nbt.getString(Constants.NBT.DIMENSION_ID)).mergeStyle(TextFormatting.GRAY));
		}
	}

	public ItemStack setBlockPos(ItemStack stack, World world, BlockPos pos)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileTeleposer)
		{
			stack = NBTHelper.checkNBT(stack);
			CompoundNBT nbt = stack.getTag();
			nbt.putInt(Constants.NBT.X_COORD, pos.getX());
			nbt.putInt(Constants.NBT.Y_COORD, pos.getY());
			nbt.putInt(Constants.NBT.Z_COORD, pos.getZ());
			nbt.putString(Constants.NBT.CUSTOM_NAME, ((TileTeleposer) tile).getDisplayName().getString());
			ResourceLocation dimensionRL = world.getDimensionKey().getLocation();
			if (dimensionRL.getNamespace() == "minecraft") // Exclude "minecraft:" from vanilla dimensions.
			{
				nbt.putString(Constants.NBT.DIMENSION_ID, dimensionRL.getPath().toString());
			} else
			{
				nbt.putString(Constants.NBT.DIMENSION_ID, dimensionRL.toString());
			}
		}
		return stack;
	}
}
