package net.sacredlabyrinth.phaed.simpleclans.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * 
 * @author RoinujNosde
 *
 */
public class Paginator {
	
	private int currentPage;
	private final int sizePerPage;
	private int totalElements;
	private Collection<?> collection;

	public Paginator(int sizePerPage, int totalElements) {
		if (sizePerPage < 1) {
			throw new IllegalArgumentException("sizePerPage cannot be less than 1");
		}
		if (totalElements < 0) {
			throw new IllegalArgumentException("totalElements cannot be less than 0");
		}
		this.sizePerPage = sizePerPage;
		this.totalElements = totalElements;
	}

	public Paginator(int sizePerPage, @NotNull Collection<?> collection) {
		if (sizePerPage < 1) {
			throw new IllegalArgumentException("sizePerPage cannot be less than 1");
		}
		this.sizePerPage = sizePerPage;
		this.collection = collection;
	}
	
	/**
	 * author: RoinujNosde
	 * @return the total elements
	 */
	public int getTotalElements() {
		if (collection != null) {
			return collection.size();
		}
		return totalElements;
	}
	
	/**
	 * author: RoinujNosde
	 * @return the size per page
	 *
	 */
	public int getSizePerPage() {
		return sizePerPage;
	}
	
	/**
	 * author: RoinujNosde
	 * @return the minimal index based on the current page
	 *
	 */
	public int getMinIndex() {
		return getCurrentPage() * getSizePerPage();
	}
	
	/**
	 * author: RoinujNosde
	 * @return the max index based on the current page
	 *
	 */
	public int getMaxIndex() {
		return (getCurrentPage() + 1) * getSizePerPage();
	}
	
	/**
	 * author: RoinujNosde
	 *
	 * @return the current page, starting at 0
	 */
	public int getCurrentPage() {
		return currentPage;
	}
	
	/**
	 * Increases the page number if there are elements to display
	 *
	 * <p>
	 * author: RoinujNosde
	 * </p>
	 */
	public boolean nextPage() {
		if (!hasNextPage()) {
			return false;
		}
		currentPage++;
		return true;
	}

	/**
	 * @return if there is a next page
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean hasNextPage() {
		return !((sizePerPage * (currentPage + 1)) > getTotalElements());
	}

	/**
	 * Decreases the page number if current {@literal >} 0
	 * <p>
	 * author: RoinujNosde
	 * </p>
	 */
	public boolean previousPage() {
		if (hasPreviousPage()) {
			currentPage--;
			return true;
		}
		return false;
	}

	/**
	 * @return if there is a previous page
	 */
	public boolean hasPreviousPage() {
		return currentPage > 0;
	}

	/**
	 * 
	 * @param index the index
	 * @return if this index will not cause any IndexOutOfBoundsException
	 *
	 * <p>
	 * author: RoinujNosde
	 * </p>
	 */
	public boolean isValidIndex(int index) {
        return index >= getMinIndex() && index < getMaxIndex() && index < getTotalElements();
    }
}
