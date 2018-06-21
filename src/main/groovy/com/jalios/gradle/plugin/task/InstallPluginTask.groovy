package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.jplatform.JModule

/**
 * This task will install the module into the jPlatform
 * @author Lionel HERVIER
 */
class InstallPluginTask extends BaseJPlatformTask {

	public InstallPluginTask(
			String moduleName,
			JFileSystem jPlatformFs,
			JFileSystem moduleFs) {
		super(moduleName, jPlatformFs, moduleFs);
	}

	void run() {
		// Files present in current module
		println "Current module file :"
		this.currModule.files.each { path ->
			println "- ${path}"
		}
		
		// Get platform plugin files (normal, and generated)
		println "Platform module generated files :"
		this.platformModule.generatedFiles.each { genFile ->
			println "- ${genFile.path} generated from ${genFile.source}"
		}
		println "Platform module files :"
		this.platformModule.files.each { path ->
			println "- ${path}"
		}
		
		// Remove files that are no longer present in current module
		println "Removing files that are no longer present in the current module"
		this.platformModule.files.each { path ->
			if( !currModule.files.contains(path) ) {
				this.platformModule.rootFs.delete(path)
			}
		}
		
		// Remove generated files whose corresponding source no longer exist in current module
		println "Removing generated files whose corresponding source no longer exist in current module"
		this.platformModule.generatedFiles.each { genFile ->
			if( !currModule.files.contains(genFile.source) ) {
				this.platformModule.rootFs.delete(genFile.path)
			}
		}
		
		// Overwrite platform module files
		println "Overwriting platform module files"
		currModule.files.each { path ->
			this.currModule.rootFs.getContentAsStream(path) { inStream ->
				this.platformModule.rootFs.setContentFromStream(path, inStream)
			}
		}
	}
}
