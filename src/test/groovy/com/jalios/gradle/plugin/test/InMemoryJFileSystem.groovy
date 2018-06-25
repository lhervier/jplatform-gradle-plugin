package com.jalios.gradle.plugin.test

import java.io.InputStream
import java.util.Map

import com.jalios.gradle.plugin.ex.JFileSystemException
import com.jalios.gradle.plugin.fs.JFileSystem

import groovy.lang.Closure

class InMemoryJFileSystem extends JFileSystem {

	private Map<String, byte[]> files = new HashMap<>()
	
	public void addFile(String name) {
		this.addFile(name, new byte[0])
	}
	
	public void addFile(String name, byte[] content) {
		this.files.put(name, content)
	}
	
	boolean match(String path, String pattern) {
		String regexp = pattern.replaceAll("\\/", "\\\\/")
		regexp = regexp.replaceAll("\\*\\*", ".%STAR%")
		regexp = regexp.replaceAll("\\*", "[^\\/]%STAR%")
		regexp = regexp.replaceAll("%STAR%", "*")
		return path ==~ ~regexp
	}
	
	// ===================================================================
	
	@Override
	public boolean exists(String path) throws JFileSystemException {
		return this.files.containsKey(path)
	}

	@Override
	public void paths(String pattern, Closure<String> closure) throws JFileSystemException {
		this.files.each { path, content ->
			if( this.match(path, pattern) )
				closure(path)
		}
	}
	
	@Override
	public void delete(String path) throws JFileSystemException {
		this.files.remove(path)
	}

	@Override
	public void setContentFromStream(String path, InputStream inStream) throws JFileSystemException {
		this.files.put(path, inStream.bytes)
	}

	@Override
	public void getContentAsStream(String path, Closure<InputStream> closure) throws JFileSystemException {
		if( !this.exists(path) ) {
			throw new JFileSystemException("File ${path} does not exists. Unable to get content.")
		}
		
		InputStream inStream = new ByteArrayInputStream(this.files.get(path))
		try {
			closure(inStream)
		} finally {
			inStream.close()
		}
	}

	@Override
	public JFileSystem createFrom(String path) throws JFileSystemException {
		InMemoryJFileSystem ret = new InMemoryJFileSystem()
		this.paths("${path}/**") { subPath ->
			ret.files.put(
					subPath.substring(path.length() + 1), 
					this.files.get(subPath)
			)
		}
		return ret;
	}
	
}
