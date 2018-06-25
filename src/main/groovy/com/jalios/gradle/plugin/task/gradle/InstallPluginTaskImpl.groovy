package com.jalios.gradle.plugin.task.gradle

import com.jalios.gradle.plugin.task.InstallPluginTask
import com.jalios.gradle.plugin.task.JPlatformTask

class InstallPluginTaskImpl extends BaseTaskImpl {
	JPlatformTask getTask() {
		return new InstallPluginTask()
	}
}
