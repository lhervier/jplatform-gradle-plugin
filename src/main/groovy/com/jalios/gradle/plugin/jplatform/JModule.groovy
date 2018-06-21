package com.jalios.gradle.plugin.jplatform

import com.jalios.gradle.plugin.fs.FileSystem
import com.jalios.gradle.plugin.jplatform.gen.CssExtractor
import com.jalios.gradle.plugin.jplatform.gen.SignatureXmlExtractor
import com.jalios.gradle.plugin.jplatform.source.PluginXmlExtractor
import com.jalios.gradle.plugin.jplatform.source.PrivateFilesExtractor
import com.jalios.gradle.plugin.jplatform.source.PublicFilesExtractor
import com.jalios.gradle.plugin.jplatform.source.TypesExtractor
import com.jalios.gradle.plugin.jplatform.source.TypesTemplatesExtractor
import com.jalios.gradle.plugin.jplatform.source.WebappFilesExtractor

/**
 * Describes a module (inside a JPlatform installation,
 * or inside a source folder)
 * @author Lionel HERVIER
 */
class JModule {

	/**
	 * Objects that will extract source files
	 */
	private static List<Class<? extends SourceFileExtractor>> SOURCE_EXTRACTORS = [
			PluginXmlExtractor.class,
			PublicFilesExtractor.class,
			PrivateFilesExtractor.class,
			WebappFilesExtractor.class,
			TypesTemplatesExtractor.class,
			TypesExtractor.class
	]
	
	/**
	 * Objects that will extract generated files
	 */
	private static List<Class<GeneratedFileExtractor>> GEN_EXTRACTORS = [
			SignatureXmlExtractor.class,
			CssExtractor.class
	]
	
	/**
	 * Does the module exists ?
	 */
	final boolean exists
	
	/**
	 * Module name
	 */
	final String name
	
	/**
	 * Filesystem of the module
	 */
	final FileSystem rootFs
	final FileSystem pubFs
	final FileSystem privFs
	
	/**
	 * Paths of file systems
	 */
	final String privFsPath
	final String pubFsPath
	
	/**
	 * plugin.prop file
	 */
	final PluginProp pluginProp
	
	/**
	 * plugin.xml file
	 */
	final PluginXml pluginXml
	
	/**
	 * Paths that compose the module
	 */
	final List<String> paths = []
	
	/**
	 * Generated paths inside the module
	 */
	final List<GeneratedPath> generatedPaths = []
	
	/**
	 * Constructor
	 * @param name the name of the module
	 * @param rootFs root file system of the module
	 * @param privFs private file system of the module
	 * @param pubFs public file system of the module
	 */
	JModule(String name, FileSystem rootFs) {
		this.name = name
		this.rootFs = rootFs
		
		this.privFsPath = "WEB-INF/plugins/${name}"
		this.privFs = this.rootFs.createFrom(this.privFsPath)
		
		this.pubFsPath = "plugins/${name}"
		this.pubFs = this.rootFs.createFrom(this.pubFsPath)
		
		if( !this.privFs.exists("plugin.xml") ) {
			this.exists = false
			return
		}
		
		this.privFs.getContentAsReader("plugin.xml", "UTF-8") { reader ->
			this.pluginXml = new PluginXml(reader)
		}
		
		this.privFs.getContentAsReader("properties/plugin.prop", "UTF-8") { reader ->
			this.pluginProp = new PluginProp(reader)
		}
		
		// compute the list of generated files
		GEN_EXTRACTORS.each { extractorClass ->
			GeneratedFileExtractor extractor = extractorClass.newInstance()
			extractor.module = this
			extractor.extract(this) { genPath -> 
				this.generatedPaths.add(genPath)
			}
		}
		
		// compute the list of source files
		SOURCE_EXTRACTORS.each { extractorClass ->
			SourceFileExtractor extractor = extractorClass.newInstance()
			extractor.module = this
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
