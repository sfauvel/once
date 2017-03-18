package fr.sf.once.model;

import java.util.List;

import fr.sf.commons.Domain;

/**
 * The text representing the code. 
 * 
 * It's a suite of *Tokens*.
 *  
 */
@Domain
public interface Code {

    List<Token> getTokenList();

    Token getToken(int position);

    int getSize();

    List<MethodLocation> getMethodList();

    MethodLocation getMethodAtTokenPosition(int tokenPosition);

}