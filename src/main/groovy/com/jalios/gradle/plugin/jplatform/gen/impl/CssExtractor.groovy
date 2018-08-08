package com.jalios.gradle.plugin.jplatform.gen.impl

import java.util.List

import com.jalios.gradle.plugin.fs.JPath
import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.gen.GeneratedFileExtractor
import com.jalios.gradle.plugin.jplatform.gen.GeneratedPath

class CssExtractor implements GeneratedFileExtractor {

	@Override
	public void extract(JModule module, Closure<GeneratedPath> closure) {
		module.pluginProp.each("channel.less.") { key, value ->
			closure(new GeneratedPath(
					path: new JPath(FSType.ROOT, key.substring("channel.less.".length())),
					source: new JPath(FSType.ROOT, value)
			))
		}
	}

}
