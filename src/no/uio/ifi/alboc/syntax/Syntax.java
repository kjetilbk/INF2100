package no.uio.ifi.alboc.syntax;

/*
 * module Syntax
 */

import no.uio.ifi.alboc.alboc.AlboC;
import no.uio.ifi.alboc.code.Code;
import no.uio.ifi.alboc.error.Error;
import no.uio.ifi.alboc.log.Log;
import no.uio.ifi.alboc.scanner.Scanner;
import no.uio.ifi.alboc.scanner.Token;
import static no.uio.ifi.alboc.scanner.Token.*;
import no.uio.ifi.alboc.types.*;

/*
 * Creates a syntax tree by parsing an AlboC program; 
 * prints the parse tree (if requested);
 * checks it;
 * generates executable code. 
 */
public class Syntax {
	static DeclList library;
	static Program program;

	public static void init() {
		// -- Must be changed in part 1+2:
	}

	public static void finish() {
		// -- Must be changed in part 1+2:
	}

	public static void checkProgram() {
		program.check(library);
	}

	public static void genCode() {
		program.genCode(null);
	}

	public static void parseProgram() {
		program = Program.parse();
	}

	public static void printProgram() {
		program.printTree();
	}
}

/*
 * Super class for all syntactic units. (This class is not mentioned in the
 * syntax diagrams.)
 */
abstract class SyntaxUnit {
	int lineNum;

	SyntaxUnit() {
		lineNum = Scanner.curLine;
	}

	abstract void check(DeclList curDecls);

	abstract void genCode(FuncDecl curFunc);

	abstract void printTree();

	void error(String message) {
		Error.error(lineNum, message);
	}
}

/*
 * A <program>
 */
class Program extends SyntaxUnit {
	DeclList progDecls;

	@Override
	void check(DeclList curDecls) {
		progDecls.check(curDecls);

		if (!AlboC.noLink) {
			// Check that 'main' has been declared properly:
			// -- Must be changed in part 2:
		}
	}

	@Override
	void genCode(FuncDecl curFunc) {
		progDecls.genCode(null);
	}

	static Program parse() {
		Log.enterParser("<program>");

		Program p = new Program();
		p.progDecls = GlobalDeclList.parse();
		if (Scanner.curToken != eofToken)
			Error.expected("A declaration");

		Log.leaveParser("</program>");
		return p;
	}

	@Override
	void printTree() {
		progDecls.printTree();
	}
}

/*
 * A declaration list. (This class is not mentioned in the syntax diagrams.)
 */

abstract class DeclList extends SyntaxUnit {
	Declaration firstDecl = null;
	DeclList outerScope;

	DeclList() {
		// -- Must be changed in part 1:
	}

	@Override
	void check(DeclList curDecls) {
		outerScope = curDecls;

		Declaration dx = firstDecl;
		while (dx != null) {
			dx.check(this);
			dx = dx.nextDecl;
		}
	}

	@Override
	void printTree() {
		// -- Must be changed in part 1:
	}

	void addDecl(Declaration d) {
		// -- Must be changed in part 1:
	}

	int dataSize() {
		Declaration dx = firstDecl;
		int res = 0;

		while (dx != null) {
			res += dx.declSize();
			dx = dx.nextDecl;
		}
		return res;
	}

	Declaration findDecl(String name, SyntaxUnit use) {
		// -- Must be changed in part 2:
		return null;
	}
}

/*
 * A list of global declarations. (This class is not mentioned in the syntax
 * diagrams.)
 */
class GlobalDeclList extends DeclList {
	@Override
	void genCode(FuncDecl curFunc) {
		// -- Must be changed in part 2:
	}

	static GlobalDeclList parse() {
		GlobalDeclList gdl = new GlobalDeclList();

		while (Scanner.curToken == intToken) {
			DeclType ts = DeclType.parse();
			gdl.addDecl(Declaration.parse(ts));
		}
		return gdl;
	}
}

/*
 * A list of local declarations. (This class is not mentioned in the syntax
 * diagrams.)
 */
class LocalDeclList extends DeclList {
	@Override
	void genCode(FuncDecl curFunc) {
		// -- Must be changed in part 2:
	}

	static LocalDeclList parse() {
		// -- Must be changed in part 1:
		return null;
	}
}

