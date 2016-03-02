/*
program -> decls stmts end
decls -> int idlist ';'
idlist -> id [',' idlist ]
stmts -> stmt [ stmts ]
stmt -> assign ';'| cmpd | cond  | loop
assign -> id '=' expr
cmpd -> '{' stmts '}'
cond -> if '(' rexp ')' stmt [ else stmt ]
loop -> for '(' [assign] ';' [rexp] ';' [assign] ')' stmt
rexp -> expr ('<' | '>' | '==' | '!= ') expr
expr -> term [ ('+' | '-') expr ]
term -> factor [ ('*' | '/') term ]
factor -> int_lit | id | '(' expr ')'
 */

public class Parser{

	public static void main(String[] args) {
		System.out.println("Enter code");
		Lexer.lex();
		new program();
        Code.output();


	}
}
class program //program -> decls stmts end
{
	decls d;
	stmts s;
	public program()
	{
		d=new decls();
		s=new stmts();
			
	}
}

class decls //decls -> int idlist ';'
{
	idlist i;
	public decls()
	{
		if(Lexer.nextToken==Token.KEY_INT)
		{
			Lexer.lex();
			i=new idlist();
		}
		
	}
}
class stmts //stmts -> stmt [ stmts ]
{
	stmt st;
	stmts s;
	public stmts()
	{
		
		st = new stmt();
		//Lexer.lex();
		if(Lexer.nextToken!=Token.KEY_END && Lexer.nextToken!=Token.RIGHT_BRACE )
		{
			//Lexer.lex();
			s=new stmts();
			//Lexer.lex();
		}
		
	}
}
class idlist //idlist -> id [',' idlist ]
{
	public static char[] id=new char[100];
	public static int i=0;
	idlist idl;
	public idlist() 
	{
		
		if(Lexer.nextToken==Token.ID)
		{
			++i;
			id[i]=Lexer.ident;
			Lexer.lex();
			switch(Lexer.nextToken)
			{
			case Token.COMMA :
			{
				//Lexer.lex();
				Lexer.lex();
				idl=new idlist();
				
				break;
				
			}
			case Token.SEMICOLON:
			{
				Lexer.lex();
				break;
			}
			}
			
		}
		
	}
}
class stmt //stmt -> assign ';'| cmpd | cond | loop
{
	assign a;
	cmpd cm;
	cond cn;
	loop l;
	char c;
	public stmt()
	{
		switch(Lexer.nextToken)
		{
		case Token.ID:
		{
			c=Lexer.ident;
			a= new assign();
			Code.gen(Code.storecode(c));
			if(Lexer.nextToken==Token.SEMICOLON)
			{
			Lexer.lex();
			}
			break;
		}
		case Token.LEFT_BRACE:
		{
			cm=new cmpd();
			if(Lexer.nextToken==Token.RIGHT_BRACE)
			{
				Lexer.lex();
			}
			break;
		}
		case Token.KEY_IF:
		{
			Lexer.lex();
			cn = new cond();
			break;
		}
		case Token.KEY_FOR:
		{
			Lexer.lex();
			l = new loop();
			break;
		}
		
		default:
		{
			break;
		}
		
		}
	}
}
class cond //cond -> if '(' rexp ')' stmt [ else stmt ]
{
	rexp re;
	stmt s1,s2;
	public cond()
	{
		if(Lexer.nextToken==Token.LEFT_PAREN)
		{
			Lexer.lex();
			re = new rexp();
			if(Lexer.nextToken==Token.RIGHT_PAREN)
			{
				Lexer.lex();
				s1=new stmt();				
			}
			
			if(Lexer.nextToken==Token.KEY_ELSE)
			{
				
				Code.gen(Code.elsecode());
				Code.iftemp2();
				Lexer.lex();
				s2=new stmt();
				Code.elsetemp();
			}
			else
			{
				Code.iftemp();
			}
		}
	}
}
class loop //loop -> for '(' [assign] ';' [rexp] ';' [assign] ')' stmt
{
assign a1,a2;
rexp r;
stmt s;
char[] c = new char[10];
char c1;
static int x;
static int lo;
static int nes=0;
public loop()
{
	++nes;
	if(Lexer.nextToken==Token.LEFT_PAREN)
	{
		Lexer.lex();
		if(Lexer.nextToken==Token.ID)
		{
			c1=Lexer.ident;
			a1=new assign();
			Code.gen(Code.storecode(c1));
		}
		if(Lexer.nextToken==Token.SEMICOLON)
		{
			Lexer.lex();
		}
		if(Lexer.nextToken==Token.ID)
		{
			Code.store3();
			r=new rexp();
		}
		if(Lexer.nextToken==Token.SEMICOLON)
		{
			Lexer.lex();
		}
		if(Lexer.nextToken==Token.ID)
		{
			++lo;
			c[nes]=Lexer.ident;
			a2=new assign();
			--lo;
		}
		if(Lexer.nextToken==Token.RIGHT_PAREN)
		{
			Lexer.lex();
			s=new stmt();
		
			for(int i=0;i<Code.codeptr2[nes];i++)
			{
			Code.code[Code.codeptr]=Code.code2[i][nes];	
			++Code.codeptr;
			++Code.b;
			Code.byt[Code.b]=Code.byt[Code.b-1-i]+Code.byt2[i][nes]+1;
			}
			if(c[nes]!=0)
			{
			Code.gen(Code.storecode(c[nes]));
			}
			Code.gen(Code.forgotocode());
			Code.iftemp();
			--nes;
		}
	}
}
}
class rexp //rexp -> expr ('<' | '>' | '==' | '!= ') expr
{
	expr e1,e2;
	char c;
	public rexp()
	{
		e1=new expr();
		if(Lexer.nextToken==Token.LESSER_OP || Lexer.nextToken==Token.GREATER_OP || Lexer.nextToken==Token.EQ_OP || Lexer.nextToken==Token.NOT_EQ)
		{
			c=Lexer.nextChar;
			Lexer.lex();
			e2=new expr();
			Code.gen(Code.ifcode(c));
		}
	}
}
class assign //assign -> id '=' expr
{
	expr e;
	public assign()
	{
		Lexer.lex();
		if(Lexer.nextToken==Token.ASSIGN_OP)
		{
			Lexer.lex();
			e=new expr();	
		}
		
	}
}
class cmpd //cmpd -> '{' stmts '}'
{
	stmts stm;
	public cmpd()
	{
			Lexer.lex();
			stm=new stmts();
	}
} 
class expr //expr -> term [ ('+' | '-') expr ]
{
	expr e;
	term t;
	char op;
	public expr()
	{
		t=new term();
		if(Lexer.nextToken==Token.ADD_OP || Lexer.nextToken==Token.SUB_OP )
		{
			op = Lexer.nextChar;
			Lexer.lex();
			e=new expr();
			if(loop.lo==0)
			{
			Code.gen(Code.opcode(op));
			}
			else
			{
			Code.gen2(Code.opcode2(op));
			}
		}
		
	}
}
class term //term -> factor [ ('*' | '/') term ]
{
	factor f;
	term t;
	char op;
	public term()
	{
		f=new factor();
		if(Lexer.nextToken==Token.MULT_OP || Lexer.nextToken==Token.DIV_OP )
		{
			op = Lexer.nextChar;
			Lexer.lex();
			t=new term();
			if(loop.lo==0)
			{
			Code.gen(Code.opcode(op));
			}
			else
			{
			Code.gen2(Code.opcode2(op));
			}
		}
	}
}
class factor //factor -> int_lit | id | '(' expr ')'
{
	expr e;
	int i;
	char c;
	public factor()
	{
		switch(Lexer.nextToken)
		{
		case Token.INT_LIT:
			i=Lexer.intValue;
			if(loop.lo==0)
			{
			Code.gen(Code.intcode(i));
			}
			else
			{
			Code.gen2(Code.intcode2(i));
			}
			Lexer.lex();
			break;
		case Token.ID:
			c=Lexer.ident;
			if(loop.lo==0)
			{
			Code.gen(Code.loadcode(c));
			}
			else
			{
			Code.gen2(Code.loadcode2(c));
			}
			Lexer.lex();
			break;
		case Token.LEFT_PAREN:
			Lexer.lex();
			e=new expr();
			Lexer.lex();
			break;
		default:
			break;	
		}
	}
}

