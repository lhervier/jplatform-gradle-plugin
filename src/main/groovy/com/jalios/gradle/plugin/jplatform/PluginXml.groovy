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

	private XmlParser parser
	def xml
	
	/**
	 * Empty constructor
	 */
	private PluginXml() {
		this.parser = new XmlParser(false, false)
		this.parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
		this.parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
	}
	
	/**
	 * Constructor from a reader
	 * @param pluginXml the plugin.xml file
	 */
	PluginXml(Reader pluginXml) {
		this()
		this.xml = this.parser.parse(pluginXml)
	}
	
	/**
	 * Constructor from a stream
	 */
	PluginXml(InputStream inStream) {
		this()
		this.xml = this.parser.parse(inStream)
	}
	
	/**
	 * Returns the name
	 */
	String getName() {
		return this.xml["@name"]
	}
	
	/**
	 * Sets the name
	 */
	void setName(String name) {
		this.xml["@name"] = name
	}
	
	/**
	 * Returns the version
	 */
	String getVersion() {
		return this.xml["@version"]
	}
	
	/**
	 * Sets the version
	 */
	void setVersion(String version) {
		this.xml["@version"] = version
	}
	
	/**
	 * Returns the jar dependencies	
	 */
	public List<JJar> getJars() {
		List<JJar> jars = new ArrayList()
		this.xml.jars.jar.each { n ->
			JJar j = new JJar(path: n["@path"])
			jars.add(j)
		}
		return jars
	}
	
	/**
	 * Add a jar to the dependencies
	 */
	public void addJar(String jarName) {
		if( !this.xml.jars ) {
			this.xml.children().add(0, this.parser.parseText("<jars/>"))
		}
		
		def jars = this.xml.find { n ->
			n.name() == 'jars' 
		}.children()
		jars.add(this.parser.parseText("<jar path=\"${jarName}\"/>"))
	}
	
	// ============================== Read only ===========================
	
	/**
	 * Returns the labels	
	 */
	public List<JLabel> getLabels() {
		List<JLabel> labels = new ArrayList()
		this.xml.label.each { n ->
			labels.add(new JLabel(lang: n["@xml:lang"], label: n.text()))
		}
		return labels
	}
	
	/**
	 * Returns the descriptions
	 */
	public List<JLabel> getDescriptions() {
		List<JLabel> descriptions = new ArrayList()
		this.xml.description.each { n ->
			descriptions.add(new JLabel(lang: n["@xml:lang"], label: n.text()))
		}
		return descriptions
	}
	
	/**
	 * Returns the dependencies
	 */
	public List<JDependency> getDependencies() {
		List<JDependency> dependencies = new ArrayList()
		this.xml.dependencies.dependency.each { n ->
			dependencies.add(new JDependency(name: n["@name"], active: n["@active"]?.toBoolean()))
		}
		return dependencies
	}
	
	/**
	 * Returns the types
	 */
	public JTypes getTypes() {
		JTypes types = new JTypes()
		this.xml.types.type.each { n ->
			JType t = new JType(name: n["@name"])
			n.file.each { nf ->
				t.files.add(new JFile(path: nf["@path"], include: nf["@include"]))
			}
			types.types.add(t)
		}
		this.xml.types.templates.each { n ->
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
			types.templates.add(ts)
		}
		return types
	}
	
	/**
	 * Returns the private files
	 */
	public JFiles getPrivateFiles() {
		JFiles privateFiles = new JFiles()
		this.xml["private-files"].directory.each { n ->
			privateFiles.directories.add(new JDirectory(path: n["@path"]))
		}
		this.xml["private-files"].file.each { n ->
			privateFiles.files.add(new JFile(path: n["@path"], include: n["@include"]))
		}
		return privateFiles
	}
	
	/**
	 * Returns the public files
	 */
	public JFiles getPublicFiles() {
		JFiles publicFiles = new JFiles()
		this.xml["public-files"].directory.each { n ->
			publicFiles.directories.add(new JDirectory(path: n["@path"]))
		}
		this.xml["public-files"].file.each { n ->
			publicFiles.files.add(new JFile(path: n["@path"], include: n["@include"]))
		}
		return publicFiles
	}
	
	/**
	 * Returns the webapp files
	 */
	public JFiles getWebappFiles() {
		JFiles webappFiles = new JFiles()
		this.xml["webapp-files"].directory.each { n ->
			webappFiles.directories.add(new JDirectory(path: n["@path"]))
		}
		this.xml["webapp-files"].file.each { n ->
			webappFiles.files.add(new JFile(path: n["@path"], include: n["@include"]))
		}
		return webappFiles
	}
	
	/**
	 * Save the XML file 
	 * @param writer to save the file
	 */
	void save(OutputStream out) {
		groovy.xml.XmlUtil.serialize(this.xml, out)
	}
}
