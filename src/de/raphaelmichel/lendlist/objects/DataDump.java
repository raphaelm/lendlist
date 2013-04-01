package de.raphaelmichel.lendlist.objects;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "dump")
public class DataDump {
	@Element
	private CategoryList categories;
	@Element
	private ItemList items;

	public CategoryList getCategories() {
		return categories;
	}

	public void setCategories(CategoryList categories) {
		this.categories = categories;
	}

	public ItemList getItems() {
		return items;
	}

	public void setItems(ItemList items) {
		this.items = items;
	}

	public DataDump() {
	}

	public DataDump(CategoryList categories, ItemList items) {
		super();
		this.categories = categories;
		this.items = items;
	}

}
