package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.fs.JFileSystem
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
	
	private String moduleName
	private JFileSystem jPlatformFs
	private JFileSystem moduleFs
	
	public BaseJPlatformTask( 
			String moduleName, 
			JFileSystem jPlatformFs, 
			JFileSystem moduleFs) {
		this.moduleName = moduleName
		this.jPlatformFs = jPlatformFs
		this.moduleFs = moduleFs
	}
	
	final void taskAction() {
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
			this.moduleFs
		);
		this.currModule.init(genFileExtractors, srcFileExtractors)
		
		this.platformModule = new JModule(
			this.moduleName,
			this.jPlatformFs
		)
		this.platformModule.init(genFileExtractors, srcFileExtractors)
		
		this.run()
	}
	
	abstract void run()
}
