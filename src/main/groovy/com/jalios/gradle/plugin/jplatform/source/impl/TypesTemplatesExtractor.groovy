package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.PluginXml
import com.jalios.gradle.plugin.jplatform.TemplateXml
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

/**
 * Extract types template files
 * @author Lionel HERVIER
 */
class TypesTemplatesExtractor implements SourceFileExtractor {

	/**
	 * Extract all source files from types
	 */
	@Override
	public void extract(JModule module, Closure<String> closure) {
		// Extract templates from types xml file
		module.pluginXml.types.types.each { type ->
			def xml = "WEB-INF/data/types/${type.name}/${type.name}-templates.xml"
			if( module.rootFs.exists(xml) ) {
				closure(xml.toString())
				module.rootFs.getContentAsReader(xml, "UTF-8") { reader ->
					TemplateXml templateXml = new TemplateXml(type.name, reader)
					templateXml.templates.each { tmpl ->
						closure("types/${type.name}/${tmpl.file}".toString())
					}
				}
			}
		}
		
		// Extract templates from plugin.xml
		module.pluginXml.types.templates.each { tmpls ->
			String type = tmpls.type
			tmpls.templates.each { tmpl ->
				closure("types/${type}/${tmpl.file}".toString())
			}
		}
	}
}
