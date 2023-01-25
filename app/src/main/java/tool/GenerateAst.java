package tool;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAst(
                outputDir,
                "Expr",
                Arrays.asList(
                        "Binary   : Expr left, Token operator, Expr right",
                        "Grouping : Expr expressions",
                        "Literal  : Object value",
                        "Unary    : Token operator, Expr right"));
    }

    public static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        Path path = Paths.get(outputDir, baseName + ".java");
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                Files.newOutputStream(path, StandardOpenOption.CREATE), StandardCharsets.UTF_8))) {
            bw.write(
                    """
                    /**
                     * This file is generated by tool.GenerateAst.
                     * DO NOT MODIFY IT MANUALLY.
                     */
                    package jlox;

                    import java.util.List;

                    abstract class %s {
                    """
                            .formatted(baseName));

            defineVisitor(bw, baseName, types);

            // The AST classes
            for (String type : types) {
                String className = type.split(":")[0].trim();
                String fields = type.split(":")[1].trim();
                defineType(bw, baseName, className, fields);
            }

            // The base accept() method for the visitor pattern
            bw.write("""
                        abstract <R> R accept(Visitor<R> visitor);
                    """);

            bw.write("}");
            bw.newLine();
        }
    }

    private static void defineVisitor(BufferedWriter bw, String baseName, List<String> types) throws IOException {
        bw.write("    interface Visitor<R> {");
        bw.newLine();
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            bw.write("        R visit%s%s(%s %s);".formatted(typeName, baseName, typeName, baseName.toLowerCase()));
            bw.newLine();
        }
        bw.write("    }");
        bw.newLine();
    }

    private static void defineType(BufferedWriter bw, String baseName, String className, String fields)
            throws IOException {
        // Constructor
        bw.write(
                """
                    static class %s extends %s {
                        %s(%s) {
                """
                        .formatted(className, baseName, className, fields));
        String[] fieldArr = fields.split(", ");
        for (String field : fieldArr) {
            String name = field.split(" ")[1];
            bw.write("            this.%s = %s;".formatted(name, name));
            bw.newLine();
        }
        bw.write("        }");
        bw.newLine();

        bw.write("""
                        @Override
                        <R> R accept(Visitor<R> visitor) {
                            return visitor.visit%s%s(this);
                        }
                """.formatted(className, baseName));

        // Fields
        for (String field : fieldArr) {
            bw.write("        final %s;".formatted(field));
            bw.newLine();
        }
        bw.write("    }");
        bw.newLine();
    }
}
