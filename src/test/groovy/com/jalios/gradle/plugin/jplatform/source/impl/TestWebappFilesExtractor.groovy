package com.jalios.gradle.plugin.jplatform.source.impl

import static org.hamcrest.CoreMatchers.not
import static org.hamcrest.core.IsCollectionContaining.hasItem
import static org.hamcrest.core.IsCollectionContaining.hasItems
import static org.junit.Assert.assertThat

import org.hamcrest.core.IsCollectionContaining
import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.JPath
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestWebappFilesExtractor {

	InMemoryJFileSystem fs
	JModule module
	WebappFilesExtractor extractor
	
	@Before
	void setUp() {
		this.fs = new InMemoryJFileSystem()
		this.module = new JModule("TestPlugin", this.fs, new InMemoryJFileSystem())
		this.extractor = new WebappFilesExtractor()
	}
	
	@Test
	void whenWebappFiles_thenFilesExtracted() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
					<plugin name="TestASIPlugin">
						<webapp-files>
							<file path="my_file.txt"/>
						</webapp-files>
					</plugin>
				"""
			)
		)
		this.fs.addFile("my_file.txt")
		this.fs.addFile("my_file_other.txt")
		this.module.init(null, null)
		
		List<JPath> paths = new ArrayList()
		this.extractor.extract(this.module) { jpath ->
			paths.add(jpath)
		}
		
		assertThat(paths, hasItems(new JPath(FSType.ROOT, "my_file.txt")))
		assertThat(paths, not(hasItems(new JPath(FSType.ROOT, "my_file_other.txt"))))
	}
	
	@Test
	void whenWildcardWebappFiles_thenFilesExtracted() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
					<plugin name="TestASIPlugin">
						<webapp-files>
							<file path="css/*.less"/>
						</webapp-files>
					</plugin>
				"""
			)
		)
		this.fs.addFile("css/foo.less")
		this.fs.addFile("css/bar.less")
		this.fs.addFile("css/x/baz.less")
		this.module.init(null, null)
		
		List<JPath> paths = new ArrayList()
		this.extractor.extract(this.module) { jpath ->
			paths.add(jpath)
		}
		
		assert paths.size() == 2
		assertThat(paths, IsCollectionContaining.hasItem(new JPath(FSType.ROOT, "css/foo.less")))
		assertThat(paths, IsCollectionContaining.hasItem(new JPath(FSType.ROOT, "css/bar.less")))
	}
	
	@Test
	void whenWebappDirectory_thenFilesExtracted() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
					<plugin name="TestASIPlugin">
						<webapp-files>
							<directory path="css"/>
						</webapp-files>
					</plugin>
				"""
			)
		)
		this.fs.addFile("css/foo.less")
		this.fs.addFile("css/bar.less")
		this.fs.addFile("css/x/baz.css")
		this.module.init(null, null)
		
		List<JPath> paths = new ArrayList()
		this.extractor.extract(this.module) { jpath ->
			paths.add(jpath)
		}
		
		assert paths.size() == 3
		assertThat(paths, hasItem(new JPath(FSType.ROOT, "css/foo.less")))
		assertThat(paths, hasItem(new JPath(FSType.ROOT, "css/bar.less")))
		assertThat(paths, hasItem(new JPath(FSType.ROOT, "css/x/baz.css")))
	}
}
