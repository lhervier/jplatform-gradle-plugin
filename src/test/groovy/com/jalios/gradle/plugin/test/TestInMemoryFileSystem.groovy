package com.jalios.gradle.plugin.test

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class TestInMemoryFileSystem {

	private InMemoryFileSystem fs
	
	@Before
	void setup() {
		this.fs = new InMemoryFileSystem()
	}

	@Test
	void whenPathMatch_thenOK() {
		assert this.fs.match("folder1/folder38/fichier.txt", "folder1/folder*/fichier.txt")
		assert !this.fs.match("folder1/folder38/folder/fichier.txt", "folder1/folder*/fichier.txt")
		assert this.fs.match("folder1/folder38/folder24/test.txt", "folder1/**/test.txt")
	}

	@Test
	void whenFileExists_thenOK() {
		this.fs.addFile("test.txt");
		assert this.fs.exists("test.txt")
		assert this.fs.exists("/test.txt")
		assert !this.fs.exists("test2.txt")
		
		this.fs.addFile("folder1/test2.txt")
		assert this.fs.exists("folder1/test2.txt")
		assert this.fs.exists("/folder1/test2.txt")
		assert !this.fs.exists("folder1/test3.txt")
	}
	
	@Test
	void whenFilesAtRoot_thenExtractionOK() {
		this.fs.addFile("test1.txt")
		this.fs.addFile("test2.txt")
		this.fs.addFile("test.other")
		
		List<String> paths = new ArrayList()
		this.fs.paths("*.txt") { path ->
			paths.add(path)
		}
		assert paths.size() == 2
		assert paths.contains("test1.txt")
		assert paths.contains("test2.txt")
	}
	
	@Test
	void whenFilesInFolder_thenExtractionOK() {
		this.fs.addFile("folder/test1.txt")
		this.fs.addFile("folder/test2.txt")
		this.fs.addFile("folder/test.other")
		List<String> paths = new ArrayList()
		this.fs.paths("**/*.txt") { path ->
			paths.add(path)
		}
		assert paths.size() == 2
		assert paths.contains("folder/test1.txt")
		assert paths.contains("folder/test2.txt")
	}

	@Test
	void whenUsingFilenameWildcards_thenExtractionOK() {
		this.fs.addFile("folder/test1.txt")
		this.fs.addFile("folder/test2.txt")
		this.fs.addFile("folder/other.txt")
		
		List<String> paths = new ArrayList()
		this.fs.paths("folder/test*.txt") { path ->
			paths.add(path)
		}
		assert paths.size() == 2
		assert paths.contains("folder/test1.txt")
		assert paths.contains("folder/test2.txt")
	}
	
	@Test
	void whenUsingFolderWildcards_thenExtractionOK() {
		this.fs.addFile("folder1/test1.txt")
		this.fs.addFile("folder1/other.txt")
		this.fs.addFile("folder2/test2.txt")
		this.fs.addFile("folder2/other.txt")
		
		List<String> paths = new ArrayList()
		this.fs.paths("**" + "/test*.txt") { path ->
			paths.add(path)
		}
		assert paths.size() == 2
		assert paths.contains("folder1/test1.txt")
		assert paths.contains("folder2/test2.txt")
	}
	
	@Test
	void whenUsingDeepFolderWildcards_thenExtractionOK() {
		this.fs.addFile("folder1/folder1.1/test1.txt")
		this.fs.addFile("folder1/folder1.1/other.txt")
		this.fs.addFile("folder2/test2.txt")
		this.fs.addFile("folder2/other.txt")
		
		List<String> paths = new ArrayList()
		this.fs.paths("**" + "/test*.txt") { path ->
			paths.add(path)
		}
		assert paths.size() == 2
		assert paths.contains("folder1/folder1.1/test1.txt")
		assert paths.contains("folder2/test2.txt")
	}
	
	@Test
	void whenDeleting_thenFileDeleted() {
		this.fs.addFile("test.txt")
		assert this.fs.exists("test.txt")
		
		this.fs.delete("test.txt")
		assert !this.fs.exists("test.txt")
	}
	
	@Test
	void whenDeletingNonExistingFile_thenDoNothing() {
		this.fs.delete("test.txt")
	}
	
	@Test
	void whenCopyingFile_thenFileCopied() {
		String path = "folder1/folder1.1/test1.txt"
		
		this.fs.addFile(path)
		this.fs.setContentFromStream(
				path, 
				new ByteArrayInputStream((byte[]) [48, 49, 50, 51])
		)
		
		InMemoryFileSystem fs2 = new InMemoryFileSystem()
		this.fs.getContentAsStream(path) { inStream ->
			fs2.setContentFromStream(path, inStream)
		}
		fs2.getContentAsReader(path, "UTF-8") { reader ->
			assert reader.text == "0123"
		}
	}
	
	@Test
	void whenGetContent_thenContentSent() {
		this.fs.addFile("test.txt")
		this.fs.setContentFromStream("test.txt", new ByteArrayInputStream((byte[]) [48, 49, 50]))
		this.fs.getContentAsReader("test.txt", "UTF-8") { reader ->
			assert reader.readLine() == "012"
		}
	}
}
