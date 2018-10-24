package com.jalios.gradle.plugin.jplatform.source

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.JPath

interface SourceFileExtractor {

	void extract(JModule module, Closure<JPath> closure)
}
