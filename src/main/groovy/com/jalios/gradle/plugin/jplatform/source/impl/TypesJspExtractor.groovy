package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.PluginXml
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

/**
 * Extract types custom jsps
 * FIXME: Really needed ?
 * @author Lionel HERVIER
 */
class TypesJspExtractor implements SourceFileExtractor {

	TypesStdJspExtractor stdJspExtractor = new TypesStdJspExtractor()
	
	@Override
	void extract(JModule module, Closure<String> closure) {
		List<String> std = new ArrayList()
		stdJspExtractor.extract(module) { path ->
			std.add(path.toString())
		}
		
		module.pluginXml.types.types.each { type ->
			module.rootFs.paths("types/${type.name}/*.jsp") { path ->
				if( !std.contains(path.toString())) {
					closure(path)
				}
			}
		}
	}
}
