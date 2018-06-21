package com.jalios.gradle.plugin.jplatform.source

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.PluginXml.JFiles

import groovy.lang.Closure

/**
 * Extract all the private files
 * TODO: Attribute "include" on plugin/private-files/file tags is not supported (and not documented)
 * @author Lionel HERVIER
 */
class PrivateFilesExtractor extends BaseFilesExtractor {
	@Override
	public void extract(Closure<String> closure) {
		this.extract(this.module.privFs, this.module.pluginXml.privateFiles) { path ->
			closure("${this.module.privFsPath}/${path}")
		}
	}
}
