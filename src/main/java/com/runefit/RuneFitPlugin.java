package com.runefit;

import com.google.gson.Gson;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import okhttp3.OkHttpClient;

@PluginDescriptor(
	name = "RuneFit",
	description = "Link the OSRS character currently logged into RuneLite with RuneFit.",
	tags = {"runefit", "fitness", "link", "account"}
)
public class RuneFitPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private OkHttpClient httpClient;

	@Inject
	private Gson gson;

	@Inject
	private RuneFitConfig config;

	private RuneFitPanel panel;
	private NavigationButton navigationButton;

	@Override
	protected void startUp()
	{
		panel = new RuneFitPanel(this::generateLinkCode);
		BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/runefit_icon.png");
		navigationButton = NavigationButton.builder()
			.tooltip("RuneFit")
			.icon(icon)
			.panel(panel)
			.priority(10)
			.build();
		clientToolbar.addNavigation(navigationButton);
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navigationButton);
		panel = null;
		navigationButton = null;
	}

	@SuppressWarnings("deprecation")
	private void generateLinkCode()
	{
		panel.showLoading();
		clientThread.invoke(() ->
		{
			try
			{
				String characterName = client.getLocalPlayer() == null ? null : client.getLocalPlayer().getName();
				LinkCodeRequest payload = LinkCodeRequestFactory.create(
					client.getGameState(),
					characterName,
					client.getAccountHash(),
					client.getVarbitValue(Varbits.ACCOUNT_TYPE)
				);

				new RuneFitClient(httpClient, gson, config.apiEndpoint()).createLinkCode(payload, new RuneFitClient.ResultCallback()
				{
					@Override
					public void onSuccess(LinkCodeResponse response)
					{
						RuneFitPanel currentPanel = panel;
						if (currentPanel != null)
						{
							currentPanel.showCode(response);
						}
					}

					@Override
					public void onFailure(String message)
					{
						RuneFitPanel currentPanel = panel;
						if (currentPanel != null)
						{
							currentPanel.showError(message);
						}
					}
				});
			}
			catch (IllegalStateException exception)
			{
				RuneFitPanel currentPanel = panel;
				if (currentPanel != null)
				{
					currentPanel.showError(exception.getMessage());
				}
			}
		});
	}

	@Provides
	RuneFitConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RuneFitConfig.class);
	}
}
