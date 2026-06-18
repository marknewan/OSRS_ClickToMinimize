package com.clicktominimize;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup(ClickToMinimizeConfig.CONFIG_GROUP)
public interface ClickToMinimizeConfig extends Config
{
	String CONFIG_GROUP = "ClickToMinimize";

	String CONFIG_KEY_ACTIONS = "actions";

	@ConfigItem(
		keyName = CONFIG_KEY_ACTIONS,
		name = "List of Actions to Minimize",
		description = "Enter actions in the format 'Action: Target, Action: Target'" +
			"<br>Example:<br>Pick-Fruit: Sq'irk tree,<br>Mine: Crashed Star"
	)
	default String actions()
	{
		return "--- Examples: ---, \n" +
			"#Pick-Fruit: Sq'irk tree, \n" +
			"#Mine: Crashed Star, \n" +
			"#Bait: Rod Fishing spot,\n" +
			"#Use: Hammer -> Infernal eel,\n" +
			"#Make: yew longbow,\n" +
			"#Make: Super defence(3),\n" +
			"#Attack: Guard,\n" +
			"\n\n" +
			"--- SPECIAL CASE ---\n" +
			"If an action has action::target, then " +
			"you need to add a \\:: to make it work. E.g. \n" +
			"#Make sets\\:: Cannonballs";
	}

	@ConfigItem(
		keyName = "ignoreCase",
		name = "Ignore Case",
		description = "Ignore text capitalization when matching actions and targets.",
		position = 2
	)
	default boolean ignoreCase()
	{
		return false;
	}

	@ConfigItem(
		keyName = "ignoreFullInventory",
		name = "Don't Minimize on Full Inventory",
		description = "Prevents minimizing window when inventory is full.",
		position = 3
	)
	default boolean ignoreFullInventory()
	{
		return false;
	}

	@ConfigItem(
		keyName = "allowEmptyMenuTarget",
		name = "Allow No Targets",
		description = "Used for menu options without a target." +
			"<br>e.g. at the Grand Exchange:" +
			"<br>'Confirm: x' will make it so that when you press the confirm after inputting a trade, it will minimize.",
		position = 4
	)
	default boolean allowEmptyMenuTarget()
	{
		return false;
	}

	@ConfigItem(
		keyName = "minimizeKeybind",
		name = "Minimize Keybind",
		description = "Keybind to minimize the screen.",
		position = 5
	)
	default Keybind minimizeKeybind()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		keyName = "holdToPreventMinimizeKeybind",
		name = "Prevent Minimize Keybind",
		description = "Hold this key to prevent the window from minimizing when clicking." +
			"<br>WARNING: Changing this to shift will prevent 'shift drop' items.",
		position = 6
	)
	default Keybind holdToPreventMinimizeKeybind()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		keyName = "logPlayerActions",
		name = "Log Player Actions",
		description = "If enabled, logs every action taken by the player to the game window."
			+ "<br>This helps to figure out how to enable some actions, as they're not always clear.",
		position = 7
	)
	default boolean logPlayerActions()
	{
		return false;
	}

	@ConfigItem(
		keyName = "sendToBack",
		name = "Send to Back",
		description = "Sends the window to the back instead of minimizing it.",
		position = 8
	)
	default boolean sendToBack()
	{
		return false;
	}
}
