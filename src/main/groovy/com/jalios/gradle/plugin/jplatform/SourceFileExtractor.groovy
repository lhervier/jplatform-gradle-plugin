package com.jalios.gradle.plugin.jplatform

abstract class SourceFileExtractor {

	JModule module
	
	abstract void extract(Closure<String> closure)
}
