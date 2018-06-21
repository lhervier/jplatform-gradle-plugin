package com.jalios.gradle.plugin.test

import java.io.InputStream

import org.gradle.internal.impldep.bsh.This
import org.gradle.internal.impldep.org.apache.commons.collections.map.HashedMap

import com.jalios.gradle.plugin.fs.FileSystem

import groovy.lang.Closure

class InMemoryFileSystem extends FileSystem {

	private Map<String, byte[]> files = new HashMap<>()
	
	public void addFile(String name) {
		this.files.put(name, new byte[0])
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
	public boolean exists(String path) {
		return this.files.containsKey(path)
	}

	@Override
	public void paths(String pattern, Closure<String> closure) {
		this.files.each { path, content ->
			if( this.match(path, pattern) )
				closure(path)
		}
	}
	
	@Override
	public void delete(String path) {
		this.files.remove(path)
	}

	@Override
	public void setContentFromStream(String path, InputStream inStream) {
		this.files.put(path, inStream.bytes)
	}

	@Override
	public void getContentAsStream(String path, Closure<InputStream> closure) {
		if( !this.exists(path) ) {
			throw new Exception("File ${path} does not exists. Unable to get content.")
		}
		
		InputStream inStream = new ByteArrayInputStream(this.files.get(path))
		try {
			closure(inStream)
		} finally {
			inStream.close()
		}
	}

	@Override
	public FileSystem createFrom(String path) {
		InMemoryFileSystem ret = new InMemoryFileSystem()
		this.paths("${path}/**/*") { subPath ->
			ret.files.put(
					subPath.substring(path.length() + 1), 
					this.files.get(subPath)
			)
		}
		return ret;
	}
	
}
