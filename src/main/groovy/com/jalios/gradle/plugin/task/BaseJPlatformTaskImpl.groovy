package com.jalios.gradle.plugin.task

import org.gradle.api.Project

import com.jalios.gradle.plugin.fs.JFileSystem

abstract class BaseJPlatformTaskImpl implements JPlatformTask {

	@Override
	public String prepareModule(String moduleName, JFileSystem fs, List<File> dependencies, String mainJarPath) {
		return "src/main/module";
	}

}
