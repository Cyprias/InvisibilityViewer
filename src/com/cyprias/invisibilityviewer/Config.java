package com.cyprias.invisibilityviewer;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;


public class Config {
	//private static JavaPlugin plugin;
	private static Configuration config;
	

	static Boolean checkNewVersionOnStartup, togglePlayerByDefault, toggleOtherByDefault, distanceEnabled, debugMessages, debugNoIntercept;
	static int distanceRadius;
	static long distanceFrequency;
	
	public Config(JavaPlugin plugin) {
		config = plugin.getConfig().getRoot();
		config.options().copyDefaults(true);
		plugin.saveConfig();
	}
	
	public static void reloadOurConfig(JavaPlugin plugin){
		plugin.reloadConfig();
		config = plugin.getConfig().getRoot();
		loadConfigOpts();
	}
	private static void loadConfigOpts(){
		checkNewVersionOnStartup = config.getBoolean("checkNewVersionOnStartup");
		togglePlayerByDefault = config.getBoolean("toggledByDefault.player");
		toggleOtherByDefault = config.getBoolean("toggledByDefault.other");
		
		distanceEnabled = config.getBoolean("distance.enabled");
		distanceRadius = config.getInt("distance.radius");
		distanceFrequency = config.getLong("distance.frequency");
		
		debugMessages = config.getBoolean("debugMessages");
		debugNoIntercept = config.getBoolean("debugNoIntercept");
	}
}
