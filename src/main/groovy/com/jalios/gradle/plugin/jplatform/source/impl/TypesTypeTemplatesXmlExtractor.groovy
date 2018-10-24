package com.jalios.gradle.plugin.jplatform.source.impl

import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.JPath
import com.jalios.gradle.plugin.jplatform.PluginXml
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

import groovy.lang.Closure

/**
 * Extract files from types/type tags of the plugin.xml
 * @author Lionel HERVIER
 */
class TypesTypeTemplatesXmlExtractor implements SourceFileExtractor {

	@Override
	public void extract(JModule module, Closure<JPath> closure) {
		this.extract(module, module.pluginXml, closure)
	}
	
	void extract(JModule module, PluginXml pluginXml, Closure<JPath> closure) {
		pluginXml.types.types.each { type ->
			String xml = "types/${type.name}/${type.name}-templates.xml"
			if( module.dataFs.exists(xml) ) {
				closure(new JPath(FSType.DATA, xml))
			}
		}
	}
}
