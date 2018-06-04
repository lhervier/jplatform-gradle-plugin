package com.jalios.gradle.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * This task will install the module into the jPlatform
 * installation
 * 
 * @author Lionel HERVIER
 */
class InstallPluginTask extends BaseJPlatformTask {

	void run() {
		// Remove private and public plugin folder in jPlatform
		this.jPlatform.privFolder(this.jModule.name).deleteDir()
		this.jPlatform.pubFolder(this.jModule.name).deleteDir()
		
		// Copy folders from jModule to jPlatform
		new AntBuilder().copy(todir: jPlatform.privFolder(this.jModule.name).absolutePath) {
			fileset(dir: this.jModule.privFolder.absolutePath)
		}
		new AntBuilder().copy(todir: jPlatform.pubFolder(this.jModule.name).absolutePath) {
			fileset(dir: this.jModule.pubFolder.absolutePath)
		}
	}
}
