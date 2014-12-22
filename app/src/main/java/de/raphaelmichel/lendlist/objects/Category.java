package de.raphaelmichel.lendlist.objects;

import org.simpleframework.xml.Attribute;

public class Category {
	@Attribute
	private long id;
	@Attribute
	private String name;
	private int count;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
