package com.jalios.gradle.plugin.task.impl

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import com.jalios.gradle.plugin.fs.impl.FileSystemFactoryImpl
import com.jalios.gradle.plugin.task.FetchPluginTask

class FetchPluginTaskImpl extends BaseTaskImpl {
	public FetchPluginTaskImpl() {
		super(FetchPluginTask.class)
	}
}
