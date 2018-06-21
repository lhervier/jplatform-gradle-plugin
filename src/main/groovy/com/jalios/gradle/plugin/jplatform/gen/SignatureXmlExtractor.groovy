package com.jalios.gradle.plugin.jplatform.gen

import java.util.List

import com.jalios.gradle.plugin.jplatform.GeneratedPath
import com.jalios.gradle.plugin.jplatform.GeneratedFileExtractor
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.PluginProp

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
