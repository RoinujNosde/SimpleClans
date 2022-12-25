package net.sacredlabyrinth.phaed.simpleclans.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author RoinujNosde
 *
 */
public class Paginator<T> {
	
	private int currentPage;
	private final int sizePerPage;
	private int totalElements;
	private List<T> collection;

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

	@Deprecated
	public Paginator(int sizePerPage, @NotNull Collection<T> collection) {
		this(sizePerPage, new ArrayList<>(collection));
	}

	public Paginator(int sizePerPage, @NotNull List<T> list) {
		if (sizePerPage < 1) {
			throw new IllegalArgumentException("sizePerPage cannot be less than 1");
		}
		this.sizePerPage = sizePerPage;
		this.collection = list;
	}
	
	/**
	 * 
	 * @return the total elements
	 *
	 * @author RoinujNosde
	 */
	public int getTotalElements() {
		if (collection != null) {
			return collection.size();
		}
		return totalElements;
	}
	
	/**
	 * 
	 * @return the size per page
	 *
	 * @author RoinujNosde
	 */
	public int getSizePerPage() {
		return sizePerPage;
	}
	
	/**
	 * 
	 * @return the minimal index based on the current page
	 *
	 * @author RoinujNosde
	 */
	public int getMinIndex() {
		return getCurrentPage() * getSizePerPage();
	}
	
	/**
	 * 
	 * @return the max index based on the current page
	 *
	 * @author RoinujNosde
	 */
	public int getMaxIndex() {
		return (getCurrentPage() + 1) * getSizePerPage();
	}
	
	/**
	 * 
	 * @return the current page, starting at 0
	 *
	 * @author RoinujNosde
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 *
	 * @return the elements in the current page
	 *
	 * @throws IllegalStateException if the underlining collection is null
	 */
	public @NotNull List<T> getCurrentElements() throws IllegalStateException {
		if (collection == null) {
			throw new IllegalStateException("the collection is null");
		}
		int maxIndex = Math.min(getMaxIndex(), collection.size());
		return collection.subList(getMinIndex(), maxIndex);
	}

	/**
	 * Increases the page number if there are elements to display
	 *
	 * @author RoinujNosde
	 */
	public boolean nextPage() {
		boolean hasNext = hasNextPage();
		if (hasNext) {
			currentPage++;
		}
		return hasNext;
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
	 *
	 * @author RoinujNosde
	 */
	public boolean previousPage() {
		boolean hasPrevious = hasPreviousPage();
		if (hasPrevious) {
			currentPage--;
		}
		return hasPrevious;
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
	 * @author RoinujNosde
	 */
	public boolean isValidIndex(int index) {
        return index >= getMinIndex() && index < getMaxIndex() && index < getTotalElements();
    }
}
