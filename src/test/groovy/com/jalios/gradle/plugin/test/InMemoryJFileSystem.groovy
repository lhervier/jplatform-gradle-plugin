package com.jalios.gradle.plugin.test

import java.io.InputStream
import java.util.Map

import com.jalios.gradle.plugin.ex.JFileSystemException
import com.jalios.gradle.plugin.fs.JFileSystem

import groovy.lang.Closure

class InMemoryJFileSystem extends JFileSystem {

	private Map<String, byte[]> files
	
	private String root
	
	InMemoryJFileSystem() {
		this("", new HashMap<>())
	}
	
	private InMemoryJFileSystem(String root, Map<String, byte[]> files) {
		this.files = files
		this.root = root
	}
	
	// ===========================================================
	
	public void addFile(String name) {
		this.addFile(name.toString(), new byte[0])
	}
	
	public void addFile(String name, byte[] content) {
		this.files.put(root + name.toString(), content)
	}
	
	boolean match(String path, String pattern) {
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
	public void paths(String pattern, Closure<String> closure) throws JFileSystemException {
		this.files.each { path, content ->
			if( this.match(path, root + pattern.toString()) )
				closure(path.substring(root.length()))
		}
	}
	
	@Override
	public void delete(String path) throws JFileSystemException {
		this.files.remove(root + path.toString())
	}

	@Override
	public void setContentFromStream(String path, InputStream inStream) throws JFileSystemException {
		this.files.put(root + path.toString(), inStream.bytes)
	}

	@Override
	public void getContentAsStream(String path, Closure<InputStream> closure) throws JFileSystemException {
		if( !this.exists(path) ) {
			throw new JFileSystemException("File ${path} does not exists. Unable to get content.")
		}
		
		InputStream inStream = new ByteArrayInputStream(this.files.get(root + path.toString()))
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
		return new InMemoryJFileSystem(path, this.files)
	}
	
}
