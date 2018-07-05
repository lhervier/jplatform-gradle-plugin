package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.PluginXml.JFiles
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

import groovy.lang.Closure

/**
 * Extract all the public files
 * TODO: Attribute "include" on plugin/webapp-files/file tags is not supported (and not documented)
 * TODO: Attribute "path" on plugin/public-files/file tags does not support wildcards (ie "css/portal/*.css")
 * @author Lionel HERVIER
 */
class WebappFilesExtractor extends BaseFilesExtractor implements SourceFileExtractor {

	@Override
	public void extract(JModule module, Closure<String> closure) {
		this.extract(module.rootFs, module.pluginXml.webappFiles, closure)
	}

}
