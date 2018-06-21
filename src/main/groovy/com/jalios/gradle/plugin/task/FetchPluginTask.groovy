package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.jplatform.JModule

/**
 * This task will fetch a (newly created) plugin into jPlatform,
 * and import its files. The plugin name must be defined in the jModule extension object.
 * 
 * This task will raise an error if a public and/or private folder already
 * exists for the plugin.
 * 
 * @author Lionel HERVIER
 */
class FetchPluginTask extends BaseJPlatformTask {

	public FetchPluginTask(
			String moduleName, 
			JFileSystem jPlatformFs, 
			JFileSystem moduleFs) {
		super(moduleName, jPlatformFs, moduleFs);
	}

	void run() {
		// Check that the current module does not exists
		if( this.currModule.exists ) {
			throw new Exception("Error : Plugin already imported in local module code. Remove ${this.currModule.privFolder.absolutePath} and ${this.currModule.pubFolder.absolutePath} folders before fetching if you want to overwrite it with version from jPlatform.")
		}
		
		// Check that module exist in jPlatform
		if( !this.platformModule.exists ) {
			throw new Exception("Error : Plugin ${this.currModule.name} does not exist in jPlatform")
		}
		
		// Copy files from jPlatform module to currentModule
		println "Copying files from jPlatform plugin into current module:"
		this.platformModule.paths.each { path ->
			this.platformModule.rootFs.getContentAsStream(path) { inStream ->
				this.currModule.rootFs.setContentFromStream(path, inStream)
			}
		}
	}
}
