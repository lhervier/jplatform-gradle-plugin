package com.jalios.gradle.plugin.fs.impl

import org.gradle.api.Project

import com.jalios.gradle.plugin.fs.FileSystem
import com.jalios.gradle.plugin.fs.FileSystemFactory

class FileSystemFactoryImpl implements FileSystemFactory {

	Project project
	
	public IFileSystemFactoryImpl(Project project) {
		this.project = project
	}
	
	@Override
	public FileSystem newFs(String path) {
		return new FileSystemImpl(this.project.files(path));
	}

}
