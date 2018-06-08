package com.jalios.gradle.plugin.jplatform.source

import java.io.File

import com.jalios.gradle.plugin.jplatform.ISourceFileExtractor
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.util.FileUtil

import groovy.lang.Closure

/**
 * Extract all the public files
 * TODO: Attribute "include" on plugin/public-files/file tags is not supported (and not documented)
 * TODO: Attribute "path" on plugin/public-files/file tags does not support wildcards (ie "css/portal/*.css")
 * @author Lionel HERVIER
 */
class PublicFilesExtractor extends BaseFilesExtractor {

	@Override
	String getTagName(JModule module) {
		return 'public-files'
	}
	
	@Override
	File getBaseFolder(JModule module) {
		return module.pubFolder;
	}
	
	@Override
	public String getBaseFolderPath(JModule module) {
		return module.pubFolderPath;
	}

}
