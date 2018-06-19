package com.jalios.gradle.plugin.jplatform.source

import com.jalios.gradle.plugin.jplatform.ISourceFileExtractor
import com.jalios.gradle.plugin.jplatform.JModule

import groovy.lang.Closure

class PluginXmlExtractor implements ISourceFileExtractor {

	@Override
	public void extract(JModule module, Closure<String> closure) {
		closure("${module.privFolderPath}/plugin.xml")
	}

}
