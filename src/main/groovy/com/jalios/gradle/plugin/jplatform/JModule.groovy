package com.jalios.gradle.plugin.jplatform

import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.jplatform.gen.GeneratedFileExtractor
import com.jalios.gradle.plugin.jplatform.gen.GeneratedPath
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

/**
 * Describes a module (inside a JPlatform installation,
 * or inside a source folder)
 * @author Lionel HERVIER
 */
class JModule {

	/**
	 * Module name
	 */
	final String name
	
	/**
	 * Filesystems of the module
	 */
	Map<FSType, JFileSystem> fileSystems = new HashMap()
	
	/**
	 * plugin.prop file
	 */
	PluginProp pluginProp
	
	/**
	 * plugin.xml file
	 */
	PluginXml pluginXml
	
	/**
	 * Paths that compose the module
	 */
	Set<JPath> paths = new HashSet()
	
	/**
	 * Generated paths inside the module
	 */
	List<GeneratedPath> generatedPaths = new ArrayList()
	
	/**
	 * Constructor
	 */
	JModule(
			String name, 
			JFileSystem rootFs,
			JFileSystem dataFs) {
		this.name = name
		this.fileSystems.put(FSType.ROOT, rootFs)
		this.fileSystems.put(FSType.DATA, dataFs)
	}
	
	/**
	 * Access to file systems
	 */
	JFileSystem getFs(FSType type) {
		return this.fileSystems.get(type)
	}
	JFileSystem getRootFs() {
		return this.getFs(FSType.ROOT)
	}
	JFileSystem getDataFs() {
		return this.getFs(FSType.DATA)
	}
	JFileSystem getPubFs() {
		return this.getFs(FSType.PUBLIC)
	}
	JFileSystem getPrivFs() {
		return this.getFs(FSType.PRIVATE)
	}
	
	/**
	 * Loads the module
	 */
	void init(List<GeneratedFileExtractor> genFileExtractors, List<SourceFileExtractor> srcFileExtractors) {
		this.fileSystems.put(FSType.PRIVATE, this.rootFs.createFrom("WEB-INF/plugins/${this.name}"))
		this.fileSystems.put(FSType.PUBLIC, this.rootFs.createFrom("plugins/${this.name}"))
		
		if( !privFs.exists("plugin.xml") ) {
			return
		}
		
		// Create plugin.xml
		this.privFs.getContentAsReader("plugin.xml", "UTF-8") { reader ->
			this.pluginXml = new PluginXml(reader)
		}
		
		// Create plugin.prop
		if( this.privFs.exists("properties/plugin.prop") ) {
			this.privFs.getContentAsReader("properties/plugin.prop", "UTF-8") { reader ->
				this.pluginProp = new PluginProp(reader)
			}
		}
		
		// compute the list of generated files
		genFileExtractors?.each { extractor ->
			extractor.extract(this) { genPath -> 
				this.generatedPaths.add(genPath)
			}
		}
		
		// compute the list of source files
		srcFileExtractors?.each { extractor ->
			extractor.extract(this) { path ->
				this.paths.add(path)
			}
		}
		
		// Remove generated files from source files list
		this.generatedPaths.each { genPath ->
			this.paths.removeElement(genPath.path)
		}
	}
}
