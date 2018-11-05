package com.jalios.gradle.plugin.jplatform.source.impl

import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.jplatform.JPath
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor

class WorkflowsFileExtractor implements SourceFileExtractor {

	@Override
	public void extract(JModule module, Closure<JPath> closure) {
		module.pluginXml.workflows.each { wf ->
			closure(new JPath(FSType.DATA, "workflows/${wf}.xml"))
		}
	}
}
