package com.jalios.gradle.plugin.jplatform

import static org.junit.Assert.*

import org.junit.Test

class TestPluginProp {

	@Test
	public void testPluginProp() {
		String prop = """
			prop1: value1
			prop2: value2
			my.prop.1: val1
			my.prop.2: val2
		"""
		
		PluginProp pluginProp = new PluginProp(new StringReader(prop))
		
		assert pluginProp.value("prop1") == "value1"
		assert pluginProp.value("do_not_exist") == null
		
		Map<String, String> values = [:]
		pluginProp.each {k, v ->
			values[k] = v
		}
		assert values.size() == 4
		
		values = [:]
		pluginProp.each("my.prop.") {k, v ->
			values[k] = v
		}
		assert values.size() == 2
		assert values.containsKey("my.prop.1")
		assert values.containsKey("my.prop.2")
		assert values["my.prop.1"] == "val1"
		assert values["my.prop.2"] == "val2"
	}

}
