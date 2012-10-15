package de.raphaelmichel.lendlist;

// based on android.util.Pair

public class MutableTriplet<F, S, T> {
    public F first;
    public S second;
    public T third;

    /**
     * Constructor for a Pair. If either are null then equals() and hashCode() will throw
     * a NullPointerException.
     * @param first the first object in the Pair
     * @param second the second object in the pair
     */
    public MutableTriplet(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		result = prime * result + ((third == null) ? 0 : third.hashCode());
		return result;
	}
    
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof MutableTriplet)) return false;
        final MutableTriplet<F, S, T> other;
        try {
            other = (MutableTriplet<F, S, T>) o;
        } catch (ClassCastException e) {
            return false;
        }
        return first.equals(other.first) && second.equals(other.second) && third.equals(other.third);
    }

}
