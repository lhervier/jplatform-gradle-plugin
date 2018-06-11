package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.util.FileUtil

/**
 * This task will install the module into the jPlatform
 * @author Lionel HERVIER
 */
class InstallPluginTask extends BaseJPlatformTask {

	void run() {
		JModule platformModule = this.platform.module(this.currModule.name)
		
		// Files present in current module
		println "Current module file :"
		this.currModule.files.each { path ->
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
				FileUtil.delete(platformModule, path)
			}
		}
		
		// Remove generated files whose corresponding source no longer exist in current module
		println "Removing generated files whose corresponding source no longer exist in current module"
		platformModule.generatedFiles.each { genFile ->
			if( !currModule.files.contains(genFile.source) ) {
				FileUtil.delete(platformModule, genFile.path)
			}
		}
		
		// Overwrite platform module files
		println "Overwriting platform module files"
		currModule.files.each { path ->
			FileUtil.copy(this.currModule.rootFolder, this.platform.rootFolder, path)
		}
	}
}
