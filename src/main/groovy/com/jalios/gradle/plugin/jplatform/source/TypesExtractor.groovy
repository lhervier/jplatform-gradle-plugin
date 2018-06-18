package com.jalios.gradle.plugin.jplatform.source

import com.jalios.gradle.plugin.jplatform.ISourceFileExtractor
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.PluginXml
import com.jalios.gradle.plugin.util.FileUtil

import groovy.lang.Closure

/**
 * Extract declared types (and templates)
 * @author Lionel HERVIER
 */
class TypesExtractor implements ISourceFileExtractor {

	/**
	 * Extract all source files from types
	 */
	@Override
	public void extract(JModule module, Closure closure) {
		module.pluginXml.types.types.each { type ->
			this.extractTypeXml(type.name, module, closure)
			this.extractTypeStdJsps(type.name, module, closure)
			this.extractTypeJsps(type.name, module, closure)
		}
	}
	
	/**
	 * Extract types generated files.
	 * Method used by FetchTypesTask
	 */
	void extractGeneratedFilesForTypes(PluginXml pluginXml, JModule module, Closure closure) {
		pluginXml.types.types.each { type ->
			this.extractTypeXml(type.name, module, closure)
			this.extractTypeStdJsps(type.name, module, closure)
		}
	}
	
	/**
	 * Extract xml and templates for a given type in a given module. 
	 * @param type
	 * @param module
	 * @param closure
	 */
	void extractTypeXml(String name, JModule module, Closure closure) {
		// Add xml file
		def xml = "WEB-INF/data/types/${name}/${name}.xml"
		if( new File(module.rootFolder, xml).exists() ) {
			closure(xml)
		}
		
		// Add template file
		def templates = "WEB-INF/data/types/${name}/${name}-templates.xml"
		if( new File(module.rootFolder, templates).exists() ) {
			closure(templates)
		}
	}
	
	private List<String> getStdJsps(String name) {
		return [
			"doEdit${name}.jsp",
			"doEdit${name}Modal.jsp",
			"do${name}Diff.jsp",
			"do${name}FormHandler.jsp",
			"do${name}FullDisplay.jsp",
			"do${name}Report.jsp",
			"do${name}ResultDisplay.jsp",
			"editForm${name}.jsp",
			"edit${name}.jsp",
			"edit${name}Modal.jsp"
		]
	}
	
	/**
	 * Extract standard jsp for a given type in a given module
	 */
	void extractTypeStdJsps(String name, JModule module, Closure closure) {
		this.getStdJsps(name).each { jsp ->
			String path = "types/${name}/${jsp}"
			if( new File(module.rootFolder, path).exists() ) {
				closure(path)
			}
		}
	}
	
	/**
	 * Extract all other jsps for a given type in a given module
	 */
	void extractTypeJsps(String name, JModule module, Closure closure) {
		List<String> stds = this.getStdJsps(name)
		// Add jsps from type folder
		FileUtil.paths(new File(module.rootFolder, "types/${name}")) { path ->
			if( !path.endsWith(".jsp") ) {
				return
			}
			if( !stds.contains(path) ) {
				return
			}
			closure("types/${name}/${path}")
		}
	}
}
