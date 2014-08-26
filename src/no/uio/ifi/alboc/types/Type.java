package no.uio.ifi.alboc.types;

public abstract class Type {
	public abstract int size();

	public abstract boolean isSameType(Type t);

	public abstract boolean mayBeIndexed();

	public abstract Type getElemType();
}
