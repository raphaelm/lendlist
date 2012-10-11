package de.raphaelmichel.lendlist.objects;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "items")
public class ItemList {
	@ElementList(inline = true)
	private List<Item> items;

	public ItemList(List<Item> items) {
		this.items = items;
	}
}
