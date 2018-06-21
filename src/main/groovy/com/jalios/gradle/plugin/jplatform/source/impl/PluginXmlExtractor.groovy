package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

import groovy.lang.Closure

class PluginXmlExtractor implements SourceFileExtractor {

	@Override
	public void extract(JModule module, Closure<String> closure) {
		closure("${module.privFsPath}/plugin.xml")
	}

}
