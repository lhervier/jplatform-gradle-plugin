package com.jalios.gradle.plugin.jplatform.source.impl

import static org.hamcrest.core.IsCollectionContaining.hasItems
import static org.junit.Assert.assertThat

import org.gradle.internal.impldep.bsh.This
import org.hamcrest.core.IsCollectionContaining
import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.ex.JTaskException
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestTypesTemplatesExtractor {

	InMemoryJFileSystem fs
	JModule module
	TypesTemplatesExtractor extractor
	
	@Before
	void setUp() {
		this.fs = new InMemoryJFileSystem()
		this.module = new JModule("TestPlugin", this.fs)
		this.extractor = new TypesTemplatesExtractor()
	}
	
	@Test
	void whenNoTemplates_thenNothingExtracted() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
				<plugin name="TestASIPlugin">
				</plugin>
			""")
		)
		this.module.init(null, null)
		
		List<String> paths = new ArrayList()
		this.extractor.extract(this.module) { path ->
			paths.add(path)
		}
		
		assert paths.size() == 0
	}
	
	@Test
	void whenTemplatesInPluginXmlFile_thenExtract() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
				<plugin name="TestASIPlugin">
					<types>
						<templates type="MyType">
							<template file="doSomething.jsp"/>
						</templates>
					</types>
				</plugin>
			""")
		)
		this.module.init(null, null)
		
		List<String> paths = new ArrayList()
		this.extractor.extract(this.module) { path ->
			paths.add(path)
		}
		assert paths.size() == 1
		assert paths[0] == "types/MyType/doSomething.jsp"
	}
	
	@Test
	void whenTemplatesInXmlFile_thenExtract() {
		this.fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
				<plugin name="TestASIPlugin">
					<types>
						<type name="MyType"/>
					</types>
				</plugin>
			""")
		)
		this.fs.addFile("WEB-INF/data/types/MyType/MyType.xml")
		this.fs.addFile(
			"WEB-INF/data/types/MyType/MyType-templates.xml",
			ByteUtils.extractBytes(
				"""
				<templates type="MyType">
					<template file="doSomething.jsp"/>
				</templates>
				"""
			)
		)
		this.module.init(null, null)
		
		List<String> paths = new ArrayList()
		this.extractor.extract(this.module) { path ->
			paths.add(path)
		}
		assertThat(paths, hasItems(
				"types/MyType/doSomething.jsp",
				"WEB-INF/data/types/MyType/MyType-templates.xml"
		))
	}
}
