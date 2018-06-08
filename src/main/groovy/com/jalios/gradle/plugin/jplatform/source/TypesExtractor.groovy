package com.jalios.gradle.plugin.jplatform.source

import com.jalios.gradle.plugin.jplatform.ISourceFileExtractor
import com.jalios.gradle.plugin.jplatform.JModule

import groovy.lang.Closure

/**
 * Extract declared types (and templates)
 * @author Lionel HERVIER
 */
class TypesExtractor implements ISourceFileExtractor {

	@Override
	public void extract(JModule module, Closure closure) {
		module.pluginXml.types.type.each { t ->
			def name = t["@name"]
			def xml = "WEB-INF/data/types/${name}/${name}.xml"
			if( new File(module.rootFolder, xml).exists() )
				closure(xml)
			def templates = "WEB-INF/data/types/${name}/${name}-templates.xml"
			if( new File(module.rootFolder, templates).exists() )
				closure(templates)
		}
	}

}
