package com.jalios.gradle.plugin.jplatform

import com.jalios.gradle.plugin.fs.FileSystem
import com.jalios.gradle.plugin.jplatform.gen.GeneratedFileExtractor
import com.jalios.gradle.plugin.jplatform.gen.GeneratedPath
import com.jalios.gradle.plugin.jplatform.gen.impl.CssExtractor
import com.jalios.gradle.plugin.jplatform.gen.impl.SignatureXmlExtractor
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.PluginXmlExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.PrivateFilesExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.PublicFilesExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.TypesExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.TypesTemplatesExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.WebappFilesExtractor

/**
 * Describes a module (inside a JPlatform installation,
 * or inside a source folder)
 * @author Lionel HERVIER
 */
class JModule {

	/**
	 * Does the module exists ?
	 */
	boolean exists
	
	/**
	 * Module name
	 */
	final String name
	
	/**
	 * Filesystem of the module
	 */
	final FileSystem rootFs
	FileSystem pubFs
	FileSystem privFs
	
	/**
	 * Paths of file systems
	 */
	String privFsPath
	String pubFsPath
	
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
	List<String> paths = []
	
	/**
	 * Generated paths inside the module
	 */
	List<GeneratedPath> generatedPaths = []
	
	/**
	 * Constructor
	 */
	JModule(
			String name, 
			FileSystem rootFs) {
		this.name = name
		this.rootFs = rootFs
	}
	
	/**
	 * Loads the module
	 */
	void init(List<GeneratedFileExtractor> genFileExtractors, List<SourceFileExtractor> srcFileExtractors) {
		this.privFsPath = "WEB-INF/plugins/${this.name}"
		this.privFs = this.rootFs.createFrom(this.privFsPath)
		
		if( !this.privFs.exists("plugin.xml") ) {
			this.exists = false
			return
		}
		
		this.pubFsPath = "plugins/${this.name}"
		this.pubFs = this.rootFs.createFrom(this.pubFsPath)
		
		this.privFs.getContentAsReader("plugin.xml", "UTF-8") { reader ->
			this.pluginXml = new PluginXml(reader)
		}
		
		this.privFs.getContentAsReader("properties/plugin.prop", "UTF-8") { reader ->
			this.pluginProp = new PluginProp(reader)
		}
		
		// compute the list of generated files
		genFileExtractors.each { extractor ->
			extractor.extract(this) { genPath -> 
				this.generatedPaths.add(genPath)
			}
		}
		
		// compute the list of source files
		srcFileExtractors.each { extractor ->
			extractor.extract(this) { path ->
				this.paths.add(path.toString())
			}
		}
		
		// Remove generated files from source files list
		this.generatedPaths.each { genPath ->
			this.paths.removeElement(genPath.path.toString())
		}
	}
}
