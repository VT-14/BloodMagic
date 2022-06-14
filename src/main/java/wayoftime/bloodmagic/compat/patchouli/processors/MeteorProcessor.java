package wayoftime.bloodmagic.compat.patchouli.processors;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;

import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import wayoftime.bloodmagic.common.meteor.MeteorLayer;
import wayoftime.bloodmagic.common.recipe.BloodMagicRecipeType;
import wayoftime.bloodmagic.recipe.RecipeMeteor;

/*
 * Example Page:
 * 
 * {
 *   "type": "meteor_recipe",	// Corresponding Template.
 *   "heading": "Title",	// (Optional) Title.
 *   "recipe": "recipe_id",	// Recipe ID.
 *   "text": "Extra text."	// (Optional) Extra text to go under the entry.
 * }
 */

public class MeteorProcessor implements IComponentProcessor
{
	private RecipeMeteor meteor; // The Meteor's "Recipe."
	private String extraText = ""; // (Optional) Text to insert at the end of the entry.
	private String[] rowState = new String[7]; // "header", "list", or null?

	@Override
	public void setup(IVariableProvider variables)
	{
		ResourceLocation id = new ResourceLocation(variables.get("recipe").asString());
		Optional<? extends IRecipe<?>> recipeHandler = Minecraft.getInstance().world.getRecipeManager().getRecipe(id);
		if (recipeHandler.isPresent())
		{
			IRecipe<?> recipe = recipeHandler.get();
			if (recipe.getType().equals(BloodMagicRecipeType.METEOR))
			{
				this.meteor = (RecipeMeteor) recipe;

				List<MeteorLayer> layerList = this.meteor.getLayerList();
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
		} else
		{
			return null;
		}
	}

}
