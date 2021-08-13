package wayoftime.bloodmagic.common.registration.impl;

import java.util.function.Supplier;

import net.minecraft.util.ResourceLocation;
import wayoftime.bloodmagic.common.item.TeleposerFocus;
import wayoftime.bloodmagic.common.registration.WrappedDeferredRegister;

public class TeleposerFocusDeferredRegister extends WrappedDeferredRegister<TeleposerFocus>
{
	public TeleposerFocusDeferredRegister(String modid)
	{
		super(modid, TeleposerFocus.class);
	}

	public TeleposerFocusRegistryObject<TeleposerFocus> register(String name, ResourceLocation rl, int range)
	{
		return register(name, () -> new TeleposerFocus(rl, range));
	}

	public <FOCUS extends TeleposerFocus> TeleposerFocusRegistryObject<FOCUS> register(String name, Supplier<? extends FOCUS> sup)
	{
		return register(name, sup, TeleposerFocusRegistryObject::new);
	}
}
