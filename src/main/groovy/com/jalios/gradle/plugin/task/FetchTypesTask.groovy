package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.util.FileUtil

class FetchTypesTask extends BaseJPlatformTask {

	@Override
	public void run() {
		this.currModule.pluginXml.types.type.each { t ->
			def name = t["@name"]
			FileUtil.copy(this.platform.rootFolder, this.currModule.rootFolder, "WEB-INF/data/types/${name}/${name}.xml")
			FileUtil.copy(this.platform.rootFolder, this.currModule.rootFolder, "WEB-INF/data/types/${name}/${name}-templates.xml")
		}
	}

}
