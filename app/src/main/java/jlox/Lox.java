package jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Lox {

    static boolean hadError = false;
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        }
        if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        run(Files.readString(Paths.get(path)));
        if (hadError) System.exit(65);
    }

    private static void runPrompt() throws IOException {
        try (InputStreamReader input = new InputStreamReader(System.in);
                BufferedReader reader = new BufferedReader(input)) {
            while (true) {
                System.out.print("> ");
                String line = reader.readLine();
                if (line == null || line.strip().equals("exit")) break;
                run(line);
                hadError = false; // mistakes are ok in REPL
            }
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        scanner.tokens().forEach(System.out::println);
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}
