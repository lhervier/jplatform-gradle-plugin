package com.jalios.gradle.plugin.jplatform.source

import java.io.File

import com.jalios.gradle.plugin.jplatform.ISourceFileExtractor
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.util.FileUtil

import groovy.lang.Closure

/**
 * Extract all the private files
 * TODO: Attribute "include" on plugin/private-files/file tags is not supported (and not documented)
 * TODO: Attribute "path" on plugin/private-files/file tags does not support wildcards (ie "css/portal/*.css")
 * @author Lionel HERVIER
 */
class PrivateFilesExtractor extends BaseFilesExtractor {

	@Override
	String getTagName(JModule module) {
		return 'private-files'
	}
	
	@Override
	File getBaseFolder(JModule module) {
		return module.privFolder;
	}
	
	@Override
	public String getBaseFolderPath(JModule module) {
		return module.privFolderPath;
	}
}
