package com.jalios.gradle.plugin.task

import org.gradle.api.Project
import org.w3c.dom.NodeList

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.fs.JPath
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.test.util.ByteUtils

/**
 * This task will install the module into the jPlatform
 * @author Lionel HERVIER
 */
class PushPluginTask implements JPlatformTask {

	static String MODULE = 'build/module'
	
	static String SRC = 'src/main/module'
	
	/**
	 * Module preparation
	 */
	public String prepareModule(String moduleName, JFileSystem fs, List<File> dependencies, String mainJarPath) {
		JFileSystem srcFs = fs.createFrom(SRC)
		JFileSystem buildFs = fs.createFrom(MODULE)
		
		println "- Checking that WEB-INF/lib is empty"
		srcFs.paths("WEB-INF/lib/*.jar") {
			throw new JTaskException("'WEB-INF/lib' folder must be empty. Use gradle dependencies to add jars to your JPlatform module")
		}
		
		println "- Checking that plugin.xml exists"
		String pluginXmlPath = "WEB-INF/plugins/${moduleName}/plugin.xml"
		if( !srcFs.exists(pluginXmlPath) ) {
			throw new JTaskException("plugin.xml file not found. Unable to push plugin")
		}
		
		println "- Parsing plugin.xml"
		XmlParser parser = new XmlParser(false, false)
		parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
		parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
		def xml
		srcFs.getContentAsStream(pluginXmlPath) { inStream -> 
			xml = parser.parse(inStream)
		}
		
		println "- Checking that plugin.xml does not contains reference to jar files"
		if( !xml.jars ) {
			xml.children().add(0, parser.parseText("<jars/>"))
		}
		if( xml.jars.jar.size() != 0 ) {
			throw new JTaskException("You must not declare jars inside your plugin.xml. Use gradle dependencies instead.")
		}
		
		println "- Emptying build folder"
		fs.delete(MODULE)
		
		println "- Copying module folder to build folder"
		srcFs.paths("**/*") { p ->
			srcFs.getContentAsStream(p) { inStream ->
				buildFs.setContentFromStream(p, inStream)
			}
		}
		
		println "- Copying dependencies to WEB-INF/lib"
		dependencies.each { dep ->
			InputStream is = dep.newInputStream()
			buildFs.setContentFromStream("WEB-INF/lib/${dep.name}", is)
			is.close()
		}
		
		if( mainJarPath != null ) {
			println "- Copying main jar to WEB-INF/lib"
			File main = new File(mainJarPath)
			InputStream is = main.newInputStream()
			buildFs.setContentFromStream("WEB-INF/lib/${main.name}", is)
			is.close()
		}
		
		println "- Add references to jar files into plugin.xml"
		JFileSystem libFs = buildFs.createFrom("WEB-INF/lib")
		def jars = xml.find { n ->
			n.name() == 'jars' 
		}.children()
		libFs.paths("*.jar") { jar ->
			def toAdd = parser.parseText("<jar path=\"${jar}\"/>")
			jars.add(toAdd)
		}
		
		println "- Saving new version of plugin.xml"
		String sXml = groovy.xml.XmlUtil.serialize(xml)
		buildFs.setContentFromStream(pluginXmlPath, new ByteArrayInputStream(ByteUtils.extractBytes(sXml)))
		
		return MODULE
	}
	
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
				platformModule.getFs(jpath.type).setContentFromStream(jpath.path, inStream)
			}
		}
	}

}
