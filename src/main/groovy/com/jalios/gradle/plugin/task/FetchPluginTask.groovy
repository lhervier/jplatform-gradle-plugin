package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.ex.JTaskException
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
class FetchPluginTask extends BaseJPlatformTaskImpl {

	void run(JModule platformModule, JModule currModule) throws JTaskException {
		// Check that the current module does not exists
		if( currModule.pluginXml != null ) {
			throw new JTaskException("Error : Plugin already imported in local module code. Remove module private and public folder before fetching if you want to overwrite it with version from jPlatform.")
		}
		
		// Check that module exist in jPlatform
		if( platformModule.pluginXml == null ) {
			throw new JTaskException("Error : Plugin ${currModule.name} does not exist in jPlatform")
		}
		
		// Copy files from jPlatform module to currentModule
		println "Copying files from jPlatform plugin into current module:"
		platformModule.paths.each { jpath ->
			platformModule.getFs(jpath.type).getContentAsStream(jpath.path) { inStream ->
				currModule.getFs(jpath.type).setContentFromStream(jpath.path, inStream)
			}
		}
	}
}
