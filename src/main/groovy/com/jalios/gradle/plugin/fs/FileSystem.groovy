package com.jalios.gradle.plugin.fs

import com.jalios.gradle.plugin.jplatform.JModule

import groovy.lang.Closure

abstract class FileSystem {

	abstract FileSystem createFrom(String path)
	
	abstract boolean exists(String path)
	
	abstract void paths(String pattern, Closure<String> closure)
	
	abstract void delete(String path)
	
	abstract void setContentFromStream(String path, InputStream inStream)
	
	abstract void getContentAsStream(String path, Closure<InputStream> closure)
	
	final void getContentAsReader(String path, String encoding, Closure<BufferedReader> closure) {
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
