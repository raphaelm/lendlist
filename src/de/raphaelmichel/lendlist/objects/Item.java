package de.raphaelmichel.lendlist.objects;

import java.util.Date;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class Item {

	@Attribute
	private long id;

	@Attribute(required = false)
	private String direction;

	@Element(required = false)
	private String thing;

	@Element(required = false)
	private String person;

	@Element(required = false)
	private long contact_id;

	@Element(required = false)
	private String contact_lookup;

	@Element(required = false)
	private Date until;

	@Element(required = false)
	private Date date = new Date();
	
	@Attribute(required = false)
	private boolean notified = false;

	@Attribute
	private boolean returned = false;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getThing() {
		return thing;
	}

	public void setThing(String thing) {
		this.thing = thing;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public long getContact_id() {
		return contact_id;
	}

	public void setContact_id(long contact_id) {
		this.contact_id = contact_id;
	}

	public String getContact_lookup() {
		return contact_lookup;
	}

	public void setContact_lookup(String contact_lookup) {
		this.contact_lookup = contact_lookup;
	}

	public Date getUntil() {
		return until;
	}

	public void setUntil(Date until) {
		this.until = until;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isReturned() {
		return returned;
	}

	public void setReturned(boolean returned) {
		this.returned = returned;
	}

	public boolean isNotified() {
		return notified;
	}

	public void setNotified(boolean notified) {
		this.notified = notified;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", direction=" + direction + ", thing="
				+ thing + ", person=" + person + ", contact_id=" + contact_id
				+ ", until=" + until + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (contact_id ^ (contact_id >>> 32));
		result = prime * result
				+ ((contact_lookup == null) ? 0 : contact_lookup.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((person == null) ? 0 : person.hashCode());
		result = prime * result + (returned ? 1231 : 1237);
		result = prime * result + ((thing == null) ? 0 : thing.hashCode());
		result = prime * result + ((until == null) ? 0 : until.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (contact_id != other.contact_id)
			return false;
		if (contact_lookup == null) {
			if (other.contact_lookup != null)
				return false;
		} else if (!contact_lookup.equals(other.contact_lookup))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (direction == null) {
			if (other.direction != null)
				return false;
		} else if (!direction.equals(other.direction))
			return false;
		if (id != other.id)
			return false;
		if (person == null) {
			if (other.person != null)
				return false;
		} else if (!person.equals(other.person))
			return false;
		if (returned != other.returned)
			return false;
		if (notified != other.notified)
			return false;
		if (thing == null) {
			if (other.thing != null)
				return false;
		} else if (!thing.equals(other.thing))
			return false;
		if (until == null) {
			if (other.until != null)
				return false;
		} else if (!until.equals(other.until))
			return false;
		return true;
	}

}
