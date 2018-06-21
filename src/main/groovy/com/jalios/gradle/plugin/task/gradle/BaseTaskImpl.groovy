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
import com.jalios.gradle.plugin.jplatform.source.impl.TypesExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.TypesTemplatesExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.WebappFilesExtractor
import com.jalios.gradle.plugin.task.JPlatformTask

abstract public class BaseTaskImpl extends DefaultTask {

	abstract JPlatformTask getTask()
	
	@TaskAction
	final def taskAction() {
		JFileSystem platformFs = new JFileSystemImpl(this.project.files(jPlatform.path))
		JFileSystem moduleFs = new JFileSystemImpl(this.project.files("src/main/module"))
		
		List<GeneratedFileExtractor> genFileExtractors = [
			new CssExtractor(),
			new SignatureXmlExtractor()
		]
		List<SourceFileExtractor> srcFileExtractors = [
			new PluginXmlExtractor(),
			new PrivateFilesExtractor(),
			new PublicFilesExtractor(),
			new WebappFilesExtractor(),
			new TypesExtractor(),
			new TypesTemplatesExtractor()
		]
		
		JModule currModule = new JModule(
			this.project.jModule.name,
			moduleFs
		);
		currModule.init(genFileExtractors, srcFileExtractors)

		JModule platformModule = new JModule(
			this.project.jModule.name,
			platformFs
		)
		platformModule.init(genFileExtractors, srcFileExtractors)
			
		this.task.run(
			platformModule,
			currModule
		)
	}
}
