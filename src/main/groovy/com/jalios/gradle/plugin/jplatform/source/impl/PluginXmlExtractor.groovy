package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.fs.JPath
import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

import groovy.lang.Closure

class PluginXmlExtractor implements SourceFileExtractor {

	@Override
	void extract(JModule module, Closure<JPath> closure) {
		closure(new JPath(FSType.PRIVATE, "plugin.xml"))
	}

}
