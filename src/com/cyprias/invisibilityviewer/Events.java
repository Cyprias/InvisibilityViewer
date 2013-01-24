package com.cyprias.invisibilityviewer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events implements Listener {
	private InvisibilityViewer plugin;

	public Events(InvisibilityViewer plugin) {
		this.plugin = plugin;
	}

	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		plugin.addPlayerInvisOps(player);
	}

}
