package com.jalios.gradle.plugin.jplatform.source.impl

import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.fs.JPath
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestTypesJspExtractor {

	InMemoryJFileSystem fs
	JModule module
	TypesJspExtractor extractor
	
	@Before
	void setUp() {
		this.fs = new InMemoryJFileSystem()
		this.module = new JModule("TestPlugin", this.fs, new InMemoryJFileSystem())
		this.extractor = new TypesJspExtractor()
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
		this.fs.addFile("types/MyType1/doEditMyType1.jsp")		// Standard JSP.
		this.fs.addFile("types/MyType1/doCustom.jsp")
		this.module.init(null, null)
		
		List<JPath> paths = new ArrayList()
		this.extractor.extract(this.module) { jpath ->
			paths.add(jpath)
		}
		
		assert paths.size() == 1
		assert paths[0].path == "types/MyType1/doCustom.jsp"
		assert paths[0].type == FSType.ROOT
	}
}
