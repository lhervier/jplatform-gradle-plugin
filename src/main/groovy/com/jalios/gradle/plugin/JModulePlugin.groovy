package com.jalios.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

import com.jalios.gradle.plugin.ext.JModuleExtension
import com.jalios.gradle.plugin.ext.JPlatformExtension
import com.jalios.gradle.plugin.task.gradle.FetchPluginTaskImpl
import com.jalios.gradle.plugin.task.gradle.FetchTypesTaskImpl
import com.jalios.gradle.plugin.task.gradle.InstallPluginTaskImpl

class JModulePlugin implements Plugin<Project> {
	void apply(Project project) {
		// jModule projects are java projects
		project.apply plugin: "java-library"
		
		// Declare extension object
		project.extensions.create("jPlatform", JPlatformExtension)
		project.extensions.create("jModule", JModuleExtension)
		
		// Add custom tasks
		project.task("fetchPlugin", type: FetchPluginTaskImpl, group: "jPlatform", description: "Fetch a newly created plugin into current module")
		project.task("installPlugin", type: InstallPluginTaskImpl, group: "jPlatform", description: "Install the current module in JPlatform")
		project.task("fetchTypes", type: FetchTypesTaskImpl, group: "jPlatform", description: "Fetch types from platform into current module")
		
		// Add custom dependency configuration
//		project.configurations {
//			jPlatform
//		}
		
		// Executed at the end of the configuration phase
		project.afterEvaluate {
			// Get jPlatform path from extension configuration
			def path = project.jPlatform.path
			
			// Add a custom default dependency to a simple module
			// project.dependencies.add("implementation", "org.apache.commons:commons-lang3:3.7")
			
			// Add a custom default dependency to a local jar
			// project.dependencies.add("implementation", project.files("src/main/libs/gson-2.8.5.jar"))
			
			// Add dependency to javax.servlet as compileOnly
			project.dependencies.add("compileOnly", "javax.servlet:javax.servlet-api:3.1.0")
			
			// Add dependencies to all jar files from jPlatform
			if( path?.trim()) {
				project.dependencies.add("implementation", project.fileTree(
					dir : "${path}/WEB-INF/lib/",
					include: ['*.jar']
				))
				project.dependencies.add("implementation", project.files("${path}/WEB-INF/classes"))
			}
		}
	}
}
