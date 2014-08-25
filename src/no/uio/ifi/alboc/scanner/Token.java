package no.uio.ifi.alboc.scanner;

/*
 * class Token
 */

/*
 * The different kinds of tokens read by Scanner.
 */
public enum Token {
	addToken, ampToken, assignToken, commaToken, divideToken, elseToken, eofToken, equalToken, forToken, greaterEqualToken, greaterToken, ifToken, intToken, leftBracketToken, leftCurlToken, leftParToken, lessEqualToken, lessToken, nameToken, notEqualToken, numberToken, returnToken, rightBracketToken, rightCurlToken, rightParToken, semicolonToken, starToken, subtractToken, whileToken;

	public static boolean isFactorOperator(Token t) {
		// -- Must be changed in part 0:
		return false;
	}

	public static boolean isTermOperator(Token t) {
		// -- Must be changed in part 0:
		return false;
	}

	public static boolean isPrefixOperator(Token t) {
		// -- Must be changed in part 0:
		return false;
	}

	public static boolean isRelOperator(Token t) {
		// -- Must be changed in part 0:
		return false;
	}

	public static boolean isOperand(Token t) {
		// -- Must be changed in part 0:
		return false;
	}
}
