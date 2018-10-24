package com.jalios.gradle.plugin.jplatform.source.impl

import static org.hamcrest.core.IsCollectionContaining.hasItems
import static org.junit.Assert.assertThat

import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.JPath
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestTypesTypeFileExtractor {

	InMemoryJFileSystem fs
	JModule module
	TypesTypeFileExtractor extractor
	
	@Before
	void setUp() {
		this.fs = new InMemoryJFileSystem()
		this.module = new JModule("TestPlugin", this.fs, new InMemoryJFileSystem())
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
		
		List<JPath> paths = new ArrayList()
		this.extractor.extract(this.module) { jpath ->
			paths.add(jpath)
		}
		
		assert paths.size() == 2
		assert paths[0].path == "types/MyType1/doSomething.jsp"
		assert paths[0].type == FSType.ROOT
		assert paths[1].path == "types/MyType1/doSomething2.jsp"
		assert paths[1].type == FSType.ROOT
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
		
		List<JPath> paths = new ArrayList()
		this.extractor.extract(this.module) { jpath ->
			paths.add(jpath)
		}
		
		assert paths.size() == 2
		assertThat(paths, hasItems(
			new JPath(FSType.ROOT, "types/MyType1/doSomething.jsp"),
			new JPath(FSType.ROOT, "types/MyType1/doSomething2.jsp")
		))
	}
}
