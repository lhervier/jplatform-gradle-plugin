package com.jalios.gradle.plugin.task.gradle

import com.jalios.gradle.plugin.task.JPlatformTask
import com.jalios.gradle.plugin.task.impl.FetchTypesTask

class FetchTypesTaskImpl extends BaseTaskImpl {
	JPlatformTask getTask() {
		return new FetchTypesTask()
	}
}
