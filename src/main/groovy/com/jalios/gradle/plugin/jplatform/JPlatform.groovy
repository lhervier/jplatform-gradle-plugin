package com.jalios.gradle.plugin.jplatform

import org.gradle.api.Project

class JPlatform {

	private def jModules = [:]
	
	final File rootFolder
	
	/**
	 * Constructor
	 * @param project current project
	 * @param rootPath the root path
	 */
	JPlatform(File rootFolder) {
		this.rootFolder = rootFolder
	}
	
	/**
	 * Return the given module
	 * @param name the name of the module
	 */
	JModule module(String name) {
		if( this.jModules[name] != null )
			return this.jModules[name]
		this.jModules[name] = new JModule(this.rootFolder, name)
		return this.jModules[name]
	}
	
	String getTypePath(String name) {
		String path = "WEB-INF/data/types/${name}.xml"
		File typeFile = new File(this.rootFolder, path)
		if( !typeFile.exists() )
			return null
		return path
	}
}
