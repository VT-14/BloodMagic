package wayoftime.bloodmagic.recipe;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import wayoftime.bloodmagic.common.recipe.BloodMagicRecipeType;
import wayoftime.bloodmagic.common.registries.BloodMagicRecipeSerializers;

public class RecipeBloodAltar extends BloodMagicRecipe
{
	@Nonnull
	private final Ingredient input;
	@Nonnull
	private final ItemStack output;
	@Nonnull
	private final int minimumTier;
	@Nonnegative
	private final int syphon;
	@Nonnegative
	private final int consumeRate;
	@Nonnegative
	private final int drainRate;

	public RecipeBloodAltar(ResourceLocation id, @Nonnull Ingredient input, @Nonnull ItemStack output, @Nonnegative int minimumTier, @Nonnegative int syphon, @Nonnegative int consumeRate, @Nonnegative int drainRate)
	{
		super(id);
		Preconditions.checkNotNull(input, "input cannot be null.");
		Preconditions.checkNotNull(output, "output cannot be null.");
		Preconditions.checkArgument(minimumTier >= 0, "minimumTier cannot be negative.");
		Preconditions.checkArgument(minimumTier <= 6, "minimumTier cannot be higher than max tier");
		Preconditions.checkArgument(syphon >= 0, "syphon cannot be negative.");
		Preconditions.checkArgument(consumeRate >= 0, "consumeRate cannot be negative.");
		Preconditions.checkArgument(drainRate >= 0, "drain cannot be negative.");

		this.input = input;
		this.output = output;
		this.minimumTier = minimumTier;
		this.syphon = syphon;
		this.consumeRate = consumeRate;
		this.drainRate = drainRate;
	}

	@Nonnull
	public final Ingredient getInput()
	{
		return input;
	}

	@Override
	public final NonNullList<Ingredient> getIngredients()
	{
		NonNullList<Ingredient> list = NonNullList.create();
		list.add(getInput());
		return list;
	}

	@Nonnull
	public final ItemStack getOutput()
	{
		return output;
	}

	public int getMinimumTier()
	{
		return minimumTier;
	}

	@Nonnegative
	public final int getSyphon()
	{
		return syphon;
	}

	@Nonnegative
	public final int getConsumeRate()
	{
		return consumeRate;
	}

	@Nonnegative
	public final int getDrainRate()
	{
		return drainRate;
	}

	@Override
	public void write(PacketBuffer buffer)
	{
		input.write(buffer);
		buffer.writeItemStack(output);
		buffer.writeInt(minimumTier);
		buffer.writeInt(syphon);
		buffer.writeInt(consumeRate);
		buffer.writeInt(drainRate);
	}

	@Override
	public IRecipeSerializer<RecipeBloodAltar> getSerializer()
	{
		return BloodMagicRecipeSerializers.ALTAR.getRecipeSerializer();
	}

	@Override
	public IRecipeType<RecipeBloodAltar> getType()
	{
		return BloodMagicRecipeType.ALTAR;
	}
}