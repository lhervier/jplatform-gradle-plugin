package com.jalios.gradle.plugin.task.gradle

import com.jalios.gradle.plugin.task.JPlatformTask
import com.jalios.gradle.plugin.task.PushTypesTask

class PushTypesTaskImpl extends BaseTaskImpl {
	JPlatformTask getTask() {
		return new PushTypesTask()
	}
}
