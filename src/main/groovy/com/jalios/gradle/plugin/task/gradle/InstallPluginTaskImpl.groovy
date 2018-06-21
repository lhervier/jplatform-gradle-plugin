package com.jalios.gradle.plugin.task.gradle

import com.jalios.gradle.plugin.task.JPlatformTask
import com.jalios.gradle.plugin.task.impl.InstallPluginTask

class InstallPluginTaskImpl extends BaseTaskImpl {
	JPlatformTask getTask() {
		return new InstallPluginTask()
	}
}
