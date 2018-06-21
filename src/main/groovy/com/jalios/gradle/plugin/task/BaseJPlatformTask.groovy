package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.fs.FileSystemFactory
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

abstract class BaseJPlatformTask {
	
	protected JModule currModule
	protected JModule platformModule
	
	private FileSystemFactory fsFactory
	private String moduleName
	private String jPlatformPath
	private String modulePath
	
	public BaseJPlatformTask(
			FileSystemFactory fsFactory, 
			String moduleName, 
			String jPlatformPath, 
			String modulePath) {
		this.fsFactory = fsFactory
		this.moduleName = moduleName
		this.jPlatformPath = jPlatformPath
		this.modulePath = modulePath
	}
	
	def taskAction() {
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
		
		this.currModule = new JModule(
			this.moduleName,
			this.fsFactory.newFs("src/main/module")
		);
		this.currModule.init(genFileExtractors, srcFileExtractors)
		
		this.platformModule = new JModule(
			this.moduleName,
			this.fsFactory.newFs(this.jPlatformPath)
		)
		this.platformModule.init(genFileExtractors, srcFileExtractors)
		
		this.run()
	}
	
	abstract void run()
}
