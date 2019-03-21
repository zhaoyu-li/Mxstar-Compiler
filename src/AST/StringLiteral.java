package AST;

public class StringLiteral extends Expression {
    private String value;

    public StringLiteral() {
        value = null;
    }

    public void setValue(String ctx) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < ctx.length(); i++) {
            char c = ctx.charAt(i);
            if(c == '\\' && i + 1 < ctx.length()) {
                switch (ctx.charAt(i + 1)) {
                    case '\\':
                        stringBuilder.append('\\');
                        break;
                    case 'n':
                        stringBuilder.append('n');
                        break;
                    case '\"':
                        stringBuilder.append('\"');
                        break;
                    case 't':
                        stringBuilder.append('t');
                        break;
                }
            } else {
                stringBuilder.append(ctx.charAt(i));
            }
        }
        this.value = stringBuilder.toString();
    }

    public String getValue() {
        return value;
    }

    @Override
    public void accept(ASTVistor vistor) {
        vistor.visit(this);
    }
}
