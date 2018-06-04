package com.jalios.gradle.plugin

import org.gradle.api.Project

import com.jalios.gradle.plugin.ext.JModuleExtension
import com.jalios.gradle.plugin.ext.JPlatformExtension

class JPlatform {

	private Project project
	private JPlatformExtension jPlatform
	
	/**
	 * Constructor
	 * @param project current project
	 */
	JPlatform(Project project) {
		this.project = project
		this.jPlatform = project.jPlatform
	}
	
	/**
	 * Return the path to the private folder of a given plugin
	 * @param pluginName name of the plugin
	 */
	File privFolder(String pluginName) {
		return this.project.file("${this.jPlatform.path}/WEB-INF/plugins/${pluginName}/")
	}
	
	/**
	 * Return the path to the private folder of a given plugin
	 * @param pluginName name of the plugin
	 */
	File pubFolder(String pluginName) {
		return this.project.file("${this.jPlatform.path}/plugins/${pluginName}/")
	}
}
