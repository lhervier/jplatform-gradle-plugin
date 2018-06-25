package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.jplatform.JModule

interface JPlatformTask {

	void run(JModule platformModule, JModule currModule) throws JTaskException
	
}
