package com.jalios.gradle.plugin.task.gradle

import com.jalios.gradle.plugin.task.FetchTypesTask
import com.jalios.gradle.plugin.task.JPlatformTask

class FetchTypesTaskImpl extends BaseTaskImpl {
	JPlatformTask getTask() {
		return new FetchTypesTask()
	}
}
