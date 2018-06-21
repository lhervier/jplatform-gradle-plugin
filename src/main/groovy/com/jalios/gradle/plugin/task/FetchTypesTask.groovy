package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.fs.FileSystemFactory
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.source.impl.TypesExtractor

/**
 * Task to fetch files generated for the set of types declared
 * in the plugin.xml file of the current module.
 * TODO: User should confirm that he is ok pour replace existing files
 * @author Lionel HERVIER
 */
class FetchTypesTask extends BaseJPlatformTask {

	public FetchTypesTask(
			FileSystemFactory fsFactory, 
			String moduleName, 
			String jPlatformPath, 
			String modulePath) {
		super(fsFactory, moduleName, jPlatformPath, modulePath);
	}
	
	@Override
	public void run() {
		if( !this.currModule.exists ) {
			return
		}
		
		// Using type extractor to extract files from platform plugin
		// while getting the list of types to extract from current plugin
		new TypesExtractor().extractGeneratedFilesForTypes(
				this.currModule.pluginXml, 
				platformModule
		) { path ->
			this.platformModule.rootFs.getContentAsStream(path) { inStream ->
				this.currModule.rootFs.setContentFromStream(path, inStream)
			}
		}
	}

}
