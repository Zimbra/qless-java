package com.zimbra.qless.web.viewmodel;


public class Tab {
	protected String name, path;
	
	public Tab(String name, String path) {
		this.name = name;
		this.path = path;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
}
