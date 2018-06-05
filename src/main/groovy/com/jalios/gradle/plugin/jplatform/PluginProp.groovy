package com.jalios.gradle.plugin.jplatform

import org.gradle.api.Project

class PluginProp {

	private def props = [:]
	
	PluginProp(File pluginProp) {
		if( !pluginProp.exists() )
			return
		
		pluginProp.eachLine("UTF-8") { line ->
			line = line.trim()
			
			// Comment
			if( line.startsWith("#") ) {
				return
			}
			
			// No separator for key and value
			int pos = line.indexOf(":")
			if( pos == -1 ) {
				return
			}
			
			props[line.substring(0, pos).trim()] = line.substring(pos + 1).trim()			
		}
	}
	
	String value(String key) {
		return this.props[key]
	}
	
	void each(Closure c) {
		this.props.each(c)
	}
}
