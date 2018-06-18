package com.jalios.gradle.plugin.jplatform

import org.gradle.api.Project

import com.jalios.gradle.plugin.ext.JModuleExtension
import com.jalios.gradle.plugin.jplatform.gen.CssExtractor
import com.jalios.gradle.plugin.jplatform.gen.SignatureXmlExtractor
import com.jalios.gradle.plugin.jplatform.source.PluginXmlExtractor
import com.jalios.gradle.plugin.jplatform.source.PrivateFilesExtractor
import com.jalios.gradle.plugin.jplatform.source.PublicFilesExtractor
import com.jalios.gradle.plugin.jplatform.source.TypesExtractor
import com.jalios.gradle.plugin.jplatform.source.TypesTemplatesExtractor
import com.jalios.gradle.plugin.jplatform.source.WebappFilesExtractor
import com.jalios.gradle.plugin.util.FileUtil

/**
 * Describes a module (inside a JPlatform installation,
 * or inside a source folder)
 * @author Lionel HERVIER
 */
class JModule {

	/**
	 * Objects that will extract source files
	 */
	private static List<ISourceFileExtractor> SOURCE_EXTRACTORS = [
			new PluginXmlExtractor(),
			new PublicFilesExtractor(),
			new PrivateFilesExtractor(),
			new WebappFilesExtractor(),
			new TypesTemplatesExtractor(),
			new TypesExtractor()
	]
	
	/**
	 * Objects that will extract generated files
	 */
	private static List<IGeneratedFileExtractor> GEN_EXTRACTORS = [
			new SignatureXmlExtractor(),
			new CssExtractor()
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
	 * Root (webapp) folder of the module
	 */
	final File rootFolder
	
	/**
	 * Public folder
	 */
	final File pubFolder
	
	/**
	 * Private folder
	 */
	final File privFolder
	
	/**
	 * plugin.prop file
	 */
	final PluginProp pluginProp
	
	/**
	 * plugin.xml file
	 */
	final PluginXml pluginXml
	
	/**
	 * Files that compose the module
	 */
	final List<String> files = []
	
	/**
	 * Generated files inside the module
	 */
	final List<GeneratedFile> generatedFiles = []
	
	// ==============================================================
	
	/**
	 * Path to the public folder (relative to the root folder)
	 */
	private final String pubFolderPath
	
	/**
	 * Path to the private folder (relative to the root folder)
	 */
	private final String privFolderPath
	
	/**
	 * Constructor
	 * @param rootFolder root of the platform
	 * @param name the name of the module
	 */
	JModule(File rootFolder, String name) {
		this.name = name
		this.rootFolder = rootFolder
		
		this.pubFolderPath = "plugins/${this.name}"
		this.privFolderPath = "WEB-INF/plugins/${this.name}"
		
		this.pubFolder = new File(this.rootFolder, this.pubFolderPath)
		this.privFolder = new File(this.rootFolder, this.privFolderPath)
		
		File fPluginXml = new File(this.privFolder, "plugin.xml")
		if( !fPluginXml.exists() ) {
			this.exists = false
			return
		} else {
			this.exists = true
		}
		
		this.pluginXml = new PluginXml(fPluginXml.newReader("UTF-8"))
		this.pluginProp = new PluginProp(new File(this.privFolder, "properties/plugin.prop"))
		
		// compute the list of generated files
		GEN_EXTRACTORS.each { extractor ->
			extractor.extract(this) { genFile -> 
				this.generatedFiles.add(genFile)
			}
		}
		
		// compute the list of source files
		SOURCE_EXTRACTORS.each { extractor ->
			extractor.extract(this) { path ->
				this.files.add(path.toString())
			}
		}
		
		// Remove generated files from source files list
		this.generatedFiles.each { genFile ->
			this.files.removeElement(genFile.path.toString())
		}
	}
}
