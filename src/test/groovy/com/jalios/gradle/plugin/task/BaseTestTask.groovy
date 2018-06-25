package com.jalios.gradle.plugin.task

import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor
import com.jalios.gradle.plugin.jplatform.source.impl.PluginXmlExtractor
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

abstract class BaseTestTask {

	protected InMemoryJFileSystem currModuleFs
	protected InMemoryJFileSystem platformModuleFs
	protected JModule currModule
	protected JModule platformModule
	
	protected void addPluginXml(JModule module) {
		module.rootFs.addFile(
			"WEB-INF/plugins/${module.name}/plugin.xml", 
			ByteUtils.extractBytes("""
				<plugin name="${module.name}">
				</plugin>
			""")
		)
	}
	
	protected void addPluginProp(JModule module) {
		module.rootFs.addFile(
			"WEB-INF/plugins/${module.name}/properties/plugin.prop", 
			ByteUtils.extractBytes("""
			
			""")
		)
	}
	
	@Before
	void internalSetUp() {
		this.currModuleFs = new InMemoryJFileSystem()
		this.currModule = new JModule("TestPlugin", this.currModuleFs)
		
		this.platformModuleFs = new InMemoryJFileSystem()
		this.platformModule = new JModule("TestPlugin", this.platformModuleFs)
		
		this.setUp()
	}
	
	abstract void setUp()
}
