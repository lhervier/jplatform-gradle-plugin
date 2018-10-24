package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.JPath
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

class JarsExtractor implements SourceFileExtractor {

	@Override
	public void extract(JModule module, Closure<JPath> closure) {
		module.pluginXml.jars.each { jar ->
			closure(new JPath(FSType.ROOT, "WEB-INF/lib/${jar.path}"))
		}
	}
}
