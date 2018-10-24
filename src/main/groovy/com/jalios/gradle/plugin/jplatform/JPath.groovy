package com.jalios.gradle.plugin.jplatform

import org.codehaus.groovy.runtime.GStringImpl

import com.jalios.gradle.plugin.fs.FSType

class JPath {
	final String path
	final FSType type
	
	JPath(FSType type, String path) {
		this.path = path
		this.type = type
	}

	@Override
	public String toString() {
		return "JPath [path=" + path + ", type=" + type + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/**
	 * WARNING ! This is NOT the Eclipse generated version !!
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JPath other = (JPath) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
