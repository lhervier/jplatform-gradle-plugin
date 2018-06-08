package com.jalios.gradle.plugin.jplatform.source

import com.jalios.gradle.plugin.jplatform.ISourceFileExtractor
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.util.FileUtil

import groovy.lang.Closure

/**
 * Base class for all FilesExtractors
 * TODO: Attribute "include" on plugin/[private|public|webapp]-files/file tags is not supported (and not documented)
 * TODO: Attribute "path" on plugin/[private|public|webapp]-files/file tags does not support wildcards (ie "css/portal/*.css")
 * @author Lionel HERVIER
 */
abstract class BaseFilesExtractor implements ISourceFileExtractor {

	abstract String getTagName(JModule module)
	
	abstract File getBaseFolder(JModule module)
	
	abstract String getBaseFolderPath(JModule module)
	
	@Override
	public void extract(JModule module, Closure closure) {
		def tag = module.pluginXml[this.getTagName(module)]
		tag.directory.each { dirNode ->
			def dirPath = dirNode["@path"]
			def dirFolder = new File(this.getBaseFolder(module), dirPath)
			FileUtil.paths(dirFolder) {path ->
				closure("${this.getBaseFolderPath(module)}/${dirPath}/${path}")
			}
		}
		tag.file.each { file ->
			def filePath = file["@path"]
			closure("${this.getBaseFolderPath(module)}/${filePath}")
		}
	}

}
