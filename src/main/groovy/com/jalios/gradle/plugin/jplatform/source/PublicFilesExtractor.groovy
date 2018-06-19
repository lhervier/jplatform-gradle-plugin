package com.jalios.gradle.plugin.jplatform.source

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.PluginXml.JFiles

/**
 * Extract all the public files
 * TODO: Attribute "include" on plugin/public-files/file tags is not supported (and not documented)
 * TODO: Attribute "path" on plugin/public-files/file tags does not support wildcards (ie "css/portal/*.css")
 * @author Lionel HERVIER
 */
class PublicFilesExtractor extends BaseFilesExtractor {

	@Override
	JFiles getJFiles(JModule module) {
		return module.pluginXml.publicFiles
	}
	
	@Override
	String getFolderPath(JModule module) {
		return module.pubFolderPath;
	}

}
