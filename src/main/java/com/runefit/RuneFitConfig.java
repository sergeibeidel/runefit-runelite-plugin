package com.runefit;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(RuneFitConfig.GROUP)
public interface RuneFitConfig extends Config
{
	String GROUP = "runefit";

	@ConfigItem(
		keyName = "apiEndpoint",
		name = "RuneFit API endpoint",
		description = "The RuneFit endpoint used to generate single-use character link codes."
	)
	default String apiEndpoint()
	{
		return "http://127.0.0.1:8000/api/runelite/link-codes";
	}
}
