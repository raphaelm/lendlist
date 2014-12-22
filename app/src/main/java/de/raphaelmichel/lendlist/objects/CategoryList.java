package de.raphaelmichel.lendlist.objects;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "categories")
public class CategoryList {
	@ElementList(inline = true)
	private List<Category> categories;

	public CategoryList() {
	}

	public CategoryList(List<Category> categories) {
		this.categories = categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public List<Category> getCategories() {
		return categories;
	}
}
