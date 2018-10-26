package com.jalios.gradle.plugin.test.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtils {

	public static List<Element> getChildrenByTagName(Element parent, String name) {
		List<Element> nodeList = new ArrayList<>();
		if( parent == null )
			return nodeList;
		for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() == Node.ELEMENT_NODE && name.equals(child.getNodeName())) {
				nodeList.add((Element) child);
			}
		}
		return nodeList;
	}
	
	public static Element getFirstChild(Element parent, String tagName) {
		NodeList nl = parent.getElementsByTagName(tagName);
		if( nl == null || nl.getLength() == 0 ) {
			return null;
		}
		return (Element) nl.item(0);
	}
}
