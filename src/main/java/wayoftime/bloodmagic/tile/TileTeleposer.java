package wayoftime.bloodmagic.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ObjectHolder;
import wayoftime.bloodmagic.tile.container.ContainerTeleposer;

public class TileTeleposer extends TileInventory implements INamedContainerProvider
{
	@ObjectHolder("bloodmagic:teleposer")
	public static TileEntityType<TileTeleposer> TYPE;

	public TileTeleposer(TileEntityType<?> type)
	{
		super(type, 1, "teleposer");
	}

	public TileTeleposer()
	{
		this(TYPE);
	}

	@Override
	public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_)
	{
		assert world != null;
		return new ContainerTeleposer(this, p_createMenu_1_, p_createMenu_2_);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new StringTextComponent("Teleposer");
	}

}
