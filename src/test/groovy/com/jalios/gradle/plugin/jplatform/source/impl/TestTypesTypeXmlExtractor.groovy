package com.jalios.gradle.plugin.jplatform.source.impl

import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.fs.JPath
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestTypesTypeXmlExtractor {

	InMemoryJFileSystem fs
	InMemoryJFileSystem dataFs
	JModule module
	TypesTypeXmlExtractor extractor
	
	@Before
	void setUp() {
		this.fs = new InMemoryJFileSystem()
		this.dataFs = new InMemoryJFileSystem()
		this.module = new JModule("TestPlugin", this.fs, this.dataFs)
		this.extractor = new TypesTypeXmlExtractor()
	}
	
	void whenTypeDoesNotExists_thenTypeIgnored() {
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
		this.module.init(null, null)
		
		List<JPath> paths = new ArrayList()
		this.extractor.extract(this.module) { jpath -> 
			paths.add(jpath)
		}
		assert paths.size() == 0
	}
	
	@Test
	void whenTypeExists_thenXmlExtracted() {
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
		this.dataFs.addFile("types/MyType1/MyType1.xml")
		this.module.init(null, null)
		
		List<JPath> paths = new ArrayList()
		this.extractor.extract(this.module) { jpath ->
			paths.add(jpath)
		}
		assert paths.size() == 1
		assert paths[0].path == "types/MyType1/MyType1.xml"
		assert paths[0].type == FSType.DATA
	}
}
