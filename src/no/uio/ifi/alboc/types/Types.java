package no.uio.ifi.alboc.types;

/*
 * module Types
 */

/*
 * Handle AlboC types.
 */

public class Types {
	public static Type intType;

	public static void init() {
		intType = new ValueType() {
			@Override
			public String toString() {
				return "int";
			}

			@Override
			public int size() {
				return 4;
			}

			@Override
			public boolean isSameType(Type t) {
				return this == t;
			}
		};
	}

	public static void finish() {
		// -- Must be changed in part 2:
	}
}
