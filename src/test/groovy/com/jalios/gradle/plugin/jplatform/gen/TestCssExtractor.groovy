package com.jalios.gradle.plugin.jplatform.gen

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

import com.jalios.gradle.plugin.jplatform.JModule
import com.jalios.gradle.plugin.test.InMemoryFileSystem

class TestCssExtractor {

	InMemoryFileSystem fs
	JModule module
	CssExtractor extractor
	
	@Before
	void before() {
		this.fs = new InMemoryFileSystem()
		this.module = new JModule("TestPlugin", this.fs)
		this.extractor = new CssExtractor(module: module)
	}
	
	@Test
	public void test() {
		
		
		fail("Not yet implemented")
	}

}
