package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.PluginXml
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

/**
 * Extract types standard jsps
 * @author Lionel HERVIER
 */
class TypesStdJspExtractor implements SourceFileExtractor {

	private List<String> getStdJsps(String name) {
		return [
			"types/${name}/doEdit${name}.jsp",
			"types/${name}/doEdit${name}Modal.jsp",
			"types/${name}/do${name}Diff.jsp",
			"types/${name}/do${name}FormHandler.jsp",
			"types/${name}/do${name}FullDisplay.jsp",
			"types/${name}/do${name}Report.jsp",
			"types/${name}/do${name}ResultDisplay.jsp",
			"types/${name}/editForm${name}.jsp",
			"types/${name}/edit${name}.jsp",
			"types/${name}/edit${name}Modal.jsp"
		]
	}
	
	/**
	 * Extract all source files from types
	 */
	@Override
	void extract(JModule module, Closure<String> closure) {
		this.extract(module, module.pluginXml, closure)
	}
	
	void extract(JModule module, PluginXml pluginXml, Closure<String> closure) {
		pluginXml.types.types.each { type ->
			this.getStdJsps(type.name).each { jsp ->
				if( module.rootFs.exists(jsp) ) {
					closure(jsp)
				}
			}
		}
	}
}
