package com.jalios.gradle.plugin.jplatform.source

import org.gradle.api.AntBuilder

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

	/**
	 * Return the list of files to include
	 * @param module the module
	 * @return the JFiles
	 */
	abstract JFiles getJFiles(JModule module)
	
	/**
	 * Return the path of the folder to search into
	 * @param module the module
	 * @return the path
	 */
	abstract String getFolderPath(JModule module)
	
	/**
	 * Extract files
	 */
	@Override
	public void extract(JModule module, Closure<String> closure) {
		JFiles jfiles = this.getJFiles(module)
		String folderPath = this.getFolderPath(module)
		File folder = new File(module.rootFolder, folderPath)
		
		jfiles.directories.each { dir ->
			File dirFolder = new File(folder, dir.path)
			FileUtil.paths(dirFolder) {path ->
				closure("${folderPath}/${dir.path}/${path}")
			}
		}
		
		jfiles.files.each { file ->
			FileUtil.paths(folder, file.path) { path ->
				closure("${folderPath}/${file.path}")
			}
		}
	}
}
