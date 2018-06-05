package com.jalios.gradle.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.JPlatform
import com.jalios.gradle.plugin.jplatform.PluginProp

abstract class BaseJPlatformTask extends DefaultTask {

	protected JModule currModule
	protected JPlatform platform
	
	@TaskAction
	def taskAction() {
		this.currModule = new JModule(this.project, "src/main/module", this.project.jModule.name)
		this.platform = new JPlatform(this.project, project.jPlatform.path)
		
		this.run()
	}
	
	abstract void run()
}
