package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.jplatform.PluginXml
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

/**
 * Extract declared types (and templates)
 * @author Lionel HERVIER
 */
class TypesExtractor extends SourceFileExtractor {

	/**
	 * Extract all source files from types
	 */
	@Override
	public void extract(Closure<String> closure) {
		this.module.pluginXml.types.types.each { type ->
			this.extractTypeXml(type.name, closure)
			this.extractTypeStdJsps(type.name, closure)
			this.extractTypeJsps(type.name, closure)
		}
	}
	
	/**
	 * Extract types generated files.
	 * Method used by FetchTypesTask
	 */
	void extractGeneratedFilesForTypes(PluginXml pluginXml, Closure closure) {
		pluginXml.types.types.each { type ->
			this.extractTypeXml(type.name, closure)
			this.extractTypeStdJsps(type.name, closure)
		}
	}
	
	/**
	 * Extract xml and templates for a given type in a given module. 
	 * @param type
	 * @param module
	 * @param closure
	 */
	void extractTypeXml(String name, Closure closure) {
		// Add xml file
		def xml = "WEB-INF/data/types/${name}/${name}.xml"
		if( this.module.rootFs.exists(xml) ) {
			closure(xml)
		}
		
		// Add template file
		def templates = "WEB-INF/data/types/${name}/${name}-templates.xml"
		if( this.module.rootFs.exists(templates) ) {
			closure(templates)
		}
	}
	
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
	 * Extract standard jsp for a given type in a given module
	 */
	void extractTypeStdJsps(String name, Closure closure) {
		this.getStdJsps(name).each { jsp ->
			String path = "types/${name}/${jsp}"
			if( this.module.rootFs.exists(path) ) {
				closure(path)
			}
		}
	}
	
	/**
	 * Extract all other jsps for a given type in a given module
	 */
	void extractTypeJsps(String name, Closure closure) {
		List<String> stds = this.getStdJsps(name)
		this.module.rootFs.paths("types/${name}/**/*.jsp") { path ->
			if( !stds.contains(path) ) {
				return
			}
			closure(path)
		}
	}
}
