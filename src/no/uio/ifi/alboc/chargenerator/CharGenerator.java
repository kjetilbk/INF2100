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
			Log.noteSourceLine(0, sourceLine);
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
		return curC != 0;
	}

	public static int curLineNum() {
		return (sourceFile == null ? 0 : sourceFile.getLineNumber());
	}

	public static void readNext() {
		if(sourceLine == null && curC == 0)
				Error.error("Read from null string wtf retard");

		curC = nextC;
		
		while(	(sourceLine != null &&
				(sourcePos == sourceLine.length() ||
				 sourceLine.charAt(0) == '#' ))) {
			try {
				sourceLine = sourceFile.readLine();
				if(sourceLine != null)
					Log.noteSourceLine(curLineNum(), sourceLine);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (sourceLine == null) {
				nextC = 0;
				return;
			}
			sourcePos = 0;
		}
		if(sourceLine != null)
			nextC = sourceLine.charAt(sourcePos);
		sourcePos++;
	}

}
