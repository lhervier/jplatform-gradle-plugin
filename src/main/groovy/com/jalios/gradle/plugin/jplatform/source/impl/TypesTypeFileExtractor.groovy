package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.PluginXml
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

/**
 * Extract files from types/type tags of the plugin.xml
 * @author Lionel HERVIER
 */
class TypesTypeFileExtractor implements SourceFileExtractor {

	@Override
	public void extract(JModule module, Closure<String> closure) {
		this.extract(module, module.pluginXml, closure)
	}
	
	void extract(JModule module, PluginXml pluginXml, Closure<String> closure) {
		pluginXml.types.types.each { type ->
			type.files.each { file ->
				module.rootFs.paths("types/${type.name}/${file.path}") { path ->
					closure(path.toString())
				}
			}
		}
	}
}
