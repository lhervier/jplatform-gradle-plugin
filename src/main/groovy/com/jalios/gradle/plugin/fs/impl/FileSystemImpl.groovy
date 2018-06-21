package com.jalios.gradle.plugin.fs.impl

import java.io.InputStream
import java.nio.file.Files

import com.jalios.gradle.plugin.fs.FileSystem
import com.jalios.gradle.plugin.jplatform.JModule

import groovy.io.FileType
import net.rubygrapefruit.platform.FileSystemInfo

class FileSystemImpl extends FileSystem {

	/**
	 * The root folder
	 */
	private File rootFolder
	
	/**
	 * Constructor
	 */
	public FileSystemImpl(File rootFolder) {
		if( !rootFolder.absolute ) {
			throw new Exception("FileSystem root folder must be absolute")
		}
		this.rootFolder = rootFolder
	}
	
	@Override
	public FileSystem createFrom(String path) {
		return new FileSystemImpl(new File(this.rootFolder, path));
	}

	@Override
	boolean exists(String path) {
		return new File(this.rootFolder, path).exists()
	}
	
	@Override
	void paths(String pattern, Closure<String> closure) {
		def ant = new AntBuilder()
		def scanner = ant.fileScanner {
			fileset(dir: rootFolder.absolutePath) {
				include(name: pattern)
			}
		}
		for( File file in scanner ) {
			String rel = file.absolutePath.substring(this.rootFolder.absolutePath.length() + 1)
			closure(rel.replace('\\', '/'))
		}
	}
	
	@Override
	void setContentFromStream(String path, InputStream inStream) {
		File destFile = new File(this.rootFolder, path)
		println "Setting content of '${destFile.absolutePath}'"
		if( destFile.exists() ) {
			if( !destFile.delete() ) {
				throw new Exception("Unable to replace existing file...")
			}
		}
		destFile.getParentFile().mkdirs()
		destFile.bytes = inStream.bytes
	}
	
	@Override
	public void getContentAsStream(String path, Closure<InputStream> closure) {
		File f = new File(this.rootFolder, path)
		InputStream fileIn = new FileInputStream(f);
		try {
			closure(fileIn)
		} finally {
			fileIn.close()
		}
	}
	
	@Override
	void delete(String path) {
		File f = new File(this.rootFolder, path)
		println "Removing '${f.absolutePath}'"
		if( !f.exists() ) {
			return
		}
		if( !f.delete() ) {
			throw new Exception("Unable to remove file ${path}")
		}
	}
}
