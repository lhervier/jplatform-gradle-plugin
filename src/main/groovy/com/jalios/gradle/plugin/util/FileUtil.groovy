package com.jalios.gradle.plugin.util

import java.nio.file.Files

import com.jalios.gradle.plugin.jplatform.JModule

import groovy.io.FileType

class FileUtil {

	/**
	 * Return the list of all the files stored
	 * in a given folder
	 * @param folder the root folder
	 * @return the list of file path
	 */
	public static def paths(File folder) {
		def ret = []
		folder.eachFileRecurse(FileType.FILES) { file ->
			String rel = file.absolutePath.substring(folder.absolutePath.length())
			ret.push(rel.replace('\\', '/'))
		}
		return ret
	}
	
	/**
	 * Copy file from one module into another
	 * @param src
	 * @param dest
	 * @param path
	 */
	public static void copy(JModule src, JModule dest, String path) {
		println "Copying '${path}' from module '${src.name}' to '${dest.name}'"
		File srcFile = new File(src.rootFolder, path)
		File destFile = new File(dest.rootFolder, path)
		
		destFile.getParentFile().mkdirs()
		
		Files.copy(
				srcFile.toPath(),
				destFile.toPath(),
				java.nio.file.StandardCopyOption.REPLACE_EXISTING,
				java.nio.file.StandardCopyOption.COPY_ATTRIBUTES
		)
	
		// FileUtils.copyFile(new File(src.rootFolder, path), new File(dest.rootFolder, path), true)
	}
	
	/**
	 * Remove file
	 * @param module
	 * @param path
	 */
	public static void delete(JModule module, String path) {
		println "Removing '${path}' from module '${module.name}'"
		if( !new File(module.rootFolder, path).delete() ) {
			throw new Exception("Unable to remove file ${path} from module ${module.name}")
		}
	}
}