class Code {
	static String[] code = new String[100];
	static int codeptr = 0;
	static String[][] code2 = new String[100][10];
	static int[] codeptr2 = new int[10];
	static int j; 
	static int[] byt=new int[100];
	static int[][] byt2=new int[100][10];
	static int[] stk1=new int[10];
	static int[] stk2=new int[10];
	static int[] a3=new int[10];
	public static int i3=0;
	public static int b=0;
	public static int[] b2=new int[10];
	public static int p=0;
	public static int q=0;
	
	public static void gen(String s) {
		code[codeptr] = s;
		codeptr++;
		
	}
	
	public static void gen2(String s) {
		code2[codeptr2[loop.nes]][loop.nes] = s;
		codeptr2[loop.nes]++;
		
	}
	
	public static String intcode(int i) {	
		++b;
		if (i > 127)
		{
			byt[b]=byt[b-1]+3;
			return "sipush " + i;
		}
		if (i > 5) 
			{
			byt[b]=byt[b-1]+2;
			return "bipush " + i;
			}
		byt[b]=byt[b-1]+1;
		return "iconst_" + i;
	}
	
	public static String intcode2(int i) {	
		++b2[loop.nes];
		if (i > 127)
		{
			byt2[b2[loop.nes]][loop.nes]=byt2[b2[loop.nes]-1][loop.nes]+3;
			return "sipush " + i;
		}
		if (i > 5) 
			{
			byt2[b2[loop.nes]][loop.nes]=byt2[b2[loop.nes]-1][loop.nes]+2;
			return "bipush " + i;
			}
		byt2[b2[loop.nes]][loop.nes]=byt2[b2[loop.nes]-1][loop.nes]+1;
		return "iconst_" + i;
	}
	
	
	 public static String storecode(char c)
	{
		 ++b;
		 
		 j=1;
		 while(c!=idlist.id[j])
		 {
			 j++;
		 }
		 if(j<=3)
		 {
			 byt[b]=byt[b-1]+1;
			 return "istore_"+j;
		 }
		 else
		 {
			 byt[b]=byt[b-1]+2;
			 return "istore "+j; 
		 }
	}
	 public static String loadcode(char c)
		{
		 	 ++b;
		     byt[b]=byt[b-1]+1;
			 j=1;
			 while(c!=idlist.id[j])
			 {
				 j++;
			 }
			 
			 if(j<=3)
			 {
				 byt[b]=byt[b-1]+1;
				 return "iload_"+j;
			 }
			 else
			 {
				 byt[b]=byt[b-1]+2;
				 return "iload "+j; 
			 }
		}
	 
