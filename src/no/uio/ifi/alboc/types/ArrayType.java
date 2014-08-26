package no.uio.ifi.alboc.types;

public class ArrayType extends Type {
	private int nElems;

	private Type elemType;

	public ArrayType(Type t, int n) {
		elemType = t;
		nElems = n;
	}

	@Override
	public String toString() {
		return elemType + "[]";
	}

	@Override
	public int size() {
		return nElems * elemType.size();
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
