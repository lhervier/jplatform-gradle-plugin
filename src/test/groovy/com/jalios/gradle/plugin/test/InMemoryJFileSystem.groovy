package com.jalios.gradle.plugin.test

import com.jalios.gradle.plugin.ex.JFileSystemException
import com.jalios.gradle.plugin.fs.FSFile
import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class InMemoryJFileSystem extends JFileSystem {

	static class InMemoryFile {
		byte[] content = new byte[0]
		long updated = System.currentTimeMillis()
	}
	
	private Map<String, InMemoryFile> files
	
	private String root
	
	InMemoryJFileSystem() {
		this("", new HashMap<>())
	}
	
	private InMemoryJFileSystem(String root, Map<String, InMemoryFile> files) {
		this.files = files
		this.root = root
	}
	
	// ===========================================================
	
	public void addFile(String name) {
		this.addFile(name.toString(), new byte[0])
	}
	
	public void addFile(String name, String content) {
		this.addFile(name, ByteUtils.extractBytes(content, "UTF-8"))
	}
	
	public void addFile(String name, byte[] content) {
		InMemoryFile imf = new InMemoryFile()
		imf.content = content
		this.files.put(root + name.toString(), imf)
	}
	
	boolean match(String path, String pattern) {
		if( pattern.endsWith("**/*") ) {
			String rootFolder = pattern.substring(0, pattern.length() - "**/*".length())
			return path.startsWith(rootFolder)
		}
		String regexp = pattern.replaceAll("\\/", "\\\\/")
		regexp = regexp.replaceAll("\\*\\*", ".%STAR%")
		regexp = regexp.replaceAll("\\*", "[^\\/]%STAR%")
		regexp = regexp.replaceAll("%STAR%", "*")
		return path.toString() ==~ ~regexp
	}
	
	// ===================================================================
	
	@Override
	public boolean exists(String path) throws JFileSystemException {
		return this.files.containsKey(root + path.toString())
	}

	@Override
	public void paths(String pattern, Closure<FSFile> closure) throws JFileSystemException {
		this.files.each { path, imf ->
			if( this.match(path, root + pattern.toString()) ) {
				FSFile fsFile = new FSFile(
						path: path.substring(root.length()),
						updated: imf.updated
				)
				closure(fsFile)
			}
		}
	}
	
	@Override
	public void delete(String path) throws JFileSystemException {
		this.files.remove(root + path.toString())
	}

	@Override
	public void setContentFromStream(String path, InputStream inStream) throws JFileSystemException {
		InMemoryFile imf = this.files.get(root + path.toString())
		if( imf == null ) {
			imf = new InMemoryFile()
			this.files.put(root + path.toString(), imf)
		}
		imf.updated = System.currentTimeMillis()
		imf.content = inStream.bytes
	}

	@Override
	public void getContentAsStream(String path, Closure<InputStream> closure) throws JFileSystemException {
		if( !this.exists(path) ) {
			throw new JFileSystemException("File ${path} does not exists. Unable to get content.")
		}
		
		InputStream inStream = new ByteArrayInputStream(this.files.get(root + path.toString()).content)
		try {
			closure(inStream)
		} finally {
			inStream.close()
		}
	}

	@Override
	public JFileSystem createFrom(String path) throws JFileSystemException {
		if( !path.endsWith("/") ) {
			path += "/"
		}
		return new InMemoryJFileSystem(root + path, this.files)
	}
	
}
