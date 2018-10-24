package com.jalios.gradle.plugin.jplatform.gen.impl

import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.JPath
import com.jalios.gradle.plugin.jplatform.gen.GeneratedFileExtractor
import com.jalios.gradle.plugin.jplatform.gen.GeneratedPath

/**
 * signature.xml file is generated because a plugin.xml file exists
 * @author Lionel HERVIER
 */
class SignatureXmlExtractor implements GeneratedFileExtractor {

	@Override
	public void extract(JModule module, Closure<GeneratedPath> closure) {
		closure(new GeneratedPath(
				path: new JPath(FSType.PRIVATE, "signature.xml"),
				source: new JPath(FSType.PRIVATE, "plugin.xml")
		))
	}

}
