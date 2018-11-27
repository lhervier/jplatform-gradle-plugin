package com.jalios.gradle.plugin.task

import org.junit.After
import org.junit.Test

import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.JPath
import com.jalios.gradle.plugin.jplatform.PluginXml
import com.jalios.gradle.plugin.jplatform.gen.GeneratedFileExtractor
import com.jalios.gradle.plugin.jplatform.gen.GeneratedPath
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor
import com.jalios.gradle.plugin.test.InMemoryJFileSystem

class TestPushPluginTask extends BaseTestTask {

	PushPluginTask task
	File tmp
		
	@Override
	void setUp() {
		this.task = new PushPluginTask()
		this.tmp = File.createTempDir()
	}

	@After
	void destroy() {
		if( !this.tmp.deleteDir() ) {
			throw new RuntimeException("Unable to remove '${this.tmp.absolutePath}")
		}
	}
	
	// ===========================================================================
	
	@Test
	void whenJarInWebInfLib_thenFail() {
		// Create fake plugin with a jar file
		InMemoryJFileSystem rootFs = new InMemoryJFileSystem()
		rootFs.setContentFromText("src/main/module/WEB-INF/lib/fake.jar", "Definitively not a jar...", "UTF-8")
		
		// Initialize task
		try {
			this.task.createModuleFs("MyPlugin", "1.0", rootFs, [], null)
		} catch(Exception e) {
			assert e.getMessage() == "'WEB-INF/lib' folder must be empty. Use gradle dependencies to add jars to your JPlatform module"
		}
	}
	
	@Test
	void whenNoPluginXml_thenPreparationFail() {
		// Create fake plugin without plugin.xml
		InMemoryJFileSystem fs = new InMemoryJFileSystem()
		fs.setContentFromText("src/main/module/plugins/MyPlugin/jsp/test.jsp", """
			<HTML>
			<BODY>
				<% out.println("hello world")
			</BODY>
			</HTML>
		""", "UTF-8")
		
		// Initialize task
		try {
			this.task.createModuleFs("MyPlugin", "1.0", fs, [], null)
		} catch(Exception e) {
			assert e.getMessage() == "plugin.xml file not found. Unable to push plugin"
		}
	}
	
	@Test
	void whenPluginXmlReferencesJars_thenFail() {
		// Create fake plugin without plugin.xml
		InMemoryJFileSystem fs = new InMemoryJFileSystem()
		fs.setContentFromText(
			"src/main/module/WEB-INF/plugins/MyPlugin/plugin.xml", 
			"""<?xml version="1.0" encoding="UTF-8"?>
			<!DOCTYPE plugin PUBLIC "-//JALIOS//DTD JCMS-PLUGIN 1.7//EN" "http://support.jalios.com/dtd/jcms-plugin-1.7.dtd">
			<plugin name="MyPlugin" version="0.1" author="Lionel HERVIER" license="As Is" initialize="true" jcms="" order="0" url="" jsync="false" appserver="">
				<jars>
					<jar path="test.jar" />
				</jars>
			</plugin>
			""", 
			"UTF-8"
		)
		
		// Initialize task
		try {
			this.task.createModuleFs("MyPlugin", "1.0", fs, [], null)
		} catch(Exception e) {
			assert e.getMessage() == "You must not declare jars inside your plugin.xml. Use gradle dependencies instead."
		}
	}
	
	@Test
	void whenDependencies_thenDependenciesCopied() {
		// Create fake plugin without plugin.xml
		InMemoryJFileSystem fs = new InMemoryJFileSystem()
		fs.setContentFromText(
			"src/main/module/WEB-INF/plugins/MyPlugin/plugin.xml",
			"""<?xml version="1.0" encoding="UTF-8"?>
			<!DOCTYPE plugin PUBLIC "-//JALIOS//DTD JCMS-PLUGIN 1.7//EN" "http://support.jalios.com/dtd/jcms-plugin-1.7.dtd">
			<plugin name="XYZ" version="ABC" author="Lionel HERVIER" license="As Is" initialize="true" jcms="" order="0" url="" jsync="false" appserver="">
			</plugin>
			""", 
			"UTF-8"
		)
		fs.setContentFromText("src/main/module/test.css", "css content", "UTF-8")
		
		// Create fake dependencies (on disk)
		File jar1 = new File(this.tmp, "jar1.jar")
		File jar2 = new File(this.tmp, "jar2.jar")
		jar1.text = "not a jar content..."
		jar2.text = "neither a jar content..."
		List<File> dependencies = new ArrayList()
		dependencies.add(jar1)
		dependencies.add(jar2)
		
		// Create fake main jar
		File main = new File(this.tmp, "main.jar")
		main.text = "content of main jar"
		
		// Prepare module
		JFileSystem rootFs = this.task.createModuleFs("MyPlugin", "1.0", fs, dependencies, main)
		
		// Check that module has been copied
		assert fs.exists("build/module/WEB-INF/plugins/MyPlugin/plugin.xml")
		assert rootFs.exists("WEB-INF/plugins/MyPlugin/plugin.xml")
		assert fs.exists("build/module/test.css")
		assert rootFs.exists("test.css")
		assert fs.exists("build/module/WEB-INF/lib/jar1.jar")
		assert rootFs.exists("WEB-INF/lib/jar1.jar")
		assert fs.exists("build/module/WEB-INF/lib/jar2.jar")
		assert rootFs.exists("WEB-INF/lib/jar2.jar")
		assert fs.exists("build/module/WEB-INF/lib/main.jar")
		assert rootFs.exists("WEB-INF/lib/main.jar")
		
		// Check that plugin.xml has been adapted
		PluginXml pluginXml = null
		rootFs.getContentAsStream("WEB-INF/plugins/MyPlugin/plugin.xml") { inStream ->
			pluginXml = new PluginXml(inStream)
		}
		assert pluginXml != null
		
		assert pluginXml.name == "MyPlugin"
		assert pluginXml.version == "1.0"
		
		assert pluginXml.jars.size() == 3
		List<String> jars = new ArrayList()
		pluginXml.jars.each {
			jars.add(it.path)
		}
		assert jars.contains("jar1.jar")
		assert jars.contains("jar2.jar")
		assert jars.contains("main.jar")
	}
	
