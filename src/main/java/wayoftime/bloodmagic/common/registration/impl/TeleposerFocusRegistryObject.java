package wayoftime.bloodmagic.common.registration.impl;

import net.minecraftforge.fml.RegistryObject;
import wayoftime.bloodmagic.common.item.TeleposerFocus;
import wayoftime.bloodmagic.common.registration.WrappedRegistryObject;

public class TeleposerFocusRegistryObject<FOCUS extends TeleposerFocus> extends WrappedRegistryObject<FOCUS>
{

	protected TeleposerFocusRegistryObject(RegistryObject<FOCUS> registryObject)
	{
		super(registryObject);
	}

}