/*
 * A list of parameter declarations. (This class is not mentioned in the syntax
 * diagrams.)
 */
class ParamDeclList extends DeclList {
	@Override
	void genCode(FuncDecl curFunc) {
		// -- Must be changed in part 2:
	}

	static ParamDeclList parse() {
		// -- Must be changed in part 1:
		return null;
	}

	@Override
	void printTree() {
		Declaration px = firstDecl;
		while (px != null) {
			px.printTree();
			px = px.nextDecl;
			if (px != null)
				Log.wTree(", ");
		}
	}
}

/*
 * A <type>
 */
class DeclType extends SyntaxUnit {
	int numStars = 0;
	Type type;

	@Override
	void check(DeclList curDecls) {
		type = Types.intType;
		for (int i = 1; i <= numStars; ++i)
			type = new PointerType(type);
	}

	@Override
	void genCode(FuncDecl curFunc) {
	}

	static DeclType parse() {
		Log.enterParser("<type>");

		DeclType dt = new DeclType();

		Scanner.skip(intToken);
		while (Scanner.curToken == starToken) {
			++dt.numStars;
			Scanner.readNext();
		}

		Log.leaveParser("</type>");
		return dt;
	}

	@Override
	void printTree() {
		Log.wTree("int");
		for (int i = 1; i <= numStars; ++i)
			Log.wTree("*");
	}
}

/*
 * Any kind of declaration.
 */
abstract class Declaration extends SyntaxUnit {
	String name, assemblerName;
	DeclType typeSpec;
	Type type;
	boolean visible = false;
	Declaration nextDecl = null;

	Declaration(String n) {
		name = n;
	}

	abstract int declSize();

	static Declaration parse(DeclType dt) {
		Declaration d = null;
		if (Scanner.curToken == nameToken && Scanner.nextToken == leftParToken) {
			d = FuncDecl.parse(dt);
		} else if (Scanner.curToken == nameToken) {
			d = GlobalVarDecl.parse(dt);
		} else {
			Error.expected("A declaration name");
		}
		d.typeSpec = dt;
		return d;
	}

	/**
	 * checkWhetherVariable: Utility method to check whether this Declaration is
	 * really a variable. The compiler must check that a name is used properly;
	 * for instance, using a variable name a in "a()" is illegal. This is
	 * handled in the following way:
	 * <ul>
	 * <li>When a name a is found in a setting which implies that should be a
	 * variable, the parser will first search for a's declaration d.
	 * <li>The parser will call d.checkWhetherVariable(this).
	 * <li>Every sub-class of Declaration will implement a checkWhetherVariable.
	 * If the declaration is indeed a variable, checkWhetherVariable will do
	 * nothing, but if it is not, the method will give an error message.
	 * </ul>
	 * Examples
	 * <dl>
	 * <dt>GlobalVarDecl.checkWhetherVariable(...)</dt>
	 * <dd>will do nothing, as everything is all right.</dd>
	 * <dt>FuncDecl.checkWhetherVariable(...)</dt>
	 * <dd>will give an error message.</dd>
	 * </dl>
	 */
	abstract void checkWhetherVariable(SyntaxUnit use);

	/**
	 * checkWhetherFunction: Utility method to check whether this Declaration is
	 * really a function.
	 * 
	 * @param nParamsUsed
	 *            Number of parameters used in the actual call. (The method will
	 *            give an error message if the function was used with too many
	 *            or too few parameters.)
	 * @param use
	 *            From where is the check performed?
	 * @see checkWhetherVariable
	 */
	abstract void checkWhetherFunction(int nParamsUsed, SyntaxUnit use);
}

/*
 * A <var decl>
 */
abstract class VarDecl extends Declaration {
	boolean isArray = false;
	int numElems = 0;

	VarDecl(String n) {
		super(n);
	}

	@Override
	void check(DeclList curDecls) {
		// -- Must be changed in part 2:
	}

	@Override
	void printTree() {
		// -- Must be changed in part 1:
	}

	@Override
	int declSize() {
		return type.size();
	}

	@Override
	void checkWhetherFunction(int nParamsUsed, SyntaxUnit use) {
		use.error(name + " is a variable and no function!");
	}

