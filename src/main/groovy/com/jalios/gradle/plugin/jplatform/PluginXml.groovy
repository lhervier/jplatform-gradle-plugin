package com.jalios.gradle.plugin.jplatform

import com.jalios.gradle.plugin.jplatform.model.JDependency
import com.jalios.gradle.plugin.jplatform.model.JDirectory
import com.jalios.gradle.plugin.jplatform.model.JFile
import com.jalios.gradle.plugin.jplatform.model.JFiles
import com.jalios.gradle.plugin.jplatform.model.JJar
import com.jalios.gradle.plugin.jplatform.model.JLabel
import com.jalios.gradle.plugin.jplatform.model.JTemplate
import com.jalios.gradle.plugin.jplatform.model.JTemplates
import com.jalios.gradle.plugin.jplatform.model.JType
import com.jalios.gradle.plugin.jplatform.model.JTypes

/**
 * Representation of a plugin.xml file
 * @author Lionel HERVIER
 */
class PluginXml {

	final List<JLabel> labels = new ArrayList()
	final List<JLabel> descriptions = new ArrayList()
	
	final List<JDependency> dependencies = new ArrayList()
	final JTypes types
	
	final List<JJar> jars = new ArrayList()
	
	final JFiles privateFiles = new JFiles()
	final JFiles publicFiles = new JFiles()
	final JFiles webappFiles = new JFiles()
	
	/**
	 * Constructor
	 * @param pluginXml the plugin.xml file
	 */
	PluginXml(Reader pluginXml) {
		XmlParser parser = new XmlParser(false, false)
		parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
		parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
		def xml = parser.parse(pluginXml)
		
		// Label and description
		xml.label.each { n ->
			this.labels.add(new JLabel(lang: n["@xml:lang"], label: n.text()))
		}
		xml.description.each { n ->
			this.descriptions.add(new JLabel(lang: n["@xml:lang"], label: n.text()))
		}
		
		// Add dependencies
		xml.dependencies.dependency.each { n ->
			this.dependencies.add(new JDependency(name: n["@name"], active: n["@active"]?.toBoolean()))
		}
		
		// Types
		this.types = new JTypes()
		xml.types.type.each { n ->
			JType t = new JType(name: n["@name"])
			n.file.each { nf ->
				t.files.add(new JFile(path: nf["@path"], include: nf["@include"]))
			}
			this.types.types.add(t)
		}
		xml.types.templates.each { n ->
			JTemplates ts = new JTemplates(type: n["@type"])
			n.template.each { nt ->
				JTemplate tmpl = new JTemplate(
					name: nt["@name"],
					file: nt["@file"],
					dir: nt["@dir"],
					usage: nt["@usage"]
				)
				
				nt.label.each { nl ->
					tmpl.labels.add(new JLabel(lang: nl["@xml:lang"], label: nl.text()))
				}
				ts.templates.add(tmpl)
			}
			this.types.templates.add(ts)
		}
		
		// Add jars dependencies
		xml.jars.jar.each { n ->
			JJar j = new JJar(path: n["@path"])
			this.jars.add(j)
		}
		
		// Add private, public and webapp files
		[
			"private-files" : this.privateFiles,
			"public-files" : this.publicFiles,
			"webapp-files" : this.webappFiles
		].each {tag, files ->
			xml[tag].directory.each { n ->
				files.directories.add(new JDirectory(path: n["@path"]))
			}
			xml[tag].file.each { n ->
				files.files.add(new JFile(path: n["@path"], include: n["@include"]))
			}
		}
	}
	
}
