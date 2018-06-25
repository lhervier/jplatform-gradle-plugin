package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.PluginXml
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

/**
 * Extract declared types (and templates)
 * @author Lionel HERVIER
 */
class TypesExtractor implements SourceFileExtractor {

	/**
	 * Extract all source files from types
	 */
	@Override
	public void extract(JModule module, Closure<String> closure) {
		module.pluginXml.types.types.each { type ->
			this.extractTypeXml(module, type.name, closure)
			this.extractTypeStdJsps(module, type.name, closure)
			this.extractTypeJsps(module, type.name, closure)
		}
	}
	
	/**
	 * Extract types generated files.
	 * Method used by FetchTypesTask
	 */
	void extractGeneratedFilesForTypes(JModule module, PluginXml pluginXml, Closure closure) {
		pluginXml.types.types.each { type ->
			this.extractTypeXml(module, type.name, closure)
			this.extractTypeStdJsps(module, type.name, closure)
		}
	}
	
	/**
	 * Extract xml and templates for a given type in a given module. 
	 * @param type
	 * @param module
	 * @param closure
	 */
	void extractTypeXml(JModule module, String name, Closure closure) {
		// Add xml file
		def xml = "WEB-INF/data/types/${name}/${name}.xml"
		if( !module.rootFs.exists(xml) ) {
			throw new JTaskException("Type '${name}' does not exist in module '${module.name}'")
		}
		closure(xml)
		
		// Add template file
		def templates = "WEB-INF/data/types/${name}/${name}-templates.xml"
		if( module.rootFs.exists(templates) ) {
			closure(templates)
		}
	}
	
	private List<String> getStdJsps(JModule module, String name) {
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
	 * Extract standard jsp for a given type in a given module
	 */
	void extractTypeStdJsps(JModule module, String name, Closure closure) {
		this.getStdJsps(module, name).each { jsp ->
			if( module.rootFs.exists(jsp) ) {
				closure(jsp)
			}
		}
	}
	
	/**
	 * Extract all other jsps for a given type in a given module
	 */
	void extractTypeJsps(JModule module, String name, Closure closure) {
		List<String> stds = this.getStdJsps(module, name)
		module.rootFs.paths("types/${name}/**/*.jsp") { path ->
			if( !stds.contains(path) ) {
				return
			}
			closure(path)
		}
	}
}
