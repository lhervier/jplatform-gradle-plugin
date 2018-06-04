package com.jalios.gradle.plugin

import org.gradle.api.Project

import com.jalios.gradle.plugin.ext.JModuleExtension

class JModule {

	private JModuleExtension jModule
	private File pubFolder
	private File privFolder
	
	/**
	 * Constructor
	 * @param project the current project
	 */
	JModule(Project project) {
		this.jModule = project.jModule
		this.pubFolder = project.file("src/main/module/plugins/${this.jModule.name}/")
		this.privFolder = project.file("src/main/module/WEB-INF/plugins/${this.jModule.name}/")
	}
	
	/**
	 * Return the name of the module
	 */
	String getName() {
		return this.jModule.name
	}
	
	/**
	 * Return the path to the public folder of the plugin
	 */
	File getPubFolder() {
		return this.pubFolder
	}
	
	/**
	 * Return the path to the private folder of the plugin
	 */
	File getPrivFolder() {
		return this.privFolder
	}
}
