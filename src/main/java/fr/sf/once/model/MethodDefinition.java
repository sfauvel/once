package fr.sf.once.model;

public class MethodDefinition {

    private final String file;
    private final String methodName;
    private final int startToken;
    private final int endToken;

    public MethodDefinition(String file, String methodName, int startToken, int endToken) {
        this.file = file;
        this.methodName = methodName;
        this.startToken = startToken;
        this.endToken = endToken;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getFile() {
        return file;
    }

    public int getStartToken() {
        return startToken;
    }

    public int getEndToken() {
        return endToken;
    }

    public int tokenNumber() {
        return endToken - startToken + 1;
    }

}
