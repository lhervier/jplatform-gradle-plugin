package com.jalios.gradle.plugin.task.gradle

import com.jalios.gradle.plugin.task.FetchPluginTask
import com.jalios.gradle.plugin.task.JPlatformTask

class FetchPluginTaskImpl extends BaseTaskImpl {
	JPlatformTask getTask() {
		return new FetchPluginTask()
	}
}
