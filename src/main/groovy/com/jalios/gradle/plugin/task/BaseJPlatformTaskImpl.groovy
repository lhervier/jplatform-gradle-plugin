package com.jalios.gradle.plugin.task

import org.gradle.api.Project

import com.jalios.gradle.plugin.fs.JFileSystem

abstract class BaseJPlatformTaskImpl implements JPlatformTask {

	@Override
	public JFileSystem createModuleFs(String moduleName, JFileSystem fs, List<File> dependencies, File mainJar) {
		return fs.createFrom("src/main/module");
	}

}
