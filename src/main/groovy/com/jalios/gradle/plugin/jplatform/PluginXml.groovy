package com.jalios.gradle.plugin.jplatform

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

import org.w3c.dom.DOMImplementation
import org.w3c.dom.Document
import org.w3c.dom.NodeList

import com.jalios.gradle.plugin.jplatform.model.JDependency
import com.jalios.gradle.plugin.jplatform.model.JDirectory
import com.jalios.gradle.plugin.jplatform.model.JFile
import com.jalios.gradle.plugin.jplatform.model.JFiles
import com.jalios.gradle.plugin.jplatform.model.JJar
import com.jalios.gradle.plugin.jplatform.model.JLabel
import com.jalios.gradle.plugin.jplatform.model.JTemplate
import com.jalios.gradle.plugin.jplatform.model.JTemplates
import com.jalios.gradle.plugin.jplatform.model.JType
import com.jalios.gradle.plugin.jplatform.model.JTypes
import com.jalios.gradle.plugin.test.util.ByteUtils
import com.jalios.gradle.plugin.test.util.XmlUtils

/**
 * Representation of a plugin.xml file
 * @author Lionel HERVIER
 */
class PluginXml {

	private DocumentBuilderFactory factory
	private DocumentBuilder builder
	private Document xml
	private org.w3c.dom.Element element
	
	/**
	 * Empty constructor
	 */
	private PluginXml() {
		this.factory = DocumentBuilderFactory.newInstance()
		this.builder = factory.newDocumentBuilder()
	}
	
	/**
	 * Constructor from a reader
	 * @param pluginXml the plugin.xml file
	 */
	PluginXml(String pluginXml) {
		this(new ByteArrayInputStream(ByteUtils.extractBytes(pluginXml)));
	}
	
	/**
	 * Constructor from a stream
	 */
	PluginXml(InputStream inStream) {
		this()
		this.xml = this.builder.parse(inStream)
		this.element = this.xml.documentElement
	}
	
	/**
	 * Returns the name
	 */
	String getName() {
		return this.element.getAttribute('name')
	}
	
	/**
	 * Sets the name
	 */
	void setName(String name) {
		this.element.setAttribute('name', name)
	}
	
	/**
	 * Returns the version
	 */
	String getVersion() {
		return this.element.getAttribute('version')
	}
	
	/**
	 * Sets the version
	 */
	void setVersion(String version) {
		this.element.setAttribute('version', version)
	}
	
	/**
	 * Returns the jar dependencies	
	 */
	public List<JJar> getJars() {
		List<JJar> jars = new ArrayList()
		
		// Get the jars tag
		org.w3c.dom.Element nJars = XmlUtils.getFirstChild(this.element, 'jars')
		if( nJars == null ) {
			return jars
		}
		
		// Get all the jar tags
		nJars.getElementsByTagName('jar').each { elt ->
			JJar j = new JJar(path: elt.getAttribute('path'))
			jars.add(j)
		}
		
		return jars
	}
	
	/**
	 * Add a jar to the dependencies
	 */
	public void addJar(String jarName) {
		// Get the jars tag, creating it if needed
		org.w3c.dom.Element nJars
		
		List<org.w3c.dom.Element> nlJars = XmlUtils.getChildrenByTagName(this.element, 'jars')
		if( nlJars == null || nlJars.isEmpty() ) {
			nJars = this.xml.createElement('jars')
			this.element.appendChild(nJars)
		} else {
			nJars = nlJars[0]
		}
		
		// Add jar node
		org.w3c.dom.Element nJar = this.xml.createElement('jar')
		nJar.setAttribute('path', jarName)
		nJars.appendChild(nJar)
	}
	
	// ============================== Read only ===========================
	
	/**
	 * Returns the labels	
	 */
	public List<JLabel> getLabels() {
		List<JLabel> labels = new ArrayList()
		XmlUtils.getChildrenByTagName(this.element, 'label').each { elt ->
			labels.add(new JLabel(
					lang: elt.getAttribute('xml:lang'), 
					label: elt.textContent)
			)
		}
		return labels
	}
	
