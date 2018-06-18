package com.jalios.gradle.plugin.jplatform

import org.gradle.api.Project

/**
 * Describes the JPlatform installation
 * @author Lionel HERVIER
 */
class JPlatform {

	/**
	 * List of all loaded modules
	 */
	private def jModules = [:]
	
	/**
	 * Root folder of the JPlatform
	 */
	final File rootFolder
	
	/**
	 * Constructor
	 * @param rootFolder path to the root folder of JPlatform
	 */
	JPlatform(File rootFolder) {
		this.rootFolder = rootFolder
	}
	
	/**
	 * Return the given module
	 * @param name the name of the module
	 * @return the module
	 */
	JModule module(String name) {
		if( this.jModules[name] != null )
			return this.jModules[name]
		this.jModules[name] = new JModule(this.rootFolder, name)
		return this.jModules[name]
	}
}
