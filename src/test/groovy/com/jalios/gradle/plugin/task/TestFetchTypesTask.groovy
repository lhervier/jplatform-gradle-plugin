package com.jalios.gradle.plugin.task

import static org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestFetchTypesTask extends BaseTestTask {

	FetchTypesTask task
	
	@Override
	void setUp() {
		this.task = new FetchTypesTask()
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
	
	@Test(expected = JTaskException)
	void whenTypesDoesNotExist_thenFail() {
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
	void whenTypesNotDeclaredInCurrModule_thenTypesNotFetched() {
		// Add two types to the platform
		this.platformModuleFs.addFile("WEB-INF/data/types/MyType/MyType.xml")
		this.platformModuleFs.addFile("WEB-INF/data/types/MyType/MyType-templates.xml")
		this.platformModuleFs.addFile("WEB-INF/data/types/MySecondType/MySecondType.xml")
		this.platformModuleFs.addFile("WEB-INF/data/types/MySecondType/MySecondType-templates.xml")
		
		// Add generated jsps files
		this.platformModuleFs.addFile("types/MyType/doEditMyType.jsp")
		this.platformModuleFs.addFile("types/MyType/doEditMyTypeModal.jsp")
		this.platformModuleFs.addFile("types/MyType/doMyTypeDiff.jsp")
		this.platformModuleFs.addFile("types/MyType/doMyTypeFullDisplay.jsp")
		this.platformModuleFs.addFile("types/MyType/doMyTypeResultDisplay.jsp")
		this.platformModuleFs.addFile("types/MyType/editMyType.jsp")
		this.platformModuleFs.addFile("types/MyType/editMyTypeModal.jsp")
		this.platformModuleFs.addFile("types/MyType2/doEditMyType2.jsp")
		this.platformModuleFs.addFile("types/MyType2/doEditMyType2Modal.jsp")
		this.platformModuleFs.addFile("types/MyType2/doMyType2Diff.jsp")
		this.platformModuleFs.addFile("types/MyType2/doMyType2FullDisplay.jsp")
		this.platformModuleFs.addFile("types/MyType2/doMyType2ResultDisplay.jsp")
		this.platformModuleFs.addFile("types/MyType2/editMyType2.jsp")
		this.platformModuleFs.addFile("types/MyType2/editMyType2Modal.jsp")
		
		// Initialize modules, and launch task
		this.addPluginXml(this.currModule)
		this.currModule.init(null, null)
		this.platformModule.init(null, null)
		this.task.run(this.platformModule, this.currModule)
		
		// Check files have NOT been fetched
		assert !this.currModuleFs.exists("types/MyType/doEditMyType.jsp")
		assert !this.currModuleFs.exists("types/MyType2/doEditMyType2Modal.jsp")
	}
	
	@Test
	void whenTypesDeclaredInCurrModule_thenTypesFetched() {
		// Add two types to the platform
		this.platformModuleFs.addFile("WEB-INF/data/types/MyType/MyType.xml")
		this.platformModuleFs.addFile("WEB-INF/data/types/MyType/MyType-templates.xml")
		this.platformModuleFs.addFile("WEB-INF/data/types/MyType2/MyType2.xml")
		
		// Add generated jsps files
		this.platformModuleFs.addFile("types/MyType/doEditMyType.jsp")
		this.platformModuleFs.addFile("types/MyType/doEditMyTypeModal.jsp")
		this.platformModuleFs.addFile("types/MyType/doMyTypeDiff.jsp")
		this.platformModuleFs.addFile("types/MyType/doMyTypeFullDisplay.jsp")
		this.platformModuleFs.addFile("types/MyType/doMyTypeResultDisplay.jsp")
		this.platformModuleFs.addFile("types/MyType/editMyType.jsp")
		this.platformModuleFs.addFile("types/MyType/editMyTypeModal.jsp")
		this.platformModuleFs.addFile("types/MyType2/doEditMyType2.jsp")
		this.platformModuleFs.addFile("types/MyType2/doEditMyType2Modal.jsp")
		this.platformModuleFs.addFile("types/MyType2/doMyType2Diff.jsp")
		this.platformModuleFs.addFile("types/MyType2/doMyType2FullDisplay.jsp")
		this.platformModuleFs.addFile("types/MyType2/doMyType2ResultDisplay.jsp")
		this.platformModuleFs.addFile("types/MyType2/editMyType2.jsp")
		this.platformModuleFs.addFile("types/MyType2/editMyType2Modal.jsp")
		
		// Create plugin.xml
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
		
		// Init modules and run task
		this.currModule.init(null, null)
		this.platformModule.init(null, null)
		this.task.run(this.platformModule, this.currModule)
		
		// Check that files have been fetched
		assert this.currModuleFs.exists("WEB-INF/data/types/MyType/MyType.xml")
		assert this.currModuleFs.exists("WEB-INF/data/types/MyType/MyType-templates.xml")
		assert this.currModuleFs.exists("types/MyType/doEditMyTypeModal.jsp")
		
		assert this.currModuleFs.exists("WEB-INF/data/types/MyType2/MyType2.xml")
		assert !this.currModuleFs.exists("WEB-INF/data/types/MyType2/MyType2-templates.xml")
		
		assert this.currModuleFs.exists("types/MyType2/editMyType2Modal.jsp")
	}
	
	@Test
	void whenCustomJspDeployed_thenCustomJspNotFetched() {
		// Add two types to the platform
		this.platformModuleFs.addFile("WEB-INF/data/types/MyType/MyType.xml")
		this.platformModuleFs.addFile("WEB-INF/data/types/MyType2/MyType2.xml")
		
		// Add a set of generated jsps files
		this.platformModuleFs.addFile("types/MyType/doEditMyType.jsp")
		this.platformModuleFs.addFile("types/MyType2/doMyType2FullDisplay.jsp")
		
		// Add custom jsp
		this.platformModuleFs.addFile("types/MyType/custom.jsp")
		
		// Create plugin.xml
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
		
		// Init modules and run task
		this.currModule.init(null, null)
		this.platformModule.init(null, null)
		this.task.run(this.platformModule, this.currModule)
		
		// Check that files have not been fetched
		assert this.currModuleFs.exists("types/MyType/doEditMyType.jsp")
		assert !this.currModuleFs.exists("types/MyType/custom.jsp")
	}

}
