package com.jalios.gradle.plugin.jplatform.source.impl

import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.core.IsCollectionContaining.hasItem
import static org.hamcrest.core.IsCollectionContaining.hasItems
import static org.junit.Assert.assertThat

import org.gradle.internal.impldep.bsh.This
import org.hamcrest.CoreMatchers
import org.hamcrest.core.IsCollectionContaining
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestTypesTypeXmlExtractor {

	InMemoryJFileSystem fs
	JModule module
	TypesTypeXmlExtractor extractor
	
	@Before
	void setUp() {
		this.fs = new InMemoryJFileSystem()
		this.module = new JModule("TestPlugin", this.fs)
		this.extractor = new TypesTypeXmlExtractor()
	}
	
	@Test(expected = JTaskException)
	void whenTypeDoesNotExists_thenError() {
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
		
		this.extractor.extract(this.module) {
		}
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
		this.fs.addFile("WEB-INF/data/types/MyType1/MyType1.xml")
		this.module.init(null, null)
		
		List<String> paths = new ArrayList()
		this.extractor.extract(this.module) { path ->
			paths.add(path)
		}
		assert paths.size() == 1
		assert paths[0] == "WEB-INF/data/types/MyType1/MyType1.xml"
	}
}
