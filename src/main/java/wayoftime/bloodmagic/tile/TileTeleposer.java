package wayoftime.bloodmagic.tile;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;
import wayoftime.bloodmagic.common.block.BlockTeleposer;
import wayoftime.bloodmagic.common.item.ItemTelepositionFocus;
import wayoftime.bloodmagic.core.data.Binding;
import wayoftime.bloodmagic.tile.container.ContainerTeleposer;
import wayoftime.bloodmagic.util.Constants;

public class TileTeleposer extends TileInventory implements INamedContainerProvider, INameable, ITickableTileEntity
{

	private int previousInput;

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
		if (tag.contains(Constants.NBT.CUSTOM_NAME))
		{
			customName = ITextComponent.Serializer.getComponentFromJson(tag.getString(Constants.NBT.CUSTOM_NAME));
		}
	}

	@Override
	public CompoundNBT serialize(CompoundNBT tag)
	{
		super.serialize(tag);
		if (customName != null)
		{
			tag.putString(Constants.NBT.CUSTOM_NAME, ITextComponent.Serializer.toJson(customName));
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

	@Override
	public ITextComponent getName()
	{
		return getDisplayName();
	}

	@Override
	@Nullable
	public ITextComponent getCustomName()
	{
		return customName;
	}

	@Override
	public void tick()
	{
		if (!world.isRemote() && canInitiateTeleport())
		{
			int currentInput = world.getStrongPower(pos);

			if (previousInput == 0 && currentInput > 0)
			{
				initiateTeleport();
			}

			previousInput = currentInput;

//			// Something command related from 1.12?
//			if (world.getTotalWorldTime() % 100 == 0)
//			{
//				ItemStack focusStack = getStackInSlot(0);
//				if (!focusStack.isEmpty())
//				{
//					if (((ItemTelepositionFocus) focusStack.getItem()).getBinding(focusStack) != null)
//						SubCommandTeleposer.teleposerSet.add(this);
//					else
//						SubCommandTeleposer.teleposerSet.remove(this);
//				} else
//					SubCommandTeleposer.teleposerSet.remove(this);
//			}
		}
	}

	private boolean canInitiateTeleport()
	{
		ItemStack focusStack = getStackInSlot(0);
		return !focusStack.isEmpty() && focusStack.getItem() instanceof ItemTelepositionFocus && ((ItemTelepositionFocus) focusStack.getItem()).getBinding(focusStack) != null;
	}

	private void initiateTeleport()
	{
		if (!world.isRemote && canInitiateTeleport() && this.getBlockState().getBlock() instanceof BlockTeleposer)
		{
			ItemStack focusStack = getStackInSlot(0);
			ItemTelepositionFocus focus = (ItemTelepositionFocus) focusStack.getItem();
			Binding binding = focus.getBinding(focusStack);
			if (binding == null)
				return;
			BlockPos focusPos = focus.getBlockPos(focusStack);
			World focusWorld = focus.getWorld(focusStack, world); // world = Tile Entity's world.
			if (focusWorld == null)
				return;

			TileEntity boundTile = focusWorld.getTileEntity(focusPos);
			if (boundTile instanceof TileTeleposer && boundTile != this)
			{
				final int focusRange = ((ItemTelepositionFocus) focusStack.getItem()).getRange();
				final int lpToBeDrained = (int) (0.5F * Math.sqrt((pos.getX() - focusPos.getX()) * (pos.getX() - focusPos.getX()) + (pos.getY() - focusPos.getY() + 1) * (pos.getY() - focusPos.getY() + 1) + (pos.getZ() - focusPos.getZ()) * (pos.getZ() - focusPos.getZ())));

			}
		}

//
//            TileEntity boundTile = focusWorld.getTileEntity(focusPos);
//            if (boundTile instanceof TileTeleposer && boundTile != this) {
//                final int focusLevel = (focusStack.getItemDamage() + 1);
//                final int lpToBeDrained = (int) (0.5F * Math.sqrt((pos.getX() - focusPos.getX()) * (pos.getX() - focusPos.getX()) + (pos.getY() - focusPos.getY() + 1) * (pos.getY() - focusPos.getY() + 1) + (pos.getZ() - focusPos.getZ()) * (pos.getZ() - focusPos.getZ())));
//
//                if (NetworkHelper.syphonFromContainer(focusStack, SoulTicket.block(world, pos, lpToBeDrained * (focusLevel * 2 - 1) * (focusLevel * 2 - 1) * (focusLevel * 2 - 1)))) {
//                    int blocksTransported = 0;
//
//                    for (int i = -(focusLevel - 1); i <= (focusLevel - 1); i++) {
//                        for (int j = 0; j <= (focusLevel * 2 - 2); j++) {
//                            for (int k = -(focusLevel - 1); k <= (focusLevel - 1); k++) {
//                                TeleposeEvent event = new TeleposeEvent(getWorld(), pos.add(i, 1 + j, k), focusWorld, focusPos.add(i, 1 + j, k));
//                                if (!MinecraftForge.EVENT_BUS.post(event) && Utils.swapLocations(event.initalWorld, event.initialBlockPos, event.finalWorld, event.finalBlockPos)) {
//                                    blocksTransported++;
//                                }
//                            }
//                        }
//                    }
//
//                    NetworkHelper.syphonFromContainer(focusStack, SoulTicket.item(focusStack, world, pos, lpToBeDrained * blocksTransported));
//
//                    List<Entity> originalWorldEntities;
//                    List<Entity> focusWorldEntities;
//                    AxisAlignedBB originalArea = new AxisAlignedBB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, Math.min(focusWorld.getHeight(), pos.getY() + 2 * focusLevel), pos.getZ() + 1).expand(focusLevel - 1, 0, focusLevel - 1);
//                    originalWorldEntities = getWorld().getEntitiesWithinAABB(Entity.class, originalArea);
//                    AxisAlignedBB focusArea = new AxisAlignedBB(focusPos.getX(), focusPos.getY() + 1, focusPos.getZ(), focusPos.getX() + 1, Math.min(focusWorld.getHeight(), focusPos.getY() + 2 * focusLevel), focusPos.getZ() + 1).expand(focusLevel - 1, 0, focusLevel - 1);
//                    focusWorldEntities = focusWorld.getEntitiesWithinAABB(Entity.class, focusArea);
//                    UUID bindingOwnerID = binding.getOwnerId();
//                    if (focusWorld.equals(getWorld())) {
//                        if (!originalWorldEntities.isEmpty()) {
//                            for (Entity entity : originalWorldEntities) {
//                                TeleportQueue.getInstance().addITeleport(new Teleports.TeleportSameDim(new BlockPos(entity.posX - pos.getX() + focusPos.getX(), entity.posY - pos.getY() + focusPos.getY(), entity.posZ - pos.getZ() + focusPos.getZ()), entity, bindingOwnerID, true));
//                            }
//                        }
//
//                        if (!focusWorldEntities.isEmpty()) {
//                            for (Entity entity : focusWorldEntities) {
//                                TeleportQueue.getInstance().addITeleport(new Teleports.TeleportSameDim(new BlockPos(entity.posX - focusPos.getX() + pos.getX(), entity.posY - focusPos.getY() + pos.getY(), entity.posZ - focusPos.getZ() + pos.getZ()), entity, bindingOwnerID, true));
//                            }
//                        }
//
//                    } else {
//                        if (!originalWorldEntities.isEmpty()) {
//                            for (Entity entity : originalWorldEntities) {
//                                TeleportQueue.getInstance().addITeleport(new Teleports.TeleportToDim(new BlockPos(entity.posX - pos.getX() + focusPos.getX(), entity.posY - pos.getY() + focusPos.getY(), entity.posZ - pos.getZ() + focusPos.getZ()), entity, bindingOwnerID, getWorld(), focusWorld.provider.getDimension(), true));
//                            }
//                        }
//
//                        if (!focusWorldEntities.isEmpty()) {
//                            for (Entity entity : focusWorldEntities) {
//                                TeleportQueue.getInstance().addITeleport(new Teleports.TeleportToDim(new BlockPos(entity.posX - focusPos.getX() + pos.getX(), entity.posY - focusPos.getY() + pos.getY(), entity.posZ - focusPos.getZ() + pos.getZ()), entity, bindingOwnerID, focusWorld, getWorld().provider.getDimension(), true));
//                            }
//                        }
//                    }
//                }
//            }
//        }
	}
}
