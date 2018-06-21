package com.jalios.gradle.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import com.jalios.gradle.plugin.fs.FileSystem
import com.jalios.gradle.plugin.fs.FileSystemFactory
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.JPlatform

abstract class BaseJPlatformTask {
	
	protected JModule currModule
	protected JModule platformModule
	
	private FileSystemFactory fsFactory
	private String moduleName
	private String jPlatformPath
	private String modulePath
	
	public BaseJPlatformTask(FileSystemFactory fsFactory, String moduleName, String jPlatformPath, String modulePath) {
		this.fsFactory = fsFactory
		this.moduleName = moduleName
		this.jPlatformPath = jPlatformPath
		this.modulePath = modulePath
	}
	
	def taskAction() {
		FileSystem moduleFs = this.fsFactory.newFs("src/main/module")
		FileSystem platformFs = this.fsFactory.newFs(this.jPlatformPath)
		
		this.currModule = new JModule(
			this.moduleName,
			moduleFs
		)
		this.platformModule = new JModule(
			this.moduleName,
			platformFs
		)
		
		this.run()
	}
	
	abstract void run()
}
