package wayoftime.bloodmagic.compat.patchouli.processors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
	private List<List<Pair<Block, Integer>>> layers = new ArrayList<List<Pair<Block, Integer>>>();
	private List<Integer> layerTotalWeights = new ArrayList<Integer>();
	private int[] layerYSpace; // returns the needed height for the indexed layer. Used to determine how much
								// space the next layer needs.
	private final int ITEMSPERROW = 6;
	private final int ROWHEIGHT = 32;

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

				for (MeteorLayer layer : this.meteor.getLayerList())
				{
					int cumulativeWeight = 0;
					List<Pair<Block, Integer>> blocks = new ArrayList<Pair<Block, Integer>>();
					RandomBlockContainer shellBlockRBC = layer.shellBlock;
					if (shellBlockRBC != null)
						blocks.add(Pair.of(shellBlockRBC.getRandomBlock(world.rand, world), -2));
					Block fillBlock = layer.fillBlock.getRandomBlock(world.rand, world);
					if (fillBlock != null)
						blocks.add(Pair.of(fillBlock, -1));
					for (Pair<RandomBlockContainer, Integer> blockWeight : layer.weightList)
					{
						Block block = blockWeight.getLeft().getRandomBlock(world.rand, world);
						if (block != null)
						{
							int weight = blockWeight.getRight();
							blocks.add(Pair.of(block, weight));
							cumulativeWeight += weight;
						}
					}
					this.layers.add(blocks);
					this.layerTotalWeights.add(cumulativeWeight);
				}

				layerYSpace = new int[layers.size()];
				int cumulativeSpace = 0;
				for (int i = 0; i < layers.size(); i++)
				{
					List<Pair<Block, Integer>> layer = layers.get(i);
					int blockTypeCount = layer.size();
					layerYSpace[i] = (((blockTypeCount / this.ITEMSPERROW) + 1) * this.ROWHEIGHT) + 18 + cumulativeSpace;
					// # of rows * row height + static offset for header and spacing.
					cumulativeSpace = layerYSpace[i];
				}
			}
		}
		if (this.meteor == null)

		{
			LogManager.getLogger().warn("Guidebook missing Meteor recipe {}", id);
		}
	}

	@Override
	public IVariable process(String key)
	{
		if (this.meteor == null)
		{
			return null;

		} else if (key.startsWith("weight")) // weight0,1 -> layer 0, index 1 (2nd block)
		{
			int layerNo = Integer.parseInt(key.substring(6, 7));
			if (layerNo >= this.layers.size())
				return null;

			float output;
			int index = Integer.parseInt(key.substring(8));
			if (index >= this.layers.get(layerNo).size())
				return null;
			else
			{
				int layerTotalWeight = Math.max(this.layerTotalWeights.get(layerNo), this.meteor.getLayerList().get(layerNo).minWeight);
				int blockWeight = this.layers.get(layerNo).get(index).getRight() * 100;
				if (blockWeight == -200) // Shell
					return IVariable.wrap("Shell"); // TODO Translate Shell.
				else if (blockWeight == -100) // Fill
				{
					return IVariable.wrap("Fill");
				} else
					output = (float) blockWeight / (float) layerTotalWeight;
			}
			if (output >= 100)
				return IVariable.wrap("100%");
			else
				return IVariable.wrap(String.format("%2.1f%%", output));

		} else if (key.startsWith("block")) // block0,1 -> layer 0, index 1 (2nd block)
		{
			int layerNo = Integer.parseInt(key.substring(5, 6));
			if (layerNo >= this.layers.size())
				return null;
			List<Pair<Block, Integer>> layer = this.layers.get(layerNo);

			int index = Integer.parseInt(key.substring(7));
			if (index >= layer.size())
				return null;
			return IVariable.from(new ItemStack(layer.get(index).getLeft()));

		} else if (key.startsWith("y")) // y1,1 -> Layer 1, Row 1. Layer 0 is hard-coded in template (starts at 0).
		{
			int prevLayer = Integer.parseInt(key.substring(1, 2)) - 1; // Layer 1 needs Layer 0's height.
			int row = Integer.parseInt(key.substring(3));
			return IVariable.wrap(layerYSpace[prevLayer] + (row * this.ROWHEIGHT));

		} else if (key.startsWith("radius")) // radius0 -> radius of layer 0.
		{
			int layerNo = Integer.parseInt(key.substring(6));
			if (layerNo >= this.layers.size())
				return null;
			return IVariable.wrap(this.meteor.getLayerList().get(layerNo).layerRadius);

		} else if (key.startsWith("rowLength")) // rowLength0,1 -> Layer 0, Row 1 (2nd row)
		{ // It appears that the Template needs a Number, the variable call is a String,
			// so it quits before getting here.
			int output = 0; // 0 is blank. 32 is 1 box. 64 is 2 boxes. etc.
			int layerNo = Integer.parseInt(key.substring(10, 11));
			if (layerNo < this.layers.size())
			{
				List<Pair<Block, Integer>> layer = this.layers.get(layerNo);
				int rowNo = Integer.parseInt(key.substring(12));
				int divValue = layer.size() / this.ITEMSPERROW;
				int modValue = layer.size() % this.ITEMSPERROW;
				if (divValue == rowNo && modValue != 0)
					output = modValue * 32;
				else if (divValue == rowNo + 1 && modValue == 0)
					output = 192; // 6 * 32.
			}
			return IVariable.wrap(output);
		}

		switch (key)
		{
		case "input":
			return IVariable.wrapList(Arrays.stream(this.meteor.getInput().getMatchingStacks()).map(IVariable::from).collect(Collectors.toList()));
		case "syphon":
			return (IVariable.wrap(String.format("%,d", this.meteor.getSyphon())));
		case "textY":
			return IVariable.wrap(this.layerYSpace[this.layerYSpace.length - 1]);
		default:
			return null;
		}
	}
}
