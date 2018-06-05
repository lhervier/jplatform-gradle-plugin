package com.jalios.gradle.plugin.jplatform

import org.gradle.api.Project

import com.jalios.gradle.plugin.BaseJPlatformElement
import com.jalios.gradle.plugin.ext.JModuleExtension
import com.jalios.gradle.plugin.ext.JPlatformExtension

class JPlatform {

	private def jModules = [:]
	private Project project
	private String root
	
	/**
	 * Constructor
	 * @param project current project
	 * @param rootPath the root path
	 */
	JPlatform(Project project, String rootPath) {
		this.project = project
		this.root = rootPath
	}
	
	/**
	 * Return the given module
	 * @param name the name of the module
	 */
	JModule module(String name) {
		if( this.jModules[name] != null )
			return this.jModules[name]
		this.jModules[name] = new JModule(this.project, this.root, name)
		return this.jModules[name]
	}
}
