package com.jalios.gradle.plugin.fs

interface FileSystemFactory {

	FileSystem newFs(String path)
	
}
