package com.jalios.gradle.plugin.task

/**
 * This task will fetch a (newly created) plugin into jPlatform,
 * and import its files. The plugin name must be defined in the jModule extension object.
 * 
 * This task will raise an error if a public and/or private folder already
 * exists for the plugin.
 * 
 * @author Lionel HERVIER
 */
class FetchPluginTask extends BaseJPlatformTask {

	void run() {
		// Check that module private and public plugin folders does not exists
		if( this.jModule.privFolder.exists() || this.jModule.pubFolder.exists() ) {
			throw new Exception("Error : Plugin already imported in local module code. Remove src/main/module/plugins/${this.jModule.name} and src/main/module/WEB-INF/plugins/${this.jModule.name} folders before fetching if you want to overwrite it with version from jPlatform.")
		}
		
		// Check that module private and public folders exists in jPlatform
		if( !this.jPlatform.privFolder(this.jModule.name).exists() || !this.jPlatform.pubFolder(this.jModule.name).exists() ) {
			throw new Exception("Error : Plugin ${this.jModule.name} does not exists (public and/or private folder not found in jPlatform)")
		}
		
		// Copy public and private folder from jPlatform to jModule
		new AntBuilder().copy(todir: this.jModule.pubFolder.absolutePath) {
			fileset(dir: this.jPlatform.pubFolder(jModule.name).absolutePath) {
				exclude(name: 'signature.xml')
			}
		}
		new AntBuilder().copy(todir: this.jModule.privFolder.absolutePath) {
			fileset(dir: this.jPlatform.privFolder(jModule.name).absolutePath)
		}
	}
}
