package com.jalios.gradle.plugin.jplatform

interface ISourceFileExtractor {

	void extract(JModule module, Closure<String> closure)
}
