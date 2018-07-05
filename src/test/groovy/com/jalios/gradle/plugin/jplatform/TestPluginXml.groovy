package com.jalios.gradle.plugin.jplatform

import static org.junit.Assert.*

import org.junit.Test

class TestPluginXml {

	@Test
	public void whenLoaded_thenValuesExtracted() {
		String xml = """
			<plugin name="TestASIPlugin" version="0.1" author="Lionel HERVIER" license="As-is" initialize="true" jcms="" order="0" url="" jsync="false" appserver="">
				<label xml:lang="fr">Plugin de test</label>
				<label xml:lang="en">Test Plugin</label>
				<description xml:lang="fr">Un plugin de test</description>
				<description xml:lang="en">A test plugin</description>
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
			</plugin>
		"""
		
		PluginXml pluginXml = new PluginXml(new StringReader(xml))
		
		assert pluginXml.labels.size() == 2
		assert pluginXml.labels[0].lang == "fr"
		assert pluginXml.labels[0].label == "Plugin de test"
		assert pluginXml.labels[1].lang == "en"
		assert pluginXml.labels[1].label == "Test Plugin"
		
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
		assert pluginXml.types.templates[0].templates[1].dir == null
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
		
	}

}
