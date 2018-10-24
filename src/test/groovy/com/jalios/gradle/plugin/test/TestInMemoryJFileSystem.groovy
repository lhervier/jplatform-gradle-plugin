package com.jalios.gradle.plugin.test

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class TestInMemoryJFileSystem {

	private InMemoryJFileSystem fs
	
	@Before
	void setup() {
		this.fs = new InMemoryJFileSystem()
	}

	@Test
	void whenPathMatch_thenOK() {
		assert this.fs.match("folder1/folder38/fichier.txt", "folder1/folder*/fichier.txt")
		assert !this.fs.match("folder1/folder38/folder/fichier.txt", "folder1/folder*/fichier.txt")
		assert this.fs.match("folder1/folder38/folder24/test.txt", "folder1/**/test.txt")
		assert this.fs.match("test.txt", "**/*")
		assert this.fs.match("folder/test.txt", "folder/**/*")
	}

	@Test
	void whenFileExists_thenOK() {
		this.fs.addFile("test.txt");
		assert this.fs.exists("test.txt")
		assert !this.fs.exists("test2.txt")
		
		this.fs.addFile("folder1/test2.txt")
		assert this.fs.exists("folder1/test2.txt")
		assert !this.fs.exists("folder1/test3.txt")
	}
	
	@Test
	void whenFilesAtRoot_thenExtractionOK() {
		this.fs.addFile("test1.txt")
		this.fs.addFile("test2.txt")
		this.fs.addFile("test.other")
		
		List<String> paths = new ArrayList()
		this.fs.paths("*.txt") { fsFile ->
			paths.add(fsFile.path)
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
		this.fs.paths("**/*.txt") { fsFile ->
			paths.add(fsFile.path)
		}
		assert paths.size() == 2
		assert paths.contains("folder/test1.txt")
		assert paths.contains("folder/test2.txt")
	}

	@Test
	void whenExtractingAllFiles_thenExtractionOK() {
		this.fs.addFile("test1.txt")
		this.fs.addFile("folder/test2.txt")
		this.fs.addFile("folder/test.other")
		List<String> paths = new ArrayList()
		this.fs.paths("**/*") { fsFile ->
			paths.add(fsFile.path)
		}
		assert paths.size() == 3
		assert paths.contains("test1.txt")
		assert paths.contains("folder/test2.txt")
		assert paths.contains("folder/test.other")
	}
	
	@Test
	void whenUsingFilenameWildcards_thenExtractionOK() {
		this.fs.addFile("folder/test1.txt")
		this.fs.addFile("folder/test2.txt")
		this.fs.addFile("folder/other.txt")
		
		List<String> paths = new ArrayList()
		this.fs.paths("folder/test*.txt") { fsFile ->
			paths.add(fsFile.path)
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
		this.fs.paths("**/test*.txt") { fsFile ->
			paths.add(fsFile.path)
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
		this.fs.paths("**" + "/test*.txt") { fsFile ->
			paths.add(fsFile.path)
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
		
		this.fs.addFile(path, (byte[]) [48, 49, 50, 51])
		
		InMemoryJFileSystem fs2 = new InMemoryJFileSystem()
		this.fs.getContentAsStream(path) { inStream ->
			fs2.setContentFromStream(path, inStream)
		}
		fs2.getContentAsReader(path, "UTF-8") { reader ->
			assert reader.text == "0123"
		}
	}
	
	@Test
	void whenGetContent_thenContentSent() {
		this.fs.addFile("test.txt", (byte[]) [48, 49, 50])
		this.fs.getContentAsReader("test.txt", "UTF-8") { reader ->
			assert reader.readLine() == "012"
		}
	}
	
	@Test
	void whenCreatingFrom_thenFileSystemShared() {
		this.fs.addFile("rep/test.txt", (byte[]) [48, 49, 50])
		InMemoryJFileSystem subFs = this.fs.createFrom("rep")
		assert subFs.exists("test.txt")
		
		subFs.addFile("test2.txt", (byte[]) [32, 33, 34])
		assert this.fs.exists("rep/test.txt")
	}
	
	
	@Test
	void whenCreatingNewFile_thenGetLastUpdateDate() {
		long curr = System.currentTimeMillis()
		this.fs.addFile("test.txt", "Hello World !")
		long updated
		this.fs.paths("*.txt") { fsFile ->
			updated = fsFile.updated
		}
		assert updated >= curr
		assert updated <= System.currentTimeMillis()
	}
	
	@Test
	void whenUpdatingFile_thenLastUpdateDateUpdated() {
		long curr = System.currentTimeMillis()
		this.fs.addFile("test.txt", "Hello World !")
		long updated
		this.fs.paths("*.txt") { fsFile ->
			updated = fsFile.updated
		}
		assert updated > curr
		
		this.fs.setContentFromText("test.txt", "Content changed", "UTF-8")
		long updated2
		this.fs.paths("*.txt") { fsFile ->
			updated2 = fsFile.updated
		}
		assert updated2 > updated
	}
}
