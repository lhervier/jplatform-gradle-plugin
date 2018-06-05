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
		List<String> currModuleFiles = this.currModule.files()
		println "Current module file :"
		currModuleFiles.each { path ->
			println "- ${path}"
		}
		
		// Get platform plugin files (normal, and generated)
		Map<String, String> platformGenFiles = platformModule.generatedFiles()
		println "Platform module generated files :"
		platformGenFiles.each { generated, source ->
			println "- ${generated} generated from ${source}"
		}
		List<String> platformFiles = platformModule.files()
		println "Platform module files :"
		platformFiles.each { path ->
			println "- ${path}"
		}
		
		// Remove files that are no longer present in current module
		println "Removing files that are no longer present in the current module"
		platformFiles.each { path ->
			if( !currModuleFiles.contains(path) ) {
				FileUtil.delete(platformModule, path)
			}
		}
		
		// Remove generated files whose corresponding source no longer exist in current module
		println "Removing generated files whose corresponding source no longer exist in current module"
		platformGenFiles.each { generated, source ->
			if( !currModuleFiles.contains(source) ) {
				FileUtil.delete(platformModule, generated)
			}
		}
		
		// Overwrite platform module files
		currModuleFiles.each { path ->
			FileUtil.copy(this.currModule, platformModule, path)
		}
	}
}
