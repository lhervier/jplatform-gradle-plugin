package com.jalios.gradle.plugin.fs.impl

import static org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestJFileSystemImpl {

	private File root
	
	private JFileSystemImpl fs
		
	@Before
	void init() {
		this.root = File.createTempDir()
		this.fs = new JFileSystemImpl(this.root)
	}
	
	@After
	void destroy() {
		if( !this.root.deleteDir() ) {
			throw new RuntimeException("Unable to remove '${this.root.absolutePath}")
		}
	}
	
	@Test
	void whenNewFsOnNonExistingFolder_thenFolderCreated() {
		assert !new File(this.root, "sub/folder").exists()
		JFileSystem fs = new JFileSystemImpl(new File(this.root, "sub/folder"))
		assert new File(this.root, "sub/folder").exists()
	}
	
	@Test
	void whenGetContentAsString_thenContentOK() {
		new File(this.root, "test.txt").text = "Content of the file"
		String content = this.fs.getContentAsString("test.txt", "UTF-8")
		assert content == "Content of the file"
	}
	
	@Test
	void whenFilesAtRoot_thenExtractionOK() {
		new File(this.root, "test1.txt").text = "Content of the first file"
		new File(this.root, "test2.txt").text = "Content of the second file"
		new File(this.root, "test.other").text = "Content of the second file"
		
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
		new File(this.root, "folder").mkdir()
		new File(this.root, "folder/test1.txt").text = "Content of the first file"
		new File(this.root, "folder/test2.txt").text = "Content of the second file"
		new File(this.root, "folder/test.other").text = "Content of the second file"
		List<String> paths = new ArrayList()
		this.fs.paths("**/*.txt") { fsFile ->
			paths.add(fsFile.path)
		}
		assert paths.size() == 2
		assert paths.contains("folder/test1.txt")
		assert paths.contains("folder/test2.txt")
	}

	@Test
	void whenFilesInAndOutFolder_thenExtractionOK() {
		new File(this.root, "testout.txt").text = "out file"
		new File(this.root, "folder").mkdir()
		new File(this.root, "folder/test1.txt").text = "Content of the first file"
		new File(this.root, "folder/test2.txt").text = "Content of the second file"
		new File(this.root, "folder/test.other").text = "Content of the second file"
		List<String> paths = new ArrayList()
		this.fs.paths("**/*.txt") { fsFile ->
			paths.add(fsFile.path)
		}
		assert paths.size() == 3
		assert paths.contains("testout.txt")
		assert paths.contains("folder/test1.txt")
		assert paths.contains("folder/test2.txt")
	}

	@Test
	void whenUsingFilenameWildcards_thenExtractionOK() {
		new File(this.root, "folder").mkdir()
		new File(this.root, "folder/test1.txt").text = "Content of the first file"
		new File(this.root, "folder/test2.txt").text = "Content of the second file"
		new File(this.root, "folder/other.txt").text = "Content of the other file"
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
		new File(this.root, "folder1").mkdir()
		new File(this.root, "folder2").mkdir()
		new File(this.root, "otherFolder").mkdir()
		
		new File(this.root, "folder1/test1.txt").text = "Content of the first file"
		new File(this.root, "folder1/other.txt").text = "Content of the other file"
		new File(this.root, "folder2/test2.txt").text = "Content of the second file"
		new File(this.root, "folder2/other.txt").text = "Content of the other file"
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
		new File(this.root, "folder1").mkdir()
		new File(this.root, "folder1/folder1.1").mkdir()
		new File(this.root, "folder2").mkdir()
		new File(this.root, "otherFolder").mkdir()
		
		new File(this.root, "folder1/folder1.1/test1.txt").text = "Content of the first file"
		new File(this.root, "folder1/folder1.1/other.txt").text = "Content of the other file"
		new File(this.root, "folder2/test2.txt").text = "Content of the second file"
		new File(this.root, "folder2/other.txt").text = "Content of the other file"
		List<String> paths = new ArrayList()
		this.fs.paths("**/test*.txt") { fsFile ->
			paths.add(fsFile.path)
		}
		assert paths.size() == 2
		assert paths.contains("folder1/folder1.1/test1.txt")
		assert paths.contains("folder2/test2.txt")
	}
	
	@Test
	void whenCopyingFile_thenFileCopied() {
		String path = "folder1/folder1.1/test1.txt"
		
		new File(this.root, "folder1").mkdir()
		new File(this.root, "folder1/folder1.1").mkdir()
		new File(this.root, path).text = "Content of the first file"
		
		File root2 = File.createTempDir()
		JFileSystemImpl fs2 = new JFileSystemImpl(root2)
		
		assert !new File(root2, path).exists()
		
		this.fs.getContentAsStream(path) { inStream ->
			fs2.setContentFromStream(path, inStream)
		}
		assert new File(root2, path).exists()
		assert new File(root2, path).text == "Content of the first file"
	}
	
	@Test
	void whenDeleting_thenFileDeleted() {
		new File(this.root, "test.txt").text = "Content of the first file"
		
		assert new File(this.root, "test.txt").exists()
		
		this.fs.delete("test.txt")
		
		assert !new File(this.root, "test.txt").exists()
	}
	
	@Test
	void whenDeletingNonExistingFile_thenDoNothing() {
		this.fs.delete("test.txt")
	}
	
	@Test
	void whenDeletingFolder_thenFolderDeleted() {
		new File(this.root, "folder").mkdir()
		new File(this.root, "folder/file1").setText("file 1")
		new File(this.root, "folder/file2").setText("file 1")
		
		this.fs.delete("folder")
		assert !new File(this.root, "folder").exists()
	}
	
	@Test
	void whenGetContent_thenContentSent() {
		new File(this.root, "test.txt").setText("Content of the first file", "UTF-8")
		this.fs.getContentAsReader("test.txt", "UTF-8") { reader ->
			assert reader.readLine() == "Content of the first file", "UTF-8"
		}
	}
	
	@Test
	void whenSetContentOnNonExistingFile_thenSetContent() {
		String name = "rep1/rep2/nonexisting.txt"
		File f = new File(this.root, name)
		assert !f.exists()
		
		this.fs.setContentFromStream(
			name, 
			new ByteArrayInputStream(
				ByteUtils.extractBytes("test")
			)
		)
		assert f.exists()
	}
	
	@Test
	void whenGetContentOnNonExisting_thenDoNothing() {
		this.fs.getContentAsStream("nonexisting") { instream ->
			assert false
		}
	}
	
	@Test
	void whenCreatingNewFile_thenGetLastUpdateDate() {
		long curr = System.currentTimeMillis()
		new File(this.root, "test.txt").text = "Hello World !"
		long updated
		this.fs.paths("*.txt") { fsFile ->
			updated = fsFile.updated
		}
		assert updated > curr
		assert updated < System.currentTimeMillis()
	}
	
	@Test
	void whenUpdatingFile_thenLastUpdateDateUpdated() {
		File f = new File(this.root, "test.txt")
		long curr = System.currentTimeMillis()
		f.text = "Hello World !"
		long updated
		this.fs.paths("*.txt") { fsFile ->
			updated = fsFile.updated
		}
		assert updated > curr
		
		f.text = "Content changed"
		long updated2
		this.fs.paths("*.txt") { fsFile ->
			updated2 = fsFile.updated
		}
		assert updated2 > updated
	}
}
