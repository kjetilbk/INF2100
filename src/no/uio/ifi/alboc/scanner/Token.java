package no.uio.ifi.alboc.scanner;

import no.uio.ifi.alboc.chargenerator.CharGenerator;
import no.uio.ifi.alboc.error.Error;
import no.uio.ifi.alboc.log.Log;

/*
 * class Token
 */

/*
 * The different kinds of tokens read by Scanner.
 */
public enum Token {
	addToken, ampToken, assignToken, commaToken, divideToken, elseToken, eofToken, equalToken, forToken, greaterEqualToken,
	greaterToken, ifToken, intToken, leftBracketToken, leftCurlToken, leftParToken, lessEqualToken, lessToken, nameToken, notEqualToken,
	numberToken, returnToken, rightBracketToken, rightCurlToken, rightParToken, semicolonToken, starToken, subtractToken, whileToken,
	startCommentToken, endCommentToken;

	public static Token checkSingleCharToken(char c) {
		switch(c) {
		case '{':
			return leftCurlToken;
		case '}':
			return rightCurlToken;
		case '[':
			return leftBracketToken;
		case ']':
			return rightBracketToken;
		case '(':
			return leftParToken;
		case ')':
			return rightParToken;
		case ';':
			return semicolonToken;
		case ',':
			return commaToken;
		case '+':
			return addToken;
		case '-':
			return subtractToken;
		case '#':
		default:
			return null;
		}
	}
	
	public static Token checkDoubleCharToken(char cur, char next) {
		switch(cur) {
		case '=':
			if(next == '=') return equalToken;
			else return assignToken;
		case '>':
			if(next == '=') return greaterEqualToken;
			else return greaterToken;
		case '<':
			if(next == '=') return lessEqualToken;
			else return lessToken;
		case '!':
			if(next == '=') return notEqualToken;
			else Error.error(CharGenerator.curLineNum(), "Expected '=', got " + next + "!");
		case '/':
			if(next == '*') return startCommentToken;
			else return divideToken;
		case '*':
			if(next == '/') return endCommentToken;
			else return starToken;
		default:
			return null;
		}
	}
	
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
