package com.clicktominimize;

import com.google.inject.Provides;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.swing.JFrame;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import static net.runelite.client.util.Text.removeTags;

@PluginDescriptor(
	name = "Click To Minimize"
)
public class ClickToMinimizePlugin extends Plugin implements KeyListener
{
	@Inject
	private Client client;
	@Inject
	private ClickToMinimizeConfig config;
	@Inject
	private KeyManager keyManager;
	@Inject
	private ChatMessageManager chatMessageManager;

	private final Map<String, List<String>> actionsMap = new HashMap<>();

	private boolean preventMinimize = false;

	@Override
	protected void startUp()
	{
		keyManager.registerKeyListener(this);
		updateActions();
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(this);
		actionsMap.clear();
		preventMinimize = false;
	}

	@Override
	public void keyTyped(final KeyEvent e)
	{
	}

	@Override
	public void keyPressed(final KeyEvent e)
	{
		if (config.minimizeKeybind().matches(e))
		{
			minimizeWindow();
			return;
		}

		if (config.holdToPreventMinimizeKeybind().matches(e))
		{
			preventMinimize = true;
		}
	}

	@Override
	public void keyReleased(final KeyEvent e)
	{
		if (config.holdToPreventMinimizeKeybind().matches(e))
		{
			preventMinimize = false;
		}
	}

	@Provides
	ClickToMinimizeConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(ClickToMinimizeConfig.class);
	}

	@Subscribe
	public void onConfigChanged(final ConfigChanged e)
	{
		if (!e.getGroup().equals(ClickToMinimizeConfig.CONFIG_GROUP))
		{
			return;
		}

		if (e.getKey().equals(ClickToMinimizeConfig.CONFIG_KEY_ACTIONS))
		{
			updateActions();
		}
	}

	@Subscribe
	public void onMenuOptionClicked(final MenuOptionClicked event)
	{
		if (preventMinimize || (config.ignoreFullInventory() && isInventoryFull()))
		{
			return;
		}

		var action = removeTags(event.getMenuOption());
		var target = removeTags(event.getMenuTarget());

		if (config.logPlayerActions())
		{
			chatMessageManager.queue(QueuedMessage.builder()
				.type(ChatMessageType.GAMEMESSAGE)
				.runeLiteFormattedMessage("[Click To Minimize] Logged action: <col=FF0000>" + action + ": " + target + "</col>")
				.build());
		}

		if (config.ignoreCase())
		{
			action = action.toLowerCase();
			target = target.toLowerCase();
		}

		for (final var entry : actionsMap.entrySet())
		{
			if (!action.equals(entry.getKey()))
			{
				continue;
			}

			if (target.isEmpty())
			{
				if (config.allowEmptyMenuTarget())
				{
					minimizeWindow();
				}
			}
			else
			{
				for (final var configTarget : entry.getValue())
				{
					if (target.contains(configTarget))
					{
						minimizeWindow();
						break;
					}
				}
			}

			break;
		}
	}

	private boolean isInventoryFull()
	{
		final var container = client.getItemContainer(InventoryID.INV);
		return container != null && container.count() >= 28;
	}

	private void minimizeWindow()
	{
		final var frame = (JFrame) javax.swing.SwingUtilities.getWindowAncestor(client.getCanvas());
		if (frame == null)
		{
			return;
		}

		if (config.sendToBack())
		{
			frame.toBack();
		}
		else
		{
			frame.setState(Frame.ICONIFIED);
		}
	}

	private void updateActions()
	{
		actionsMap.clear();

		for (final var action : config.actions().split(","))
		{
			final var parts = action.trim().replace("\\:", "[COLON]").split(":");
			if (parts.length != 2)
			{
				continue;
			}

			var key = parts[0].trim().replace("[COLON]", ":");
			var value = parts[1].trim();

			if (config.ignoreCase())
			{
				key = key.toLowerCase();
				value = value.toLowerCase();
			}

			actionsMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
		}
	}
}