	@Override
	void checkWhetherVariable(SyntaxUnit use) {
		// OK
	}
}

/*
 * A global <var decl>.
 */
class GlobalVarDecl extends VarDecl {
	GlobalVarDecl(String n) {
		super(n);
		assemblerName = (AlboC.underscoredGlobals() ? "_" : "") + n;
	}

	@Override
	void genCode(FuncDecl curFunc) {
		// -- Must be changed in part 2:
	}

	static GlobalVarDecl parse(DeclType dt) {
		Log.enterParser("<var decl>");

		// -- Must be changed in part 1:
		return null;
	}
}

/*
 * A local variable declaration
 */
class LocalVarDecl extends VarDecl {
	LocalVarDecl(String n) {
		super(n);
	}

	@Override
	void genCode(FuncDecl curFunc) {
		// -- Must be changed in part 2:
	}

	static LocalVarDecl parse(DeclType dt) {
		Log.enterParser("<var decl>");

		// -- Must be changed in part 1:
		return null;
	}
}

/*
 * A <param decl>
 */
class ParamDecl extends VarDecl {
	ParamDecl(String n) {
		super(n);
	}

	@Override
	void genCode(FuncDecl curFunc) {
		// -- Must be changed in part 2:
	}

	static ParamDecl parse(DeclType dt) {
		Log.enterParser("<param decl>");

		// -- Must be changed in part 1:
		return null;
	}

	@Override
	void printTree() {
		typeSpec.printTree();
		Log.wTree(" " + name);
	}
}

/*
 * A <func decl>
 */
class FuncDecl extends Declaration {
	ParamDeclList funcParams;
	String exitLabel;

	FuncDecl(String n) {
		// Used for user functions:

		super(n);
		assemblerName = (AlboC.underscoredGlobals() ? "_" : "") + n;
		// -- Must be changed in part 1:
	}

	@Override
	int declSize() {
		return 0;
	}

	@Override
	void check(DeclList curDecls) {
		// -- Must be changed in part 2:
	}

	@Override
	void checkWhetherFunction(int nParamsUsed, SyntaxUnit use) {
		// -- Must be changed in part 2:
	}

	@Override
	void checkWhetherVariable(SyntaxUnit use) {
		// -- Must be changed in part 2:
	}

	@Override
	void genCode(FuncDecl curFunc) {
		// -- Must be changed in part 2:
	}

	static FuncDecl parse(DeclType ts) {
		// -- Must be changed in part 1:
		return null;
	}

	@Override
	void printTree() {
		// -- Must be changed in part 1:
	}
}

/*
 * A <statm list>.
 */
class StatmList extends SyntaxUnit {
	// -- Must be changed in part 1:

	@Override
	void check(DeclList curDecls) {
		// -- Must be changed in part 2:
	}

	@Override
	void genCode(FuncDecl curFunc) {
		// -- Must be changed in part 2:
	}

	static StatmList parse() {
		Log.enterParser("<statm list>");

		StatmList sl = new StatmList();
		Statement lastStatm = null;
		while (Scanner.curToken != rightCurlToken) {
			// -- Must be changed in part 1:
		}

		Log.leaveParser("</statm list>");
		return sl;
	}

	@Override
	void printTree() {
		// -- Must be changed in part 1:
	}
}

/*
 * A <statement>.
 */
abstract class Statement extends SyntaxUnit {
	Statement nextStatm = null;

	static Statement parse() {
		Log.enterParser("<statement>");

		Statement s = null;
		if (Scanner.curToken == nameToken && Scanner.nextToken == leftParToken) {
			// -- Must be changed in part 1:
		} else if (Scanner.curToken == nameToken
				|| Scanner.curToken == starToken) {
			// -- Must be changed in part 1:
		} else if (Scanner.curToken == forToken) {
			// -- Must be changed in part 1:
		} else if (Scanner.curToken == ifToken) {
			s = IfStatm.parse();
		} else if (Scanner.curToken == returnToken) {
			// -- Must be changed in part 1:
		} else if (Scanner.curToken == whileToken) {
			s = WhileStatm.parse();
		} else if (Scanner.curToken == semicolonToken) {
			s = EmptyStatm.parse();
		} else {
			Error.expected("A statement");
		}

		Log.leaveParser("</statement>");
		return s;
	}
}

