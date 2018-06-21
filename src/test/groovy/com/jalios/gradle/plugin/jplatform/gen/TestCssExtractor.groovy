package com.jalios.gradle.plugin.jplatform.gen

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.gen.impl.CssExtractor
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
		this.module = new JModule(
			"TestPlugin", 
			this.fs
		)
		this.extractor = new CssExtractor()
	}
	
	@Test
	public void whenLess_thenCssGenerated() {
		this.fs.addFile(
				"WEB-INF/plugins/TestPlugin/properties/plugin.prop",
				ByteUtils.extractBytes("""
					channel.less.plugins/TestASIPlugin/css/plugin.css: plugins/TestASIPlugin/css/plugin.less
					channel.less.plugins/TestASIPlugin/css/other.css: plugins/TestASIPlugin/css/test.less
				""")
		)
		this.module.init([this.extractor], new ArrayList())
		
		List<GeneratedPath> gps = new ArrayList()
		this.module.generatedPaths.each() { path ->
			gps.add(path)
		}
		assert gps.size() == 2
		assert gps[0].path == "plugins/TestASIPlugin/css/plugin.css"
		assert gps[0].source == "plugins/TestASIPlugin/css/plugin.less"
		assert gps[1].path == "plugins/TestASIPlugin/css/other.css"
		assert gps[1].source == "plugins/TestASIPlugin/css/test.less"
	}

}
