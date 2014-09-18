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
	startCommentToken;

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
		case '*':
			return starToken;
		case '&':
			return ampToken;
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
		case '/':
			if(next == '*') return startCommentToken;
			else return divideToken;
		case '!':
			if(next == '=') return notEqualToken;
			else Error.error(CharGenerator.curLineNum(), "Expected '=', got " + next + "!");
		default:
			return null;
		}
	}
	
	public static Token checkStringToken(String str) {
		if(str.compareTo("return") == 0) return returnToken;
		else if(str.compareTo("int") == 0) return intToken;
		else if(str.compareTo("while") == 0) return whileToken;
		else if(str.compareTo("if") == 0) return ifToken;
		else if(str.compareTo("else") == 0) return elseToken;
		else if(str.compareTo("for") == 0) return forToken;
		return nameToken;
	}
	
	public static boolean isFactorOperator(Token t) {
		return (t == starToken || t == divideToken);
	}

	public static boolean isTermOperator(Token t) {
		return (t == addToken || t == subtractToken);
	}

	public static boolean isPrefixOperator(Token t) {
		return (t == starToken || t == subtractToken);
	}

	public static boolean isRelOperator(Token t) {
		return (t == equalToken 	|| 	t == notEqualToken ||
				t == greaterToken 	|| 	t == greaterEqualToken ||
				t == lessToken 		|| 	t == lessEqualToken);
	}

	public static boolean isOperand(Token t) {
		return (t == ampToken || t == nameToken || t == numberToken);
	}
}
