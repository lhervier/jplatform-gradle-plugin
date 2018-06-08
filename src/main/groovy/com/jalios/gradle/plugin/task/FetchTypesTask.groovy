package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.source.TypesExtractor
import com.jalios.gradle.plugin.util.FileUtil

class FetchTypesTask extends BaseJPlatformTask {

	@Override
	public void run() {
		if( !this.currModule.exists ) {
			return
		}
		
		// Using type extractor to extract files from platform plugin
		// while getting the list of types to extract from current plugin
		new TypesExtractor().extractTypes(
				this.currModule.pluginXml.types.type, 
				this.platform.module(this.currModule.name)
		) { path ->
			FileUtil.copy(this.platform.rootFolder, this.currModule.rootFolder, path)
		}
	}

}
