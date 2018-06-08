package com.jalios.gradle.plugin.jplatform.source

import com.jalios.gradle.plugin.jplatform.ISourceFileExtractor
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.util.FileUtil

import groovy.lang.Closure

/**
 * Extract declared types (and templates)
 * @author Lionel HERVIER
 */
class TypesExtractor implements ISourceFileExtractor {

	@Override
	public void extract(JModule module, Closure closure) {
		// Extractng all types from current module
		this.extractTypes(module.pluginXml.types.type, module, closure)
	}

	/**
	 * Extract files for a given set of types in a given module.
	 * Used by FetchTypesTask
	 * @param types types to extract
	 * @param module module where to find the types
	 * @param closure
	 */
	void extractTypes(def types, JModule module, Closure closure) {
		types.each { typeNode ->
			this.extractType(typeNode["@name"], module, closure)
		}
	}
	
	/**
	 * Extract files for a given type in a given module. 
	 * @param type
	 * @param module
	 * @param closure
	 */
	void extractType(String name, JModule module, Closure closure) {
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
		
		// Add jsps from type folder
		FileUtil.paths(new File(module.rootFolder, "types/${name}")) { path ->
			if( !path.endsWith(".jsp") ) {
				return
			}
			closure("types/${name}/${path}")
		}
	}
}
