package com.cyprias.invisibilityviewer;

import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;


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
