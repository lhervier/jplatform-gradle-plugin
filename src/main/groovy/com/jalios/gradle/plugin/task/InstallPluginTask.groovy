package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.jplatform.JModule

/**
 * This task will install the module into the jPlatform
 * @author Lionel HERVIER
 */
class InstallPluginTask implements JPlatformTask {

	void run(JModule platformModule, JModule currModule) {
		println "Relevant files :"
		println "================"
		
		println "Current module files :"
		currModule.paths.each { path ->
			println "- ${path}"
		}
		
		println "Platform module files :"
		platformModule.paths.each { path ->
			println "- ${path}"
		}
		
		println "Platform module generated files :"
		platformModule.generatedPaths.each { genFile ->
			println "- ${genFile.path} generated from ${genFile.source}"
		}
		
		println "Installing module :"
		println "==================="
		
		println "Removing platform module files that are no longer present in the current module"
		platformModule.paths.each { path ->
			if( !currModule.paths.contains(path) ) {
				platformModule.rootFs.delete(path)
			}
		}
		
		println "Removing platform module generated files whose corresponding source no longer exist in the current module"
		platformModule.generatedPaths.each { genFile ->
			if( !currModule.paths.contains(genFile.source) ) {
				platformModule.rootFs.delete(genFile.path)
			}
		}
		
		println "Overwriting platform module files"
		currModule.paths.each { path ->
			currModule.rootFs.getContentAsStream(path) { inStream ->
				platformModule.rootFs.setContentFromStream(path, inStream)
			}
		}
	}
}
