package com.jalios.gradle.plugin.task

import static org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.fs.JFileSystem
import com.jalios.gradle.plugin.test.InMemoryJFileSystem

class TestFetchPluginTask {

	JFileSystem fs
	FetchPluginTask task
	
	@Before
	void setUp() {
		this.fs = new InMemoryJFileSystem()
		this.task = new FetchPluginTask(
				"TestPlugin", 
				new InMemoryJFileSystem(), 
				new InMemoryJFileSystem()
		)
	}

	@Test
	void test() {
		// fail("Not yet implemented")
	}

}
