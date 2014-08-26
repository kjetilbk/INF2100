package no.uio.ifi.alboc.types;

public class PointerType extends ValueType {
	private Type elemType;

	public PointerType(Type t) {
		elemType = t;
	}

	@Override
	public String toString() {
		return elemType + "*";
	}

	@Override
	public int size() {
		return 4;
	}

	@Override
	public boolean isSameType(Type t) {
		return elemType.isSameType(t.getElemType());
	}

	@Override
	public boolean mayBeIndexed() {
		return true;
	}

	@Override
	public Type getElemType() {
		return elemType;
	}
}
