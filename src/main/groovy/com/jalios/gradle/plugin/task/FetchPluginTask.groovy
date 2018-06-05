package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.util.FileUtil

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

	void run() {
		// Check that the current module exists
		if( this.currModule.exists() ) {
			throw new Exception("Error : Plugin already imported in local module code. Remove ${this.currModule.privFolder.absolutePath} and ${this.currModule.pubFolder.absolutePath} folders before fetching if you want to overwrite it with version from jPlatform.")
		}
		
		// Check that module exist in jPlatform
		JModule platformModule = this.platform.module(this.currModule.name)
		if( !platformModule.exists() ) {
			throw new Exception("Error : Plugin ${this.currModule.name} does not exist in jPlatform")
		}
		
		// Copy files from jPlatform module to currentModule
		def files = platformModule.files()
		println "Files from jPlatform plugin (to be copied into current module):"
		files.each { path ->
			println "- ${path}"
		}
		files.each { path ->
			FileUtil.copy(platformModule, this.currModule, path)
		}
	}
}
