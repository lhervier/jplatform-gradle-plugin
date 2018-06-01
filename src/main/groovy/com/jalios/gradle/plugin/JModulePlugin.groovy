package com.jalios.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class JModulePlugin implements Plugin<Project> {
	void apply(Project project) {
		// jModule projects are java projects
		project.apply plugin: "java-library"
		
		// Declare extension object
		project.extensions.create("jPlatform", JPlatformExtension)
		project.extensions.create("jModule", JModuleExtension)
		
		// Add custom tasks
		project.task("fetchPlugin", type: FetchPluginTask, group: "jPlatform")
		
		// Add custom dependency configuration
		project.configurations {
			jPlatform
		}
		
		// Executed at the end of the configuration phase
		project.afterEvaluate {
			// Get jPlatform path from extension configuration
			def path = project.jPlatform.path
			
			// Add a custom default dependency to a simple module
			// project.dependencies.add("implementation", "org.apache.commons:commons-lang3:3.7")
			
			// Add a custom default dependency to a local jar
			// project.dependencies.add("implementation", project.files("src/main/libs/gson-2.8.5.jar"))
			
			// Add dependencies to all jar files from jPlatform
			if( path?.trim()) {
				project.dependencies.add("implementation", project.fileTree(
					dir : "${path}/WEB-INF/lib/",
					include: ['*.jar']
				))
			}
			
			// List all implementation dependencies (for debug)
			println "Dependencies :"
			project.configurations.implementation.dependencies.all {
				println "-> ${it.name} / ${it.version}"
			}
		}
	}
}
