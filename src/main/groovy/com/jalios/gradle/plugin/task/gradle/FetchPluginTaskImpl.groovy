package com.jalios.gradle.plugin.task.gradle

import com.jalios.gradle.plugin.task.JPlatformTask
import com.jalios.gradle.plugin.task.impl.FetchPluginTask

class FetchPluginTaskImpl extends BaseTaskImpl {
	JPlatformTask getTask() {
		return new FetchPluginTask()
	}
}
