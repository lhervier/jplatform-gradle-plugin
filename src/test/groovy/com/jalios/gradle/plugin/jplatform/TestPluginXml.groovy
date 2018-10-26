package com.jalios.gradle.plugin.jplatform

import static org.junit.Assert.*

import org.junit.Test

import com.jalios.gradle.plugin.test.util.ByteUtils

class TestPluginXml {

	@Test
	public void whenLoaded_thenValuesExtracted() {
		String xml = """
			<plugin name="TestASIPlugin" version="0.1" author="Lionel HERVIER" license="As-is" initialize="true" jcms="" order="0" url="" jsync="false" appserver="">
				<label xml:lang="fr">Plugin de test</label>
				<label xml:lang="en">Test Plugin</label>
				<description xml:lang="fr">Un plugin de test</description>
				<description xml:lang="en">A test plugin</description>
				<dependencies>
					<dependency name="OtherPlugin" />
					<dependency name="XXX" active="false" />
				</dependencies>
				<types>
					<type name="Type">
						<file path="doTypeFullDisplay.jsp" />
					</type>
					<templates type="AbstractPortletSkinable">
						<template name="custom1" file="doAbstractPortletSkinableFullDisplay.jsp" dir="type" usage='full'>
							<label xml:lang="en">Custom Template</label>
							<label xml:lang="fr">Gabarit Spécifique</label>
						</template>
						<template name="custom2" file="doAbstractPortletSkinableResultDisplay.jsp" usage='query'>
							<label xml:lang="en">Custom Template</label>
							<label xml:lang="fr">Gabarit Spécifique</label>
						</template>
					</templates>
				</types>
				<jars>
					<jar path="jar1.jar"/>
					<jar path="jar2.jar"/>
				</jars>
				<private-files>
					<directory path="properties" />
					<file path="docs/license.txt" include="true"/>
				</private-files>
				<public-files>
					<directory path="js" />
					<directory path="css" />
					<directory path="docs" />
					<directory path="jsp" />
				</public-files>
				<webapp-files>
					<directory path="toto" />
					<file path="test.txt" />
				</webapp-files>
			</plugin>
		"""
		
		PluginXml pluginXml = new PluginXml(xml)
		
		assert pluginXml.name == "TestASIPlugin"
		assert pluginXml.version == "0.1"
		
		assert pluginXml.jars.size() == 2
		assert pluginXml.jars[0].path == "jar1.jar"
		assert pluginXml.jars[1].path == "jar2.jar"
		
		assert pluginXml.labels.size() == 2
		assert pluginXml.labels[0].lang == "fr"
		assert pluginXml.labels[0].label == "Plugin de test"
		assert pluginXml.labels[1].lang == "en"
		assert pluginXml.labels[1].label == "Test Plugin"
		
		assert pluginXml.descriptions.size() == 2
		assert pluginXml.descriptions[0].lang == "fr"
		assert pluginXml.descriptions[0].label == "Un plugin de test"
		assert pluginXml.descriptions[1].lang == "en"
		assert pluginXml.descriptions[1].label == "A test plugin"
		
		assert pluginXml.dependencies.size() == 2
		assert pluginXml.dependencies[0].name == "OtherPlugin"
		assert pluginXml.dependencies[0].active
		assert pluginXml.dependencies[1].name == "XXX"
		assert !pluginXml.dependencies[1].active
		
		assert pluginXml.types.types.size() == 1
		assert pluginXml.types.types[0].name == "Type"
		assert pluginXml.types.types[0].files.size() == 1
		assert pluginXml.types.types[0].files[0].path == "doTypeFullDisplay.jsp"
		
		assert pluginXml.types.templates.size() == 1
		assert pluginXml.types.templates[0].templates.size() == 2
		assert pluginXml.types.templates[0].templates[0].name == "custom1"
		assert pluginXml.types.templates[0].templates[0].file == "doAbstractPortletSkinableFullDisplay.jsp"
		assert pluginXml.types.templates[0].templates[0].dir == "type"
		assert pluginXml.types.templates[0].templates[0].usage == "full"
		assert pluginXml.types.templates[0].templates[0].labels[0].label == "Custom Template"
		assert pluginXml.types.templates[0].templates[0].labels[0].lang == "en"
		assert pluginXml.types.templates[0].templates[0].labels[1].label == "Gabarit Spécifique"
		assert pluginXml.types.templates[0].templates[0].labels[1].lang == "fr"
		assert pluginXml.types.templates[0].templates[1].name == "custom2"
		assert pluginXml.types.templates[0].templates[1].file == "doAbstractPortletSkinableResultDisplay.jsp"
		assert pluginXml.types.templates[0].templates[1].dir == ""
		assert pluginXml.types.templates[0].templates[1].usage == "query"
		assert pluginXml.types.templates[0].templates[1].labels[0].label == "Custom Template"
		assert pluginXml.types.templates[0].templates[1].labels[0].lang == "en"
		assert pluginXml.types.templates[0].templates[1].labels[1].label == "Gabarit Spécifique"
		assert pluginXml.types.templates[0].templates[1].labels[1].lang == "fr"
		
		assert pluginXml.privateFiles.directories.size() == 1
		assert pluginXml.privateFiles.directories[0].path == "properties"
		assert pluginXml.privateFiles.files.size() == 1
		assert pluginXml.privateFiles.files[0].path == "docs/license.txt"
		assert pluginXml.privateFiles.files[0].include == "true"
		
		assert pluginXml.publicFiles.files.size() == 0
		assert pluginXml.publicFiles.directories.size() == 4
		assert pluginXml.publicFiles.directories[0].path == "js"
		assert pluginXml.publicFiles.directories[1].path == "css"
		assert pluginXml.publicFiles.directories[2].path == "docs"
		assert pluginXml.publicFiles.directories[3].path == "jsp"
		
		assert pluginXml.webappFiles.files.size() == 1
		assert pluginXml.webappFiles.files[0].path == "test.txt"
		assert pluginXml.webappFiles.files[0].include == "true"
		assert pluginXml.webappFiles.directories.size() == 1
		assert pluginXml.webappFiles.directories[0].path == "toto"
		
	}

