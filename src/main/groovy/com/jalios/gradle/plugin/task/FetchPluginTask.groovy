package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.JException
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
class FetchPluginTask implements JPlatformTask {

	void run(JModule platformModule, JModule currModule) {
		// Check that the current module does not exists
		if( currModule.pluginXml != null ) {
			throw new JException("Error : Plugin already imported in local module code. Remove ${currModule.privFolder.absolutePath} and ${currModule.pubFolder.absolutePath} folders before fetching if you want to overwrite it with version from jPlatform.")
		}
		
		// Check that module exist in jPlatform
		if( platformModule.pluginXml == null ) {
			throw new JException("Error : Plugin ${currModule.name} does not exist in jPlatform")
		}
		
		// Copy files from jPlatform module to currentModule
		println "Copying files from jPlatform plugin into current module:"
		platformModule.paths.each { path ->
			platformModule.rootFs.getContentAsStream(path) { inStream ->
				currModule.rootFs.setContentFromStream(path, inStream)
			}
		}
	}
}
