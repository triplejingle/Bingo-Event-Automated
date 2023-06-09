package com.bingoeventautomated;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BingoEventAutomatedPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BingoEventAutomatedPlugin.class);
		RuneLite.main(args);
	}
}