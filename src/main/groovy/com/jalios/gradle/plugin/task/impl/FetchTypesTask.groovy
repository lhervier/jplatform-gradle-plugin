package com.jalios.gradle.plugin.task.impl

import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.source.impl.TypesExtractor
import com.jalios.gradle.plugin.task.JPlatformTask

/**
 * Task to fetch files generated for the set of types declared
 * in the plugin.xml file of the current module.
 * TODO: User should confirm that he is ok pour replace existing files
 * @author Lionel HERVIER
 */
class FetchTypesTask implements JPlatformTask {

	@Override
	void run(JModule platformModule, JModule currModule) {
		if( currModule.pluginXml == null ) {
			return
		}
		
		// Using type extractor to extract files from platform plugin
		// while getting the list of types to extract from current plugin
		new TypesExtractor().extractGeneratedFilesForTypes(
				currModule.pluginXml, 
				platformModule
		) { path ->
			platformModule.rootFs.getContentAsStream(path) { inStream ->
				currModule.rootFs.setContentFromStream(path, inStream)
			}
		}
	}

}
