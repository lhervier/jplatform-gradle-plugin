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

	abstract JPlatformTask getTask()
	
	@TaskAction
	final def taskAction() {
		JFileSystem platformFs = new JFileSystemImpl(this.project.files(jPlatform.path))
		JFileSystem platformFsData = new JFileSystemImpl(this.project.files(jPlatform.dataPath))
		JFileSystem moduleFs = new JFileSystemImpl(this.project.files("src/main/module"))
		JFileSystem moduleFsData = new JFileSystemImpl(this.project.files("src/main/module/WEB-INF/data"))
		
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
			new TypesJspExtractor()					// Extract custom jsp for declared types
		]
		
		JModule currModule = new JModule(
			this.project.jModule.name,
			moduleFs,
			moduleFsData
		);
		currModule.init(genFileExtractors, srcFileExtractors)

		JModule platformModule = new JModule(
			this.project.jModule.name,
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
