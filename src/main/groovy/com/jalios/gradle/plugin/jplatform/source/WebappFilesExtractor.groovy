package com.jalios.gradle.plugin.jplatform.source

import java.io.File

import com.jalios.gradle.plugin.jplatform.ISourceFileExtractor
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.util.FileUtil

import groovy.lang.Closure

/**
 * Extract all the public files
 * TODO: Attribute "include" on plugin/webapp-files/file tags is not supported (and not documented)
 * TODO: Attribute "path" on plugin/public-files/file tags does not support wildcards (ie "css/portal/*.css")
 * @author Lionel HERVIER
 */
class WebappFilesExtractor extends BaseFilesExtractor {

	@Override
	String getTagName(JModule module) {
		return 'webapp-files'
	}
	
	@Override
	File getBaseFolder(JModule module) {
		return module.rootFolder;
	}
	
	@Override
	public String getBaseFolderPath(JModule module) {
		return "";
	}

}
