package com.jalios.gradle.plugin.jplatform.source

import com.jalios.gradle.plugin.jplatform.SourceFileExtractor

/**
 * Declare files from "types" subfolder of public folder.
 * Types templates must be stored here.
 * 
 * FIXME: Read entries from plugin.xml
 * 
 * @author Lionel HERVIER
 */
class TypesTemplatesExtractor extends SourceFileExtractor {

	@Override
	public void extract(Closure<String> closure) {
		if( !this.module.pubFs.exists("types") )
			return
		
		this.module.pubFs.paths("types/**/*") {path ->
			closure("${this.module.pubFsPath}/${path}")
		}
	}

}
