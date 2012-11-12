package com.cyprias.invisibilityviewer;

import org.bukkit.configuration.Configuration;


public class Config {
	private InvisibilityViewer plugin;
	private static Configuration config;
	

	static Boolean checkNewVersionOnStartup, viewPlayerByDefault, viewOtherByDefault, colouredConsoleMessages;


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
		viewPlayerByDefault = config.getBoolean("viewByDefault.player");
		viewOtherByDefault = config.getBoolean("viewByDefault.other");
		colouredConsoleMessages = config.getBoolean("colouredConsoleMessages");
	}
}
