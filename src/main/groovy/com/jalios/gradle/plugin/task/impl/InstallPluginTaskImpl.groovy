package com.jalios.gradle.plugin.task.impl

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import com.jalios.gradle.plugin.fs.impl.FileSystemFactoryImpl
import com.jalios.gradle.plugin.task.InstallPluginTask

class InstallPluginTaskImpl extends BaseTaskImpl {

	public InstallPluginTaskImpl() {
		super(InstallPluginTask.class)
	}
}
