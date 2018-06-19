package com.jalios.gradle.plugin.util

import static org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class TestFileutil {

	private File root
	
	@Before
	public void init() {
		this.root = File.createTempDir()
	}
	
	@After
	public void destroy() {
		if( !this.root.deleteDir() ) {
			throw new Exception("Unable to remove '${this.root.absolutePath}")
		}
	}
	
	@Test
	public void whenFilesAtRoot_thenExtractionOK() {
		new File(this.root, "test1.txt").text = "Content of the first file"
		new File(this.root, "test2.txt").text = "Content of the second file"
		List<String> paths = new ArrayList()
		FileUtil.paths(this.root) { path ->
			paths.add(path)
		}
		assert paths.size() == 2
		assert paths.contains("test1.txt")
		assert paths.contains("test2.txt")
	}
	
	@Test
	public void whenFilesInFolder_thenExtractionOK() {
		new File(this.root, "folder").mkdir()
		new File(this.root, "folder/test1.txt").text = "Content of the first file"
		new File(this.root, "folder/test2.txt").text = "Content of the second file"
		List<String> paths = new ArrayList()
		FileUtil.paths(this.root) { path ->
			paths.add(path)
		}
		assert paths.size() == 2
		assert paths.contains("folder/test1.txt")
		assert paths.contains("folder/test2.txt")
	}

	@Test
	public void whenUsingFilenameWildcards_thenExtractionOK() {
		new File(this.root, "folder").mkdir()
		new File(this.root, "folder/test1.txt").text = "Content of the first file"
		new File(this.root, "folder/test2.txt").text = "Content of the second file"
		new File(this.root, "folder/other.txt").text = "Content of the other file"
		List<String> paths = new ArrayList()
		FileUtil.paths(this.root, "folder/test*.txt") { path ->
			paths.add(path)
		}
		assert paths.size() == 2
		assert paths.contains("folder/test1.txt")
		assert paths.contains("folder/test2.txt")
	}
	
	@Test
	public void whenUsingFolderWildcards_thenExtractionOK() {
		new File(this.root, "folder1").mkdir()
		new File(this.root, "folder2").mkdir()
		new File(this.root, "otherFolder").mkdir()
		
		new File(this.root, "folder1/test1.txt").text = "Content of the first file"
		new File(this.root, "folder1/other.txt").text = "Content of the other file"
		new File(this.root, "folder2/test2.txt").text = "Content of the second file"
		new File(this.root, "folder2/other.txt").text = "Content of the other file"
		List<String> paths = new ArrayList()
		FileUtil.paths(this.root, "**/test*.txt") { path ->
			paths.add(path)
		}
		assert paths.size() == 2
		assert paths.contains("folder1/test1.txt")
		assert paths.contains("folder2/test2.txt")
	}
}
