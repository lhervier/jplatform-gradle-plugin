package com.jalios.gradle.plugin.jplatform

import org.gradle.api.Project

import com.jalios.gradle.plugin.ext.JModuleExtension
import com.jalios.gradle.plugin.util.FileUtil

class JModule {

	final String name
	final File rootFolder
	final File pubFolder
	final File privFolder
	final PluginProp pluginProp
	
	private final String pubFolderPath
	private final String privFolderPath
	
	/**
	 * Constructor
	 * @param project the current project
	 * @param rootPath path to the root of the platform
	 * @param name the name of the module
	 */
	JModule(Project project, String rootPath, String name) {
		println "Creating JModule '${name}'"
		this.name = name
		println "- rootPath = ${rootPath}"
		this.rootFolder = project.file(rootPath)
		println "- rootFolder = ${this.rootFolder.absolutePath}"
		
		this.pubFolderPath = "plugins/${this.name}"
		println "- pubFolderPath = ${this.pubFolderPath}"
		this.privFolderPath = "WEB-INF/plugins/${this.name}"
		println "- privFolderPath = ${this.privFolderPath}"
		
		this.pubFolder = new File(this.rootFolder, this.pubFolderPath)
		println "- pubFolder = ${this.pubFolder.absolutePath}"
		this.privFolder = new File(this.rootFolder, this.privFolderPath)
		println "- privFolder = ${this.privFolder.absolutePath}"
		
		this.pluginProp = new PluginProp(new File(this.privFolder, "properties/plugin.prop"))
	}
	
	/**
	 * Check if module exists
	 */
	public boolean exists() {
		File pluginXml = new File(this.privFolder, "plugin.xml")
		return pluginXml.exists()
	}
	
	/**
	 * Compute the list of files that compose the module
	 */
	public List<String> files() {
		def ret = []
		
		// All files from public and private folders
		FileUtil.paths(this.pubFolder).each {
			ret.push(this.pubFolderPath + it)
		}
		FileUtil.paths(this.privFolder).each {
			ret.push(this.privFolderPath + it)
		}
		
		// Remove generated files
		this.generatedFiles().each { key, value ->
			ret.removeElement(key)
		}
		
		return ret
	}
	
	/**
	 * Compute the list of generated files
	 */
	public Map<String, String> generatedFiles() {
		def ret = [:]
		
		// signature.xml is generated because it is plugin
		ret["${this.privFolderPath}/signature.xml".toString()] = "${this.privFolderPath}/plugin.xml".toString()
		
		// css files are generated from less files
		this.pluginProp.each { key, value ->
			if( !key.startsWith("channel.less.") ) {
				return
			}
			ret[key.substring("channel.less.".length())] = value
		}
		
		return ret
	}
}
