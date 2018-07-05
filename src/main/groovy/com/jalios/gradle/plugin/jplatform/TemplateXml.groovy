package com.jalios.gradle.plugin.jplatform

import java.util.List

import com.jalios.gradle.plugin.jplatform.model.JDependency
import com.jalios.gradle.plugin.jplatform.model.JDirectory
import com.jalios.gradle.plugin.jplatform.model.JFile
import com.jalios.gradle.plugin.jplatform.model.JFiles
import com.jalios.gradle.plugin.jplatform.model.JLabel
import com.jalios.gradle.plugin.jplatform.model.JTemplate
import com.jalios.gradle.plugin.jplatform.model.JTemplates
import com.jalios.gradle.plugin.jplatform.model.JType
import com.jalios.gradle.plugin.jplatform.model.JTypes

/**
 * Representation of a template.xml file associated with a type
 * @author Lionel HERVIER
 */
class TemplateXml {

	final String type
	final List<JTemplate> templates = new ArrayList()
	
	/**
	 * Constructor
	 * @param templateXml the template.xml file
	 */
	TemplateXml(String typeName, Reader templateXml) {
		this.type = typeName
		
		XmlParser parser = new XmlParser(false, false)
		parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
		parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
		def xml = parser.parse(templateXml)
		
		xml.template.each { nt ->
			JTemplate tmpl = new JTemplate(
				name: nt["@name"],
				file: nt["@file"],
				dir: nt["@dir"],
				usage: nt["@usage"]
			)
			
			nt.label.each { nl ->
				tmpl.labels.add(new JLabel(lang: nl["@xml:lang"], label: nl.text()))
			}
			this.templates.add(tmpl)
		}
	}
	
}
