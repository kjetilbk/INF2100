package no.uio.ifi.alboc.scanner;

/*
 * module Scanner
 */

import no.uio.ifi.alboc.chargenerator.CharGenerator;
import no.uio.ifi.alboc.error.Error;
import no.uio.ifi.alboc.log.Log;
import static no.uio.ifi.alboc.scanner.Token.*;

/*
 * Module for forming characters into tokens.
 */
public class Scanner {
	public static Token curToken, nextToken;
	public static String curName, nextName;
	public static int curNum, nextNum;
	public static int curLine, nextLine;

	public static void init() {
		// -- Must be changed in part 0:
	}

	public static void finish() {
		// Kjetil er dum
		// -- Must be changed in part 0:
	}

	public static void readNext() {
		curToken = nextToken;
		curName = nextName;
		curNum = nextNum;
		curLine = nextLine;

		nextToken = null;
		while (nextToken == null) {
			nextLine = CharGenerator.curLineNum();

			if (!CharGenerator.isMoreToRead()) {
				nextToken = eofToken;
			} else
			// -- Must be changed in part 0:
			{
				Error.error(nextLine, "Illegal symbol: '" + CharGenerator.curC
						+ "'!");
			}
		}
		Log.noteToken();
	}

	private static boolean isLetterAZ(char c) {
		return (c <= 'a' && c >= 'z' ) || (c <= 'A' && c >= 'Z');
	}

	public static void check(Token t) {
		if (curToken != t)
			Error.expected("A " + t);
	}

	public static void check(Token t1, Token t2) {
		if (curToken != t1 && curToken != t2)
			Error.expected("A " + t1 + " or a " + t2);
	}

	public static void skip(Token t) {
		check(t);
		readNext();
	}

	public static void skip(Token t1, Token t2) {
		check(t1, t2);
		readNext();
	}
}
