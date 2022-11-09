# SPLAT---Custom-Programming-Language

Splat - Software Principles Language And Tools), which is a simple programming language and associated tools for parsing, typechecking, and interpretive execution of the language. 

LEXER/TOKENIZER – This takes a single SPLAT program file, and performs lexical processing to produce the List of Tokens that make up the program. Tokens  contain location information—line and column number, to indicate the character in the file where the token began.

 PARSER - This takes the Tokens in the Token List produced by the Lexer, and uses them to produce the abstract syntax trees (ASTs) which represent the structures of the language. The Basic SPLAT grammar is simple enough that I used a recursive descent parsing approach to do this.
 
 SEMANTIC ANALYZER (including Typechecking) – I made sure your program is indeed valid, and follows all of the rules and restrictions in its definition and use of functions, variables, types, etc. Typechecking and other checks to make sure that referenced labels have actually been defined are performed. 
 
 EXECUTOR – This component will run the actual program by essentially executing one-by-one the individual program statements in the program body ASTs.
 
 
         ----------------------------------------------------------------> GRAMMAR <--------------------------------------------------------
   
 Grammar file 

         ----------------------------------------------------------------> GRAMMAR <--------------------------------------------------------
