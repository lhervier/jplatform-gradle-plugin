package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.fs.JPath
import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.model.JFiles
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

import groovy.lang.Closure

/**
 * Extract all the public files
 * TODO: Attribute "include" on plugin/public-files/file tags is not supported (and not documented)
 * TODO: Attribute "path" on plugin/public-files/file tags does not support wildcards (ie "css/portal/*.css")
 * @author Lionel HERVIER
 */
class PublicFilesExtractor extends BaseFilesExtractor implements SourceFileExtractor {

	@Override
	public void extract(JModule module, Closure<JPath> closure) {
		this.extract(module.pubFs, FSType.PUBLIC, module.pluginXml.publicFiles, closure)
	}

}
