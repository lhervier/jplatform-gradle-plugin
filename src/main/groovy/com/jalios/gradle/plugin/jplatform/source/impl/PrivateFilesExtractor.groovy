package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.JPath
import com.jalios.gradle.plugin.jplatform.model.JFiles
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

import groovy.lang.Closure

/**
 * Extract all the private files
 * TODO: Attribute "include" on plugin/private-files/file tags is not supported (and not documented)
 * @author Lionel HERVIER
 */
class PrivateFilesExtractor extends BaseFilesExtractor implements SourceFileExtractor {
	@Override
	public void extract(JModule module, Closure<JPath> closure) {
		this.extract(module.privFs, FSType.PRIVATE, module.pluginXml.privateFiles, closure)
	}
}
