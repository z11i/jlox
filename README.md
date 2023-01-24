# Java implementation of the Lox language

This is a Java implementation of the Lox language from the book [Crafting Interpreters](http://craftinginterpreters.com/). It is an interpreter for a dynamically typed language with classes, inheritance, and closures.

## Running

```shell
make run
```

## EBNF of Lox

```ebnf
expression     → literal
               | unary
               | binary
               | grouping ;
literal        → NUMBER | STRING | "true" | "false" | "nil" ;
grouping       → "(" expression ")" ;
unary          → ( "-" | "!" ) expression ;
binary         → expression operator expression ;
operator       → "==" | "!=" | "<" | "<=" | ">" | ">="
               | "+"  | "-"  | "*" | "/" ;
```