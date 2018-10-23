package com.jalios.gradle.plugin.task

import static org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestPushTypesTask extends BaseTestTask {

	PushTypesTask task
	
	@Override
	void setUp() {
		this.task = new PushTypesTask()
	}

	@Test(expected = JTaskException)
	void whenNoCurrModule_thenFetchFails() {
		// Create fake platform module
		this.addPluginXml(this.platformModule)
		this.platformModule.init(null, null)
		
		// Create empty current module
		this.currModule.init(null, null)
		
		this.task.run(this.platformModule, this.currModule)
	}
	
	void whenTypesDoesNotExist_thenTypeIgnored() {
		// Declare types
		this.currModuleFs.addFile(
			"WEB-INF/plugins/${this.currModule.name}/plugin.xml",
			ByteUtils.extractBytes("""
					<plugin name="${this.currModule.name}">
						<types>
							<type name="MyType"/>
							<type name="MyType2"/>
						</types>
					</plugin>
				""")
		)
		
		// Try to launch tasks
		this.currModule.init(null, null)
		this.platformModule.init(null, null)
		this.task.run(this.platformModule, this.currModule)
	}
	
	@Test
	void whenTypesNotDeclaredInCurrModule_thenTypesRemoved() {
		// Add two types to the platform
		this.platformModuleDataFs.addFile("types/MyType/MyType.xml")
		this.platformModuleDataFs.addFile("types/MySecondType/MySecondType.xml")
		this.addPluginXml(
				this.platformModule, 
				"""
					<types>
						<type name="MyType"/>
						<type name="MySecondType"/>
					</types>
				"""
		)
		this.platformModule.init(null, null)
		
		// Initialize modules with only one type
		this.currModuleDataFs.addFile("types/MyType/MyType.xml")
		this.currModuleDataFs.addFile("types/MyThirdType/MyThirdType.xml")
		this.addPluginXml(
			this.currModule,
			"""
				<types>
					<type name="MyType"/>
					<type name="MyThirdType"/>
				</types>
			"""
		)
		this.currModule.init(null, null)
		
		// Push types
		this.task.run(this.platformModule, this.currModule)
		
		// Check that non existing has been deleted
		assert !this.platformModuleDataFs.exists("types/MySecondType/MySecondType.xml")
		
		// Check that the other one has been pushed
		assert this.platformModuleDataFs.exists("types/MyThirdType/MyThirdType.xml")
		assert this.platformModuleDataFs.exists("types/MyType/MyType.xml")
	}
	
	@Test
	void whenPluginNotInPlatform_thenTypesPushed() {
		// Empty module (not deployed in platform)
		this.platformModule.init(null, null)
		
		// Initialize modules with only one type
		this.currModuleDataFs.addFile("types/MyType/MyType.xml")
		this.addPluginXml(
			this.currModule,
			"""
				<types>
					<type name="MyType"/>
				</types>
			"""
		)
		this.currModule.init(null, null)
		
		// Push types
		this.task.run(this.platformModule, this.currModule)
		
		// Check that the other one has been pushed
		assert this.platformModuleDataFs.exists("types/MyType/MyType.xml")
	}
}
