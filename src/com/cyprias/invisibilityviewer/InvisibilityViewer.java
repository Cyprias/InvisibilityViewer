package com.cyprias.invisibilityviewer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import net.minecraft.server.MobEffectList;
import net.minecraft.server.WatchableObject;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.StructureModifier;

public class InvisibilityViewer extends JavaPlugin {
	public static String chatPrefix = "§f[§aIV§f] ";

	private ProtocolManager protocolManager;
	private String stPluginEnabled = "§f%s §7v§f%s §7is enabled.";
	String pluginName;

	public Config config;
	public Events events;
	
	public VersionChecker versionChecker;
	public Commands commands;
	
	MobEffectList invisEffect;
	public void onLoad() {
		pluginName = getDescription().getName();
		this.config = new Config(this);
		this.events = new Events(this);
		this.commands = new Commands(this);
		this.versionChecker = new VersionChecker(this, "http://dev.bukkit.org/server-mods/invisibilityviewer/files.rss");
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {}
		
		invisEffect = net.minecraft.server.MobEffectList.INVISIBILITY;
	}
	
	public void onEnable() {
		this.config.reloadOurConfig();
		
		getServer().getPluginManager().registerEvents(this.events, this);
		getCommand("iv").setExecutor(this.commands);

		if (Config.checkNewVersionOnStartup == true)
			this.versionChecker.retreiveVersionInfo();

		setupPacketHandler();
		fillViewInvis();
		
		info(String.format(stPluginEnabled, pluginName, this.getDescription().getVersion()));
		
		
	}

	public void onDisable(){
		protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.removePacketListeners(this);
		viewInvis.clear();
		info (pluginName + " disabled.");
	}
	
	
	public static HashMap<String, Integer> viewInvis = new HashMap<String, Integer>();

	
	private Entity getEntity(List<Entity> ents, int eID) {
		for (int i = 0; i < ents.size(); i++) {
			if (ents.get(i).getEntityId() == eID)
				return ents.get(i);
		}

		return null;
	}