/*
 * An <empty statm>.
 */
class EmptyStatm extends Statement {
	// -- Must be changed in part 1+2:

	@Override
	void check(DeclList curDecls) {
		// -- Must be changed in part 2:
	}

	@Override
	void genCode(FuncDecl curFunc) {
		// -- Must be changed in part 2:
	}

	static EmptyStatm parse() {
		// -- Must be changed in part 1:
		return null;
	}

	@Override
	void printTree() {
		// -- Must be changed in part 1:
	}
}

/*
 * A <for-statm>.
 */

/*
 * An <if-statm>.
 */
class IfStatm extends Statement {
	// -- Must be changed in part 1+2:

	@Override
	void check(DeclList curDecls) {
		// -- Must be changed in part 2:
	}

	@Override
	void genCode(FuncDecl curFunc) {
		// -- Must be changed in part 2:
	}

	static IfStatm parse() {
		// -- Must be changed in part 1:
		return null;
	}

	@Override
	void printTree() {
		// -- Must be changed in part 1:
	}
}

/*
 * A <return-statm>.
 */

/*
 * A <while-statm>.
 */
class WhileStatm extends Statement {
	Expression test;
	StatmList body;

	@Override
	void check(DeclList curDecls) {
		test.check(curDecls);
		body.check(curDecls);

		Log.noteTypeCheck("while (t) ...", test.type, "t", lineNum);
		if (test.type instanceof ValueType) {
			// OK
		} else {
			error("While-test must be a value.");
		}
	}

	@Override
	void genCode(FuncDecl curFunc) {
		String testLabel = Code.getLocalLabel(), endLabel = Code
				.getLocalLabel();

		Code.genInstr(testLabel, "", "", "Start while-statement");
		test.genCode(curFunc);
		Code.genInstr("", "cmpl", "$0,%eax", "");
		Code.genInstr("", "je", endLabel, "");
		body.genCode(curFunc);
		Code.genInstr("", "jmp", testLabel, "");
		Code.genInstr(endLabel, "", "", "End while-statement");
	}

	static WhileStatm parse() {
		Log.enterParser("<while-statm>");

		WhileStatm ws = new WhileStatm();
		Scanner.skip(whileToken);
		Scanner.skip(leftParToken);
		ws.test = Expression.parse();
		Scanner.skip(rightParToken);
		Scanner.skip(leftCurlToken);
		ws.body = StatmList.parse();
		Scanner.skip(rightCurlToken);

		Log.leaveParser("</while-statm>");
		return ws;
	}

	@Override
	void printTree() {
		Log.wTree("while (");
		test.printTree();
		Log.wTreeLn(") {");
		Log.indentTree();
		body.printTree();
		Log.outdentTree();
		Log.wTreeLn("}");
	}
}

/*
 * An <Lhs-variable>
 */

class LhsVariable extends SyntaxUnit {
	int numStars = 0;
	Variable var;
	Type type;

	@Override
	void check(DeclList curDecls) {
		var.check(curDecls);
		type = var.type;
		for (int i = 1; i <= numStars; ++i) {
			Type e = type.getElemType();
			if (e == null)
				error("Type error in left-hand side variable!");
			type = e;
		}
	}

	@Override
	void genCode(FuncDecl curFunc) {
		var.genAddressCode(curFunc);
		for (int i = 1; i <= numStars; ++i)
			Code.genInstr("", "movl", "(%eax),%eax", "  *");
	}

	static LhsVariable parse() {
		Log.enterParser("<lhs-variable>");

		LhsVariable lhs = new LhsVariable();
		while (Scanner.curToken == starToken) {
			++lhs.numStars;
			Scanner.skip(starToken);
		}
		Scanner.check(nameToken);
		lhs.var = Variable.parse();

		Log.leaveParser("</lhs-variable>");
		return lhs;
	}

	@Override
	void printTree() {
		for (int i = 1; i <= numStars; ++i)
			Log.wTree("*");
		var.printTree();
	}
}

/*
 * An <expression list>.
 */

class ExprList extends SyntaxUnit {
	Expression firstExpr = null;

	@Override
	void check(DeclList curDecls) {
		// -- Must be changed in part 2:
	}

	@Override
	void genCode(FuncDecl curFunc) {
		// -- Must be changed in part 2:
	}

