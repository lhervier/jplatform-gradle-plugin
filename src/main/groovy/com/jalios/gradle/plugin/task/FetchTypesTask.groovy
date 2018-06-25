package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.source.impl.TypesExtractor

/**
 * Task to fetch files generated for the set of types declared
 * in the plugin.xml file of the current module.
 * TODO: User should confirm that he is ok pour replace existing files
 * @author Lionel HERVIER
 */
class FetchTypesTask implements JPlatformTask {

	@Override
	void run(JModule platformModule, JModule currModule) throws JTaskException {
		if( currModule.pluginXml == null ) {
			throw new JTaskException("Current module is empty... Unable to fetch types")
		}
		
		// Using type extractor to extract files from platform plugin
		// while getting the list of types to extract from current plugin
		new TypesExtractor().extractGeneratedFilesForTypes(
				platformModule,
				currModule.pluginXml 
		) { path ->
			platformModule.rootFs.getContentAsStream(path) { inStream ->
				currModule.rootFs.setContentFromStream(path, inStream)
			}
		}
	}

}
