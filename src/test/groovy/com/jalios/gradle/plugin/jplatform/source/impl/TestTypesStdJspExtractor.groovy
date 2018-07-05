package com.jalios.gradle.plugin.jplatform.source.impl

import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestTypesStdJspExtractor {

	InMemoryJFileSystem fs
	JModule module
	TypesStdJspExtractor extractor
	
	@Before
	void setUp() {
		this.fs = new InMemoryJFileSystem()
		this.module = new JModule("TestPlugin", this.fs)
		this.extractor = new TypesStdJspExtractor()
	}
	
	@Test
	void whenNoStdJsp_thenNothingExtracted() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
				<plugin name="TestASIPlugin">
					<types>
						<type name="MyType1"/>
					</types>
				</plugin>
			""")
		)
		this.fs.addFile("WEB-INF/data/types/MyType1/MyType1.xml")
		this.module.init(null, null)
		
		List<String> paths = new ArrayList()
		this.extractor.extract(this.module) { path ->
			paths.add(path)
		}
		
		assert paths.size() == 0
	}
	
	@Test
	void whenStdJspExist_thenJspExtracted() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
				<plugin name="TestASIPlugin">
					<types>
						<type name="MyType1"/>
					</types>
				</plugin>
			""")
		)
		this.fs.addFile("WEB-INF/data/types/MyType1/MyType1.xml")
		this.fs.addFile("types/MyType1/doEditMyType1.jsp")
		this.module.init(null, null)
		
		List<String> paths = new ArrayList()
		this.extractor.extract(this.module) { path ->
			paths.add(path)
		}
		
		assert paths.size() == 1
		assert paths[0] == "types/MyType1/doEditMyType1.jsp"
	}
}
