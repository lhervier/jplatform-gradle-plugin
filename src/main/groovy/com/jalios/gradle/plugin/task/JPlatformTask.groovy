package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.jplatform.JModule

interface JPlatformTask {

	/**
	 * Prepare the module folder
	 * @return path to the module folder (relative to gradle project)
	 * @param fs file system on the current project
	 * @param dependencies jars dependencies (as File)
	 * @param mainJarPath path (relative to project) to the compiled jar
	 * @return the path to the module source (relative to project)
	 */
	String prepareModule(String moduleName, JFileSystem fs, List<File> dependencies, String mainJarPath)
	
	/**
	 * Run the task
	 * @param platformModule the platform module
	 * @param currModule the current module (the source version)
	 * @throws JTaskException
	 */
	void run(JModule platformModule, JModule currModule) throws JTaskException
	
}
