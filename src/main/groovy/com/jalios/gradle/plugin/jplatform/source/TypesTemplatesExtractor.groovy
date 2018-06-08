package com.jalios.gradle.plugin.jplatform.source

import com.jalios.gradle.plugin.jplatform.ISourceFileExtractor
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.util.FileUtil

import groovy.lang.Closure

/**
 * Declare files from "types" subfolder of public folder.
 * Types templates must be stored here.
 * @author Lionel HERVIER
 */
class TypesTemplatesExtractor implements ISourceFileExtractor {

	@Override
	public void extract(JModule module, Closure closure) {
		File pubTypes = new File(module.pubFolder, "types")
		if( !pubTypes.exists() )
			return
		
		FileUtil.paths(pubTypes) {path ->
			closure("${module.pubFolderPath}/types/${path}")
		}
	}

}