	static ExprList parse() {
		Expression lastExpr = null;

		Log.enterParser("<expr list>");

		// -- Must be changed in part 1:
		return null;
	}

	@Override
	void printTree() {
		// -- Must be changed in part 1:
	}
	// -- Must be changed in part 1:
}

/*
 * An <expression>
 */
class Expression extends SyntaxUnit {
	Expression nextExpr = null;
	Term firstTerm, secondTerm = null;
	Operator relOpr = null;
	Type type = null;

	@Override
	void check(DeclList curDecls) {
		// -- Must be changed in part 2:
	}

	@Override
	void genCode(FuncDecl curFunc) {
		// -- Must be changed in part 2:
	}

	static Expression parse() {
		Log.enterParser("<expression>");

		Expression e = new Expression();
		e.firstTerm = Term.parse();
		if (Token.isRelOperator(Scanner.curToken)) {
			e.relOpr = RelOpr.parse();
			e.secondTerm = Term.parse();
		}

		Log.leaveParser("</expression>");
		return e;
	}

	@Override
	void printTree() {
		// -- Must be changed in part 1:
	}
}

/*
 * A <term>
 */
class Term extends SyntaxUnit {
	// -- Must be changed in part 1+2:

	@Override
	void check(DeclList curDecls) {
		// -- Must be changed in part 2:
	}

	@Override
	void genCode(FuncDecl curFunc) {
		// -- Must be changed in part 2:
	}

	static Term parse() {
		// -- Must be changed in part 1:
		return null;
	}

	@Override
	void printTree() {
		// -- Must be changed in part 1:
	}
}

/*
 * An <operator>
 */
abstract class Operator extends SyntaxUnit {
	Operator nextOpr = null;
	Token oprToken;

	@Override
	void check(DeclList curDecls) {
	} // Never needed.
}

/*
 * A <rel opr> (==, !=, <, <=, > or >=).
 */

class RelOpr extends Operator {
	@Override
	void genCode(FuncDecl curFunc) {
		Code.genInstr("", "popl", "%ecx", "");
		Code.genInstr("", "cmpl", "%eax,%ecx", "");
		Code.genInstr("", "movl", "$0,%eax", "");
		switch (oprToken) {
		case equalToken:
			Code.genInstr("", "sete", "%al", "Test ==");
			break;
		case notEqualToken:
			Code.genInstr("", "setne", "%al", "Test !=");
			break;
		case lessToken:
			Code.genInstr("", "setl", "%al", "Test <");
			break;
		case lessEqualToken:
			Code.genInstr("", "setle", "%al", "Test <=");
			break;
		case greaterToken:
			Code.genInstr("", "setg", "%al", "Test >");
			break;
		case greaterEqualToken:
			Code.genInstr("", "setge", "%al", "Test >=");
			break;
		}
	}

	static RelOpr parse() {
		Log.enterParser("<rel opr>");

		RelOpr ro = new RelOpr();
		ro.oprToken = Scanner.curToken;
		Scanner.readNext();

		Log.leaveParser("</rel opr>");
		return ro;
	}

	@Override
	void printTree() {
		String op = "?";
		switch (oprToken) {
		case equalToken:
			op = "==";
			break;
		case notEqualToken:
			op = "!=";
			break;
		case lessToken:
			op = "<";
			break;
		case lessEqualToken:
			op = "<=";
			break;
		case greaterToken:
			op = ">";
			break;
		case greaterEqualToken:
			op = ">=";
			break;
		}
		Log.wTree(" " + op + " ");
	}
}

/*
 * An <operand>
 */
abstract class Operand extends SyntaxUnit {
	Operand nextOperand = null;
	Type type;

	static Operand parse() {
		Log.enterParser("<operand>");

		Operand o = null;
		if (Scanner.curToken == numberToken) {
			o = Number.parse();
		} else if (Scanner.curToken == nameToken
				&& Scanner.nextToken == leftParToken) {
			o = FunctionCall.parse();
		} else if (Scanner.curToken == nameToken) {
			o = Variable.parse();
		} else if (Scanner.curToken == ampToken) {
			o = Address.parse();
		} else if (Scanner.curToken == leftParToken) {
			o = InnerExpr.parse();
		} else {
			Error.expected("An operand");
		}

		Log.leaveParser("</operand>");
		return o;
	}
}

