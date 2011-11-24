package com.vo;

import java.util.ArrayList;
import java.util.List;

public class Blog {
	@SuppressWarnings("unused")
	private Author writer;
	@SuppressWarnings("unchecked")
	private List entries = new ArrayList();

	public Blog(Author writer) {
		this.writer = writer;
	}

	@SuppressWarnings("unchecked")
	public void add(Entry entry) {
		entries.add(entry);
	}

	@SuppressWarnings("unchecked")
	public List getContent() {
		return entries;
	}
}
