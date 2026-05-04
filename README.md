# ScoreScript

A domain-specific language for defining students, grouping them, and running grade operations.
Designed and implemented for CSE 341 — Concepts of Programming Languages, Spring 2026, Gebze Technical University.

---

## Project Structure

```
ScoreScript/
├── src/                    # Java source files
│   ├── TokenType.java
│   ├── Token.java
│   ├── Lexer.java
│   ├── AST.java
│   ├── Parser.java
│   └── Main.java
├── examples/
│   ├── valid/              # Programs that parse successfully
│   │   ├── program1.ss
│   │   ├── program2.ss
│   │   └── program3.ss
│   └── invalid/            # Programs that trigger parser errors
│       ├── error1.ss
│       ├── error2.ss
│       ├── error3.ss
│       ├── error4.ss
│       └── error5.ss
├── out/                    # Compiled .class files (created by build step)
└── README.md
```

---

## Requirements

- Java 11 or later
- No external libraries or build tools required

Verify your Java version:

```bash
java -version
```

---

## Build

Compile all source files from the project root and output `.class` files into `out/`:

```bash
javac -d out src/*.java
```

If the `out/` directory does not exist yet, create it first:

```bash
mkdir out
javac -d out src/*.java
```

You should see no output and exit code 0 if compilation succeeds.

---

## Run

All commands are run from the project root. The `-cp out` flag tells Java where to find the compiled classes.

### Parse a program

Parses the given `.ss` file and reports success or prints an error with a line number:

```bash
java -cp out Main <source_file>
```

### Dump the AST

Parses the file and prints the full Abstract Syntax Tree to stdout:

```bash
java -cp out Main <source_file> --dump-ast
```

### Exit codes

| Code | Meaning |
|------|---------|
| `0`  | Parsed successfully |
| `1`  | Lexer or parser error |
| `2`  | Wrong number of arguments |

---

## Examples

### Valid programs

**program1.ss** — Student declarations, groups, variable declarations, all four operations:

```bash
java -cp out Main examples/valid/program1.ss
# OK — 'examples/valid/program1.ss' parsed successfully.

java -cp out Main examples/valid/program1.ss --dump-ast
# Program
#   Declarations:
#     StudentDecl name=s1 id=230104004090 ...
#     ...
```

**program2.ss** — Functions with if / else if / else chains, bool variables, print statements:

```bash
java -cp out Main examples/valid/program2.ss
# OK — 'examples/valid/program2.ss' parsed successfully.
```

**program3.ss** — For loops, assignment statements, expressions in curve, multiple groups:

```bash
java -cp out Main examples/valid/program3.ss
# OK — 'examples/valid/program3.ss' parsed successfully.
```

---

### Invalid programs

Each invalid program triggers a distinct parser error with a line number.

| File | Error | Expected message |
|------|-------|-----------------|
| `error1.ss` | Wrong field order in student block | `Parse error at line 10: expected 'firstName' but found 'lastName'` |
| `error2.ss` | `:` instead of `=` in group declaration | `Parse error at line 22: expected '=' but found ':'` |
| `error3.ss` | Missing `->` in func declaration | `Parse error at line 17: expected '->' but found 'string'` |
| `error4.ss` | Missing expression after `by` in curve | `Parse error at line 19: expected an expression but found 'average'` |
| `error5.ss` | Statement appears before declaration section ends | `Parse error at line 18: unexpected token 'student' — expected a statement` |

Run any invalid program to see its error:

```bash
java -cp out Main examples/invalid/error1.ss
# Parse error at line 10: expected 'firstName' but found 'lastName'
```

---

## Language Quick Reference

A ScoreScript program has two sections — all declarations must come before all statements:

```
program = decl_section stmt_section
```

### Student declaration

Fields are mandatory and must appear in this exact order:

```
student s1 {
    id: "230104004090",
    firstName: "Fatih Emre",
    lastName: "OGAN",
    grades: { math: 85, science: 90, history: 72 }
}
```

### Group declaration

```
group ClassZ23 = [s1, s2, s3]
```

### Variable declaration

```
int    curveAmount = 5
float  threshold   = 59.5
string label       = "passing"
bool   applied     = false
```

### Function declaration

```
func letterGrade(avg: float) -> string {
    if avg >= 90 { return "AA" }
    else if avg >= 85 { return "BA" }
    else { return "FF" }
}

func printAll() -> void {
    transcript s1
}
```

### Operations

```
curve      ClassZ23 by 5          // add points to every grade, cap at 100
curve      ClassZ23 by curveAmount + 3   // expression allowed after by
average    ClassZ23               // print overall group average
rank       ClassZ23               // rank students by overall average
transcript s1                     // print all grades for one student
```

### Control flow

```
if threshold < 60.0 {
    curve ClassZ23 by 10
}
else if threshold < 70.0 {
    curve ClassZ23 by 5
}
else {
    rank ClassZ23
}
```

### For loop

Iterates over every student in a group. The `student` keyword is mandatory:

```
for student s in ClassZ23 {
    transcript s
}
```

### Comments

Single-line only, using `//`:

```
// This is a comment
curve ClassZ23 by 5   // inline comment
```

---

## Token Summary

| Category | Tokens |
|----------|--------|
| Declaration keywords | `student` `group` `func` `for` `in` `if` `else` `return` `print` `by` |
| Operation keywords | `curve` `average` `rank` `transcript` |
| Type keywords | `int` `float` `string` `bool` `void` |
| Student field keywords | `id` `firstName` `lastName` `grades` |
| Literals | `INT` `FLOAT` `STRING` `true` `false` |
| Operators | `+` `-` `*` `/` `!` `=` `==` `!=` `<` `<=` `>` `>=` `&&` `\|\|` `->` |
| Separators | `{` `}` `(` `)` `[` `]` `,` `:` |

---

## Operator Precedence

From tightest to loosest binding:

| Level | Operator(s) | Associativity |
|-------|------------|---------------|
| 1 | `-` `!` (unary) | Right |
| 2 | `*` `/` | Left |
| 3 | `+` `-` | Left |
| 4 | `<` `<=` `>` `>=` | Left |
| 5 | `==` `!=` | Left |
| 6 | `&&` | Left |
| 7 | `\|\|` | Left |
