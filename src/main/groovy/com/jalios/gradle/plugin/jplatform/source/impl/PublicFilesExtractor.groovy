package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.PluginXml.JFiles

import groovy.lang.Closure

/**
 * Extract all the public files
 * TODO: Attribute "include" on plugin/public-files/file tags is not supported (and not documented)
 * TODO: Attribute "path" on plugin/public-files/file tags does not support wildcards (ie "css/portal/*.css")
 * @author Lionel HERVIER
 */
class PublicFilesExtractor extends BaseFilesExtractor {

	@Override
	public void extract(Closure<String> closure) {
		this.extract(this.module.pubFs, this.module.pluginXml.publicFiles) { path ->
			closure("${this.module.pubFsPath}/${path}")
		}
	}

}
