package de.raphaelmichel.lendlist.objects;

public class Person {
	private String name;
	private long id;
	private String lookup;
	private int count;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLookup() {
		return lookup;
	}
	public void setLookup(String lookup) {
		this.lookup = lookup;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
