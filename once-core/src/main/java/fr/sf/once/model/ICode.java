package fr.sf.once.model;

import java.util.List;

public interface ICode {

    List<Token> getTokenList();

    Token getToken(int position);

    int getSize();

    List<MethodLocation> getMethodList();

    MethodLocation getMethodAtTokenPosition(int tokenPosition);

}