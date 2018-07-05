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

class TestTypesTypeFileExtractor {

	InMemoryJFileSystem fs
	JModule module
	TypesTypeFileExtractor extractor
	
	@Before
	void setUp() {
		this.fs = new InMemoryJFileSystem()
		this.module = new JModule("TestPlugin", this.fs)
		this.extractor = new TypesTypeFileExtractor()
	}
	
	@Test
	void whenFilesInType_thenExtractFiles() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
				<plugin name="TestASIPlugin">
					<types>
						<type name="MyType1">
							<file path="doSomething.jsp"/>
							<file path="doSomething2.jsp"/>
						</type>
					</types>
				</plugin>
			""")
		)
		this.fs.addFile("WEB-INF/data/types/MyType1/MyType1.xml")
		this.fs.addFile("types/MyType1/doSomething.jsp")
		this.fs.addFile("types/MyType1/doSomething2.jsp")
		this.module.init(null, null)
		
		List<String> paths = new ArrayList()
		this.extractor.extract(this.module) { path ->
			paths.add(path)
		}
		
		assert paths.size() == 2
		assert paths[0] == "types/MyType1/doSomething.jsp"
		assert paths[1] == "types/MyType1/doSomething2.jsp"
	}
	
	@Test
	void whenWildcardFilesInType_thenExtractFiles() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
				<plugin name="TestASIPlugin">
					<types>
						<type name="MyType1">
							<file path="*.jsp"/>
						</type>
					</types>
				</plugin>
			""")
		)
		this.fs.addFile("WEB-INF/data/types/MyType1/MyType1.xml")
		this.fs.addFile("types/MyType1/doSomething.jsp")
		this.fs.addFile("types/MyType1/doSomething2.jsp")
		this.module.init(null, null)
		
		List<String> paths = new ArrayList()
		this.extractor.extract(this.module) { path ->
			paths.add(path)
		}
		
		assert paths.size() == 2
		assertThat(paths, hasItems(
			"types/MyType1/doSomething.jsp",
			"types/MyType1/doSomething2.jsp"
		))
	}
}
