package no.uio.ifi.alboc.error;

/*
 * module Error
 */

public class AlboCError extends RuntimeException {
	AlboCError(String message) {
		super(message);
	}
}
