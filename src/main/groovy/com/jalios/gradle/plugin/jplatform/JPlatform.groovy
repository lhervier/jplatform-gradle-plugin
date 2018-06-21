package com.jalios.gradle.plugin.jplatform

import org.gradle.api.Project

import com.jalios.gradle.plugin.fs.FileSystem

/**
 * Describes the JPlatform installation
 * @author Lionel HERVIER
 */
class JPlatform {

	/**
	 * List of all loaded modules
	 */
	private Map<String, JModule> jModules = [:]
	
	/**
	 * Root folder of the JPlatform
	 */
	final FileSystem rootFs
	
	/**
	 * Constructor
	 * @param rootFs root file system of JPlatform
	 */
	JPlatform(FileSystem rootFs) {
		this.rootFs = rootFs
	}
	
	/**
	 * Return the given module
	 * @param name the name of the module
	 * @return the module
	 */
	JModule module(String name) {
		if( this.jModules[name] != null )
			return this.jModules[name]
		this.jModules[name] = new JModule(name, this.rootFs)
		return this.jModules[name]
	}
}
