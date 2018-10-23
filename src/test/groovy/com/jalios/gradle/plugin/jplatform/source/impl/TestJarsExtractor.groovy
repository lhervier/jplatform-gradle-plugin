package com.jalios.gradle.plugin.jplatform.source.impl

import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.fs.JPath
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestJarsExtractor {

	InMemoryJFileSystem fs
	InMemoryJFileSystem dataFs
	JModule module
	JarsExtractor extractor
	
	@Before
	void setUp() {
		this.fs = new InMemoryJFileSystem()
		this.dataFs = new InMemoryJFileSystem()
		this.module = new JModule("TestPlugin", this.fs, this.dataFs)
		this.extractor = new JarsExtractor()
	}
	
	@Test
	void whenNoJarsTag_thenNoJarExtracted() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
				<plugin name="TestASIPlugin">
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
	void whenNoJarTag_thenNoJarExtracted() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
				<plugin name="TestASIPlugin">
					<jars/>
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
	
	void whenJarDoesNotExists_thenJarIgnored() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
				<plugin name="TestASIPlugin">
					<jars>
						<jar path="jar1.jar"/>
					</jars>
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
	void whenJarExists_thenJarExtracted() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
				<plugin name="TestASIPlugin">
					<jars>
						<jar path="jar1.jar"/>
					</jars>
				</plugin>
			""")
		)
		this.fs.addFile("WEB-INF/lib/jar1.jar")
		this.module.init(null, null)
		
		List<JPath> paths = new ArrayList()
		this.extractor.extract(this.module) { jpath ->
			paths.add(jpath)
		}
		assert paths.size() == 1
		assert paths[0].path == "WEB-INF/lib/jar1.jar"
		assert paths[0].type == FSType.ROOT
	}
	
}
