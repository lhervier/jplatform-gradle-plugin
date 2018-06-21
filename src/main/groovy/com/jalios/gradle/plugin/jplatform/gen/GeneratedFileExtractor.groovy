package com.jalios.gradle.plugin.jplatform.gen

import com.jalios.gradle.plugin.jplatform.JModule

abstract class GeneratedFileExtractor {

	JModule module
	
	abstract void extract(Closure<GeneratedPath> closure)
}
