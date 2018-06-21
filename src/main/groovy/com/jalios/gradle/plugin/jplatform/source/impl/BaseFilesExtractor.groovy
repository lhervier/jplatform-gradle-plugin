package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.fs.FileSystem
import com.jalios.gradle.plugin.jplatform.PluginXml.JFiles
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

/**
 * Base class for all FilesExtractors
 * TODO: Attribute "include" on plugin/[private|public|webapp]-files/file tags is not supported (and not documented)
 * TODO: Attribute "path" on plugin/[private|public|webapp]-files/file tags does not support wildcards (ie "css/portal/*.css")
 * @author Lionel HERVIER
 */
abstract class BaseFilesExtractor implements SourceFileExtractor {

	/**
	 * Extract files
	 */
	protected void extract(FileSystem fs, JFiles jfiles, Closure<String> closure) {
		jfiles.directories.each { dir ->
			fs.paths("${dir.path}/**/*", closure)
		}
		jfiles.files.each { file ->
			fs.paths(file.path, closure)
		}
	}
}
