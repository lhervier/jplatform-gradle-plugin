package com.jalios.gradle.plugin.jplatform.gen

import java.util.List

import com.jalios.gradle.plugin.jplatform.GeneratedFile
import com.jalios.gradle.plugin.jplatform.IGeneratedFileExtractor
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.PluginProp

/**
 * signature.xml file is generated because a plugin.xml file exists
 * @author Lionel HERVIER
 */
class SignatureXmlExtractor implements IGeneratedFileExtractor {

	@Override
	public void extract(JModule module, Closure closure) {
		GeneratedFile ret = new GeneratedFile(
				path: "${module.privFolderPath}/signature.xml",
				source: "${module.privFolderPath}/plugin.xml"
		)
		closure(ret)
	}

}
