package com.jalios.gradle.plugin.jplatform.source

import com.jalios.gradle.plugin.jplatform.SourceFileExtractor
import com.jalios.gradle.plugin.jplatform.JModule

import groovy.lang.Closure

class PluginXmlExtractor extends SourceFileExtractor {

	@Override
	public void extract(Closure<String> closure) {
		closure("${module.privFsPath}/plugin.xml")
	}

}
