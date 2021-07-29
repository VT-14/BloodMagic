package wayoftime.bloodmagic.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ObjectHolder;
import wayoftime.bloodmagic.tile.container.ContainerTeleposer;
import wayoftime.bloodmagic.util.Constants;

public class TileTeleposer extends TileInventory implements INamedContainerProvider
{
	@ObjectHolder("bloodmagic:teleposer")
	public static TileEntityType<TileTeleposer> TYPE;
	private ITextComponent customName;

	public TileTeleposer(TileEntityType<?> type)
	{
		super(type, 1, "teleposer");
	}

	public TileTeleposer()
	{
		this(TYPE);
	}

	@Override
	public void deserialize(CompoundNBT tag)
	{
		super.deserialize(tag);

		customName = new StringTextComponent(tag.getString(Constants.NBT.CUSTOM_NAME));
	}

	@Override
	public CompoundNBT serialize(CompoundNBT tag)
	{
		super.serialize(tag);

		if (customName != null)
		{
			tag.putString(Constants.NBT.CUSTOM_NAME, customName.getString());
		}
		return tag;
	}

	public void setCustomName(ITextComponent name)
	{
		this.customName = name;
	}

	@Override
	public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMplenu_3_)
	{
		assert world != null;
		return new ContainerTeleposer(this, p_createMenu_1_, p_createMenu_2_);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return this.customName != null ? this.customName : new TranslationTextComponent("block.bloodmagic.teleposer");
	}
}
