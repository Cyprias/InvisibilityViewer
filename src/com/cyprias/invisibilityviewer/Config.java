package com.cyprias.invisibilityviewer;

import org.bukkit.configuration.Configuration;


public class Config {
	private InvisibilityViewer plugin;
	private static Configuration config;
	

	static Boolean checkNewVersionOnStartup, togglePlayerByDefault, toggleOtherByDefault, colouredConsoleMessages, distanceEnabled, debugMessages;
	static int distanceRadius;
	static long distanceFrequency;
	
	public Config(InvisibilityViewer plugin) {
		this.plugin = plugin;
		
		config = plugin.getConfig().getRoot();
		config.options().copyDefaults(true);
		plugin.saveConfig();
	}
	
	public void reloadOurConfig(){
		plugin.reloadConfig();
		config = plugin.getConfig().getRoot();
		loadConfigOpts();
	}
	private void loadConfigOpts(){
		checkNewVersionOnStartup = config.getBoolean("checkNewVersionOnStartup");
		togglePlayerByDefault = config.getBoolean("toggledByDefault.player");
		toggleOtherByDefault = config.getBoolean("toggledByDefault.other");
		colouredConsoleMessages = config.getBoolean("colouredConsoleMessages");
		
		distanceEnabled = config.getBoolean("distance.enabled");
		distanceRadius = config.getInt("distance.radius");
		distanceFrequency = config.getLong("distance.frequency");
		
		debugMessages = config.getBoolean("debugMessages");
	}
}
