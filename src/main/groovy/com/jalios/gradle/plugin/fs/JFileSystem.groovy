package com.jalios.gradle.plugin.fs

import com.jalios.gradle.plugin.ex.JFileSystemException
import com.jalios.gradle.plugin.jplatform.JModule

import groovy.lang.Closure

abstract class JFileSystem {

	abstract JFileSystem createFrom(String path) throws JFileSystemException
	
	abstract boolean exists(String path) throws JFileSystemException
	
	abstract void paths(String pattern, Closure<String> closure) throws JFileSystemException
	
	abstract void delete(String path) throws JFileSystemException
	
	abstract void setContentFromStream(String path, InputStream inStream) throws JFileSystemException
	
	abstract void getContentAsStream(String path, Closure<InputStream> closure) throws JFileSystemException
	
	final void getContentAsReader(String path, String encoding, Closure<BufferedReader> closure) throws JFileSystemException {
		this.getContentAsStream(path) { inStream ->
			Reader reader = new InputStreamReader(inStream, encoding)
			BufferedReader breader = new BufferedReader(reader)
			try {
				closure(breader)
			} finally {
				reader.close()
			}
		}
	}
}
