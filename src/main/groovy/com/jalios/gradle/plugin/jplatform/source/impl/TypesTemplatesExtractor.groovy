package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

/**
 * Declare files from "types" subfolder of public folder.
 * Types templates must be stored here.
 * 
 * FIXME: Read entries from plugin.xml
 * 
 * @author Lionel HERVIER
 */
class TypesTemplatesExtractor implements SourceFileExtractor {

	@Override
	public void extract(JModule module, Closure<String> closure) {
		if( !module.pubFs.exists("types") )
			return
		
		module.pubFs.paths("types/**/*") {path ->
			closure("${module.pubFsPath}/${path}")
		}
	}

}
