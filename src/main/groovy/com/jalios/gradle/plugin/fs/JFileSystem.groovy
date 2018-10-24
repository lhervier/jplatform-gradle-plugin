package com.jalios.gradle.plugin.fs

import com.jalios.gradle.plugin.ex.JFileSystemException
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.test.util.ByteUtils

import groovy.lang.Closure

abstract class JFileSystem {

	abstract JFileSystem createFrom(String path) throws JFileSystemException
	
	abstract boolean exists(String path) throws JFileSystemException
	
	abstract void paths(String pattern, Closure<FSFile> closure) throws JFileSystemException
	
	abstract void delete(String path) throws JFileSystemException
	
	abstract void getContentAsStream(String path, Closure<InputStream> closure) throws JFileSystemException
	
	abstract void setContentFromStream(String path, InputStream inStream) throws JFileSystemException
	
	// ======================================================================
	
	final FSFile path(String path) throws JFileSystemException {
		FSFile ret = null
		this.paths(path) { fsFile -> 
			ret = fsFile
		}
		return ret
	}
	
	final void delete(FSFile file) throws JFileSystemException {
		this.delete(file.path)
	}
	
	final void getContentAsStream(FSFile file, Closure<InputStream> closure) throws JFileSystemException {
		this.getContentAsStream(file.path, closure)
	}
	
	final void setContentFromStream(FSFile file, InputStream inStream) throws JFileSystemException {
		this.setContentFromStream(file.path, inStream)
	}
	
	final void setContentFromText(String path, String text, String encoding) throws JFileSystemException {
		this.setContentFromStream(path, new ByteArrayInputStream(ByteUtils.extractBytes(text, encoding)))
	}
	
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
	
	final String getContentAsString(String path, String encoding) {
		StringBuffer ret = new StringBuffer()
		char[] buffer = new char[4 * 1024]
		this.getContentAsReader(path, encoding) { reader ->
			int read = reader.read(buffer, 0, buffer.length)
			while( read != -1 ) {
				ret.append(buffer, 0, read)
				read = reader.read(buffer, 0, buffer.length)
			}
		}
		return ret.toString()
	}
}
