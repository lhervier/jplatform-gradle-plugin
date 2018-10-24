package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.jplatform.JPath
import com.jalios.gradle.plugin.jplatform.model.JFiles

/**
 * Base class for all FilesExtractors
 * TODO: Attribute "include" on plugin/[private|public|webapp]-files/file tags is not supported
 * @author Lionel HERVIER
 */
class BaseFilesExtractor {

	/**
	 * Extract files
	 */
	void extract(JFileSystem fs, FSType type, JFiles jfiles, Closure<JPath> closure) {
		jfiles.directories.each { dir ->
			fs.paths("${dir.path}/**/*", { closure(new JPath(type, it))})		// dir.path may be of the form "**/mydir"
		}
		jfiles.files.each { file ->
			fs.paths(file.path, { closure(new JPath(type, it))})				// file.path may be of the form "css/*.css"
		}
	}
}
