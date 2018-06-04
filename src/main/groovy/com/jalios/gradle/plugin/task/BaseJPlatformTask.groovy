package com.jalios.gradle.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import com.jalios.gradle.plugin.JModule
import com.jalios.gradle.plugin.JPlatform

abstract class BaseJPlatformTask extends DefaultTask {

	protected JModule jModule
	protected JPlatform jPlatform
	
	@TaskAction
	def taskAction() {
		this.jModule = new JModule(this.project)
		this.jPlatform = new JPlatform(this.project)
		
		this.run()
	}
	
	abstract void run()
}
