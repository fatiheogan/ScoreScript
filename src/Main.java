import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * ScoreScript — Main Entry Point
 *
 * Usage:
 *   java Main <source_file>             // parse only, report errors
 *   java Main <source_file> --dump-ast  // parse and print the AST
 *
 * Exit codes:
 *   0 — success
 *   1 — lexer or parser error
 *   2 — usage error (wrong arguments)
 *
 * CSE 341 · Spring 2026
 */
public class Main {

    public static void main(String[] args) {

        // ── Argument validation ──
        if (args.length < 1 || args.length > 2) {
            System.err.println("Usage: java Main <source_file> [--dump-ast]");
            System.exit(2);
        }

        String  sourceFile = args[0];
        boolean dumpAst    = args.length == 2 && args[1].equals("--dump-ast");

        // ── Read source ──
        String source;
        try {
            source = new String(Files.readAllBytes(Paths.get(sourceFile)));
        } catch (IOException e) {
            System.err.println("Error: cannot read file '" + sourceFile + "'");
            System.exit(1);
            return;
        }

        // ── Lex ──
        List<Token> tokens;
        try {
            Lexer lexer = new Lexer(source);
            tokens = lexer.tokenize();
        } catch (Lexer.LexerException e) {
            System.err.println(e.getMessage());
            System.exit(1);
            return;
        }

        // ── Parse ──
        AST.Program program;
        try {
            Parser parser = new Parser(tokens);
            program = parser.parse();
        } catch (Parser.ParseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
            return;
        }

        // ── Output ──
        if (dumpAst) {
            System.out.println(program.dump(""));
        } else {
            System.out.println("OK — '" + sourceFile + "' parsed successfully.");
        }
    }
}
