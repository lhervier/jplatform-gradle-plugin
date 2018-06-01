package com.jalios.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class FetchPluginTask extends DefaultTask {

	@TaskAction
	def fetchPluginXml() {
		String jModuleName = project.jModule.name
		String jPath = project.jPlatform.path
		
		// Get destination folders (public and private)
		def privateDestPlugins = project.file("src/main/module/WEB-INF/plugins/${jModuleName}/")
		def publicDestPlugins = project.file("src/main/module/plugins/${jModuleName}/")
		
		// Check they does not exist
		if( privateDestPlugins.exists() || publicDestPlugins.exists() ) {
			throw new Exception("Error : Plugin already imported in local module code. Remove src/main/module/plugins/${jModuleName} and src/main/module/WEB-INF/plugins/${jModuleName} folders before fetching if you want to overwrite it with version from jPlatform.")
		}
		
		// Get source folders (from jPlatform)
		def privateSrcPlugins = project.file("${jPath}/WEB-INF/plugins/${jModuleName}/")
		def publicSrcPlugins = project.file("${jPath}/plugins/${jModuleName}/")
		
		// Copy from jPlatform to local module
		new AntBuilder().copy(todir: privateDestPlugins.absolutePath) {
				fileset(dir: privateSrcPlugins.absolutePath) {
					exclude(name: 'signature.xml')
				}
		}
		new AntBuilder().copy(todir: publicDestPlugins.absolutePath) {
			fileset(dir: publicSrcPlugins.absolutePath)
		}
	}
}
