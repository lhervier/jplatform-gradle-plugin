package com.jalios.gradle.plugin.jplatform

abstract class GeneratedFileExtractor {

	JModule module
	
	abstract void extract(Closure<GeneratedPath> closure)
}
