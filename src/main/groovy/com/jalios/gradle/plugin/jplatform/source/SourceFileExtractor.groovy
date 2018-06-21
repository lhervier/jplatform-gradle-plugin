package com.jalios.gradle.plugin.jplatform.source

import com.jalios.gradle.plugin.jplatform.JModule

interface SourceFileExtractor {

	void extract(JModule module, Closure<String> closure)
}
