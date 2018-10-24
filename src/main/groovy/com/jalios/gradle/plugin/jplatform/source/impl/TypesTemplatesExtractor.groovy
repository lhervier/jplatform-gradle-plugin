package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.JPath
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
	public void extract(JModule module, Closure<JPath> closure) {
		// Extract templates from types xml file
		module.pluginXml.types.types.each { type ->
			def xml = "types/${type.name}/${type.name}-templates.xml"
			if( module.dataFs.exists(xml) ) {
				closure(new JPath(FSType.DATA, xml))
				module.dataFs.getContentAsReader(xml, "UTF-8") { reader ->
					TemplateXml templateXml = new TemplateXml(type.name, reader)
					templateXml.templates.each { tmpl ->
						closure(new JPath(FSType.ROOT, "types/${type.name}/${tmpl.file}"))
					}
				}
			}
		}
		
		// Extract templates from plugin.xml
		module.pluginXml.types.templates.each { tmpls ->
			String type = tmpls.type
			tmpls.templates.each { tmpl ->
				closure(new JPath(FSType.ROOT, "types/${type}/${tmpl.file}"))
			}
		}
	}
}
