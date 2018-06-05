package com.jalios.gradle.plugin

import org.gradle.api.Project

import com.jalios.gradle.plugin.jplatform.PluginProp

class BaseJPlatformElement {

	protected PluginProp pluginProp
	protected String root
	
	BaseJPlatformElement(Project project, String root) {
		this.root = root
		File pluginPropFile = project.file("${root}/WEB-INF/plugins/${project.jModule.name}/properties/plugin.prop")
		this.pluginProp = new PluginProp(pluginPropFile)
	}
	
	PluginProp getPluginProp() {
		return pluginProp
	}
	
	String getRoot() {
		return root;
	}
}
