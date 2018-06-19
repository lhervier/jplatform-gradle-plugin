package com.jalios.gradle.plugin.jplatform.source

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.PluginXml.JFiles

/**
 * Extract all the private files
 * TODO: Attribute "include" on plugin/private-files/file tags is not supported (and not documented)
 * TODO: Attribute "path" on plugin/private-files/file tags does not support wildcards (ie "css/portal/*.css")
 * @author Lionel HERVIER
 */
class PrivateFilesExtractor extends BaseFilesExtractor {

	@Override
	JFiles getJFiles(JModule module) {
		return module.pluginXml.privateFiles
	}
	
	@Override
	String getFolderPath(JModule module) {
		return module.privFolderPath;
	}
}
