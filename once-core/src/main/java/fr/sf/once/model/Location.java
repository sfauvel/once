package fr.sf.once.model;


public class Location {
    private final String fileName;
    private final int line;
    private final int column;
    
    public Location(String fileName, int line, int column) {
        super();
        this.fileName = fileName;
        this.line = line;
        this.column = column;
    }
    
    public String getFileName() {
        return fileName;
    }
    public int getLine() {
        return line;
    }
    public int getColumn() {
        return column;
    }
    
    public void appendLocation(StringBuffer buffer) {
        
        buffer.append("(")
                .append(fileName)
                .append(":")
                .append(line)
                .append("/")
                .append(column)
                .append(")");
    }

}