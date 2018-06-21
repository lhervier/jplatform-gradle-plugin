package com.jalios.gradle.plugin.jplatform.source

import com.jalios.gradle.plugin.jplatform.JModule

abstract class SourceFileExtractor {

	JModule module
	
	abstract void extract(Closure<String> closure)
}
