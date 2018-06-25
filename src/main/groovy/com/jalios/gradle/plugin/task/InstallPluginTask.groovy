package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.jplatform.JModule

/**
 * This task will install the module into the jPlatform
 * @author Lionel HERVIER
 */
class InstallPluginTask implements JPlatformTask {

	void run(JModule platformModule, JModule currModule) {
		// Files present in current module
		println "Current module file :"
		currModule.files.each { path ->
			println "- ${path}"
		}
		
		// Get platform plugin files (normal, and generated)
		println "Platform module generated files :"
		platformModule.generatedFiles.each { genFile ->
			println "- ${genFile.path} generated from ${genFile.source}"
		}
		println "Platform module files :"
		platformModule.files.each { path ->
			println "- ${path}"
		}
		
		// Remove files that are no longer present in current module
		println "Removing files that are no longer present in the current module"
		platformModule.files.each { path ->
			if( !currModule.files.contains(path) ) {
				platformModule.rootFs.delete(path)
			}
		}
		
		// Remove generated files whose corresponding source no longer exist in current module
		println "Removing generated files whose corresponding source no longer exist in current module"
		platformModule.generatedFiles.each { genFile ->
			if( !currModule.files.contains(genFile.source) ) {
				platformModule.rootFs.delete(genFile.path)
			}
		}
		
		// Overwrite platform module files
		println "Overwriting platform module files"
		currModule.files.each { path ->
			currModule.rootFs.getContentAsStream(path) { inStream ->
				platformModule.rootFs.setContentFromStream(path, inStream)
			}
		}
	}
}
