package com.jalios.gradle.plugin.fs.impl

import com.jalios.gradle.plugin.ex.JFileSystemException
import com.jalios.gradle.plugin.fs.JFileSystem

class JFileSystemImpl extends JFileSystem {

	/**
	 * The root folder
	 */
	private File rootFolder
	
	/**
	 * Constructor
	 */
	public JFileSystemImpl(File rootFolder) throws JFileSystemException {
		if( !rootFolder.absolute ) {
			throw new JFileSystemException("FileSystem root folder must be absolute")
		}
		this.rootFolder = rootFolder
	}
	
	@Override
	public JFileSystem createFrom(String path) throws JFileSystemException {
		return new JFileSystemImpl(new File(this.rootFolder, path));
	}

	@Override
	boolean exists(String path) throws JFileSystemException {
		return new File(this.rootFolder, path).exists()
	}
	
	@Override
	void paths(String pattern, Closure<String> closure) throws JFileSystemException {
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
	void setContentFromStream(String path, InputStream inStream) throws JFileSystemException {
		File destFile = new File(this.rootFolder, path)
		println "Setting content of '${destFile.absolutePath}'"
		if( destFile.exists() ) {
			if( !destFile.delete() ) {
				throw new JFileSystemException("Unable to replace existing file...")
			}
		}
		destFile.getParentFile().mkdirs()
		destFile.bytes = inStream.bytes
	}
	
	@Override
	public void getContentAsStream(String path, Closure<InputStream> closure) throws JFileSystemException {
		File f = new File(this.rootFolder, path)
		InputStream fileIn = new FileInputStream(f);
		try {
			closure(fileIn)
		} finally {
			fileIn.close()
		}
	}
	
	@Override
	void delete(String path) throws JFileSystemException {
		File f = new File(this.rootFolder, path)
		println "Removing '${f.absolutePath}'"
		if( !f.exists() ) {
			return
		}
		if( !f.delete() ) {
			throw new JFileSystemException("Unable to remove file ${path}")
		}
	}
}
