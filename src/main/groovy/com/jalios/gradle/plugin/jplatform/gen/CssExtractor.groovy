package com.jalios.gradle.plugin.jplatform.gen

import java.util.List

import com.jalios.gradle.plugin.jplatform.GeneratedPath
import com.jalios.gradle.plugin.jplatform.GeneratedFileExtractor
import com.jalios.gradle.plugin.jplatform.JModule

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
