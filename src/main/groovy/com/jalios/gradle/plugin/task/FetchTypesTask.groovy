package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.source.impl.TypesStdJspExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.TypesTypeTemplatesXmlExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.TypesTypeXmlExtractor

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
		
		// Fetch type.xml file
		new TypesTypeXmlExtractor().extract(platformModule, currModule.pluginXml) { path ->
			platformModule.rootFs.getContentAsStream(path) { inStream ->
				currModule.rootFs.setContentFromStream(path, inStream)
			}
		}
		
		// Fetch type-templates.xml file
		new TypesTypeTemplatesXmlExtractor().extract(platformModule, currModule.pluginXml) { path ->
			platformModule.rootFs.getContentAsStream(path) { inStream ->
				currModule.rootFs.setContentFromStream(path, inStream)
			}
		}
		
		// Fetch the standard JSPs
		new TypesStdJspExtractor().extract(platformModule, currModule.pluginXml) { path ->
			platformModule.rootFs.getContentAsStream(path) { inStream ->
				currModule.rootFs.setContentFromStream(path, inStream)
			}
		}
	}

}