	/**
	 * Returns the descriptions
	 */
	public List<JLabel> getDescriptions() {
		List<JLabel> descriptions = new ArrayList()
		XmlUtils.getChildrenByTagName(this.element, 'description').each { elt ->
			descriptions.add(new JLabel(
				lang: elt.getAttribute('xml:lang'), 
				label: elt.textContent)
			)
		}
		return descriptions
	}
	
	/**
	 * Returns the dependencies
	 */
	public List<JDependency> getDependencies() {
		List<JDependency> dependencies = new ArrayList()
		
		org.w3c.dom.Element eltDependencies = XmlUtils.getFirstChild(this.element, 'dependencies')
		if( eltDependencies == null ) {
			return dependencies
		}
		
		XmlUtils.getChildrenByTagName(eltDependencies, 'dependency').each { elt ->
			String active = elt.getAttribute('active')
			boolean bActive = true
			if( active != null && active.length() != 0 ) {
				bActive = active.toBoolean()
			}
			dependencies.add(new JDependency(
				name: elt.getAttribute('name'), 
				active: bActive
			))
		}
		
		return dependencies
	}
	
	/**
	 * Returns the types
	 */
	public JTypes getTypes() {
		JTypes types = new JTypes()
		XmlUtils.getChildrenByTagName(
			XmlUtils.getFirstChild(this.element, 'types'), 
			'type'
		).each { n ->
			JType t = new JType(name: n.getAttribute('name'))
			XmlUtils.getChildrenByTagName(n, 'file').each { nf ->
				t.files.add(new JFile(
					path: nf.getAttribute('path'), 
					include: nf.getAttribute('include')
				))
			}
			types.types.add(t)
		}
		
		XmlUtils.getChildrenByTagName(
			XmlUtils.getFirstChild(this.element, 'types'), 
			'templates'
		).each { n ->
			JTemplates ts = new JTemplates(type: n.getAttribute('type'))
			XmlUtils.getChildrenByTagName(n, 'template').each { nt ->
				JTemplate tmpl = new JTemplate(
					name: nt.getAttribute('name'),
					file: nt.getAttribute('file'),
					dir: nt.getAttribute('dir'),
					usage: nt.getAttribute('usage')
				)
				
				XmlUtils.getChildrenByTagName(nt, 'label').each { nl ->
					tmpl.labels.add(new JLabel(
						lang: nl.getAttribute('xml:lang'), 
						label: nl.textContent
					))
				}
				ts.templates.add(tmpl)
			}
			types.templates.add(ts)
		}
		return types
	}
	
	/**
	 * Returns the private files
	 */
	private JFiles getFiles(String tagName) {
		JFiles files = new JFiles()
		XmlUtils.getChildrenByTagName(
			XmlUtils.getFirstChild(this.element, tagName),
			'directory'
		).each { n ->
			files.directories.add(new JDirectory(path: n.getAttribute('path')))
		}
		XmlUtils.getChildrenByTagName(
			XmlUtils.getFirstChild(this.element, tagName),
			'file'
		).each { n ->
			String include = n.getAttribute('include')
			if( include == null || include.length() == 0 ) {
				include = "true"
			}
			files.files.add(new JFile(
				path: n.getAttribute('path'),
				include: include
			))
		}
		return files
	}
	
	/**
	 * Returns the private files
	 */
	public JFiles getPrivateFiles() {
		return this.getFiles('private-files')
	}
	
	/**
	 * Returns the public files
	 */
	public JFiles getPublicFiles() {
		return this.getFiles('public-files')
	}
	
	/**
	 * Returns the webapp files
	 */
	public JFiles getWebappFiles() {
		return this.getFiles('webapp-files')
	}
	
	/**
	 * Save the XML file 
	 * @param writer to save the file
	 */
	void save(OutputStream out) {
		DOMSource domSource = new DOMSource(this.xml);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StreamResult sr = new StreamResult(out);
		transformer.transform(domSource, sr);
	}
}
