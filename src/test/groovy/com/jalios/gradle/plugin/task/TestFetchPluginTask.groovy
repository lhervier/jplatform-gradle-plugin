package com.jalios.gradle.plugin.task

import org.junit.Test

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.JPath
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.PluginXmlExtractor
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestFetchPluginTask extends BaseTestTask {

	FetchPluginTask task
	
	@Override
	void setUp() {
		this.task = new FetchPluginTask()
	}

	@Test(expected = JTaskException.class)
	void whenCurrModuleExists_thenFetchFails() {
		this.addPluginXml(this.currModule)
		this.currModule.init(null, null)
		
		this.platformModule.init(null, null)
		this.addPluginXml(this.platformModule)
		
		this.task.run(this.platformModule, this.currModule)
	}
	
	@Test(expected = JTaskException.class)
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
					extract: {JModule module, Closure<String> closure -> closure(new JPath(FSType.ROOT, "test1"))}
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