/*
 * A <function call>.
 */
class FunctionCall extends Operand {
	// -- Must be changed in part 1+2:

	@Override
	void check(DeclList curDecls) {
		// -- Must be changed in part 2:
	}

	@Override
	void genCode(FuncDecl curFunc) {
		// -- Must be changed in part 2:
	}

	static FunctionCall parse() {
		// -- Must be changed in part 1:
		return null;
	}

	@Override
	void printTree() {
		// -- Must be changed in part 1:
	}
	// -- Must be changed in part 1+2:
}

/*
 * A <number>.
 */
class Number extends Operand {
	int numVal;

	@Override
	void check(DeclList curDecls) {
		// -- Must be changed in part 2:
	}

	@Override
	void genCode(FuncDecl curFunc) {
		Code.genInstr("", "movl", "$" + numVal + ",%eax", "" + numVal);
	}

	static Number parse() {
		// -- Must be changed in part 1:
		return null;
	}

	@Override
	void printTree() {
		Log.wTree("" + numVal);
	}
}

/*
 * A <variable>.
 */

class Variable extends Operand {
	String varName;
	VarDecl declRef = null;
	Expression index = null;

	@Override
	void check(DeclList curDecls) {
		Declaration d = curDecls.findDecl(varName, this);
		d.checkWhetherVariable(this);
		declRef = (VarDecl) d;

		if (index == null) {
			type = d.type;
		} else {
			index.check(curDecls);
			Log.noteTypeCheck("a[e]", d.type, "a", index.type, "e", lineNum);

			if (index.type == Types.intType) {
				// OK
			} else {
				error("Only integers may be used as index.");
			}
			if (d.type.mayBeIndexed()) {
				// OK
			} else {
				error("Only arrays and pointers may be indexed.");
			}
			type = d.type.getElemType();
		}
	}

	@Override
	void genCode(FuncDecl curFunc) {
		// -- Must be changed in part 2:
	}

	void genAddressCode(FuncDecl curFunc) {
		// Generate code to load the _address_ of the variable
		// rather than its value.

		if (index == null) {
			Code.genInstr("", "leal", declRef.assemblerName + ",%eax", varName);
		} else {
			index.genCode(curFunc);
			if (declRef.type instanceof ArrayType) {
				Code.genInstr("", "leal", declRef.assemblerName + ",%edx",
						varName + "[...]");
			} else {
				Code.genInstr("", "movl", declRef.assemblerName + ",%edx",
						varName + "[...]");
			}
			Code.genInstr("", "leal", "(%edx,%eax,4),%eax", "");
		}
	}

	static Variable parse() {
		Log.enterParser("<variable>");
		// -- Must be changed in part 1:
		return null;
	}

	@Override
	void printTree() {
		// -- Must be changed in part 1:
	}
}

/*
 * An <address>.
 */
class Address extends Operand {
	Variable var;

	@Override
	void check(DeclList curDecls) {
		var.check(curDecls);
		type = new PointerType(var.type);
	}

	@Override
	void genCode(FuncDecl curFunc) {
		var.genAddressCode(curFunc);
	}

	static Address parse() {
		Log.enterParser("<address>");

		Address a = new Address();
		Scanner.skip(ampToken);
		a.var = Variable.parse();

		Log.leaveParser("</address>");
		return a;
	}

	@Override
	void printTree() {
		Log.wTree("&");
		var.printTree();
	}
}

/*
 * An <inner expr>.
 */
class InnerExpr extends Operand {
	Expression expr;

	@Override
	void check(DeclList curDecls) {
		expr.check(curDecls);
		type = expr.type;
	}

	@Override
	void genCode(FuncDecl curFunc) {
		expr.genCode(curFunc);
	}

	static InnerExpr parse() {
		Log.enterParser("<inner expr>");

		InnerExpr ie = new InnerExpr();
		Scanner.skip(leftParToken);
		ie.expr = Expression.parse();
		Scanner.skip(rightParToken);

		Log.leaveParser("</inner expr>");
		return ie;
	}

	@Override
	void printTree() {
		Log.wTree("(");
		expr.printTree();
		Log.wTree(")");
	}
}
