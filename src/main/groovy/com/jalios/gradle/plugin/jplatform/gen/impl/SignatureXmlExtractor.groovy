package com.jalios.gradle.plugin.jplatform.gen.impl

import com.jalios.gradle.plugin.jplatform.JModule
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
				path: "${module.privFsPath}/signature.xml",
				source: "${module.privFsPath}/plugin.xml"
		))
	}

}
