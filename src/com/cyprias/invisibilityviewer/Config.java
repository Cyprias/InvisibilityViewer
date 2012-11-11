package com.cyprias.invisibilityviewer;

import org.bukkit.configuration.Configuration;


public class Config {
	private InvisibilityViewer plugin;
	private static Configuration config;
	

	static Boolean checkNewVersionOnStartup;


	public Config(InvisibilityViewer plugin) {
		this.plugin = plugin;
		config = plugin.getConfig().getRoot();
		config.options().copyDefaults(true);
		plugin.saveConfig();

		checkNewVersionOnStartup = config.getBoolean("checkNewVersionOnStartup");
	}
}
