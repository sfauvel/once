/**
 * 
 */
package fr.sf.once.model;

import org.apache.commons.lang.StringUtils;



public class Token {
	
	private final String tokenValue;
	private final Type type;
	private final Location location;
	
	public Token(Location location, String token, Type type) {
		this.location = location;
		this.tokenValue = token;
        this.type = type;
	}
	
	public String getTokenValue() {
		return this.tokenValue;
	}

    public Location getLocation() {
        return location;
    }

    public Type getType() {
        return type;
    }
    
    public Integer getStartingLine() {
        return location.getLine();
    }
    public Integer getStartingColumn() {
        return location.getColumn();
    }

    public int getEndingColumn() {
        return getStartingColumn() + tokenValue.length();
    }
    
    public String format() {

        StringBuffer buffer = new StringBuffer();
        appendToken(buffer);
        return buffer.toString();
    }

    public void appendToken(StringBuffer buffer) {
        Location location = getLocation();
        buffer.append(StringUtils.rightPad(getTokenValue(), 25));
        location.appendLocation(buffer);
        buffer.append(" col:")
                .append(StringUtils.rightPad(Integer.toString(location.getColumn()), 5))
                .append(" type:")
                .append(getType().toString());
    }

}