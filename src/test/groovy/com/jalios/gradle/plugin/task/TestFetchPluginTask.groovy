package com.jalios.gradle.plugin.task

import static org.junit.Assert.*

import org.gradle.internal.impldep.bsh.This
import org.junit.After
import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.JException
import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.PluginXmlExtractor
import com.jalios.gradle.plugin.task.impl.FetchPluginTask
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

import groovy.lang.Closure

class TestFetchPluginTask {

	FetchPluginTask task
	InMemoryJFileSystem currModuleFs
	InMemoryJFileSystem platformModuleFs
	JModule currModule
	JModule platformModule
	
	private void addPluginXml(JModule module) {
		module.rootFs.addFile("WEB-INF/plugins/${module.name}/plugin.xml", ByteUtils.extractBytes("""
			<plugin name="${module.name}">
			</plugin>
		"""))
	}
	
	private void addPluginProp(JModule module) {
		module.rootFs.addFile("WEB-INF/plugins/${module.name}/properties/plugin.prop", ByteUtils.extractBytes("""
			
		"""))
	}
	
	@Before
	void setUp() {
		this.currModuleFs = new InMemoryJFileSystem()
		this.currModule = new JModule("TestPlugin", this.currModuleFs)
		
		this.platformModuleFs = new InMemoryJFileSystem()
		this.platformModule = new JModule("TestPlugin", this.platformModuleFs)
		
		this.task = new FetchPluginTask()
	}

	@Test(expected = JException.class)
	void whenCurrModuleExists_thenFetchFails() {
		this.currModuleFs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml", 
			ByteUtils.extractBytes("""
				<plugin name="TestPlugin">
				</plugin>
			"""))
		this.platformModule.init(null, null)
		this.currModule.init(null, null)
		
		this.task.run(this.platformModule, this.currModule)
	}
	
	@Test(expected = JException.class)
	void whenPlatformModuleDoesNotExist_thenFetchFail() {
		this.platformModule.init(null, null)
		this.currModule.init(null, null)
		
		this.task.run(this.platformModule, this.currModule)
	}
	
	@Test
	void whenFetching_thenFilesCopied() {
		// Add two files
		this.addPluginXml(this.platformModule)
		this.addPluginProp(this.platformModule)
		this.platformModuleFs.addFile("test1", ByteUtils.extractBytes("hello"))
		this.platformModuleFs.addFile("test2", ByteUtils.extractBytes("world"))
		
		// Init platform module with a source extractor that only extract the first one
		// Also adding PluginXmlExtractor for fun !!
		this.platformModule.init(
			null, 
			[
				new Expando(
					extract: {JModule module, Closure<String> closure -> closure("test1")}
				) as SourceFileExtractor,
				new PluginXmlExtractor()
			]
		)
		
		// Init curr module
		this.currModule.init(null, null)
		
		// Launch task
		this.task.run(this.platformModule, this.currModule)
		
		// Expect only the first file
		assert !this.currModuleFs.exists("test2")
		assert this.currModuleFs.exists("WEB-INF/plugins/TestPlugin/plugin.xml")
		assert this.currModuleFs.exists("test1")
		this.currModuleFs.getContentAsReader("test1", "UTF-8") { reader ->
			assert reader.text == "hello"
		}
	}
}
