package com.jalios.gradle.plugin.task.impl

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import com.jalios.gradle.plugin.fs.impl.JFileSystemImpl
import com.jalios.gradle.plugin.task.BaseJPlatformTask

abstract public class BaseTaskImpl extends DefaultTask {

	Class<?extends BaseJPlatformTask> cl
	
	public BaseTaskImpl(Class<?extends BaseJPlatformTask> cl) {
		this.cl = cl
	}
	
	@TaskAction
	final def taskAction() {
		this.cl.newInstance(
			this.project.jModule.name,
			new JFileSystemImpl(this.project.files(jPlatform.path)),
			new JFileSystemImpl(this.project.files("src/main/module"))
		).run()
	}
}
