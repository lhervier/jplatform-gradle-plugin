package com.jalios.gradle.plugin.task.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.fs.impl.JFileSystemImpl
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.gen.GeneratedFileExtractor
import com.jalios.gradle.plugin.jplatform.gen.impl.CssExtractor
import com.jalios.gradle.plugin.jplatform.gen.impl.SignatureXmlExtractor
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.JarsExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.PluginXmlExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.PrivateFilesExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.PublicFilesExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.TypesJspExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.TypesStdJspExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.TypesTemplatesExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.TypesTypeFileExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.TypesTypeTemplatesXmlExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.TypesTypeXmlExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.WebappFilesExtractor
import com.jalios.gradle.plugin.task.JPlatformTask

abstract public class BaseTaskImpl extends DefaultTask {

	/**
	 * The task to execute
	 * @return the task
	 */
	abstract JPlatformTask getTask()
	
	/**
	 * Return the module name
	 */
	String getModuleName() {
		return this.project.jModule.name
	}
	
	/**
	 * Task execution
	 * @return
	 */
	@TaskAction
	final def taskAction() {
		println "Preparing module folder"
		String path = this.task.prepareModule(
			this.getModuleName(), 
			this.project
		)
		println "=> Module folder to run task on is : ${path}"
		
		println "Initializing JPlatform build"
		JFileSystem platformFs = new JFileSystemImpl(this.project.file(this.project.jPlatform.path))
		JFileSystem platformFsData = new JFileSystemImpl(this.project.file(this.project.jPlatform.dataPath))
		
		JFileSystem moduleFs = new JFileSystemImpl(this.project.file(path))
		JFileSystem moduleFsData = moduleFs.createFrom("WEB-INF/data")
		
		List<GeneratedFileExtractor> genFileExtractors = [
			new CssExtractor(),
			new SignatureXmlExtractor()
		]
		List<SourceFileExtractor> srcFileExtractors = [
			new PluginXmlExtractor(),				// Extract plugin.xml
			new PrivateFilesExtractor(),			// Extract private files
			new PublicFilesExtractor(),				// Extract public files
			new WebappFilesExtractor(),				// Extract webapp files
			new TypesTypeXmlExtractor(),			// Extract <type>.xml file
			new TypesTypeFileExtractor(),			// Extract files declared inside <type> tags (in plugin.xml)
			new TypesTypeTemplatesXmlExtractor(),	// Extract <type>-templates.xml
			new TypesTemplatesExtractor(),			// Extract files from <type>-templates.xml and from tag <types>/<templates> in plugin.xml
			new TypesStdJspExtractor(),				// Extract standard jsp files associated with declared types
			new TypesJspExtractor(),				// Extract custom jsp for declared types
			new JarsExtractor()						// Extract jar files
		]
		
		JModule currModule = new JModule(
			this.moduleName,
			moduleFs,
			moduleFsData
		);
		currModule.init(genFileExtractors, srcFileExtractors)

		JModule platformModule = new JModule(
			this.moduleName,
			platformFs,
			platformFsData
		)
		platformModule.init(genFileExtractors, srcFileExtractors)
		
		this.task.run(
			platformModule,
			currModule
		)
	}
}