	@Test
	void whenAlreadyCopied_thenDontCopy() {
		// Create fake plugin without plugin.xml
		InMemoryJFileSystem fs = new InMemoryJFileSystem()
		fs.setContentFromText(
			"src/main/module/WEB-INF/plugins/MyPlugin/plugin.xml",
			"""<?xml version="1.0" encoding="UTF-8"?>
			<!DOCTYPE plugin PUBLIC "-//JALIOS//DTD JCMS-PLUGIN 1.7//EN" "http://support.jalios.com/dtd/jcms-plugin-1.7.dtd">
			<plugin name="XYZ" version="ABC" author="Lionel HERVIER" license="As Is" initialize="true" jcms="" order="0" url="" jsync="false" appserver="">
			</plugin>
			""", 
			"UTF-8"
		)
		fs.setContentFromText("src/main/module/test.css", "css content", "UTF-8")
		
		// Prepare module
		JFileSystem rootFs = this.task.createModuleFs("MyPlugin", "1.0", fs, new ArrayList(), null)
		
		// Check that module has been copied
		assert fs.exists("build/module/test.css")
		assert rootFs.exists("test.css")
		
		// Add file
		long cssUpdated = rootFs.path("test.css").updated
		fs.setContentFromText("src/main/module/test2.css", "content of second css", "UTF-8")
		
		// Prepare module again
		rootFs = this.task.createModuleFs("MyPlugin", "1.0", fs, new ArrayList(), null)
		
		// Check that module has been copied
		assert fs.exists("build/module/test.css")
		assert rootFs.exists("test.css")
		assert fs.exists("build/module/test2.css")
		assert rootFs.exists("test2.css")
		
		// Check first file has not been copied again
		assert rootFs.path("test.css").updated == cssUpdated
	}
	
	// ===========================================================================
	
	@Test
	void whenEmptyPlatformModule_thenModuleDeployed() {
		this.addPluginXml(this.currModule)
		this.currModuleFs.addFile("css/styles.css")
		this.currModuleFs.addFile("css/styles2.css")
		
		this.currModule.init(
			null, 
			[{JModule m, Closure<JPath> closure ->
				closure(new JPath(FSType.ROOT, "css/styles.css"))
				closure(new JPath(FSType.ROOT, "css/styles2.css"))
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
			m.rootFs.paths("css/*.css") { fsFile ->
				closure(new JPath(FSType.ROOT, fsFile.path))
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
			m.rootFs.paths("css/*.less") { fsFile ->
				String css = fsFile.path.substring(0, fsFile.path.length() - 4) + "css"
				closure(new GeneratedPath(
					path: new JPath(FSType.ROOT, fsFile.path), 
					source: new JPath(FSType.ROOT, css)
				))
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
				String css = less.path.substring(0, less.path.length() - 4) + "css"
				closure(new GeneratedPath(
					path: new JPath(FSType.ROOT, css), 
					source: new JPath(FSType.ROOT, less.path)
				))
			}
		} as GeneratedFileExtractor
		
		// less extractor
		// => Declare less files
		def srcExtractor = {JModule m, Closure<JPath> closure ->
			m.rootFs.paths("css/*.less") { fsFile ->
				closure(new JPath(FSType.ROOT, fsFile.path))
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
	
	@Test
	void whenFileNotUpdated_thenFileNotOverwritten() {
		// Populate plugin
		this.addPluginXml(
				this.currModule,
				"""<public-files>
					<directory path="images" />
				</public-files>"""
		)
		this.currModuleFs.setContentFromText("images/test.jpg", "image content", "UTF-8")
		
		// Initialize modules
		def extractor = {JModule m, Closure<String> closure ->
			m.rootFs.paths("images/*.jpg") { fsFile ->
				closure(new JPath(FSType.ROOT, fsFile.path))
			}
		} as SourceFileExtractor
		this.currModule.init(null, [extractor])
		this.platformModule.init(null, [extractor])
		
		// Check platform fs
		assert !this.platformModuleFs.exists("images/test.jpg")
		
		// Push plugin
		this.task.run(this.platformModule, this.currModule)
		
		// File must have been created
		assert this.platformModuleFs.exists("images/test.jpg")
		long updated = this.platformModuleFs.path("images/test.jpg").updated
		
		// Push plugin again
		this.currModule.init(null, [extractor])
		this.platformModule.init(null, [extractor])
		this.task.run(this.platformModule, this.currModule)
		
		// File must not have been updated
		assert this.platformModuleFs.path("images/test.jpg").updated == updated
	}
}
