package com.jalios.gradle.plugin.jplatform.gen.impl

import java.util.List

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.PluginProp
import com.jalios.gradle.plugin.jplatform.gen.GeneratedFileExtractor
import com.jalios.gradle.plugin.jplatform.gen.GeneratedPath

/**
 * signature.xml file is generated because a plugin.xml file exists
 * @author Lionel HERVIER
 */
class SignatureXmlExtractor extends GeneratedFileExtractor {

	@Override
	public void extract(Closure<GeneratedPath> closure) {
		closure(new GeneratedPath(
				path: "${module.privFsPath}/signature.xml",
				source: "${module.privFsPath}/plugin.xml"
		))
	}

}
