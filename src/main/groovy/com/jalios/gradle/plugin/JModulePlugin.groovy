package com.jalios.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

import com.jalios.gradle.plugin.ext.JModuleExtension
import com.jalios.gradle.plugin.ext.JPlatformExtension
import com.jalios.gradle.plugin.task.gradle.FetchPluginTaskImpl
import com.jalios.gradle.plugin.task.gradle.FetchTypesTaskImpl
import com.jalios.gradle.plugin.task.gradle.PushPluginTaskImpl
import com.jalios.gradle.plugin.task.gradle.PushTypesTaskImpl

class JModulePlugin implements Plugin<Project> {
	void apply(Project project) {
		project.apply plugin: "java-library"
		
		// Declare extension object
		project.extensions.create("jPlatform", JPlatformExtension)
		project.extensions.create("jModule", JModuleExtension)
		
		// Add custom tasks
		project.task(
			"fetchTypes", 
			type: FetchTypesTaskImpl, 
			group: "jPlatform", 
			description: "Fetch types from jPlatform into current module"
		)
		project.task(
			"pushTypes", 
			type: PushTypesTaskImpl, 
			group: "jPlatform", 
			description: "Push types from current module into jPlatform"
		)
		project.task(
			"fetchPlugin", 
			type: FetchPluginTaskImpl, 
			group: "jPlatform", 
			description: "Fetch a newly created plugin into current module"
		)
		project.task(
			"pushPlugin", 
			type: PushPluginTaskImpl, 
			group: "jPlatform", 
			description: "Push the current module in jPlatform"
		).dependsOn(project.tasks['build'])
		
		// Executed at the end of the configuration phase
		project.afterEvaluate {
			// Get jPlatform path from extension configuration
			String path = project.jPlatform.path
			
			// Add dependency to javax.servlet as compileOnly
			project.dependencies.add("compileOnly", "javax.servlet:javax.servlet-api:3.1.0")
			
			// Add dependencies to all jar files from jPlatform
			if( path?.trim()) {
				project.dependencies.add("compileOnly", project.fileTree(
					dir : "${path}/WEB-INF/lib/",
					include: ['*.jar']
				))
				project.dependencies.add("compileOnly", project.files("${path}/WEB-INF/classes"))
			}
		}
	}
}
