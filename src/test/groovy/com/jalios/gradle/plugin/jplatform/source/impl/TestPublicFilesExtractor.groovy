package com.jalios.gradle.plugin.jplatform.source.impl

import static org.hamcrest.CoreMatchers.not
import static org.hamcrest.core.IsCollectionContaining.hasItem
import static org.hamcrest.core.IsCollectionContaining.hasItems
import static org.junit.Assert.assertThat

import org.hamcrest.CoreMatchers
import org.hamcrest.core.IsCollectionContaining
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestPublicFilesExtractor {

	InMemoryJFileSystem fs
	JModule module
	PublicFilesExtractor extractor
	
	@Before
	void setUp() {
		this.fs = new InMemoryJFileSystem()
		this.module = new JModule("TestPlugin", this.fs)
		this.extractor = new PublicFilesExtractor()
	}
	
	@Test
	void whenPublicFiles_thenFilesExtracted() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
					<plugin name="TestASIPlugin">
						<public-files>
							<file path="my_file.txt"/>
						</public-files>
					</plugin>
				"""
			)
		)
		this.fs.addFile("plugins/TestPlugin/my_file.txt")
		this.fs.addFile("plugins/TestPlugin/my_file_other.txt")
		this.module.init(null, null)
		
		List<String> paths = new ArrayList()
		this.extractor.extract(this.module) { path ->
			paths.add(path.toString())
		}
		
		assertThat(paths, hasItems("plugins/TestPlugin/my_file.txt"))
		assertThat(paths, not(hasItems("plugins/TestPlugin/my_file_other.txt")))
	}
	
	@Test
	void whenWildcardPublicFiles_thenFilesExtracted() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
					<plugin name="TestASIPlugin">
						<public-files>
							<file path="css/*.less"/>
						</public-files>
					</plugin>
				"""
			)
		)
		this.fs.addFile("plugins/TestPlugin/css/foo.less")
		this.fs.addFile("plugins/TestPlugin/css/bar.less")
		this.fs.addFile("plugins/TestPlugin/css/baz.css")
		this.module.init(null, null)
		
		List<String> paths = new ArrayList()
		this.extractor.extract(this.module) { path ->
			paths.add(path.toString())
		}
		
		assert paths.size() == 2
		assertThat(paths, IsCollectionContaining.hasItem("plugins/TestPlugin/css/foo.less"))
		assertThat(paths, IsCollectionContaining.hasItem("plugins/TestPlugin/css/bar.less"))
	}
	
	@Test
	void whenPublicDirectory_thenFilesExtracted() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
					<plugin name="TestASIPlugin">
						<public-files>
							<directory path="css"/>
						</public-files>
					</plugin>
				"""
			)
		)
		this.fs.addFile("plugins/TestPlugin/css/foo.less")
		this.fs.addFile("plugins/TestPlugin/css/bar.less")
		this.fs.addFile("plugins/TestPlugin/css/x/baz.css")
		this.module.init(null, null)
		
		List<String> paths = new ArrayList()
		this.extractor.extract(this.module) { path ->
			paths.add(path.toString())
		}
		
		assert paths.size() == 3
		assertThat(paths, hasItem("plugins/TestPlugin/css/foo.less"))
		assertThat(paths, hasItem("plugins/TestPlugin/css/bar.less"))
		assertThat(paths, hasItem("plugins/TestPlugin/css/x/baz.css"))
	}
}
