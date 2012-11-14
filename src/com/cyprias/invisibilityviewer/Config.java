package com.cyprias.invisibilityviewer;

import org.bukkit.configuration.Configuration;


public class Config {
	private InvisibilityViewer plugin;
	private static Configuration config;
	

	static Boolean checkNewVersionOnStartup, togglePlayerByDefault, toggleOtherByDefault, colouredConsoleMessages, distanceEnabled;
	static int distanceRadius;
	static long distanceFrequentcy;
	
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
		distanceFrequentcy = config.getLong("distance.frequentcy");
	}
}
