package no.uio.ifi.alboc.chargenerator;

/*
 * module CharGenerator
 */

import java.io.*;

import no.uio.ifi.alboc.alboc.AlboC;
import no.uio.ifi.alboc.error.Error;
import no.uio.ifi.alboc.log.Log;

/*
 * Module for reading single characters.
 */
public class CharGenerator {
	public static char curC, nextC;
	
	private static LineNumberReader sourceFile = null;
	private static String sourceLine;
	private static int sourcePos;
	
	public static void init() {
		try {
			sourceFile = new LineNumberReader(new FileReader(AlboC.sourceName));
		} catch (FileNotFoundException e) {
			Error.error("Cannot read " + AlboC.sourceName + "!");
		}
		try {
			sourceLine = sourceFile.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sourcePos = 0;
		curC = nextC = ' ';
		readNext();
		readNext();
	}

	public static void finish() {
		if (sourceFile != null) {
			try {
				sourceFile.close();
			} catch (IOException e) {
				Error.error("Could not close source file!");
			}
		}
	}

	public static boolean isMoreToRead() {
		return sourceLine != null;
	}

	public static int curLineNum() {
		return (sourceFile == null ? 0 : sourceFile.getLineNumber());
	}

	public static void readNext() {
		if(sourceLine == null) return;
		
		while(sourcePos == sourceLine.length()) {
			try {
				sourceLine = sourceFile.readLine();
				Log.noteSourceLine(curLineNum(), sourceLine);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (sourceLine == null)
				return;
			sourcePos = 0;
		}
		curC = nextC;
		nextC = sourceLine.charAt(sourcePos);
		sourcePos++;
	}

}
