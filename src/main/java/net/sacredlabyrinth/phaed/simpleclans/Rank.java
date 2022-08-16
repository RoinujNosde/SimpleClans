package net.sacredlabyrinth.phaed.simpleclans;

import java.util.HashSet;
import java.util.Set;

/**
*
* @author RoinujNosde
*/
public class Rank implements Comparable<Rank> {
	
	private String name;
	private String displayName;
	private Set<String> permissions;
	
	public Rank(String name) {
		setName(name);
		permissions = new HashSet<>();
		setDisplayName(displayName);
	}
	
	public Rank(String name, String displayName, Set<String> permissions) {
		setName(name);
		setDisplayName(displayName);
		setPermissions(permissions);
	}
	
	public void setName(String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("name cannot be null or empty");
		}
		this.name = name;
	}

	public void setDisplayName(String displayName) {
		if (displayName == null || displayName.isEmpty()) {
			displayName = name;
		}
		this.displayName = displayName;
	}
	
	public void setPermissions(Set<String> permissions) {
		if (permissions == null) {
			permissions = new HashSet<>();
		}
		this.permissions = permissions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Rank other = (Rank) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * Returns the rank's permissions
	 * 
	 * @return
	 */
	public Set<String> getPermissions() {
		return permissions;
	}

	/**
	 * Returns the rank's display name
	 * 
	 * @return
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Returns the rank's name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	@Override
	public int compareTo(Rank other) {
		return Integer.compare(permissions.size(), other.permissions.size());
	}
}
