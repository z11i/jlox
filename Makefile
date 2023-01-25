run:
	@./gradlew run -q --console=plain

gen:
	@rm app/src/main/java/jlox/Expr.java
	@./gradlew generateAst -q