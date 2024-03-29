package no.uio.ifi.alboc.scanner;

/*
 * module Scanner
 */

import no.uio.ifi.alboc.chargenerator.CharGenerator;
import no.uio.ifi.alboc.error.AlboCError;
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
		curToken = null;
		curName = "";
		curNum = -1;
		curLine = 0;
		readNext();
		readNext();
	}

	public static void finish() {
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
			{
				Token token = null;
				while(CharGenerator.isMoreToRead() && (CharGenerator.curC == '\t' || CharGenerator.curC == ' ' || CharGenerator.curC == '\n')) CharGenerator.readNext();
				if(!CharGenerator.isMoreToRead()) {
					token = eofToken;
				}

				/*
				 * Read tokens from CharGenerator and skip /* comments
				 */

				if(token == null) token = Token.checkSingleCharToken(CharGenerator.curC);
				if(token == null) token = Token.checkDoubleCharToken(CharGenerator.curC, CharGenerator.nextC);
				if(token == Token.startCommentToken) {
					CharGenerator.readNext();
					while(true) {
						try { // Try reading to the first end-comment characters. If CharGenerator can't read more, an exception will be caught.
							CharGenerator.readNext();
							if(CharGenerator.curC == '*' && CharGenerator.nextC == '/') {
								CharGenerator.readNext();
								CharGenerator.readNext();
								break;
							}
						} catch (AlboCError e) {
							System.err.println("Comment starting on line " + curLine + " never ends");
							System.exit(4);
						}
					}
					nextLine = CharGenerator.curLineNum();
					continue; // restart parsing of tokens after the comment ends
				}
				if(		token == Token.equalToken || 
						token == Token.greaterEqualToken || 
						token == Token.lessEqualToken ||
						token == Token.notEqualToken)
				{
					CharGenerator.readNext();
				}
				if(isLetterAZ(CharGenerator.curC)) { 
					/*
					 * A name may not start with a digit or an underscore,
					 * however it may contain digits and/or underscores after its first character.
					 */
					String name = "";
					while(isLetterAZ(CharGenerator.curC) || isValidNameChar(CharGenerator.curC) || isDigit(CharGenerator.curC)) {
						name += CharGenerator.curC;
						CharGenerator.readNext();
					}
					nextName = name;
					token = Token.checkStringToken(name); // Check for special tokens like if, for etc.
				}
				else if(CharGenerator.curC == '\'') {
					/*
					 * Read characters enclosed in single-quotes
					 */
					CharGenerator.readNext();
					if(CharGenerator.nextC == '\'') {
						nextNum = (int)CharGenerator.curC;
						token = Token.numberToken;
						CharGenerator.readNext();
						CharGenerator.readNext();
					} else {
						Error.error(curLine, "Illegal character constant");
					}
				} else if(isDigit(CharGenerator.curC)) {
					/*
					 * Read a whole number
					 */
					String num = "";
					while(isDigit(CharGenerator.curC)) {
						num += CharGenerator.curC;
						CharGenerator.readNext();
					}
					nextNum = Integer.parseInt(num);
					token = Token.numberToken;
				} else if (token != null && CharGenerator.isMoreToRead())
					CharGenerator.readNext(); // If there is one, read the next character
				if(token == null)
					Error.error(nextLine, "Illegal symbol: '" + CharGenerator.curC
							+ "'!");

				nextToken = token;
			}
		}
		Log.noteToken();
	}

	private static boolean isDigit(char c) {
		return (c >= '0' && c <= '9');
	}

	private static boolean isLetterAZ(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	private static boolean isValidNameChar(char c) {
		switch(c) {
		case '_':
			return true;
		default:
			return false;
		}
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
