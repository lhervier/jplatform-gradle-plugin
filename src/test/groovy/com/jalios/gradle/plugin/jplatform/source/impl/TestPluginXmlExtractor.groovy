package com.jalios.gradle.plugin.jplatform.source.impl

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestPluginXmlExtractor {

	InMemoryJFileSystem fs
	JModule module
	PluginXmlExtractor extractor
	
	@Before
	void before() {
		this.fs = new InMemoryJFileSystem()
		this.fs.addFile(
				"WEB-INF/plugins/TestPlugin/plugin.xml",
				ByteUtils.extractBytes("""
					<plugin name="TestASIPlugin">
					</plugin>
				""")
		)
		this.module = new JModule(
			"TestPlugin",
			this.fs,
			new InMemoryJFileSystem()
		)
		this.extractor = new PluginXmlExtractor()
	}
	
	@Test
	void whenExtracting_thenFileExtracted() {
		this.module.init(null, [this.extractor])
		
		assert module.paths.size() == 1
		assert module.paths[0].path == "plugin.xml"
		assert module.paths[0].type == FSType.PRIVATE
	}
	
}
