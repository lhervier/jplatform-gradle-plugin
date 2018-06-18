package com.jalios.gradle.plugin.jplatform.source

import com.jalios.gradle.plugin.jplatform.ISourceFileExtractor
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.PluginXml.JFiles
import com.jalios.gradle.plugin.util.FileUtil

import groovy.lang.Closure

/**
 * Base class for all FilesExtractors
 * TODO: Attribute "include" on plugin/[private|public|webapp]-files/file tags is not supported (and not documented)
 * TODO: Attribute "path" on plugin/[private|public|webapp]-files/file tags does not support wildcards (ie "css/portal/*.css")
 * @author Lionel HERVIER
 */
abstract class BaseFilesExtractor implements ISourceFileExtractor {

	abstract JFiles getTagName(JModule module)
	
	abstract File getBaseFolder(JModule module)
	
	abstract String getBaseFolderPath(JModule module)
	
	@Override
	public void extract(JModule module, Closure closure) {
		this.getTagName(module).directories.each { dir ->
			def dirFolder = new File(this.getBaseFolder(module), dir.path)
			FileUtil.paths(dirFolder) {path ->
				closure("${this.getBaseFolderPath(module)}/${dir.path}/${path}")
			}
		}
		
		this.getTagName(module).files.each { file ->
			closure("${this.getBaseFolderPath(module)}/${file.path}")
		}
	}

}
