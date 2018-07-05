package com.jalios.gradle.plugin.jplatform.gen.impl

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestSignatureXmlExtractor {

	InMemoryJFileSystem fs
	JModule module
	SignatureXmlExtractor extractor
	
	@Before
	void before() {
		this.fs = new InMemoryJFileSystem()
		this.fs.addFile(
				"WEB-INF/plugins/TestPlugin/plugin.xml",
				ByteUtils.extractBytes("""
					<plugin name="TestASIPlugin">
					</plugin>
				""")
		)
		this.module = new JModule(
			"TestPlugin",
			this.fs
		)
		this.extractor = new SignatureXmlExtractor()
	}
	
	@Test
	void whenExtracting_thenExtracted() {
		this.module.init([this.extractor], null)
		
		assert module.generatedPaths.size() == 1
		assert module.generatedPaths[0].path == "WEB-INF/plugins/TestPlugin/signature.xml"
		assert module.generatedPaths[0].source == "WEB-INF/plugins/TestPlugin/plugin.xml"
	}
}
