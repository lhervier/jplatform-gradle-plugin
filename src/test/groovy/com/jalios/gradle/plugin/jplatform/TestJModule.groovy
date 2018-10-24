package com.jalios.gradle.plugin.jplatform

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.fs.FSType
import com.jalios.gradle.plugin.jplatform.gen.GeneratedFileExtractor
import com.jalios.gradle.plugin.jplatform.gen.GeneratedPath
import com.jalios.gradle.plugin.jplatform.source.SourceFileExtractor
import com.jalios.gradle.plugin.test.InMemoryJFileSystem
import com.jalios.gradle.plugin.test.util.ByteUtils

class TestJModule {

	@Before
	void setUp() throws Exception {
	}

	@Test
	void whenPluginXmlDoesNotExists_thenEmptyJModule() {
		JModule module = new JModule("TestPlugin", new InMemoryJFileSystem(), new InMemoryJFileSystem())
		module.init(null, null)
		
		assert module.name == "TestPlugin"
		assert module.pluginXml == null
		assert module.privFs != null
		assert module.pubFs != null
	}

	@Test
	void whenPluginXmlExist_thenJModuleInitialized() {
		InMemoryJFileSystem fs = new InMemoryJFileSystem()
		fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml", 
			ByteUtils.extractBytes("""
				<plugin name="TestASIPlugin">
					<label xml:lang="fr">Plugin de test</label>
					<description xml:lang="fr">Un plugin de test</description>
				</plugin>
			""")
		)
		fs.addFile(
			"plugins/TestPlugin/public_file.txt",
			ByteUtils.extractBytes("content of the file")
		)
		fs.addFile(
			"js/webapp_file.js",
			ByteUtils.extractBytes("content of js file")
		)
		
		InMemoryJFileSystem dataFs = new InMemoryJFileSystem()
		dataFs.addFile(
			"types/MyType/MyType.xml",
			ByteUtils.extractBytes("content of type xml file")
		)
		
		JModule module = new JModule("TestPlugin", fs, dataFs)
		module.init(null, null)
		
		assert module.name == "TestPlugin"
		assert module.pluginXml.labels[0].label == "Plugin de test"
		assert module.pluginXml.labels[0].lang == "fr"
		assert module.pluginXml.descriptions[0].label == "Un plugin de test"
		assert module.pluginXml.descriptions[0].lang == "fr"
		
		assert module.privFs.exists("plugin.xml")
		assert module.pubFs.exists("public_file.txt")
		assert module.rootFs.exists("js/webapp_file.js")
		assert module.dataFs.exists("types/MyType/MyType.xml")
	}
	
	@Test
	void whenPluginPropDoesnotExist_thenPluginPropNotLoaded() throws Exception {
		InMemoryJFileSystem fs = new InMemoryJFileSystem()
		fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
				<plugin name="TestASIPlugin">
					<label xml:lang="fr">Plugin de test</label>
					<description xml:lang="fr">Un plugin de test</description>
				</plugin>
			""")
		)
		JModule module = new JModule("TestPlugin", fs, new InMemoryJFileSystem())
		module.init(null, null)
		
		assert module.pluginProp == null
		assert module.pluginXml != null
		assert module.privFs != null
		assert module.pubFs != null
	}
	
	@Test
	void whenPluginPropExist_thenPluginPropLoaded() throws Exception {
		InMemoryJFileSystem fs = new InMemoryJFileSystem()
		fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
				<plugin name="TestASIPlugin">
					<label xml:lang="fr">Plugin de test</label>
					<description xml:lang="fr">Un plugin de test</description>
				</plugin>
			""")
		)
		fs.addFile(
			"WEB-INF/plugins/TestPlugin/properties/plugin.prop",
			ByteUtils.extractBytes("""
				prop: value
			""")
		)
		
		JModule module = new JModule("TestPlugin", fs, new InMemoryJFileSystem())
		module.init(null, null)
		
		assert module.pluginProp.value("prop") == "value"
	}
	
	@Test
	void whenGeneratedFileExtractor_thenGeneratedFilesExtracted() {
		InMemoryJFileSystem fs = new InMemoryJFileSystem()
		fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
				<plugin name="TestASIPlugin">
					<label xml:lang="fr">Plugin de test</label>
					<description xml:lang="fr">Un plugin de test</description>
				</plugin>
			""")
		)
		JModule module = new JModule("TestPlugin", fs, new InMemoryJFileSystem())
		
		def extractor = {JModule m, Closure<GeneratedPath> closure ->
			closure(new GeneratedPath(
				path: new JPath(FSType.ROOT, "css/styles.css"), 
				source: new JPath(FSType.ROOT, "css/styles.less")
			))
		} as GeneratedFileExtractor
		
		module.init([extractor], null)
		
		assert module.generatedPaths[0].path.path == "css/styles.css"
		assert module.generatedPaths[0].path.type == FSType.ROOT
		assert module.generatedPaths[0].source.path == "css/styles.less"
		assert module.generatedPaths[0].source.type == FSType.ROOT
	}
	
	@Test
	void whenSourceFileExtractor_thenSourceFilesExtracted() {
		InMemoryJFileSystem fs = new InMemoryJFileSystem()
		fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
				<plugin name="TestASIPlugin">
					<label xml:lang="fr">Plugin de test</label>
					<description xml:lang="fr">Un plugin de test</description>
				</plugin>
			""")
		)
		JModule module = new JModule("TestPlugin", fs, new InMemoryJFileSystem())
		
		def extractor = {JModule m, Closure<String> closure ->
			closure(new JPath(FSType.ROOT, "js/test.js"))
		} as SourceFileExtractor
		
		module.init(null, [extractor])
		
		assert module.paths[0].path == "js/test.js"
		assert module.paths[0].type == FSType.ROOT
	}
	
	@Test
	void whenSourceAndGeneratedFileExtractor_thenGeneratedRemovedFromSource() {
		InMemoryJFileSystem fs = new InMemoryJFileSystem()
		fs.addFile(
			"WEB-INF/plugins/TestPlugin/plugin.xml",
			ByteUtils.extractBytes("""
				<plugin name="TestASIPlugin">
					<label xml:lang="fr">Plugin de test</label>
					<description xml:lang="fr">Un plugin de test</description>
				</plugin>
			""")
		)
		JModule module = new JModule("TestPlugin", fs, new InMemoryJFileSystem())
		
		def genExtractor = {JModule m, Closure<GeneratedPath> closure ->
			closure(new GeneratedPath(
				path: new JPath(FSType.ROOT, "css/styles.css"), 
				source: new JPath(FSType.ROOT, "css/styles.less")
			))
		} as GeneratedFileExtractor
		
		def srcExtractor = {JModule m, Closure<JPath> closure ->
			closure(new JPath(FSType.ROOT, "css/styles.css"))
			closure(new JPath(FSType.ROOT, "css/styles2.css"))
		} as SourceFileExtractor
		
		module.init([genExtractor], [srcExtractor])
		
		assert module.generatedPaths.size() == 1
		assert module.generatedPaths[0].path.path == "css/styles.css"
		assert module.generatedPaths[0].path.type == FSType.ROOT
		assert module.generatedPaths[0].source.path == "css/styles.less"
		assert module.generatedPaths[0].source.type == FSType.ROOT
		
		assert module.paths.size() == 1
		assert module.paths[0].path == "css/styles2.css"
		assert module.paths[0].type == FSType.ROOT
		// assert module.paths[1] == "css/styles.css"	<= Generated file, so not present 
	}
}