	private void setupPacketHandler() {

		
		protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(this, ConnectionSide.SERVER_SIDE, ListenerPriority.NORMAL, Packets.Server.ENTITY_METADATA) {
			public void onPacketSending(PacketEvent event) {
				// Item packets
				PacketContainer packet = event.getPacket();

				Player player = event.getPlayer();

				switch (event.getPacketID()) {
				case Packets.Server.ENTITY_METADATA: // Entity Metadata

					StructureModifier<Object> mods = packet.getModifier();

					WatchableObject changedData;
					Entity entity = null;
					if (mods.size() > 0) {
						int eID = 0;
						try {
							eID = (Integer) mods.read(0);
						} catch (FieldAccessException e) {
							e.printStackTrace();
						}
						entity = getEntity(player.getWorld().getEntities(), eID);

						for (int i = 1; i < mods.size(); i++) {
							try {
								if (mods.read(i) instanceof ArrayList) {
									ArrayList<WatchableObject> list = (ArrayList<WatchableObject>) mods.read(i);

									for (int a = 0; a < list.size(); a++) {

										if (list.get(a) instanceof WatchableObject) {
											changedData = removeInvisibility((WatchableObject) list.get(a));

											if (changedData != null) {
												if (entity instanceof Player) {
													
													
													
													if (hasPermission(player, "invisibilityviewer.canView.player") && hasMask(viewInvis.get(player.getName()), maskPlayer)) {
														list.set(a, changedData);
														mods.write(i, list);
													}

												} else {
													if (hasPermission(player, "invisibilityviewer.canView.other") && hasMask(viewInvis.get(player.getName()), maskOther)) {
														list.set(a, changedData);
														mods.write(i, list);
													}
												}
											}
										}

									}

								}
							} catch (FieldAccessException e) {
								e.printStackTrace();
							}
						}
					}

					break;
				}
			}
		});

	}

	public void sendSurroundingInvisPackets(Player player){
		int radius = getServer().getViewDistance() * 16;
		List<Entity> ents = player.getNearbyEntities(radius, radius, radius);
		String pName = player.getName();
		Integer pFlags = viewInvis.get(pName);

		for (Entity e : ents) {
			
			if (isInvisible(e)){
				
				if (e instanceof Player) {
					
					if (hasPermission(player, "invisibilityviewer.canView.player") && hasMask(pFlags, maskPlayer)) { // 
						sendPacket(player, e.getEntityId(), false);
					}else{
						sendPacket(player, e.getEntityId(), true);
					}
					
				}else{
					if (hasPermission(player, "invisibilityviewer.canView.other")&& hasMask(pFlags, maskOther)) { // 
						sendPacket(player, e.getEntityId(), false);
					}else{
						sendPacket(player, e.getEntityId(), true);
					}
					
				}
				
			}

		}
	}
	
	
	public Boolean isInvisible(Entity entity){
		if (entity instanceof CraftLivingEntity) {
			
			CraftLivingEntity cEnt = (CraftLivingEntity) entity;
			if(cEnt != null) {
				Collection<PotionEffect> collection = cEnt.getActivePotionEffects();
				if (collection != null && !collection.isEmpty()) {
					Iterator<PotionEffect> iterator = collection.iterator();
					while (iterator.hasNext()) {
						PotionEffect effect = (PotionEffect) iterator.next();
						if (effect.getType().getId() == invisEffect.getId()) {
							return true;
						}
					}
				}
			}
			
		}

		return false;
	}
	
	
	public void sendPacket(Player player, int entID, Boolean invisible){
		PacketContainer invisPacket = protocolManager.createPacket(Packets.Server.ENTITY_METADATA);

		ArrayList<WatchableObject> list = new ArrayList<WatchableObject>();
		if (invisible == true){
			list.add(new WatchableObject(0, 0, (byte) 32));
			info("Sending invis packet on " + entID);
		}else{
			list.add(new WatchableObject(0, 0, (byte) 0));
		}

		try {
			
			invisPacket.getModifier().write(0, entID);
			invisPacket.getModifier().write(1, list);
			protocolManager.sendServerPacket(player, invisPacket);
		} catch (FieldAccessException e) {e.printStackTrace();
		} catch (InvocationTargetException e) {e.printStackTrace();}
		
	}
	
	
	
	
	public WatchableObject removeInvisibility(WatchableObject data) {
		switch (data.a()) {
		case 0:// Flags
			try {
				Byte b = (Byte) data.b();
				if (b == 32) {
					return new WatchableObject(data.c(), data.a(), (byte) 0);
				}
			} catch (NumberFormatException e) {e.printStackTrace();}
			break;
		}
		return null;
	}

	
	public void fillViewInvis(){
		for (Player p : getServer().getOnlinePlayers()) {
			addPlayerInvisOps(p);
		}
	}
	
	public void addPlayerInvisOps(Player player){

		int flags = 0;
		if (Config.viewPlayerByDefault == true)
			flags = addMask(flags, maskPlayer);

		if (Config.viewOtherByDefault == true)
			flags = addMask(flags, maskOther);

		viewInvis.put(player.getName(), flags);
	}
	
    public ChatColor colouredHasMask(int flags, int mask){
    	if (hasMask(flags, mask)){
    		return ChatColor.GREEN;
    	}
    	return ChatColor.RED;
    }
    

	private Logger log = Logger.getLogger("Minecraft");
	public void info(String msg) {
		if (Config.colouredConsoleMessages == true){
			getServer().getConsoleSender().sendMessage(chatPrefix + msg);
		}else{
			log.info(ChatColor.stripColor(chatPrefix + msg));
		}
	}

	public boolean hasPermission(CommandSender sender, String node) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;

		if (player.isPermissionSet(node)) // in case admin purposely set the
											// node to false.
			return player.hasPermission(node);

		if (player.isPermissionSet(pluginName.toLowerCase() + ".*"))
			return player.hasPermission(pluginName.toLowerCase() + ".*");

		String[] temp = node.split("\\.");
		String wildNode = temp[0];
		for (int i = 1; i < (temp.length); i++) {
			wildNode = wildNode + "." + temp[i];

			if (player.isPermissionSet(wildNode + ".*"))
				// plugin.info("wildNode1 " + wildNode+".*");
				return player.hasPermission(wildNode + ".*");
		}

		return player.hasPermission(node);
	}
	
	public static int maskPlayer=(int) Math.pow(2, 0);
	public static int maskOther=(int) Math.pow(2, 1);

	
    public static boolean hasMask(int flags, int mask) {
        return ((flags & mask) == mask);
    }
	
    public static int addMask(int flags, int mask){
    	return (flags |= mask);
    }
    public static int delMask(int flags, int mask){
    	return (flags &= ~mask);
    }
    
	public void sendMessage(CommandSender sender, String message, Boolean showConsole, Boolean sendPrefix) {
		if (sender instanceof Player && showConsole == true) {
			info("§e" + sender.getName() + "->§f" + message);
		}
		if (sendPrefix == true) {
			sender.sendMessage(chatPrefix + message);
		} else {
			sender.sendMessage(message);
		}
	}
	public void sendMessage(CommandSender sender, String message, Boolean showConsole) {
		sendMessage(sender, message, showConsole, true);
	}

	public void sendMessage(CommandSender sender, String message) {
		sendMessage(sender, message, true);
	}
}
