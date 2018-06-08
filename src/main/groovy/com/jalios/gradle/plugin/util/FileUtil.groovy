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
	public static void paths(File folder, Closure closure) {
		if( !folder.exists() ) {
			return
		}
		folder.eachFileRecurse(FileType.FILES) { file ->
			String rel = file.absolutePath.substring(folder.absolutePath.length() + 1)
			closure(rel.replace('\\', '/'))
		}
	}
	
	/**
	 * Copy file from one module into another
	 * @param src
	 * @param dest
	 * @param path
	 */
	public static void copy(File srcFolder, File destFolder, String path) {
		println "Copying '${path}' from '${srcFolder}' to '${destFolder}'"
		File srcFile = new File(srcFolder, path)
		if( !srcFile.exists() ) {
			println "  => It does not exist..."
			return
		}
		File destFile = new File(destFolder, path)
		
		destFile.getParentFile().mkdirs()
		
		Files.copy(
				srcFile.toPath(),
				destFile.toPath(),
				java.nio.file.StandardCopyOption.REPLACE_EXISTING,
				java.nio.file.StandardCopyOption.COPY_ATTRIBUTES
		)
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
