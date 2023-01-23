package jlox;

import java.util.ArrayList;
import java.util.List;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        TokenType t =
                switch (c) {
                    case '(':
                        yield TokenType.LEFT_PAREN;
                    case ')':
                        yield TokenType.RIGHT_PAREN;
                    case '{':
                        yield TokenType.LEFT_BRACE;
                    case '}':
                        yield TokenType.RIGHT_BRACE;
                    case ',':
                        yield TokenType.COMMA;
                    case '.':
                        yield TokenType.DOT;
                    case '-':
                        yield TokenType.MINUS;
                    case '+':
                        yield TokenType.PLUS;
                    case ';':
                        yield TokenType.SEMICOLON;
                    case '*':
                        yield TokenType.STAR;
                    case '!':
                        yield match('=') ? TokenType.BANG_EQUAL : TokenType.BANG;
                    case '=':
                        yield match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL;
                    case '<':
                        yield match('=') ? TokenType.LESS_EQUAL : TokenType.LESS;
                    case '>':
                        yield match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER;
                    case '"':
                        string();
                        yield null;
                    case '/':
                        if (match('/')) {
                            while (peek() != '\n' && !isAtEnd()) advance();
                            yield null;
                        } else {
                            yield TokenType.SLASH;
                        }
                    case ' ', '\r', '\t':
                        yield null;
                    case '\n':
                        line++;
                        yield null;
                    default:
                        if (isDigit(c)) {
                            number();
                        } else {
                            Lox.error(line, "Unexpected character.");
                        }
                        yield null;
                };
        if (t != null) addToken(t);
    }

    private void number() {
        while (isDigit(peek())) advance();
        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) advance();
        }
        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType t) {
        addToken(t, null);
    }

    private void addToken(TokenType t, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(t, text, literal, line));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
}
