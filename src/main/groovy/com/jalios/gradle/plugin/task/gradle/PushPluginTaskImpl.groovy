package com.jalios.gradle.plugin.task.gradle

import com.jalios.gradle.plugin.task.PushPluginTask
import com.jalios.gradle.plugin.task.JPlatformTask

class PushPluginTaskImpl extends BaseTaskImpl {
	JPlatformTask getTask() {
		return new PushPluginTask()
	}
}