	 public static String loadcode2(char c)
		{
		 	 ++b2[loop.nes];
		     byt2[b2[loop.nes]][loop.nes]=byt2[b2[loop.nes]-1][loop.nes]+1;
			 j=1;
			 while(c!=idlist.id[j])
			 {
				 j++;
			 }
			 
			 if(j<=3)
			 {
				 byt2[b2[loop.nes]][loop.nes]=byt2[b2[loop.nes]-1][loop.nes]+1;
				 return "iload_"+j;
			 }
			 else
			 {
				 byt2[b2[loop.nes]][loop.nes]=byt2[b2[loop.nes]-1][loop.nes]+2;
				 return "iload "+j; 
			 }
		}
	 
	 
	
	public static String opcode(char op) {
		++b;
		byt[b]=byt[b-1]+1;
		switch(op) {
		case '+' : return "iadd";
		case '-':  return "isub";
		case '*':  return "imul";
		case '/':  return "idiv";
		default: return "";
		}
	}
	
	public static String opcode2(char op) {
		++b2[loop.nes];
		byt2[b2[loop.nes]][loop.nes]=byt2[b2[loop.nes]-1][loop.nes]+1;
		switch(op) {
		case '+' : return "iadd";
		case '-':  return "isub";
		case '*':  return "imul";
		case '/':  return "idiv";
		default: return "";
		}
	}
	
	
	public static String ifcode(char op) {
		++b;
		byt[b]=byt[b-1]+3;
		stk1[p]=codeptr;
		++p;
		switch(op) {
		case '<' : return "if_icmpge ";
		case '>':  return "if_icmple ";
		case '=':  return "if_icmpne ";
		case '!':  return "if_icmpeq ";
		default: return "";
		}
	}
	public static void iftemp()
	{
		--p;
		code[stk1[p]]=code[stk1[p]]+byt[b];
		
	}
	public static void iftemp2()
	{
		--p;
		code[stk1[p]]=code[stk1[p]]+byt[b];
		
	}
	public static String elsecode()
	{
		++b;
		byt[b]=byt[b-1]+3;
		stk2[q]=codeptr;
		++q;
		return "goto ";
	}
	public static void elsetemp()
	{
		--q;
		code[stk2[q]]=code[stk2[q]]+byt[b];
		
	}
	public static void store3()
	{
		a3[i3]=byt[b];	
		++i3;
	}
	public static String forgotocode()
	{
		++b;
		--i3;
		byt[b]=byt[b-1]+3;
		return "goto "+a3[i3];
	}
	
	public static void output() {
		for (int i=0,b=0; i<codeptr; i++,b++)
		{
			System.out.println(byt[b]+":"+code[i]);
		}
		System.out.println(byt[b]+":return");
	}

	
}

