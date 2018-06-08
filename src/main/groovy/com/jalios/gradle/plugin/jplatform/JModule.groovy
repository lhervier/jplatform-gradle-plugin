package com.jalios.gradle.plugin.jplatform

import org.gradle.api.Project

import com.jalios.gradle.plugin.ext.JModuleExtension
import com.jalios.gradle.plugin.jplatform.gen.CssExtractor
import com.jalios.gradle.plugin.jplatform.gen.SignatureXmlExtractor
import com.jalios.gradle.plugin.jplatform.source.PluginXmlExtractor
import com.jalios.gradle.plugin.jplatform.source.PrivateFilesExtractor
import com.jalios.gradle.plugin.jplatform.source.PublicFilesExtractor
import com.jalios.gradle.plugin.jplatform.source.TypesTemplatesExtractor
import com.jalios.gradle.plugin.jplatform.source.WebappFilesExtractor
import com.jalios.gradle.plugin.util.FileUtil

class JModule {

	private static List<ISourceFileExtractor> SOURCE_EXTRACTORS = [
			new PluginXmlExtractor(),
			new PublicFilesExtractor(),
			new PrivateFilesExtractor(),
			new WebappFilesExtractor(),
			new TypesTemplatesExtractor()
	]
	
	private static List<IGeneratedFileExtractor> GEN_EXTRACTORS = [
			new SignatureXmlExtractor(),
			new CssExtractor()
	]
	
	final boolean exists
	final String name
	final File rootFolder
	final File pubFolder
	final File privFolder
	final PluginProp pluginProp
	final def pluginXml
	
	private final String pubFolderPath
	private final String privFolderPath
	
	final List<String> files = []
	final List<GeneratedFile> generatedFiles = []
	
	/**
	 * Constructor
	 * @param project the current project
	 * @param rootPath path to the root of the platform
	 * @param name the name of the module
	 */
	JModule(Project project, String rootPath, String name) {
		this.name = name
		this.rootFolder = project.file(rootPath)
		
		this.pubFolderPath = "plugins/${this.name}"
		this.privFolderPath = "WEB-INF/plugins/${this.name}"
		
		this.pubFolder = new File(this.rootFolder, this.pubFolderPath)
		this.privFolder = new File(this.rootFolder, this.privFolderPath)
		
		this.pluginProp = new PluginProp(new File(this.privFolder, "properties/plugin.prop"))
		
		XmlParser parser = new XmlParser()
		parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
		parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
		def fPluginXml = new File(this.privFolder, "plugin.xml")
		this.exists = fPluginXml.exists()
		if( !this.exists )
			return
		
		this.pluginXml = parser.parse(fPluginXml)
		
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
