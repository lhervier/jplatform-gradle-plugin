package com.jalios.gradle.plugin.jplatform.source.impl

import static org.hamcrest.core.IsCollectionContaining.hasItems
import static org.junit.Assert.assertThat

import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.fs.JPath
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestTypesTemplatesExtractor {

	InMemoryJFileSystem fs
	InMemoryJFileSystem dataFs
	JModule module
	TypesTemplatesExtractor extractor
	
	@Before
	void setUp() {
		this.fs = new InMemoryJFileSystem()
		this.dataFs = new InMemoryJFileSystem()
		this.module = new JModule("TestPlugin", this.fs, this.dataFs)
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
		
		List<JPath> paths = new ArrayList()
		this.extractor.extract(this.module) { jpath ->
			paths.add(jpath)
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
		
		List<JPath> paths = new ArrayList()
		this.extractor.extract(this.module) { jpath ->
			paths.add(jpath)
		}
		assert paths.size() == 1
		assert paths[0].path == "types/MyType/doSomething.jsp"
		assert paths[0].type == FSType.ROOT
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
		this.dataFs.addFile("types/MyType/MyType.xml")
		this.dataFs.addFile(
			"types/MyType/MyType-templates.xml",
			ByteUtils.extractBytes(
				"""
				<templates type="MyType">
					<template file="doSomething.jsp"/>
				</templates>
				"""
			)
		)
		this.module.init(null, null)
		
		List<JPath> paths = new ArrayList()
		this.extractor.extract(this.module) { jpath ->
			paths.add(jpath)
		}
		assertThat(paths, hasItems(
				new JPath(FSType.ROOT, "types/MyType/doSomething.jsp"),
				new JPath(FSType.DATA, "types/MyType/MyType-templates.xml")
		))
	}
}
