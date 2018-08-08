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
		currModule.paths.each { jpath ->
			println "- ${jpath.type} - ${jpath.path}"
		}
		
		println "Platform module files :"
		platformModule.paths.each { jpath ->
			println "- ${jpath.type} - ${jpath.path}"
		}
		
		println "Platform module generated files :"
		platformModule.generatedPaths.each { genFile ->
			println "- ${genFile.path.path} generated from ${genFile.source.path} (${genFile.source.type})"
		}
		
		println "Installing module :"
		println "==================="
		
		println "Removing platform module files that are no longer present in the current module"
		platformModule.paths.each { jpath ->
			if( !currModule.paths.contains(jpath) ) {
				platformModule.getFs(jpath.type).delete(jpath.path)
			}
		}
		
		println "Removing platform module generated files whose corresponding source no longer exist in the current module"
		platformModule.generatedPaths.each { genFile ->
			if( !currModule.paths.contains(genFile.source) ) {
				platformModule.getFs(genFile.source.type).delete(genFile.path.path)
			}
		}
		
		println "Overwriting platform module files"
		currModule.paths.each { jpath ->
			currModule.getFs(jpath.type).getContentAsStream(jpath.path) { inStream ->
				platformModule.getFs(jpath.type).setContentFromStream(jpath.path, inStream)
			}
		}
	}
}
