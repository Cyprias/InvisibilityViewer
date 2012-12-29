package com.cyprias.invisibilityviewer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class VersionChecker {
	public static void retreiveVersionInfo(JavaPlugin plugin, String curseRSS, Object... args) {
		getVersionInfoTask task = new getVersionInfoTask(plugin.getServer().getPluginManager(), plugin.getDescription().getName(), curseRSS);
		BukkitTask taskID = plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
		task.setArgs(args);
	}

	public static int compareVersions(String a, String b){
		String[] aParts = a.split("\\.");
		String[] bParts = b.split("\\.");

		int aInt, bInt;
		
		int i=0;
		while (aParts.length > i && bParts.length > i) {
			aInt = Integer.parseInt(aParts[i]);
			bInt = Integer.parseInt(bParts[i]);
			if (aInt != bInt){
				return aInt - bInt;
			}
			i+=1;
		}
		return 0;
	}
	
	

	public static class versionInfo {
		private String title;
		private String link;
		private String description[];
		private String pubDate;

		public versionInfo(String title2, String link2, String pubDate2, String description2) {
			this.title = title2;
			this.link = link2;
			this.pubDate = pubDate2;
			description2 = description2.replaceAll("\\<.*?>", "");
			this.description = description2.split("\\r?\\n");
		}

		public String getTitle() {
			return this.title;
		}

		public String getLink() {
			return this.link;
		}

		public String getPubDate() {
			return this.pubDate;
		}

		public String[] getDescription() {
			return this.description;
		}
	}

	private static class getVersionInfoTask implements Runnable {
		private Object[] args;
		private String pluginName, curseRSS;
		private PluginManager pm;
		public getVersionInfoTask(PluginManager pm, String pluginName, String curseRSS) {
			this.pm = pm;
			this.pluginName = pluginName;
			this.curseRSS = curseRSS;
		}
		public void setArgs(Object... args) {
			this.args = args;
		}


		@Override
		public void run() {
			try {
				URL url = new URL(this.curseRSS);
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
				doc.getDocumentElement().normalize();
				NodeList nodes = doc.getElementsByTagName("item");

				Element element, subElement;
				NodeList subNodes;

				//log.info(ChatColor.WHITE + "getVersionInfoTask run B getLenth: " + nodes.getLength());
				List<versionInfo> versions = new ArrayList<versionInfo>();
				
				if (nodes.getLength() > 0) {
					versions.clear();

					String title, link, pubDate, description;
					for (int v = 0; v < nodes.getLength(); v++) {

						element = (Element) nodes.item(v);
						subNodes = element.getElementsByTagName("title");
						subElement = (Element) subNodes.item(0);
						title = subElement.getChildNodes().item(0).getNodeValue();

						subNodes = element.getElementsByTagName("link");
						subElement = (Element) subNodes.item(0);
						link = subElement.getChildNodes().item(0).getNodeValue();

						subNodes = element.getElementsByTagName("pubDate");
						subElement = (Element) subNodes.item(0);
						pubDate = subElement.getChildNodes().item(0).getNodeValue();

						subNodes = element.getElementsByTagName("description");
						subElement = (Element) subNodes.item(0);
						description = subElement.getChildNodes().item(0).getNodeValue();
						versions.add(new versionInfo(title, link, pubDate, description));
					}
					VersionCheckerEvent event = new VersionCheckerEvent(this.pluginName, versions, this.args);
					this.pm.callEvent(event);
				}

			} catch (Exception localException) {
			}
		}
	}

	public static class VersionCheckerEvent extends Event {
		private static final HandlerList handlers = new HandlerList(); 
		public HandlerList getHandlers() { return handlers; }
		public static HandlerList getHandlerList() { return handlers; }
		 
		private List<versionInfo> versions;

		private String pluginName;
		private Object[] args;
		public VersionCheckerEvent(String pluginName, List<versionInfo> versions, Object... args) {
			this.pluginName = pluginName;
			this.versions = versions;
			this.args = args;
		}

		public String getPluginName() {
			return this.pluginName;
		}

		public int getVersionCount() {
			return versions.size();
		}

		public versionInfo getVersionInfo(int index) {
			if (versions.size() >= (index+1))
				return versions.get(index);
			
			return null;
		}

		public Object[] getArgs(){
			return this.args;
		}
		
	}
	

	
}

