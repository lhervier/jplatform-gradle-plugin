package com.jalios.gradle.plugin.jplatform.source.impl

import static org.hamcrest.CoreMatchers.not
import static org.hamcrest.core.IsCollectionContaining.hasItem
import static org.hamcrest.core.IsCollectionContaining.hasItems
import static org.junit.Assert.assertThat

import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.fs.JPath
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestPrivateFilesExtractor {

	InMemoryJFileSystem fs
	JModule module
	PrivateFilesExtractor extractor
	
	@Before
	void setUp() {
		this.fs = new InMemoryJFileSystem()
		this.module = new JModule("TestPlugin", this.fs, new InMemoryJFileSystem())
		this.extractor = new PrivateFilesExtractor()
	}
	
	@Test
	void whenPrivateFiles_thenFilesExtracted() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
					<plugin name="TestASIPlugin">
						<private-files>
							<file path="rep/foo.xml"/>
						</private-files>
					</plugin>
				"""
			)
		)
		this.fs.addFile("WEB-INF/plugins/TestPlugin/rep/foo.xml")
		this.fs.addFile("WEB-INF/plugins/TestPlugin/rep/bar.xml")
		this.fs.addFile("WEB-INF/plugins/TestPlugin/rep/baz.yaml")
		this.module.init(null, null)
		
		List<JPath> paths = new ArrayList()
		this.extractor.extract(this.module) { jpath ->
			paths.add(jpath)
		}
		
		assertThat(paths, hasItems(new JPath(FSType.PRIVATE, "rep/foo.xml")))
		assertThat(paths, not(hasItems(new JPath(FSType.PRIVATE, "rep/bar.xml"))))
	}
	
	@Test
	void whenWildcardPrivateFiles_thenFilesExtracted() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
					<plugin name="TestASIPlugin">
						<private-files>
							<file path="rep/*.xml"/>
						</private-files>
					</plugin>
				"""
			)
		)
		this.fs.addFile("WEB-INF/plugins/TestPlugin/rep/foo.xml")
		this.fs.addFile("WEB-INF/plugins/TestPlugin/rep/bar.xml")
		this.fs.addFile("WEB-INF/plugins/TestPlugin/rep/baz.yaml")
		this.module.init(null, null)
		
		List<JPath> paths = new ArrayList()
		this.extractor.extract(this.module) { jpath ->
			paths.add(jpath)
		}
		
		assertThat(paths, hasItems(
			new JPath(FSType.PRIVATE, "rep/foo.xml"),
			new JPath(FSType.PRIVATE, "rep/bar.xml")
		))
	}
	
	@Test
	void whenPrivateDirectory_thenFilesExtracted() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
					<plugin name="TestASIPlugin">
						<private-files>
							<directory path="rep"/>
						</private-files>
					</plugin>
				"""
			)
		)
		this.fs.addFile("WEB-INF/plugins/TestPlugin/rep/foo.xml")
		this.fs.addFile("WEB-INF/plugins/TestPlugin/rep/bar.xml")
		this.fs.addFile("WEB-INF/plugins/TestPlugin/rep/baz.yaml")
		this.fs.addFile("WEB-INF/plugins/TestPlugin/other/other.yaml")
		this.module.init(null, null)
		
		List<JPath> paths = new ArrayList()
		this.extractor.extract(this.module) { jpath ->
			paths.add(jpath)
		}
		
		assert paths.size() == 3
		assertThat(paths, hasItem(new JPath(FSType.PRIVATE, "rep/foo.xml")))
		assertThat(paths, hasItem(new JPath(FSType.PRIVATE, "rep/bar.xml")))
		assertThat(paths, hasItem(new JPath(FSType.PRIVATE, "rep/baz.yaml")))
	}
}
