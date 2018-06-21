package com.jalios.gradle.plugin.jplatform.gen

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.jplatform.GeneratedPath
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.test.InMemoryFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestCssExtractor {

	InMemoryFileSystem fs
	JModule module
	CssExtractor extractor
	
	@Before
	void before() {
		this.fs = new InMemoryFileSystem()
		this.fs.addFile(
				"WEB-INF/plugins/TestPlugin/plugin.xml",
				ByteUtils.extractBytes("""
					<plugin name="TestASIPlugin">
					</plugin>
				""")
		)
		this.module = new JModule("TestPlugin", this.fs)
		this.extractor = new CssExtractor(module: module)
	}
	
	@Test
	public void whenLess_thenCssGenerated() {
		this.fs.addFile(
				"WEB-INF/plugins/TestPlugin/properties/plugin.prop",
				ByteUtils.extractBytes("""
					channel.less.plugins/TestASIPlugin/css/plugin.css: plugins/TestASIPlugin/css/plugin.less
				""")
		)
		this.module.reload()
		
		List<GeneratedPath> gps = this.module.generatedPaths
		assert gps.size() == 1
		assert gps[0].path == "plugins/TestASIPlugin/css/plugin.css"
		assert gps[0].source == "plugins/TestASIPlugin/css/plugin.less"
	}

}
