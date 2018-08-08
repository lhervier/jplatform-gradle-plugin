package com.jalios.gradle.plugin.jplatform.gen.impl

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.fs.FSType
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
			this.fs,
			new InMemoryJFileSystem()
		)
		this.extractor = new SignatureXmlExtractor()
	}
	
	@Test
	void whenExtracting_thenExtracted() {
		this.module.init([this.extractor], null)
		
		assert module.generatedPaths.size() == 1
		assert module.generatedPaths[0].path.path == "signature.xml"
		assert module.generatedPaths[0].path.type == FSType.PRIVATE
		assert module.generatedPaths[0].source.path == "plugin.xml"
		assert module.generatedPaths[0].source.type == FSType.PRIVATE
	}
}
