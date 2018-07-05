package com.jalios.gradle.plugin.task

import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.gen.GeneratedFileExtractor
import com.jalios.gradle.plugin.jplatform.gen.GeneratedPath
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.PluginXmlExtractor
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestInstallPluginTask extends BaseTestTask {

	InstallPluginTask task
	
	@Override
	void setUp() {
		this.task = new InstallPluginTask()
	}

	@Test
	void whenEmptyPlatformModule_thenModuleDeployed() {
		this.addPluginXml(this.currModule)
		this.currModuleFs.addFile("css/styles.css")
		this.currModuleFs.addFile("css/styles2.css")
		
		this.currModule.init(
			null, 
			[{JModule m, Closure<String> closure ->
				closure("css/styles.css")
				closure("css/styles2.css")
			} as SourceFileExtractor]
		)
		this.platformModule.init(null, null)
		
		this.task.run(this.platformModule, this.currModule)
		
		assert this.platformModule.rootFs.exists("css/styles.css")
		assert this.platformModule.rootFs.exists("css/styles2.css")
	}
	
	@Test
	void whenFileRemovedFromCurrent_thenFileRemovedFromPlatform() {
		// css extractors
		def extractor = {JModule m, Closure<String> closure ->
			m.rootFs.paths("css/*.css") { path ->
				closure(path)
			}
		} as SourceFileExtractor
		
		// Initialize platform module
		this.addPluginXml(this.platformModule)
		this.platformModuleFs.addFile("css/styles.css")
		this.platformModuleFs.addFile("css/styles2.css")
		this.platformModuleFs.addFile("css/styles3.css")
		this.platformModule.init(
			null, 
			[extractor]
		)
		
		// Initialize current module
		this.addPluginXml(this.currModule)
		this.currModuleFs.addFile("css/styles.css")
		this.currModuleFs.addFile("css/styles2.css")
		this.currModule.init(
			null,
			[extractor]
		)
		
		// Run the task
		this.task.run(this.platformModule, this.currModule)
		
		assert this.platformModule.rootFs.exists("css/styles.css")
		assert this.platformModule.rootFs.exists("css/styles2.css")
		assert !this.platformModule.rootFs.exists("css/styles3.css")
	}
	
	@Test
	void whenGeneratedFilesInPlatform_thenGeneratedNotRemoved() {
		// less generator
		def extractor = {JModule m, Closure<String> closure ->
			m.rootFs.paths("css/*.less") { path ->
				String css = path.substring(0, path.length() - 4) + "css"
				closure(new GeneratedPath(path: path, source: css))
			}
		} as GeneratedFileExtractor
		
		// Initialize platform module
		this.addPluginXml(this.platformModule)
		this.platformModuleFs.addFile("css/styles.less")
		this.platformModuleFs.addFile("css/styles.css")
		this.platformModuleFs.addFile("css/styles2.less")
		this.platformModuleFs.addFile("css/styles2.css")
		this.platformModule.init(
			[extractor],
			null
		)
		
		// Initialize current module
		this.addPluginXml(this.currModule)
		this.currModuleFs.addFile("css/styles.less")
		this.currModuleFs.addFile("css/styles2.less")
		this.currModule.init(
			[extractor],
			null
		)
		
		// Run the task
		this.task.run(this.platformModule, this.currModule)
		
		assert this.platformModule.rootFs.exists("css/styles.css")
		assert this.platformModule.rootFs.exists("css/styles2.css")
	}
	
	@Test
	void whenSourceFilesRemovedInCurrModule_thenGeneratedRemovedFromPlatform() {
		// css generator
		// => Declare generated css files
		def genExtractor = {JModule m, Closure<String> closure ->
			m.rootFs.paths("css/*.less") { less ->
				String css = less.substring(0, less.length() - 4) + "css"
				closure(new GeneratedPath(path: css, source: less))
			}
		} as GeneratedFileExtractor
		
		// less extractor
		// => Declare less files
		def srcExtractor = {JModule m, Closure<String> closure ->
			m.rootFs.paths("css/*.less") { path ->
				closure(path)
			}
		} as GeneratedFileExtractor
		
		// Initialize platform module
		this.addPluginXml(this.platformModule)
		this.platformModuleFs.addFile("css/styles.less")
		this.platformModuleFs.addFile("css/styles.css")
		this.platformModuleFs.addFile("css/styles2.less")
		this.platformModuleFs.addFile("css/styles2.css")
		this.platformModule.init(
			[genExtractor],
			[srcExtractor]
		)
		
		// Initialize current module
		this.addPluginXml(this.currModule)
		this.currModuleFs.addFile("css/styles.less")
		this.currModule.init(
			[genExtractor],
			[srcExtractor]
		)
		
		// Run the task
		this.task.run(this.platformModule, this.currModule)
		
		// The css file, generated in the platform module from the now removed less file
		// must be removed to.
		assert this.platformModule.rootFs.exists("css/styles.less")
		assert this.platformModule.rootFs.exists("css/styles.css")
		assert !this.platformModule.rootFs.exists("css/styles2.css")
		assert !this.platformModule.rootFs.exists("css/styles2.less")
	}
}
