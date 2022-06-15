package wayoftime.bloodmagic.compat.patchouli.processors;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import wayoftime.bloodmagic.common.meteor.MeteorLayer;
import wayoftime.bloodmagic.common.meteor.RandomBlockContainer;
import wayoftime.bloodmagic.common.recipe.BloodMagicRecipeType;
import wayoftime.bloodmagic.recipe.RecipeMeteor;

/*
 * Example Page:
 * 
 * {
 *   "type": "bloodmagic:meteor",	// Corresponding Template.
 *   "heading": "Title",	// (Optional) Title.
 *   "recipe": "recipe_id",	// Recipe ID.
 *   "text": "Extra text."	// (Optional) Extra text to go under the entry.
 * }
 */

public class MeteorProcessor implements IComponentProcessor
{
	private World world = Minecraft.getInstance().world;
	private RecipeMeteor meteor; // The Meteor's "Recipe."
	private String extraText = ""; // (Optional) Text to insert at the end of the entry.
	List<MeteorLayer> layerList;
	private int[] layerYValue; // index is the layer. Value is spacing needed between layers.
	private final int ITEMSPERROW = 6;

	@Override
	public void setup(IVariableProvider variables)
	{
		ResourceLocation id = new ResourceLocation(variables.get("recipe").asString());
		Optional<? extends IRecipe<?>> recipeHandler = world.getRecipeManager().getRecipe(id);
		if (recipeHandler.isPresent())
		{
			IRecipe<?> recipe = recipeHandler.get();
			if (recipe.getType().equals(BloodMagicRecipeType.METEOR))
			{
				this.meteor = (RecipeMeteor) recipe;

				layerList = this.meteor.getLayerList();
				this.layerYValue = new int[layerList.size()];

				for (int i : layerYValue)
				{
					MeteorLayer layer = layerList.get(i);
					int blockTypeCount = layer.weightList.size();
					layerYValue[i] = (((blockTypeCount / ITEMSPERROW) + 1) * 18) + 18;
				}
			}
		}
		if (this.meteor == null)
		{
			LogManager.getLogger().warn("Guidebook missing Meteor recipe {}", id);
		}

		if (variables.has("text"))
		{
			extraText = variables.get("text").asString();
		}

	}

	@Override
	public IVariable process(String key)
	{
		if (this.meteor == null)
		{
			return null;
		} else if (key.startsWith("item,")) // item,0,1,2 -> Layer 0, Row 1, Column 2.
		{
			int layerNo = Integer.parseInt(key.substring(5, 6));
			if (layerNo >= layerList.size())
				return null;
			MeteorLayer layer = layerList.get(layerNo);
			int index = (Integer.parseInt(key.substring(7, 8)) * ITEMSPERROW) + Integer.parseInt(key.substring(9, 10));
			if (index >= layer.weightList.size())
				return null;

			List<Pair<RandomBlockContainer, Integer>> weightList = layer.weightList;
			Say(weightList.toString());
			Pair<RandomBlockContainer, Integer> pair = weightList.get(index);
			Say(pair.toString());
			RandomBlockContainer rbc = pair.getKey();
			Say(rbc.toString());
			Block block = rbc.getRandomBlock(world.rand, world);
			// Say(block.toString());
			ItemStack itemStack = new ItemStack(block);
			Say(itemStack.toString());
			return IVariable.from(itemStack);

			// TO-DO: Skip "air" entries. Will probably need to handle in setup().

		} else if (key.startsWith("Y,")) // Y,0,1 -> Layer 0, Row 1.
		{
			return null;
		} else
		{
			return null;
		}
	}

	private void Say(String str)
	{
		System.out.println(String.format("VT Test: %s", str));
	}
}
