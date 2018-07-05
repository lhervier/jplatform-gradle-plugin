package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.PluginXml.JFiles
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

import groovy.lang.Closure

/**
 * Extract all the private files
 * TODO: Attribute "include" on plugin/private-files/file tags is not supported (and not documented)
 * @author Lionel HERVIER
 */
class PrivateFilesExtractor extends BaseFilesExtractor implements SourceFileExtractor {
	@Override
	public void extract(JModule module, Closure<String> closure) {
		this.extract(module.privFs, module.pluginXml.privateFiles) { path ->
			closure("${module.privFsPath}/${path}")
		}
	}
}
