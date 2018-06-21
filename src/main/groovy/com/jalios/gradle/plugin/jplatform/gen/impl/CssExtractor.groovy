package com.jalios.gradle.plugin.jplatform.gen.impl

import java.util.List

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.gen.GeneratedFileExtractor
import com.jalios.gradle.plugin.jplatform.gen.GeneratedPath

class CssExtractor extends GeneratedFileExtractor {

	@Override
	public void extract(Closure<GeneratedPath> closure) {
		this.module.pluginProp.each("channel.less.") { key, value ->
			closure(new GeneratedPath(
					path: key.substring("channel.less.".length()),
					source: value
			))
		}
	}

}
