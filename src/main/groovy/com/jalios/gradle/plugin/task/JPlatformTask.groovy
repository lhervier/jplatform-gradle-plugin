package com.jalios.gradle.plugin.task

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.jplatform.JModule

interface JPlatformTask {

	/**
	 * Create the root file system for the project
	 * @param moduleName name of the module
	 * @param version version of the module
	 * @param fs file system of the current project
	 * @param dependencies jars dependencies (as File)
	 * @param mainJar the compiled jar (as File)
	 * @return the path to the module source (relative to project)
	 */
	JFileSystem createModuleFs(
			String moduleName, 
			String version,
			JFileSystem fs, 
			List<File> dependencies, 
			File mainJar)
	
	/**
	 * Run the task
	 * @param platformModule the platform module
	 * @param currModule the current module (the source version)
	 * @throws JTaskException
	 */
	void run(JModule platformModule, JModule currModule) throws JTaskException
	
}
