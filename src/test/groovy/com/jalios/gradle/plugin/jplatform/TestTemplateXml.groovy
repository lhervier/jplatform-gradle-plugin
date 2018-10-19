package com.jalios.gradle.plugin.jplatform

import static org.junit.Assert.*

import org.junit.Test

class TestTemplateXml {

	@Test
	public void whenLoaded_thenValuesExtracted() {
		String xml = """
			<templates>
				<template name="custom1" file="doAbstractPortletSkinableFullDisplay.jsp" dir="type" usage='full'>
					<label xml:lang="en">Custom Template</label>
					<label xml:lang="fr">Gabarit Spécifique</label>
				</template>
				<template name="custom2" file="doAbstractPortletSkinableResultDisplay.jsp" usage='query'>
					<label xml:lang="en">Custom Template</label>
					<label xml:lang="fr">Gabarit Spécifique</label>
				</template>
			</templates>
		"""
	
		TemplateXml templateXml = new TemplateXml("MyType", new StringReader(xml))
		
		assert templateXml.type == "MyType"
		assert templateXml.templates.size() == 2
		assert templateXml.templates[0].name == "custom1"
		assert templateXml.templates[0].file == "doAbstractPortletSkinableFullDisplay.jsp"
		assert templateXml.templates[0].dir == "type"
		assert templateXml.templates[0].usage == "full"
		assert templateXml.templates[0].labels[0].label == "Custom Template"
		assert templateXml.templates[0].labels[0].lang == "en"
		assert templateXml.templates[0].labels[1].label == "Gabarit Spécifique"
		assert templateXml.templates[0].labels[1].lang == "fr"
		assert templateXml.templates[1].name == "custom2"
		assert templateXml.templates[1].file == "doAbstractPortletSkinableResultDisplay.jsp"
		assert templateXml.templates[1].dir == null
		assert templateXml.templates[1].usage == "query"
		assert templateXml.templates[1].labels[0].label == "Custom Template"
		assert templateXml.templates[1].labels[0].lang == "en"
		assert templateXml.templates[1].labels[1].label == "Gabarit Spécifique"
		assert templateXml.templates[1].labels[1].lang == "fr"
	}
}
