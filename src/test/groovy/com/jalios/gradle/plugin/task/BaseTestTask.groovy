package com.jalios.gradle.plugin.task

import org.junit.Before

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

abstract class BaseTestTask {

	protected InMemoryJFileSystem currModuleFs
	protected InMemoryJFileSystem currModuleDataFs
	protected InMemoryJFileSystem platformModuleFs
	protected InMemoryJFileSystem platformModuleDataFs
	protected JModule currModule
	protected JModule platformModule
	
	protected void addPluginXml(JModule module) {
		this.addPluginXml(module, "")
	}
	protected void addPluginXml(JModule module, String content) {
		module.rootFs.addFile(
			"WEB-INF/plugins/${module.name}/plugin.xml", 
			ByteUtils.extractBytes("""
				<plugin name="${module.name}">
					${content}
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
		this.currModuleDataFs = new InMemoryJFileSystem()
		this.currModule = new JModule("TestPlugin", this.currModuleFs, this.currModuleDataFs)
		
		this.platformModuleFs = new InMemoryJFileSystem()
		this.platformModuleDataFs = new InMemoryJFileSystem()
		this.platformModule = new JModule("TestPlugin", this.platformModuleFs, this.platformModuleDataFs)
		
		this.setUp()
	}
	
	abstract void setUp()
}
