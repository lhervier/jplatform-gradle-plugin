package com.jalios.gradle.plugin.jplatform

import org.gradle.api.Project
import org.gradle.internal.impldep.org.apache.commons.collections.map.HashedMap

class PluginProp {

	private Map<String, String> props = [:]
	
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
			
			this.props[line.substring(0, pos).trim()] = line.substring(pos + 1).trim()
		}
	}
	
	String value(String key) {
		return this.props[key]
	}
	
	void each(Closure c) {
		this.props.each(c)
	}
	
	void each(String prefix, Closure closure) {
		this.props.each { key, value ->
			if( !key.startsWith(prefix) ) {
				return
			}
			closure(key, value)
		}
	}
}
