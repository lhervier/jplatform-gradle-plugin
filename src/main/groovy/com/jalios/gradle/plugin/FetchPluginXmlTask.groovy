package com.jalios.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class FetchPluginXmlTask extends DefaultTask {

	@TaskAction
	def fetchPluginXml() {
		def dest = project.file("src/main/module/WEB-INF/plugins/${project.jModule.name}/plugin.xml")
		println "Checking of plugin.xml already exists : '${dest.absolutePath}'"
		if( dest.exists() ) {
			throw new Exception("Error : plugin.xml file already exists. Remove it before fecthing is you want to overwrite it with version from jPlatform.")	
		}
		
		def src = project.file("${project.jPlatform.path}/WEB-INF/plugins/${project.jModule.name}/plugin.xml")
		println "Fetching '${src.absolutePath}'"
		
		FileTreeBuilder treeBuilder = new FileTreeBuilder(project.file('src'))
		treeBuilder.dir('main') {
			dir('module') {
				dir('WEB-INF') {
					dir('plugins') {
						dir(project.jModule.name) {
							file('plugin.xml') {
								println "Copying plugin.xml to '${it.absolutePath}'"
								withOutputStream { out ->
									out.write src.bytes
								}
							}
						}
					}
				}
			}
		}
	}
}
