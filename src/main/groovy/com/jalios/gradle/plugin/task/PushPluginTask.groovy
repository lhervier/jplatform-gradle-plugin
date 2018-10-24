package com.jalios.gradle.plugin.task

import org.gradle.api.Project
import org.w3c.dom.NodeList

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.fs.FSFile
import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.JPath
import com.jalios.gradle.plugin.jplatform.PluginXml
import com.jalios.gradle.plugin.test.util.ByteUtils

/**
 * This task will install the module into the jPlatform
 * @author Lionel HERVIER
 */
class PushPluginTask implements JPlatformTask {

	static String BUILD = 'build/module'
	
	static String SRC = 'src/main/module'
	
	/**
	 * Module preparation
	 */
	@Override
	public JFileSystem createModuleFs(
			String moduleName, 
			String version,
			JFileSystem fs, 
			List<File> dependencies, 
			File mainJar) {
		JFileSystem srcFs = fs.createFrom(SRC)
		JFileSystem buildFs = fs.createFrom(BUILD)
		
		println "- Checking that WEB-INF/lib is empty"
		srcFs.paths("WEB-INF/lib/*.jar") {
			throw new JTaskException("'WEB-INF/lib' folder must be empty. Use gradle dependencies to add jars to your JPlatform module")
		}
		
		println "- Checking that plugin.xml exists"
		String pluginXmlPath = "WEB-INF/plugins/${moduleName}/plugin.xml"
		if( !srcFs.exists(pluginXmlPath) ) {
			throw new JTaskException("plugin.xml file not found. Unable to push plugin")
		}
		
		println "- Checking that plugin.xml does not contains reference to jar files"
		PluginXml srcPluginXml
		srcFs.getContentAsStream(pluginXmlPath) { inStream -> 
			srcPluginXml = new PluginXml(inStream)
		}
		if( srcPluginXml.jars.size() != 0 ) {
			throw new JTaskException("You must not declare jars inside your plugin.xml. Use gradle dependencies instead.")
		}
		
		println "- Removing files from build folder"
		buildFs.paths("**/*") {  fsFile ->
			if( !srcFs.exists(fsFile.path) ) {
				println "  - ${fsFile.path}"
				buildFs.delete(fsFile.path)
			}
		}
		
		println "- Copying files from src to build folder"
		srcFs.paths("**/*") { fsFile ->
			if( buildFs.exists(fsFile.path) ) {
				FSFile dest = buildFs.path(fsFile.path)
				if( dest.updated >= fsFile.updated ) {
					return
				}
			}
			println "  - ${fsFile.path}"
			srcFs.getContentAsStream(fsFile.path) { inStream ->
				buildFs.setContentFromStream(fsFile.path, inStream)
			}
		}
		
		println "- Loading copied plugin.xml"
		PluginXml pluginXml
		buildFs.getContentAsStream(pluginXmlPath) { inStream ->
			pluginXml = new PluginXml(inStream)
		}
		
		println "- Updating module name and version in plugin.xml"
		pluginXml.name = moduleName
		pluginXml.version = version
		
		println "- Copying dependencies to WEB-INF/lib"
		dependencies.each { dep ->
			InputStream is = dep.newInputStream()
			buildFs.setContentFromStream("WEB-INF/lib/${dep.name}", is)
			is.close()
		}
		
		if( mainJar != null ) {
			println "- Copying main jar to WEB-INF/lib"
			InputStream is = mainJar.newInputStream()
			buildFs.setContentFromStream("WEB-INF/lib/${mainJar.name}", is)
			is.close()
		}
		
		println "- Add references to jar files into plugin.xml"
		buildFs.createFrom("WEB-INF/lib").paths("*.jar") { fsJar ->
			pluginXml.addJar(fsJar.path)
		}
		
		println "- Saving new version of plugin.xml"
		ByteArrayOutputStream out = new ByteArrayOutputStream()
		pluginXml.save(out)
		buildFs.setContentFromStream(pluginXmlPath, new ByteArrayInputStream(out.toByteArray()))
		
		return buildFs
	}
	
	/**
	 * Task execution
	 */
	@Override
	void run(JModule platformModule, JModule currModule) {
		println "Relevant files :"
		println "================"
		
		println "Current module files :"
		currModule.paths.each { jpath ->
			println "- ${jpath.type} - ${jpath.path}"
		}
		
		println "Platform module files :"
		platformModule.paths.each { jpath ->
			println "- ${jpath.type} - ${jpath.path}"
		}
		
		println "Platform module generated files :"
		platformModule.generatedPaths.each { genFile ->
			println "- ${genFile.path.path} generated from ${genFile.source.path} (${genFile.source.type})"
		}
		
		println "Installing module :"
		println "==================="
		
		println "Removing platform module files that are no longer present in the current module"
		platformModule.paths.each { jpath ->
			if( !currModule.paths.contains(jpath) ) {
				platformModule.getFs(jpath.type).delete(jpath.path)
			}
		}
		
		println "Removing platform module generated files whose corresponding source no longer exist in the current module"
		platformModule.generatedPaths.each { genFile ->
			if( !currModule.paths.contains(genFile.source) ) {
				platformModule.getFs(genFile.source.type).delete(genFile.path.path)
			}
		}
		
		println "Overwriting platform module files"
		currModule.paths.each { jpath ->
			currModule.getFs(jpath.type).getContentAsStream(jpath.path) { inStream ->
				JFileSystem destFs = platformModule.getFs(jpath.type)
				if( destFs.exists(jpath.path) ) {
					JFileSystem srcFs = currModule.getFs(jpath.type)
					FSFile src = srcFs.path(jpath.path)
					FSFile dest = destFs.path(jpath.path)
					if( src.updated <= dest.updated ) {
						println "'${jpath.path}': Content not changed"
						return
					}
					println "'${jpath.path}': Updating content"
				} else {
					println "'${jpath.path}': Creating new file"
				}
				destFs.setContentFromStream(jpath.path, inStream)
			}
		}
	}

}
