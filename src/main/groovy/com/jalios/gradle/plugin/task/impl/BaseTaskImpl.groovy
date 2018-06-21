package com.jalios.gradle.plugin.task.impl

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import com.jalios.gradle.plugin.fs.impl.FileSystemFactoryImpl
import com.jalios.gradle.plugin.task.BaseJPlatformTask

abstract public class BaseTaskImpl extends DefaultTask {

	Class<?extends BaseJPlatformTask> cl
	
	public BaseTaskImpl(Class<?extends BaseJPlatformTask> cl) {
		this.cl = cl
	}
	
	@TaskAction
	final def taskAction() {
		this.cl.newInstance(
			new FileSystemFactoryImpl(project: this.project),
			this.project.jModule.name,
			project.jPlatform.path,
			"src/main/module"
		).run()
	}
}
