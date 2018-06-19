package com.jalios.gradle.plugin.jplatform.gen

import java.util.List

import com.jalios.gradle.plugin.jplatform.GeneratedFile
import com.jalios.gradle.plugin.jplatform.IGeneratedFileExtractor
import com.jalios.gradle.plugin.jplatform.JModule

class CssExtractor implements IGeneratedFileExtractor {

	@Override
	public void extract(JModule module, Closure<GeneratedFile> closure) {
		module.pluginProp.each("channel.less.") { key, value ->
			closure(new GeneratedFile(
					path: key.substring("channel.less.".length()),
					source: value
			))
		}
	}

}
