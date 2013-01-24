package com.cyprias.invisibilityviewer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* Pulls version info from the project's files page on Curse. */

public class VersionChecker {
	public List<versionInfo> versions = new ArrayList<versionInfo>();
	public VersionChecker(String curseRSS) throws SAXException, IOException, ParserConfigurationException {
		URL url = new URL(curseRSS);
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
		doc.getDocumentElement().normalize();
		NodeList nodes = doc.getElementsByTagName("item");

		Element element, subElement;

		if (nodes.getLength() > 0) {
			versions.clear();

			String title, link, pubDate, description;
			for (int v = 0; v < nodes.getLength(); v++) {
				element = (Element) nodes.item(v);
				subElement = (Element) element.getElementsByTagName("title").item(0);
				title = subElement.getChildNodes().item(0).getNodeValue();

				subElement = (Element) element.getElementsByTagName("link").item(0);
				link = subElement.getChildNodes().item(0).getNodeValue();

				subElement = (Element) element.getElementsByTagName("pubDate").item(0);
				pubDate = subElement.getChildNodes().item(0).getNodeValue();

				subElement = (Element) element.getElementsByTagName("description").item(0);
				description = subElement.getChildNodes().item(0).getNodeValue();
				
				versions.add(new versionInfo(title, link, pubDate, description));
			}
		}
	}

	public static int compareVersions(String a, String b){
		String[] aParts = a.split("\\.");
		String[] bParts = b.split("\\.");

		int aInt, bInt;
		
		int i=0;
		while (aParts.length > i && bParts.length > i) {
			aInt = Integer.parseInt(aParts[i]);
			bInt = Integer.parseInt(bParts[i]);
			if (aInt != bInt)
				return aInt - bInt;
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

}
