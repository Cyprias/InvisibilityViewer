package com.cyprias.invisibilityviewer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.cyprias.invisibilityviewer.VersionChecker.VersionCheckerEvent;

public class Events implements Listener {
	private InvisibilityViewer plugin;

	public Events(InvisibilityViewer plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVersionCheckerEvent(VersionCheckerEvent event) {
		if (event.getPluginName() == plugin.getName()) {
			VersionChecker.versionInfo info = event.getVersionInfo(0);
			String curVersion = plugin.getDescription().getVersion();
			int compare = VersionChecker.compareVersions(curVersion, info.getTitle());
			if (Config.debugMessages == true)
				plugin.info("Latest version available is v" + info.getTitle());
			
			if (compare < 0) {
				plugin.info("We're running v" + curVersion + ", v" + info.getTitle() + " is available");
				plugin.info(info.getLink());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		plugin.addPlayerInvisOps(player);
	}

}