	@Test
	void whenChangingName_thenNameChanged() {
		String xml = """
			<plugin name="TestASIPlugin" version="0.1" author="Lionel HERVIER" license="As-is" initialize="true" jcms="" order="0" url="" jsync="false" appserver="">
			</plugin>
		"""
		PluginXml pluginXml = new PluginXml(xml)
		assert pluginXml.name == "TestASIPlugin"
		
		pluginXml.name = "OtherPlugin"
		assert pluginXml.name == "OtherPlugin"
	}
	
	@Test
	void whenChangingVersion_thenVersionChanged() {
		String xml = """
			<plugin name="TestASIPlugin" version="0.1" author="Lionel HERVIER" license="As-is" initialize="true" jcms="" order="0" url="" jsync="false" appserver="">
			</plugin>
		"""
		PluginXml pluginXml = new PluginXml(xml)
		assert pluginXml.version == "0.1"
		
		pluginXml.version = "0.2"
		assert pluginXml.version == "0.2"
	}
	
	@Test
	void whenAddingJar_thenJarAdded() {
		String xml = """
			<plugin name="TestASIPlugin" version="0.1" author="Lionel HERVIER" license="As-is" initialize="true" jcms="" order="0" url="" jsync="false" appserver="">
				<jars/>
			</plugin>
		"""
		PluginXml pluginXml = new PluginXml(xml)
		assert pluginXml.jars.isEmpty()
		
		pluginXml.addJar("jar1.jar")
		assert pluginXml.jars.size() == 1
		assert pluginXml.jars[0].path == "jar1.jar"
	}
	
	@Test
	void whenAddingJarAndNoJarsTag_thenJarAdded() {
		String xml = """
			<plugin name="TestASIPlugin" version="0.1" author="Lionel HERVIER" license="As-is" initialize="true" jcms="" order="0" url="" jsync="false" appserver="">
			</plugin>
		"""
		PluginXml pluginXml = new PluginXml(xml)
		assert pluginXml.jars.isEmpty()
		
		pluginXml.addJar("jar1.jar")
		assert pluginXml.jars.size() == 1
		assert pluginXml.jars[0].path == "jar1.jar"
		
		pluginXml.addJar("jar2.jar")
		assert pluginXml.jars.size() == 2
		assert pluginXml.jars[0].path == "jar1.jar"
		assert pluginXml.jars[1].path == "jar2.jar"
	}
	
	@Test
	void whenSaving_thenDTDOK() {
		String xml = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plugin PUBLIC "-//JALIOS//DTD JCMS-PLUGIN 1.7//EN" "http://support.jalios.com/dtd/jcms-plugin-1.7.dtd">
<plugin name="TestASIPlugin" version="0.1" author="Lionel HERVIER" license="As-is" initialize="true" jcms="" order="0" url="" jsync="false" appserver="">
		<jars>
		</jars>
</plugin>"""
		PluginXml pluginXml = new PluginXml(xml)
		ByteArrayOutputStream out = new ByteArrayOutputStream()
		pluginXml.save(out)
		String s = new String(out.toByteArray(), "UTF-8")
		println s
		
		List<String> lines = s.readLines()
		assert lines[0] == """<?xml version="1.0" encoding="UTF-8"?>"""
		assert lines[1] == """<!DOCTYPE plugin PUBLIC "-//JALIOS//DTD JCMS-PLUGIN 1.7//EN" "http://support.jalios.com/dtd/jcms-plugin-1.7.dtd">"""
	}
}
