package no.uio.ifi.alboc.types;

public abstract class ValueType extends Type {
	@Override
	public boolean mayBeIndexed() {
		return false; // Most value types may not.
	}

	@Override
	public Type getElemType() {
		return null; // Most value types have none.
	}
}
