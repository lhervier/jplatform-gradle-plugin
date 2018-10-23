package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.source.impl.TypesTypeXmlExtractor

/**
 * Task to push Types declared
 * in the plugin.xml file of the current module.
 * @author Lionel HERVIER
 */
class PushTypesTask extends BaseJPlatformTaskImpl {

	@Override
	void run(JModule platformModule, JModule currModule) throws JTaskException {
		if( currModule.pluginXml == null ) {
			throw new JTaskException("Current module is empty... Unable to push types")
		}
		
		// Remove types declared in jplatform
		if( platformModule.pluginXml != null ) {
			new TypesTypeXmlExtractor().extract(platformModule) { jpath ->
				platformModule.getFs(jpath.type).delete(jpath.path)
			}
		}
		
		// Copy types from current module into platform
		new TypesTypeXmlExtractor().extract(currModule) { jpath ->
			currModule.getFs(jpath.type).getContentAsStream(jpath.path) { inStream ->
				platformModule.getFs(jpath.type).setContentFromStream(jpath.path, inStream)
			}
		}
	}

}
