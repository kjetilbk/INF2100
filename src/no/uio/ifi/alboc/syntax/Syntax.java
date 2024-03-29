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

	public static FuncDecl genFuncDecl(String signature) {

		int prtIndex = signature.indexOf('(');
		
		String []decl = signature.substring(0, prtIndex).split("\\s");
		
		String type = decl[0];
		Type t = Types.intType;
		for(int n = 0; n < type.length(); n++) {
			if(type.charAt(n) == '*')
				t = new PointerType(t);
		}
		String name = decl[1];
		
		FuncDecl fd = new FuncDecl(name);
		
		fd.type = t;

		String param = signature.substring(++prtIndex, signature.length()-1);
		String [] params = param.split(",");
		fd.funcParams = new ParamDeclList();
		
		if(param.equals(""))
			return fd;
		
		for (String s : params) {
			fd.funcParams.size(fd.funcParams.size()+1);
			String [] var = s.split("\\s");
			ParamDecl pd = new ParamDecl(var[1]);
			DeclType dt = new DeclType();
			dt.numStars = 0;
			dt.check(null);
			pd.typeSpec = dt;
			pd.type = dt.type;
			pd.isArray = false;
			fd.funcParams.addDecl(pd);
		}
		
		return fd;
	}
	
	public static void init() {
		// TODO Fiks library
		
		library = new GlobalDeclList();
		library.addDecl(genFuncDecl("int putchar(int char)"));
		library.addDecl(genFuncDecl("int putint(int char)"));
		library.addDecl(genFuncDecl("int getchar()"));
		library.addDecl(genFuncDecl("int getint()"));
		library.addDecl(genFuncDecl("int exit(int status)"));
	}

	public static void finish() {
		// -- Must be changed in part 2:
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
			Declaration main = progDecls.findDecl("main", this);
			main.checkWhetherFunction(0, this);
			if(main.type != Types.intType) {
				this.error("Main function should be of type int!");
			}
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


/**
 * 
 * @author vetlebr/kjetilbk
 *
 * @param <_Ty1>
 * @param <_Ty2>
 * 
 * A generic pair class
 * 
 */
class Pair <_Ty1, _Ty2> {
	_Ty1 first;
	_Ty2 second;
	
	public Pair(_Ty1 first, _Ty2 second) {
		this.first = first;
		this.second = second;
	}
}

class GenericNode<_Ty> {
	_Ty data;
	
	GenericNode<_Ty> next, prev;
}

/**
 * 
 * @author vetlebr/kjetilbk
 *
 * @param <_Ty> A generic parameter
 * A generic iterator for looping through the GenericList.
 */

class GenericIt<_Ty> {
	GenericNode<_Ty> cur;
	
	boolean hasNext() {return cur != null;}
	
	_Ty next() {
		_Ty ret = cur.data;
		cur = cur.next;
		return ret;
	}
	
	GenericIt(GenericNode<_Ty> n) {cur = n;}
}
class GenericRevIt<_Ty> {
	GenericNode<_Ty> cur;
	
	boolean hasNext() {return cur != null;}
	
	_Ty next() {
		_Ty ret = cur.data;
		cur = cur.prev;
		return ret;
	}
	
	GenericRevIt(GenericNode<_Ty> n) {cur = n;}
}

/**
 * A generic linked list for use in Factor, Term etc.
 * 
 * @author vetlebr/kjetilbk
 * 
 * @param <_Ty>
 * 
 */
class GenericList<_Ty> {
	
	GenericNode<_Ty> first, last;
	
	private int _size = 0;
	
	public int size() {return _size;}
	
	public void add(_Ty p) {
		
		GenericNode<_Ty> node = new GenericNode<_Ty>();
		
		node.data = p;
		
		if(first == null) {
			first = node;
			last = first;
		} else {
			last.next = node;
			node.prev = last;
			last = last.next;
		}
		_size++;
		
	}
	
	GenericIt<_Ty> iterator() {return new GenericIt<_Ty>(first);}
	GenericRevIt<_Ty> reverseIterator() {return new GenericRevIt<_Ty>(last);}
	
}
/*
 * A declaration list. (This class is not mentioned in the syntax diagrams.)
 */
abstract class DeclList extends SyntaxUnit {
	Declaration firstDecl = null;
	DeclList outerScope;
	
	DeclList() {
	}

	int indexOf(Declaration d) {
		int curInd = 0;
		for(Declaration cur = firstDecl; cur != null; cur = cur.nextDecl) {
			if(cur == d) 
				return curInd + d.declSize();
			curInd += cur.declSize();
		}
		return -1;
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
		for(Declaration cur = firstDecl; cur != null; cur = cur.nextDecl)
			cur.printTree();
	}

	void addDecl(Declaration d) {
		//d.check(this);
		if(firstDecl == null) {
			firstDecl = d;
		} else {
			Declaration putDecl = firstDecl;
			while(putDecl.nextDecl != null)
				putDecl = putDecl.nextDecl;
			putDecl.nextDecl = d;
		}
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

	/**
	 * Looper gjennom alle deklarasjoner for å finne deklarasjonen <name>.
	 * Finner den ikke, sjekker den outerScope. etc.
	 * 
	 * @param name name of declaration you want.
	 * @param use current SyntaxUnit(for error throwing).
	 * @return the declaration you're looking for
	 */
	Declaration findDecl(String name, SyntaxUnit use) {
		DeclList curList = this;
		while(curList != null) {
			for(Declaration decl = curList.firstDecl; decl != null; decl = decl.nextDecl)
				if(decl.name.equals(name)) {
					int declLine = decl.lineNum;
					if(curList.outerScope == null) //If outerscope is null we are inside the library declarations
						declLine = -1;
					Log.noteBinding(name, use.lineNum, declLine);
					return decl;
				}
			curList = curList.outerScope;
		}
		use.error("Could not find declaration " + name + "!");
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
		for(Declaration d = firstDecl; d != null; d = d.nextDecl) {
			d.genCode(curFunc);
		}
	}

	static GlobalDeclList parse() {
		GlobalDeclList gdl = new GlobalDeclList();

		
		while (Scanner.curToken == intToken) {
			DeclType ts = DeclType.parse();
			gdl.addDecl(Declaration.parse(ts, true));
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
		// ok
	}

	static LocalDeclList parse() {
		
		LocalDeclList list = new LocalDeclList();
		
		while(Scanner.curToken == Token.intToken) {
			DeclType dt = DeclType.parse();
			Declaration d = Declaration.parse(dt, false);
			list.addDecl(d);
			
		}
		return list;
	}
}

/*
 * A list of parameter declarations. (This class is not mentioned in the syntax
 * diagrams.)
 */
class ParamDeclList extends DeclList {
	private int size = 0;
	
	public int size() {
		return size;
	}
	
	public void size(int size) {
		this.size = size;
	}

	@Override
	void genCode(FuncDecl curFunc) {
		// Ok
	}
	
	static ParamDeclList parse() {
		
		ParamDeclList list = new ParamDeclList();
		
		Scanner.readNext();
		Scanner.readNext();
		
		while(Scanner.curToken != Token.rightParToken) {
			
			list.size++;
			
			DeclType dt = DeclType.parse();
			
			ParamDecl pd = ParamDecl.parse(dt);
			
			pd.typeSpec = dt;
			pd.type = dt.type;
			
			list.addDecl(pd);
			
			if(Scanner.curToken == commaToken) {
				if(Scanner.nextToken == Token.rightParToken)
					Error.expected("An operand");
				Scanner.readNext();
			}
		}
		
		Scanner.readNext();
		return list;
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
		// Ok :|
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
		
		dt.check(null);
		
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

	static boolean checkName(DeclList curDecls, Declaration curDecl) {
		Declaration cur = curDecls.firstDecl;
		while(cur != null) {
			if(cur == curDecl) {
				cur = cur.nextDecl;
				continue;
			}
			if(curDecl.name.equals(cur.name))
				return false;
			cur = cur.nextDecl;
		}
		return true;
	}
	
	static Declaration parse(DeclType dt, boolean isGlobal) {
		Declaration d = null;
		
		if(dt == null) Error.error("dt er null");
		
		if (Scanner.curToken == Token.nameToken && Scanner.nextToken == Token.leftParToken) {
			d = FuncDecl.parse(dt);
		} else if (Scanner.curToken == Token.nameToken) {
			if(isGlobal) d = GlobalVarDecl.parse(dt);
			else d = LocalVarDecl.parse(dt);
		} else {
			Error.expected("A declaration name");
		}
		d.type = dt.type;
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
		visible = true;
		Declaration cur = curDecls.firstDecl;
		if(isArray)
			type = new ArrayType(typeSpec.type, numElems);
		else
			type = typeSpec.type;
		while(cur != null) {
			if(cur == this) {
				return;
			}
			if(this.name.equals(cur.name))
				Error.error(lineNum, "Name " + cur.name + " already declared!");
			cur = cur.nextDecl;
		}
	}

	@Override
	void printTree() {
		typeSpec.printTree(); Log.wTreeLn(" " + name + ";");
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
		Code.genVar(this.assemblerName, true, (this.isArray ? this.numElems : 1), 4, "int" + (this.isArray ? "[] " : " ") + this.name);
	}

	static GlobalVarDecl parse(DeclType dt) {
		Log.enterParser("<var decl>");

		GlobalVarDecl var = new GlobalVarDecl(Scanner.curName);
		
		Scanner.skip(Token.nameToken);
		
		if(Scanner.curToken == Token.leftBracketToken) {
			var.isArray = true;
			Scanner.readNext();
			
			var.numElems = Scanner.curNum;
			
			Scanner.skip(Token.numberToken);
			Scanner.skip(Token.rightBracketToken);
		}
		
		Scanner.skip(Token.semicolonToken);
		
		Log.leaveParser("</var decl>");
		
		return var;
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
		// Ok
	}

	static LocalVarDecl parse(DeclType dt) {
		Log.enterParser("<var decl>");

		LocalVarDecl var = new LocalVarDecl(Scanner.curName);
		
		Scanner.skip(Token.nameToken);
		
		if(Scanner.curToken == Token.leftBracketToken) {
			var.isArray = true;
			Scanner.readNext();
			
			var.numElems = Scanner.curNum;
			
			Scanner.skip(Token.numberToken);
			Scanner.skip(Token.rightBracketToken);
		}
		
		Scanner.skip(Token.semicolonToken);
		
		Log.leaveParser("</var decl>");
		
		return var;
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
		// ok
	}

	static ParamDecl parse(DeclType dt) {
		Log.enterParser("<param decl>");

		ParamDecl pd = null;
		
		if(Scanner.curToken == Token.nameToken) {
			pd = new ParamDecl(Scanner.curName);
		} else {
			Error.expected("nameToken");
		}

		Scanner.skip(nameToken);
		Log.leaveParser("</param decl>");

		pd.typeSpec = dt;
		
		return pd;
	}

	@Override
	void printTree() {
		this.typeSpec.printTree();
		Log.wTree(" " + name);
	}
}

/*
 * A <func decl>
 */
class FuncDecl extends Declaration {
	ParamDeclList funcParams;
	String exitLabel;

	LocalDeclList lList = new LocalDeclList();
	StatmList sList = new StatmList();
	FuncDecl(String n) {
		// Used for user functions:

		super(n);
		assemblerName = (AlboC.underscoredGlobals() ? "_" : "") + n;
	}

	@Override
	int declSize() {
		return 0;
	}

	@Override
	void check(DeclList curDecls) {
		type = typeSpec.type;
		
		if(!Declaration.checkName(curDecls, this)) {
			if(curDecls.findDecl(name, this).visible)
				Error.error(this.lineNum, " Function " + name + " already declared!");
		}

		visible = true;
		
		funcParams.check(curDecls);
		lList.check(funcParams);
		sList.check(lList);
		
		for(Statement s = sList.firstStatm;s != null; s = s.nextStatm) {
			if(s instanceof ReturnStatm) {
				if( ((ReturnStatm) s).expr.type != type)
					Error.error(s.lineNum, "Return value with wrong type");
			}
		}
	}

	String getVarLabel(Declaration d) {
		int paramPos = funcParams.indexOf(d);
		int localPos = lList.indexOf(d);
		if(paramPos != -1) {
			int stackPtr = paramPos + 4;
			return stackPtr + "(%ebp)";
		} else if (localPos != -1) {
			int stackPtr = localPos * -1;
			return stackPtr + "(%ebp)";
		} else {
			return d.name;
		}
	}
	
	@Override
	void checkWhetherFunction(int nParamsUsed, SyntaxUnit use) {
		if(nParamsUsed != funcParams.size()) {
			use.error("Function " + name + " should have " + nParamsUsed + " parameters!");
		}
	}

	@Override
	void checkWhetherVariable(SyntaxUnit use) {
		use.error(name + " is not a variable!");
	}

	@Override
	void genCode(FuncDecl curFunc) {
		Code.genInstr("", ".globl " + this.name, "", "");
		Code.genInstr(this.name, "enter", "$" + lList.dataSize() + ",$0", "Start function " + this.name);
		lList.genCode(this);
		sList.genCode(this);
		Code.genInstr(".exit$" + this.name, "", "", "");
		Code.genInstr("", "leave", "", "");
		Code.genInstr("", "ret", "", "End function " + this.name);
	}

	static FuncDecl parse(DeclType ts) {
		Log.enterParser("<func decl>");
		
		FuncDecl decl = new FuncDecl(Scanner.curName);
		
		decl.funcParams = ParamDeclList.parse();
		decl.typeSpec = ts;
		
		Scanner.skip(Token.leftCurlToken);
		
		Log.enterParser("<func body>");
		
		decl.lList = LocalDeclList.parse();
		
		decl.sList = StatmList.parse();
		
		Log.leaveParser("</func body>");
		Log.leaveParser("</func decl>");
		
		Scanner.readNext();
		
		return decl;
	}

	@Override
	void printTree() {
		Log.wTree(exitLabel + " " + name);
		Log.wTree("("); funcParams.printTree(); Log.wTreeLn(")");
		Log.wTreeLn("{");
		Log.indentTree();
		lList.printTree();
		Log.wTreeLn();
		sList.printTree();
		Log.outdentTree();
		Log.wTreeLn("}");
	}
}

class Assignment extends SyntaxUnit {

	LhsVariable var = null;
	Expression expr = null;
	@Override
	void check(DeclList curDecls) {
		var.check(curDecls);
		expr.check(curDecls);
		
		if(!(var.type instanceof ValueType && (var.type.isSameType(expr.type) || expr.type == Types.intType)))
			Error.error(lineNum, "Assignment type mismatch");
	}

	@Override
	void genCode(FuncDecl curFunc) {
		//String lhsLabel = curFunc.getVarLabel(var.var.declRef);
		var.genCode(curFunc);
		Code.genInstr("", "pushl", "%eax", "");
		
		expr.genCode(curFunc);

		Code.genInstr("", "popl", "%edx", "");
		Code.genInstr("", "movl", "%eax,(%edx)", "  =");
		
	}

	@Override
	void printTree() {
		var.printTree(); Log.wTree(" = "); expr.printTree();
	}
	
	static Assignment parse() {
		
		Log.enterParser("<assignment>");
		
		Assignment asn = new Assignment();
		
		asn.var = LhsVariable.parse();
		
		Scanner.skip(Token.assignToken);
		
		asn.expr = Expression.parse();
		
		Log.leaveParser("</assignment>");
		
		return asn;
	}
	
}

/*
 * A <statm list>.
 */
class StatmList extends SyntaxUnit {
	Statement firstStatm;

	StatmList() {
		super();
		firstStatm = null;
	}
	
	void addStatement(Statement st) {
		if(firstStatm == null)
			firstStatm = st;
		else {
			Statement putStatm = firstStatm;
			while(putStatm.nextStatm != null)
				putStatm = putStatm.nextStatm;
			putStatm.nextStatm = st;
		}
	}
	
	@Override
	void check(DeclList curDecls) {
		for(Statement statm = firstStatm; statm != null; statm = statm.nextStatm)
			statm.check(curDecls);
	}

	@Override
	void genCode(FuncDecl curFunc) {
		for(Statement s = firstStatm; s != null; s = s.nextStatm) {
			s.genCode(curFunc);
		}
	}

	static StatmList parse() {
		Log.enterParser("<statm list>");
		
		StatmList sl = new StatmList();
		while (Scanner.curToken != Token.rightCurlToken) {
			Statement st = Statement.parse();
			sl.addStatement(st);
		}

		Log.leaveParser("</statm list>");
		return sl;
	}

	@Override
	void printTree() {
		for(Statement cur = firstStatm; cur != null; cur = cur.nextStatm) {
			cur.printTree();
		}
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
			s = CallStatm.parse();
		} else if (Scanner.curToken == nameToken
				|| Scanner.curToken == starToken) {
			s = AssignStatm.parse();
		} else if (Scanner.curToken == forToken) {
			s = ForStatm.parse();
		} else if (Scanner.curToken == ifToken) {
			s = IfStatm.parse();
		} else if (Scanner.curToken == returnToken) {
			s = ReturnStatm.parse();
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
 * A <call statm>.
 */

class CallStatm extends Statement {

	FunctionCall fnc = null;
	
	@Override
	void check(DeclList curDecls) {

		fnc.check(curDecls);
		
	}

	@Override
	void genCode(FuncDecl curFunc) {
		fnc.genCode(curFunc);
	}

	@Override
	void printTree() {
		fnc.printTree(); Log.wTreeLn(";");
	}
	
	static CallStatm parse() {
		Log.enterParser("<call-statm>");
		
		CallStatm st = new CallStatm();
		
		st.fnc = FunctionCall.parse();
		
		Scanner.skip(Token.semicolonToken);
		
		Log.leaveParser("</call-statm>");
		
		return st;
	}
	
}

/*
 * An <empty statm>.
 */
class EmptyStatm extends Statement {
	// -- Must be changed in part 2:

	@Override
	void check(DeclList curDecls) {
		// OK
	}

	@Override
	void genCode(FuncDecl curFunc) {
	}

	static EmptyStatm parse() {
		Log.enterParser("<empty-statm>");
		Scanner.skip(Token.semicolonToken);
		Log.leaveParser("</empty-statm>");
		return new EmptyStatm();
	}

	@Override
	void printTree() {
		Log.wTreeLn(";");
	}
}

/*
 * An <assign-statm>
 */

class AssignStatm extends Statement {

	Assignment asn = null;
	
	@Override
	void check(DeclList curDecls) {
		
		asn.check(curDecls);
		
	}

	@Override
	void genCode(FuncDecl curFunc) {
		asn.genCode(curFunc);
		
	}

	@Override
	void printTree() {
		asn.printTree(); Log.wTreeLn(";");
		
	}
	
	static AssignStatm parse() {
		Log.enterParser("<assign-statm>");
		
		AssignStatm asnstm = new AssignStatm();
		
		asnstm.asn = Assignment.parse();
		
		Scanner.skip(Token.semicolonToken);
		
		Log.leaveParser("</assign-statm>");
		
		return asnstm;
	}
	
}

/*
 * A <for-statm>.
 */

class ForStatm extends Statement {

	ForControl fc = null;
	
	StatmList list = null;
	
	static class ForControl {
		Assignment init = null, incdec = null;
		
		Expression cond = null;
		
		int lineNum;
		
		static ForControl parse() {
			
			ForControl fc = new ForControl();
			
			fc.init = Assignment.parse();
			Scanner.skip(Token.semicolonToken);
			
			fc.cond = Expression.parse();
			Scanner.skip(Token.semicolonToken);
			
			fc.incdec = Assignment.parse();
			
			return fc;
			
		}
		
		void printTree() {
			init.printTree(); Log.wTree("; ");
			cond.printTree(); Log.wTree("; ");
			incdec.printTree();
		}
		
		void check(DeclList curDecls) {
			init.check(curDecls);
			incdec.check(curDecls);
			cond.check(curDecls);
			if(cond.type instanceof ArrayType)
				Error.error("Line statisk klassehelvete: For condition cannot be of array type");
		}
	}
	
	@Override
	void check(DeclList curDecls) {
		fc.check(curDecls);
		list.check(curDecls);
	}

	@Override
	void genCode(FuncDecl curFunc) {
		String testLabel = Code.getLocalLabel();
		String endLabel = Code.getLocalLabel();

		Code.genInstr("", "", "", "Start for-statement");
		
		fc.init.genCode(curFunc);
		Code.genInstr(testLabel, "", "", "");
		fc.cond.genCode(curFunc);
		Code.genInstr("", "cmpl", "$0,%eax", "");
		Code.genInstr("", "je", endLabel, "");
		list.genCode(curFunc);
		fc.incdec.genCode(curFunc);
		Code.genInstr("", "jmp", testLabel, "");
		Code.genInstr(endLabel, "", "", "End for-statement");
	}

	@Override
	void printTree() {
		Log.wTree("for("); fc.printTree(); Log.wTreeLn(") {");
		Log.indentTree();
		list.printTree();
		Log.outdentTree(); Log.wTreeLn("}");
	}
	
	static ForStatm parse() {
		Log.enterParser("<for-statm>");
		
		Scanner.readNext();
		ForStatm st = new ForStatm();
		
		Scanner.skip(Token.leftParToken);
		st.fc = ForControl.parse();
		Scanner.skip(Token.rightParToken);
		
		Scanner.skip(Token.leftCurlToken);
		st.list = StatmList.parse();
		Scanner.skip(Token.rightCurlToken);
		Log.leaveParser("</for-statm>");
		
		return st;
	}
	
}

/*
 * An <if-statm>.
 */
class IfStatm extends Statement {
	Expression test = null;
	StatmList list = null;
	ElsePart elsep = null;

	static class ElsePart {
		StatmList list = null;
		
		static ElsePart parse() {
			Log.enterParser("<else-part>");
			ElsePart ep = new ElsePart();
			Scanner.readNext();
			
			Scanner.skip(Token.leftCurlToken);
			ep.list = StatmList.parse();
			Scanner.skip(Token.rightCurlToken);
			
			Log.leaveParser("</else-part>");
			
			return ep;
		}
		
		void printTree() {
			list.printTree();
		}
		
		void check(DeclList curDecls) {
			list.check(curDecls);
		}
	}
	
	@Override
	void check(DeclList curDecls) {
		test.check(curDecls);
		list.check(curDecls);
		if(elsep != null)
			elsep.check(curDecls);
		if(test.type instanceof ArrayType)
			Error.error(lineNum, "If-test can not be of array-type");
	}

	@Override
	void genCode(FuncDecl curFunc) {
		String exitLabel = Code.getLocalLabel();
		String elseLabel = (elsep != null ? Code.getLocalLabel() : null);
		
		Code.genInstr("", "", "", "Start if-statement");
		
		test.genCode(curFunc);

		Code.genInstr("", "cmpl", "$0,%eax", "");
		if(elseLabel != null) {
			Code.genInstr("", "je", elseLabel, "");
		} else {
			Code.genInstr("", "je", exitLabel, "");
		}
		
		list.genCode(curFunc);
		
		if(elsep != null) {
			Code.genInstr("", "jmp", exitLabel, "");
			Code.genInstr(elseLabel, "", "", "else-part");
			elsep.list.genCode(curFunc);
		}
		Code.genInstr(exitLabel, "", "", "End if-statement");
		
	}

	static IfStatm parse() {
		Log.enterParser("<if-statm>");
		
		IfStatm st = new IfStatm();
		
		Scanner.readNext();
		
		Scanner.skip(Token.leftParToken);
		st.test = Expression.parse();
		Scanner.skip(Token.rightParToken);
		
		Scanner.skip(Token.leftCurlToken);
		st.list = StatmList.parse();
		Scanner.skip(Token.rightCurlToken);
		
		if(Scanner.curToken == Token.elseToken) {
			st.elsep = ElsePart.parse();
		}
		
		Log.leaveParser("</if-statm>");
		
		return st;
	}

	@Override
	void printTree() {
		Log.wTree("if("); test.printTree(); Log.wTreeLn(") {");
		Log.indentTree();
		list.printTree();
		Log.outdentTree(); Log.wTreeLn("}");
		if(elsep != null) {
			Log.wTreeLn("else {");
			Log.indentTree();
			elsep.printTree();
			Log.outdentTree(); Log.wTreeLn("}");
		}
	}
}

/*
 * A <return-statm>.
 */

class ReturnStatm extends Statement {
	Expression expr;

	static ReturnStatm parse() {
		Log.enterParser("<return-statm>");
		ReturnStatm ret = new ReturnStatm();
		
		Scanner.readNext();
		
		ret.expr = Expression.parse();
		
		Scanner.skip(Token.semicolonToken);
		
		Log.leaveParser("</return-statm>");
		
		return ret;
	}
	
	@Override
	void check(DeclList curDecls) {
		expr.check(curDecls);
	}

	@Override
	void genCode(FuncDecl curFunc) {
		expr.genCode(curFunc);
		Code.genInstr("", "jmp", ".exit$" + curFunc.name, "Return-statement");
	}

	@Override
	void printTree() {
		Log.wTree("return "); expr.printTree(); Log.wTreeLn(";");
	}
}

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
			Log.enterParser("<star>");
			Scanner.skip(starToken);
			Log.leaveParser("<star>");
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
	GenericList<Expression> list = new GenericList<Expression>();

	@Override
	void check(DeclList curDecls) {
		GenericIt<Expression> it = list.iterator();
		
		while(it.hasNext()) {
			Expression e = it.next();
			e.check(curDecls);
		}
	}

	@Override
	void genCode(FuncDecl curFunc) {
		GenericRevIt<Expression> it = list.reverseIterator();
		
		int index = list.size();
		
		while(it.hasNext()) {
			Expression e = it.next();
			
			e.genCode(curFunc);
			Code.genInstr("", "pushl", "%eax", "Push parameter #" + index--);
		}
	}

	static ExprList parse() {

		Log.enterParser("<expr list>");

		ExprList exList = new ExprList();
		
		while(Scanner.curToken != Token.rightParToken) {
			Expression expr = Expression.parse();
			exList.list.add(expr);
			if(Scanner.curToken == commaToken) {
				if(Scanner.nextToken == Token.rightParToken)
					Error.expected("An operand");
				Scanner.readNext();
				continue;
			}
			Scanner.check(Token.rightParToken);
		}
		
		Log.leaveParser("</expr list>");
		return exList;
	}

	@Override
	void printTree() {
		GenericIt<Expression> iter = list.iterator();
		
		while(iter.hasNext()) {
			Expression e = iter.next();
			e.printTree();
			if(iter.hasNext())
				Log.wTree(", ");
		}
	}
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
		firstTerm.check(curDecls);
		type = firstTerm.getType();
		
		if(relOpr != null) {
			secondTerm.check(curDecls);
			relOpr.check(curDecls);
			
			// Sjekker krav 1 i tabell 6.3
			if(relOpr.oprToken == Token.equalToken || relOpr.oprToken == Token.notEqualToken) {
				if(!(firstTerm.getType() instanceof ValueType && secondTerm.getType() instanceof ValueType
						&& (firstTerm.getType().isSameType(secondTerm.getType())
						|| firstTerm.getType() == Types.intType
						|| secondTerm.getType() == Types.intType)))
					Error.error(lineNum, "Type mismatch for rel operator");
			// Sjekker krav 2 i tabell 6.3
			} else if (!(firstTerm.getType() == Types.intType
					&& secondTerm.getType() == Types.intType)) {
				Error.error(lineNum, "Type mismatch for rel operator");
			}
			type = Types.intType;
		}
		
		
		if(nextExpr != null)
			nextExpr.check(curDecls);
	}

	@Override
	void genCode(FuncDecl curFunc) {
		firstTerm.genCode(curFunc);
		if(relOpr != null) {
			Code.genInstr("", "pushl", "%eax", "");
			secondTerm.genCode(curFunc);
			relOpr.genCode(curFunc);
		}
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
		firstTerm.printTree();
		if(relOpr != null) {
			relOpr.printTree(); secondTerm.printTree();
		}
	}
}

/*
 * A <term>
 */
class Term extends SyntaxUnit {
	Type getType() {
		if(list.size() > 0)
			return list.first.data.first.getType();
		return null;
	}

	GenericList<Pair<Factor, TermOpr>> list = new GenericList<Pair<Factor, TermOpr>>();
	
	@Override
	void check(DeclList curDecls) {
		GenericIt<Pair<Factor, TermOpr>> it = list.iterator();
		
		while(it.hasNext()) {
			Pair<Factor, TermOpr> p = it.next();
			
			p.first.check(curDecls);
			
			if(p.second != null)
				p.second.check(curDecls);
			
			if(list.size() > 1 && p.first.getType() != Types.intType)
				Error.error(lineNum, "Terms must be of int type");
		}
	}

	@Override
	void genCode(FuncDecl curFunc) {
		GenericIt<Pair<Factor, TermOpr>> it = list.iterator();
		
		Pair<Factor, TermOpr> p = it.next();
		p.first.genCode(curFunc);
		
		while(p.second != null) {
			Code.genInstr("", "pushl", "%eax", "");
			TermOpr operator = p.second;
			p = it.next();
			p.first.genCode(curFunc);
			operator.genCode(curFunc);
		}
	}

	static Term parse() {
		Log.enterParser("<term>");
		
		Term t = new Term();
		
		Factor f = Factor.parse();
		
		t.list.add(new Pair<Factor,TermOpr>(f, null));
		
		while(Token.isTermOperator(Scanner.curToken)) {
			TermOpr to = TermOpr.parse();
			
			t.list.last.data.second = to;
			
			Factor fak = Factor.parse();
			
			t.list.add(new Pair<Factor, TermOpr>(fak, null));
		}
		
		Log.leaveParser("</term>");
		return t;
	}

	@Override
	void printTree() {
		GenericIt<Pair<Factor, TermOpr>> it = list.iterator();
		while(it.hasNext()) {
			Pair<Factor, TermOpr> cur = it.next();
			cur.first.printTree();
			if(cur.second != null) cur.second.printTree();
		}
	}
}

/*
 * A <factor>
 */

class Factor extends SyntaxUnit {
	
	Type getType() {
		if(list.size() > 0)
			return list.first.data.first.getType();
		return null;
	}
	
    public GenericList<Pair<Primary, FacOpr>> list = new GenericList<Pair<Primary, FacOpr>>();
	
	@Override
	void check(DeclList curDecls) {
		GenericIt<Pair<Primary, FacOpr>> it = list.iterator();
		while(it.hasNext()) {
			Pair<Primary, FacOpr> p = it.next();
			
			p.first.check(curDecls);
			if(p.second != null)
				p.second.check(curDecls);

			if(list.size() > 1 && p.first.getType() != Types.intType)
				Error.error(lineNum, "Factors must be of int type");
		}
	}

	@Override
	void genCode(FuncDecl curFunc) {
		GenericIt<Pair<Primary, FacOpr>> it = list.iterator();
		
		Pair<Primary, FacOpr> p = it.next();
		p.first.genCode(curFunc);
		
		while(p.second != null) {
			Code.genInstr("", "pushl", "%eax", "");
			FacOpr operator = p.second;
			p = it.next();
			p.first.genCode(curFunc);
			operator.genCode(curFunc);
		}
	}

	@Override
	void printTree() {
		GenericIt<Pair<Primary, FacOpr>> it = list.iterator();
		while(it.hasNext()) {
			Pair<Primary, FacOpr> cur = it.next();
			cur.first.printTree();
			if(cur.second != null) cur.second.printTree();
		}
	}
	
	static Factor parse() {
		Factor f = new Factor();
		
		Log.enterParser("<factor>");
		
		Primary p = Primary.parse();
		
		f.list.add(new Pair<Primary, FacOpr>(p, null));
		
		while(Token.isFactorOperator(Scanner.curToken)) {
			
			FacOpr fak = FacOpr.parse();
			
			f.list.last.data.second = fak;
			
			Primary pr = Primary.parse();
			
			f.list.add(new Pair<Primary, FacOpr>(pr, null));
			
		}
		
		Log.leaveParser("</factor>");
		
		return f;
	}
	
}

/*
 * A <primary>
 */

class Primary extends SyntaxUnit {

	Type getType() {
		if(prefix == Token.starToken)
			return op.type.getElemType();
		return op.type;
	}
	
	Token prefix = null;
	Operand op = null;
	
	@Override
	void check(DeclList curDecls) {
		op.check(curDecls);
		if(prefix == Token.starToken && !(op.type instanceof PointerType))
			Error.error(lineNum, "Can't dereference non-pointer type");
		else if (prefix == Token.subtractToken && !(op.type == Types.intType))
			Error.error(lineNum, "Can't negate non-int type");
	}

	@Override
	void genCode(FuncDecl curFunc) {
		if(op instanceof Variable && ((Variable)op).declRef.isArray && ((Variable)op).index == null) {
			((Variable)op).genAddressCode(curFunc);
		} else {
			op.genCode(curFunc);
		}
		if(prefix == Token.starToken) {
			Code.genInstr("", "movl", "(%eax),%eax", "Compute *");
		} else if (prefix == Token.subtractToken) {
			Code.genInstr("", "negl", "%eax", "Compute -");
		}
	}

	@Override
	void printTree() {
		if(prefix == Token.starToken) Log.wTree("*");
		else if (prefix == Token.subtractToken) Log.wTree("-");
		op.printTree();
	}
	
	static Primary parse() {
		Primary p = new Primary();
		
		Log.enterParser("<primary>");
		
		if(Token.isPrefixOperator(Scanner.curToken)) {
			p.prefix = Scanner.curToken;
			Scanner.readNext();
		}
		
		p.op = Operand.parse();
		
		Log.leaveParser("</primary>");
		
		return p;
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
 * A <fac opr> (* or /)
 */

class FacOpr extends Operator {

	@Override
	void genCode(FuncDecl curFunc) {
		Code.genInstr("", "movl", "%eax,%ecx", "");
		Code.genInstr("", "popl", "%eax", "");
		if(this.oprToken == Token.starToken) {
			Code.genInstr("", "imull", "%ecx,%eax", "Compute *");
		} else {
			Code.genInstr("", "cdq", "", "");
			Code.genInstr("", "idivl", "%ecx", "Compute /");
		}
	}
	
	static FacOpr parse() {
		Log.enterParser("<factor opr>");
		
		FacOpr fac = new FacOpr();
		
		fac.oprToken = Scanner.curToken;
		Scanner.readNext();

		Log.leaveParser("</factor opr>");
		
		return fac;
	}

	@Override
	void printTree() {
		if(oprToken == Token.starToken) Log.wTree(" * ");
		else Log.wTree(" / ");
	}
	
}

class TermOpr extends Operator {

	@Override
	void genCode(FuncDecl curFunc) {
		Code.genInstr("", "movl", "%eax,%ecx", "");
		Code.genInstr("", "popl", "%eax", "");
		if(this.oprToken == Token.addToken) {
			Code.genInstr("", "addl", "%ecx,%eax", "Compute +");
		} else {
			Code.genInstr("", "subl", "%ecx,%eax", "Compute -");
		}	
	}
	
	static TermOpr parse() {
		Log.enterParser("<term opr>");
		
		TermOpr term = new TermOpr();
		
		term.oprToken = Scanner.curToken;
		Scanner.readNext();

		Log.leaveParser("</term opr>");
		
		return term;
	}

	@Override
	void printTree() {
		if(oprToken == Token.subtractToken) Log.wTree(" - ");
		else Log.wTree(" + ");
	}
	
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
	// -- Must be changed in part 2:
	
	String name;
	ExprList list;

	@Override
	void check(DeclList curDecls) {
		Declaration d = curDecls.findDecl(name, this);
		list.check(curDecls);
		this.type = d.type;
		if(d instanceof FuncDecl) {
			FuncDecl fnc = (FuncDecl)d;
			if(list.list.size() != fnc.funcParams.size())
				this.error("Calls to " + name + " should have " + fnc.funcParams.size() + " param(s) not " + list.list.size());
			
			GenericIt<Expression> callIt = list.list.iterator();
			Declaration curFormalParam = fnc.funcParams.firstDecl;
			while(callIt.hasNext()) {
				Expression curExpr = callIt.next();
				if(!(curFormalParam.type.isSameType(curExpr.type) || curExpr.type == Types.intType)) {
					Error.error(lineNum, "Parameter must be same type as the formal parameter");
				}
				if(curFormalParam != null)
					curFormalParam = curFormalParam.nextDecl;
			}
		}
	}

	@Override
	void genCode(FuncDecl curFunc) {
		list.genCode(curFunc);

		Code.genInstr("", "call", this.name, "Call " + this.name);
		if(list.list.size() > 0)
			Code.genInstr("", "addl", "$" + list.list.size() * 4 + ",%esp", "Remove parameters");
	}

	static FunctionCall parse() {
		Log.enterParser("<function call>");
		
		FunctionCall fnc = new FunctionCall();
		
		fnc.name = Scanner.curName;
		Scanner.skip(Token.nameToken);
		
		Scanner.skip(Token.leftParToken);
		fnc.list = ExprList.parse();
		Scanner.skip(Token.rightParToken);
		
		Log.leaveParser("</function call>");
		return fnc;
	}

	@Override
	void printTree() {
		Log.wTree(name + "("); list.printTree(); Log.wTree(")");
	}
	// -- Must be changed in part 2:
}

/*
 * A <number>.
 */
class Number extends Operand {
	int numVal;

	@Override
	void check(DeclList curDecls) {
		type = Types.intType;
	}

	@Override
	void genCode(FuncDecl curFunc) {
		Code.genInstr("", "movl", "$" + numVal + ",%eax", "" + numVal);
	}

	static Number parse() {
		Log.enterParser("<number>");
		
		Number n = new Number();
		n.numVal = Scanner.curNum;
		Scanner.skip(Token.numberToken);
		
		n.type = Types.intType;
		
		Log.leaveParser("</number>");
		
		return n;
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
		if(!d.visible)
			this.error("Name " + varName + " is unknown!");
		type = declRef.type;

		if(index != null) {
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
			
			type = type.getElemType();
		}
	}

	@Override
	void genCode(FuncDecl curFunc) {
		declRef.assemblerName = curFunc.getVarLabel(declRef);
		if (index == null) {
			Code.genInstr("", "movl", declRef.assemblerName + ",%eax", varName);
		} else {
			index.genCode(curFunc);
			if (declRef.type instanceof ArrayType) {
				Code.genInstr("", "leal", declRef.assemblerName + ",%edx",
						varName + "[...]");
			} else {
				Code.genInstr("", "movl", declRef.assemblerName + ",%edx",
						varName + "[...]");
			}
			Code.genInstr("", "movl", "(%edx,%eax,4),%eax", "");
		}
		
//		String label = curFunc.getVarLabel(declRef);
//		Code.genInstr("", "movl", label + ",%eax", varName);
	}

	void genAddressCode(FuncDecl curFunc) {
		// Generate code to load the _address_ of the variable
		// rather than its value.

		declRef.assemblerName = curFunc.getVarLabel(declRef);
		
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
		Variable var = new Variable();
		var.varName = Scanner.curName;
		
		Scanner.skip(Token.nameToken);
		
		if(Scanner.curToken == Token.leftBracketToken) {
			Scanner.readNext();
			var.index = Expression.parse();
			Scanner.skip(rightBracketToken);
		}
		
		Log.leaveParser("</variable>");
		
		
		
		return var;
	}

	@Override
	void printTree() {
		Log.wTree(varName);
		if(index != null) {
			Log.wTree("["); index.printTree(); Log.wTree("]");
		}
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
		
		// Vi sjekker ikke typen til var da resultatet blir satt riktig i var.
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
