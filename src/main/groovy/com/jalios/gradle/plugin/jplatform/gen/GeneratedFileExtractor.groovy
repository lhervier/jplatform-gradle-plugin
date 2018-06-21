package com.jalios.gradle.plugin.jplatform.gen

import com.jalios.gradle.plugin.jplatform.JModule

interface GeneratedFileExtractor {

	void extract(JModule module, Closure<GeneratedPath> closure)
}
