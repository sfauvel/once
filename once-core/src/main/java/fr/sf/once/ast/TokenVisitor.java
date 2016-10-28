package fr.sf.once.ast;


import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EmptyMemberDeclaration;
import com.github.javaparser.ast.body.EmptyTypeDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.MultiTypeParameter;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralMinValueExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralMinValueExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.QualifiedNameExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.TypeDeclarationStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.type.WildcardType;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;

import fr.sf.once.model.Location;
import fr.sf.once.model.MethodLocation;
import fr.sf.once.model.Token;

public class TokenVisitor implements VoidVisitor<List<Token>> {

    static final Logger logOutput = Logger.getLogger("OUTPUT");
    static final Logger LOG = Logger.getLogger(TokenVisitor.class);
    private final String fileName;
    private final List<MethodLocation> methodList;
    private final Stack<String> currentClassList = new Stack<String>();
    private String currentPackage = "";
    private int firstTokenNumber;

    public TokenVisitor() {
        this("");
    }

    public TokenVisitor(String fileName) {
        this(fileName, new ArrayList<MethodLocation>(), 0);
    }

    public TokenVisitor(String fileName, List<MethodLocation> methodList, int firstTokenNumber) {
        this.fileName = fileName;
        this.methodList = methodList;
        this.firstTokenNumber = firstTokenNumber;
    }

    public void genericVisit(Node n, List<Token> arg) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("     " + n.getClass().getName());
        }
        LOG.trace("");
    }

    public void visit(AnnotationDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        visitBodyHeader(n, arg);
        for (BodyDeclaration member : notNull(n.getMembers())) {
            member.accept(this, arg);
        }
    }

    public void visit(AnnotationMemberDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        visitBodyHeader(n, arg);

        n.getType().accept(this, arg);
        if (n.getDefaultValue() != null) {
            n.getDefaultValue().accept(this, arg);
        }
    }

    public void visit(ArrayAccessExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getName().accept(this, arg);
        addToken(nextNode(n.getName()), TokenJava.OPENING_ARRAY, arg);
        n.getIndex().accept(this, arg);
        addToken(endOfToken(arg), TokenJava.CLOSING_ARRAY, arg);

    }

    public void visit(ArrayCreationExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.NEW, arg);
        n.getType().accept(this, arg);
        if (isNotEmpty(n.getDimensions())) {
            for (Expression dim : n.getDimensions()) {
                addToken(endOfToken(arg), TokenJava.OPENING_ARRAY, arg);
                dim.accept(this, arg);
                addToken(endOfToken(arg), TokenJava.CLOSING_ARRAY, arg);
            }
        } else {
            addToken(endOfToken(arg), TokenJava.OPENING_ARRAY, arg);
            addToken(endOfToken(arg), TokenJava.CLOSING_ARRAY, arg);
            n.getInitializer().accept(this, arg);
        }

    }

    public void visit(ArrayInitializerExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.OPENING_BRACE, arg);
        addParameterList(n.getValues(), arg);

        addToken(endOfToken(arg), TokenJava.CLOSING_BRACE, arg);

    }

    public void visit(AssertStmt n, List<Token> arg) {
        genericVisit(n, arg);
        n.getCheck().accept(this, arg);
        notNull(n.getMessage()).accept(this, arg);
    }

    public void visit(AssignExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getTarget().accept(this, arg);
        addToken(nextNode(n.getTarget()), TokenJava.ASSIGNEMENT, arg);
        n.getValue().accept(this, arg);
    }

    public void visit(BinaryExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getLeft().accept(this, arg);
        addOperator(nextNode(n.getLeft()), arg, n.getOperator());
        n.getRight().accept(this, arg);
    }

    public void visit(BlockComment n, List<Token> arg) {
        genericVisit(n, arg);
    }

    public void visit(BlockStmt n, List<Token> arg) {
        genericVisit(n, arg);
        Position nextToken = nextToken(arg);
        int beginLine = n.getBeginLine();
        int beginColumn = n.getBeginColumn();
        addToken(n, TokenJava.OPENING_BRACE, arg);
        acceptList(n.getStmts(), arg);
        addToken(finNode(n), TokenJava.CLOSING_BRACE, arg);
    }

    public void visit(BooleanLiteralExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, n.toString(), arg);
    }

    public void visit(BreakStmt n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.BREAK, arg);
        addToken(finToken(n, TokenJava.BREAK), TokenJava.ENDING_STATEMENT, arg);
    }

    public void visit(CastExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.OPENING_PARENTHESIS, arg);
        n.getType().accept(this, arg);
        addToken(nextNode(n.getType()), TokenJava.CLOSING_PARENTHESIS, arg);
        n.getExpr().accept(this, arg);
    }

    public void visit(CatchClause n, List<Token> arg) {
        genericVisit(n, arg);

        addToken(n, TokenJava.CATCH, arg);
        
        addToken(finToken(n, TokenJava.CATCH), TokenJava.OPENING_PARENTHESIS, arg);
//        addToken(avantToken(n.getExcept(), TokenJava.PARENTHESE_OUVRANTE), TokenJava.PARENTHESE_OUVRANTE, arg);
        n.getExcept().accept(this, arg);
        addToken(nextNode(n.getExcept().getId()), TokenJava.CLOSING_PARENTHESIS, arg);
        n.getCatchBlock().accept(this, arg);
    }

    public void visit(CharLiteralExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, "'" + n.getValue() + "'", arg);
    }

    public void visit(ClassExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getType().accept(this, arg);

        addToken(nextNode(n.getType()), TokenJava.DOT, arg);
        addToken(endOfToken(arg), TokenJava.CLASS, arg);
    }

    public void visit(ClassOrInterfaceDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        visitBodyHeader(n, arg);

        int endColumn = n.getBeginColumn();
        addModifier(n, n.getModifiers(), arg);
        
        Position position = position(n);      
        if (n.getModifiers()!=0) {
            position = nextToken(arg);
        }
        
        TokenJava typeObjet = n.isInterface() ? TokenJava.INTERFACE : TokenJava.CLASS;
        endColumn += typeObjet.getTokenValue().length() + 1;
        addToken(position, typeObjet, arg);
        position = nextToken(arg);
        addToken(position, n.getName(), arg);
        endColumn += n.getName().length();

        if (currentClassList.isEmpty()) {
            currentClassList.push(currentPackage + "." + n.getName());
        } else {
            currentClassList.push(currentClassList.peek() + "$" + n.getName());
        }

        acceptList(n.getTypeParameters(), arg);

        for (ClassOrInterfaceType c : notNull(n.getExtends())) {
            addToken(avantToken(c, TokenJava.EXTENDS), TokenJava.EXTENDS, arg);
            c.accept(this, arg);
            endColumn = c.getEndColumn() + 1;
        }

        if (isNotEmpty(n.getImplements())) {            
            addToken(nextToken(arg), TokenJava.IMPLEMENTS, arg);
            boolean first = true;
            for (ClassOrInterfaceType c : n.getImplements()) {
                first = addIfTrue(endOfToken(arg), TokenJava.PARAMETER_SEPARATOR, arg, !first);
                c.accept(this, arg);
            }
        }

        addToken(endOfToken(arg), TokenJava.OPENING_BRACE, arg);

        acceptList(notNull(n.getMembers()), arg);

        addToken(finNode(n), TokenJava.CLOSING_BRACE, arg);
        currentClassList.pop();
    }

    public void visit(ClassOrInterfaceType n, List<Token> arg) {
        genericVisit(n, arg);
        notNull(n.getScope()).accept(this, arg);
        
        Position position = position(n);
       
        if (n.getScope() != null) {
            position = finNode(n.getScope());
        }
        addToken(position, n.getName(), TypeJava.CLASS, arg);
        
        if (isNotEmpty(n.getTypeArgs())) {            
            addToken(endOfToken(arg), TokenJava.OPENING_GENERIC, arg);
            acceptList(n.getTypeArgs(), arg);
            addToken(endOfToken(arg), TokenJava.CLOSING_GENERIC, arg);
        }
    }

    public void visit(CompilationUnit n, List<Token> arg) {
        genericVisit(n, arg);
        notNull(n.getPackage()).accept(this, arg);
        acceptList(n.getImports(), arg);
        acceptList(n.getTypes(), arg);
    }

    public void visit(ConditionalExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getCondition().accept(this, arg);
        n.getThenExpr().accept(this, arg);
        n.getElseExpr().accept(this, arg);
    }

    // TODO mutualiser avec visit(MethodDeclaration
    public void visit(ConstructorDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        visitBodyHeader(n, arg);

        addModifier(n, n.getModifiers(), arg);
        int modifierSize = Modifier.toString(n.getModifiers()).length();
        int column = n.getBeginColumn();
        column += (modifierSize == 0 ? 0 : modifierSize + 1);

        addToken(n.getBeginLine(), column, n.getName(), arg);

        column += n.getName().length();

        addToken(new Position(n.getBeginLine(), column), TokenJava.OPENING_PARENTHESIS, arg);
        column += TokenJava.OPENING_PARENTHESIS.getTokenValue().length();

        acceptList(n.getTypeParameters(), arg);
        acceptList(n.getParameters(), arg);     

        addToken(new Position(n.getBeginLine(), column), TokenJava.CLOSING_PARENTHESIS, arg);
        int currentColumn = column + TokenJava.CLOSING_PARENTHESIS.getTokenValue().length();

        if (n.getThrows() != null && !n.getThrows().isEmpty()) {
            addToken(avantToken(n.getThrows().get(0), TokenJava.THROWS), TokenJava.THROWS, arg);
            addParameterList(n.getThrows(), arg);
            currentColumn = n.getThrows().get(n.getThrows().size() - 1).getEndColumn() + 1;
        }
        addToken(n.getBeginLine(), currentColumn, TokenJava.METHOD_LIMIT, arg);

        n.getBlock().accept(this, arg);
    }

    public void visit(ContinueStmt n, List<Token> arg) {
        genericVisit(n, arg);
    }

    public void visit(DoStmt n, List<Token> arg) {
        genericVisit(n, arg);
        n.getBody().accept(this, arg);
        n.getCondition().accept(this, arg);
    }

    public void visit(DoubleLiteralExpr n, List<Token> arg) {
        genericVisit(n, arg);
    }

    public void visit(EmptyMemberDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        visitBodyHeader(n, arg);
    }

    public void visit(EmptyStmt n, List<Token> arg) {
        genericVisit(n, arg);
    }

    public void visit(EmptyTypeDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        visitBodyHeader(n, arg);
    }

    public void visit(EnclosedExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.OPENING_PARENTHESIS, arg);
        n.getInner().accept(this, arg);
        addToken(finNode(n), TokenJava.CLOSING_PARENTHESIS, arg);
    }

    public void visit(EnumConstantDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        visitBodyHeader(n, arg);

        acceptList(n.getArgs(), arg);     
        acceptList(n.getClassBody(), arg);     
       
    }

    public void visit(EnumDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        visitBodyHeader(n, arg);

        acceptList(n.getImplements(), arg);     
        acceptList(n.getEntries(), arg);     
        acceptList(n.getMembers(), arg);            
    }

    public void visit(ExplicitConstructorInvocationStmt n, List<Token> arg) {
        genericVisit(n, arg);
        if (!n.isThis()) {
            notNull(n.getExpr()).accept(this, arg);
        }
        
        acceptList(n.getTypeArgs(), arg);    
        acceptList(n.getArgs(), arg);    
    }

    public void visit(ExpressionStmt n, List<Token> arg) {
        genericVisit(n, arg);
        n.getExpression().accept(this, arg);
        addToken(finNode(n), TokenJava.ENDING_STATEMENT, arg);
    }

    public void visit(FieldAccessExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getScope().accept(this, arg);
        addToken(n, TokenJava.DOT, arg);
        addToken(n, n.getField(), arg);
    }

    public void visit(FieldDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        visitBodyHeader(n, arg);
        addModifier(n, n.getModifiers(), arg);
        n.getType().accept(this, arg);
        addParameterList(n.getVariables(), arg);
        addToken(finNode(n), TokenJava.ENDING_STATEMENT, arg);
    }

    public void visit(ForeachStmt n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.FOR, arg);
        addToken(n, TokenJava.OPENING_PARENTHESIS, arg);
        n.getVariable().accept(this, arg);
        addToken(n, TokenJava.TRAVEL_LIST, arg);
        n.getIterable().accept(this, arg);
        addToken(n, TokenJava.CLOSING_PARENTHESIS, arg);
        n.getBody().accept(this, arg);
    }

    public void visit(ForStmt n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.FOR, arg);

        Position currentPosition = finToken(n, TokenJava.FOR);
        addToken(currentPosition, TokenJava.OPENING_PARENTHESIS, arg);

        currentPosition = new Position(currentPosition.line, currentPosition.column + TokenJava.OPENING_PARENTHESIS.getTokenValue().length());

        for (Expression e : notNull(n.getInit())) {
            e.accept(this, arg);
            currentPosition = nextNode(e);
        }
        addToken(currentPosition, TokenJava.ENDING_STATEMENT, arg);

        if (n.getCompare() != null) {
            n.getCompare().accept(this, arg);
            currentPosition = nextNode(n.getCompare());
        }
        addToken(currentPosition, TokenJava.ENDING_STATEMENT, arg);

        for (Expression e : notNull(n.getUpdate())) {
            e.accept(this, arg);
            currentPosition = nextNode(e);
        }
        addToken(currentPosition, TokenJava.CLOSING_PARENTHESIS, arg);

        n.getBody().accept(this, arg);
    }

    public void visit(IfStmt n, List<Token> arg) {
        genericVisit(n, arg);

        addToken(n, TokenJava.IF, arg);
        addToken(finToken(n, TokenJava.IF), TokenJava.OPENING_PARENTHESIS, arg);
        n.getCondition().accept(this, arg);
        addToken(nextNode(n.getCondition()), TokenJava.CLOSING_PARENTHESIS, arg);
        n.getThenStmt().accept(this, arg);

        Statement elseStmt = n.getElseStmt();
        if (elseStmt != null) {
            addToken(nextNode(n.getThenStmt()), TokenJava.ELSE, arg);
            elseStmt.accept(this, arg);
        }
    }

    public void visit(ImportDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.IMPORT, arg);
        n.getName().accept(this, arg);
        addToken(finNode(n), TokenJava.ENDING_STATEMENT, arg);
    }

    public void visit(InitializerDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        visitBodyHeader(n, arg);

        addIfTrue(n, TokenJava.STATIC, arg, n.isStatic());
        n.getBlock().accept(this, arg);
    }

    public void visit(InstanceOfExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getExpr().accept(this, arg);
        addToken(afterWithSpace(n.getExpr()), TokenJava.INSTANCE_OF, arg);
        n.getType().accept(this, arg);
    }

    public void visit(IntegerLiteralExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, n.getValue(), arg);
    }

    public void visit(IntegerLiteralMinValueExpr n, List<Token> arg) {
        genericVisit(n, arg);
    }

    public void visit(JavadocComment n, List<Token> arg) {
        genericVisit(n, arg);
    }

    public void visit(LabeledStmt n, List<Token> arg) {
        genericVisit(n, arg);
        n.getStmt().accept(this, arg);
    }

    public void visit(LineComment n, List<Token> arg) {
        genericVisit(n, arg);
    }

    public void visit(LongLiteralExpr n, List<Token> arg) {
        genericVisit(n, arg);
    }

    public void visit(LongLiteralMinValueExpr n, List<Token> arg) {
        genericVisit(n, arg);
    }

    public void visit(MarkerAnnotationExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getName().accept(this, arg);
    }

    public void visit(MemberValuePair n, List<Token> arg) {
        genericVisit(n, arg);
        n.getValue().accept(this, arg);
    }

    public void visit(MethodCallExpr n, List<Token> arg) {
        genericVisit(n, arg);
        Position currentPosition = position(n);
        if (n.getScope() != null) {
            n.getScope().accept(this, arg);
            currentPosition = nextNode(n.getScope());
            addToken(currentPosition, TokenJava.DOT, arg);
            currentPosition = new Position(currentPosition.line, currentPosition.column + 1);
        }

        addToken(currentPosition, n.getName(), TypeJava.METHOD, arg);
        currentPosition = new Position(currentPosition.line, currentPosition.column + n.getName().length());

        addToken(currentPosition, TokenJava.OPENING_PARENTHESIS, arg);
        acceptList(n.getTypeArgs(), arg);
        addParameterList(n.getArgs(), arg);

        addToken(finNode(n), TokenJava.CLOSING_PARENTHESIS, arg);
    }

    public void visit(MethodDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        visitBodyHeader(n, arg);
        acceptList(n.getTypeParameters(), arg);

        addModifier(n, n.getModifiers(), arg);

        n.getType().accept(this, arg);

        addToken(afterWithSpace(n.getType()), n.getName(), arg);

        int line = n.getType().getEndLine();
        int column = n.getType().getEndColumn() + n.getName().length() + 1;
        column++;
        addToken(line, column, TokenJava.OPENING_PARENTHESIS, arg);

        boolean first = true;
        Position lastPostion = null;
        for (Parameter p : notNull(n.getParameters())) {
            first = addIfTrue(lastPostion, TokenJava.PARAMETER_SEPARATOR, arg, !first);
            p.accept(this, arg);
            line = p.getEndLine();
            column = p.getEndColumn();
            lastPostion = nextNode(p);
        }

        column++;
        addToken(line, column, TokenJava.CLOSING_PARENTHESIS, arg);
        int currentColumn = column + TokenJava.CLOSING_PARENTHESIS.getTokenValue().length();

        if (n.getThrows() != null && !n.getThrows().isEmpty()) {
            addToken(avantToken(n.getThrows().get(0), TokenJava.THROWS), TokenJava.THROWS, arg);
            addParameterList(n.getThrows(), arg);
            currentColumn = n.getThrows().get(n.getThrows().size() - 1).getEndColumn() + 1;
        }
        addToken(line, currentColumn, TokenJava.METHOD_LIMIT, arg);

        if (n.getBody() != null) {
            Position startPosition = position(n.getBody());
            // addToken(startPosition, TokenJava.METHOD_LIMIT, arg);
            int startTokenPosition = firstTokenNumber + arg.size() - 1;
            n.getBody().accept(this, arg);
            Position endPosition = nextNode(n.getBody());
            addToken(endPosition, TokenJava.METHOD_LIMIT, arg);
            int endTokenPosition = firstTokenNumber + arg.size() - 1;
            String classContexte = "";
            if (!currentClassList.isEmpty()) {
                classContexte = currentClassList.peek() + ".";
            }
            MethodLocation methodLocalisation =
                    new MethodLocation(classContexte + n.getName(), new Location(fileName, startPosition.line, startPosition.column),
                            new Location(fileName, endPosition.line, endPosition.column), new IntRange(startTokenPosition, endTokenPosition));

            methodList.add(methodLocalisation);
        }
    }

    public void visit(NameExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, n.getName(), arg);
    }

    public void visit(NormalAnnotationExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getName().accept(this, arg);

        acceptList(n.getPairs(), arg);
    }

    public void visit(NullLiteralExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.NULL, arg);

    }

    public void visit(ObjectCreationExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.NEW, arg);

        if (n.getScope() != null) {
            n.getScope().accept(this, arg);
            addToken(n, TokenJava.DOT, arg);
        }

        acceptList(n.getTypeArgs(), arg);
     
        n.getType().accept(this, arg);
        addToken(nextNode(n.getType()), TokenJava.OPENING_PARENTHESIS, arg);

        addParameterList(n.getArgs(), arg);

        acceptList(n.getAnonymousClassBody(), arg);
        addToken(finNode(n), TokenJava.CLOSING_PARENTHESIS, arg);

    }

    public void visit(PackageDeclaration n, List<Token> arg) {
        genericVisit(n, arg);

        acceptList(n.getAnnotations(), arg);

        addToken(n, TokenJava.PACKAGE, arg);
        currentPackage = n.getName().toString();
        n.getName().accept(this, arg);
        addToken(finNode(n), TokenJava.ENDING_STATEMENT, arg);
    }

    public void visit(Parameter n, List<Token> arg) {
        genericVisit(n, arg);
        acceptList(n.getAnnotations(), arg);
        n.getType().accept(this, arg);
        n.getId().accept(this, arg);
    }

    public void visit(PrimitiveType n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, n.getType().name().toLowerCase(), arg);
    }

    public void visit(QualifiedNameExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getQualifier().accept(this, arg);
        addToken(n.getEndLine(), n.getEndColumn() - n.getName().length(), TokenJava.DOT, arg);
        addToken(n.getEndLine(), n.getEndColumn() - n.getName().length() + 1, n.getName(), arg);
    }

    public void visit(ReferenceType n, List<Token> arg) {
        genericVisit(n, arg);
        n.getType().accept(this, arg);
        int arrayCount = n.getArrayCount();
        for (int i = 0; i < arrayCount; i++) {
            addToken(endOfToken(arg), TokenJava.OPENING_ARRAY, arg);
            addToken(endOfToken(arg), TokenJava.CLOSING_ARRAY, arg);
        }
    }

    public void visit(ReturnStmt n, List<Token> arg) {
        genericVisit(n, arg);

        addToken(n, TokenJava.RETURN, arg);
        notNull(n.getExpr()).accept(this, arg);

        addToken(finNode(n), TokenJava.ENDING_STATEMENT, arg);
    }

    public void visit(SingleMemberAnnotationExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getName().accept(this, arg);
        n.getMemberValue().accept(this, arg);
    }

    public void visit(StringLiteralExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, "\"" + n.getValue() + "\"", TypeJava.STRING, arg);

    }

    public void visit(SuperExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.SUPER, arg);

        notNull(n.getClassExpr()).accept(this, arg);
    }

    public void visit(SwitchEntryStmt n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.CASE, arg);
        notNull(n.getLabel()).accept(this, arg);

        addToken(nextNode(n.getLabel()==null?n:n.getLabel()), TokenJava.CASE_SEPARATEUR, arg);
        acceptList(n.getStmts(), arg);

    }

    public void visit(SwitchStmt n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.SWITCH, arg);

        addToken(finToken(n, TokenJava.SWITCH), TokenJava.OPENING_PARENTHESIS, arg);
        n.getSelector().accept(this, arg);
        Position endParenthese = nextNode(n.getSelector());
        addToken(endParenthese, TokenJava.CLOSING_PARENTHESIS, arg);

        Position position = new Position(endParenthese.line, endParenthese.column + TokenJava.CLOSING_PARENTHESIS.getTokenValue().length());
        addToken(position, TokenJava.OPENING_BRACE, arg);
        acceptList(n.getEntries(), arg);       
        addToken(finNode(n), TokenJava.CLOSING_BRACE, arg);
    }

    public void visit(SynchronizedStmt n, List<Token> arg) {
        genericVisit(n, arg);
        n.getExpr().accept(this, arg);
        n.getBlock().accept(this, arg);

    }

    public void visit(ThisExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.THIS, arg);
        notNull(n.getClassExpr()).accept(this, arg);
    }

    public void visit(ThrowStmt n, List<Token> arg) {
        genericVisit(n, arg);
        n.getExpr().accept(this, arg);
    }

    public void visit(TryStmt n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.TRY, arg);

        n.getTryBlock().accept(this, arg);
        for (CatchClause c : notNull(n.getCatchs())) {
            c.accept(this, arg);
        }
        if (n.getFinallyBlock() != null) {
            addToken(avantToken(n.getFinallyBlock(), TokenJava.FINALLY), TokenJava.FINALLY, arg);
            n.getFinallyBlock().accept(this, arg);
        }
    }

    public void visit(TypeDeclarationStmt n, List<Token> arg) {
        genericVisit(n, arg);
        n.getTypeDeclaration().accept(this, arg);
    }

    public void visit(TypeParameter n, List<Token> arg) {
        genericVisit(n, arg);
        acceptList(n.getTypeBound(), arg);       
    }

    public void visit(UnaryExpr n, List<Token> arg) {
        genericVisit(n, arg);

        if (isPostOperator(n.getOperator())) {
            n.getExpr().accept(this, arg);
            addUnaryExpr(nextNode(n.getExpr()), arg, n.getOperator());
        } else {
            addUnaryExpr(position(n), arg, n.getOperator());
            n.getExpr().accept(this, arg);
        }

    }

    public void visit(VariableDeclarationExpr n, List<Token> arg) {
        genericVisit(n, arg);
        acceptList(n.getAnnotations(), arg);     
        n.getType().accept(this, arg);
        addParameterList(n.getVars(), arg);
    }

    public void visit(VariableDeclarator n, List<Token> arg) {
        genericVisit(n, arg);
        n.getId().accept(this, arg);
        if (n.getInit() != null) {
            addToken(nextNode(n.getId()), TokenJava.ASSIGNEMENT, arg);
            n.getInit().accept(this, arg);
        }
    }

    public void visit(VariableDeclaratorId n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, n.getName(), TypeJava.VARIABLE, arg);
    }

    public void visit(VoidType n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.VOID, arg);
    }

    public void visit(WhileStmt n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.WHILE, arg);
        addToken(finToken(n, TokenJava.WHILE), TokenJava.OPENING_PARENTHESIS, arg);
        n.getCondition().accept(this, arg);
        addToken(nextNode(n.getCondition()), TokenJava.CLOSING_PARENTHESIS, arg);
        n.getBody().accept(this, arg);
    }

    public void visit(WildcardType n, List<Token> arg) {
        genericVisit(n, arg);
        notNull(n.getExtends()).accept(this, arg);
        notNull(n.getSuper()).accept(this, arg);
    }
    
    @Override
    public void visit(MultiTypeParameter n, List<Token> arg) {
        genericVisit(n, arg);
        List<Type> types = n.getTypes();
        
        boolean first = true;
        Position lastPostion = null;
        for (Type t : notNull(n.getTypes())) {
            first = addIfTrue(lastPostion, TokenJava.EXCPTION_SEPARATOR, arg, !first);
            t.accept(this, arg);         
            lastPostion = nextNode(t);
        }
        
        n.getId().accept(this, arg);
    }

    @Override
    public void visit(UnknownType n, List<Token> arg) {
        genericVisit(n, arg);
        
    }

    @Override
    public void visit(LambdaExpr n, List<Token> arg) {
        genericVisit(n, arg);
        
    }

    @Override
    public void visit(MethodReferenceExpr n, List<Token> arg) {
        genericVisit(n, arg);
        
    }

    @Override
    public void visit(TypeExpr arg0, List<Token> arg1) {
        // TODO Auto-generated method stub
        
    }

    private void visitBodyHeader(BodyDeclaration n, List<Token> arg) {
        // TODO Check we can replace getJavaDoc by getComment
//        notNull(n.getJavaDoc()).accept(this, arg);
        notNull(n.getComment()).accept(this, arg);
        acceptList(n.getAnnotations(), arg);
    }

    private void addOperator(Position position, List<Token> arg, BinaryExpr.Operator operator) {
        SymboleOperator symbole = SymboleOperator.valueOf(operator.toString());
        addToken(position, symbole.toString(), arg);
    }

    private boolean isPostOperator(UnaryExpr.Operator operator) {
        SymboleUnaryExpr symbole = SymboleUnaryExpr.valueOf(operator.toString());
        return symbole.isPost();
    }

    private void addUnaryExpr(Position position, List<Token> arg, UnaryExpr.Operator operator) {
        SymboleUnaryExpr symbole = SymboleUnaryExpr.valueOf(operator.toString());
        addToken(position, symbole.toString(), arg);
    }

    public static enum SymboleOperator {
        or("||"), and("&&"), binOr("|"), binAnd("&"), xor("^"), equals("=="), notEquals("!="), less("<"), greater(">"), lessEquals("<="), greaterEquals(">="),
        lShift("<<"), rSignedShift(">>"), rUnsignedShift(">>>"), plus("+"), minus("-"), times("*"), divide("/"), remainder("%");

        private final String symbole;

        SymboleOperator(String symbole) {
            this.symbole = symbole;
        }

        public String toString() {
            return symbole;
        }

    }

    public static enum SymboleUnaryExpr {
        positive("+", false), negative("-", false), preIncrement("++", false), preDecrement("--", false), not("!", false), inverse("~", false),
        posIncrement("++", true), posDecrement("--", true);

        private final String symbole;
        private final boolean isPost;

        SymboleUnaryExpr(String symbole, boolean isPost) {
            this.symbole = symbole;
            this.isPost = isPost;
        }

        public String toString() {
            return symbole;
        }

        public boolean isPost() {
            return isPost;
        }
    }

    private boolean addIfTrue(Node n, TokenJava token, List<Token> arg, boolean condition) {
        return addIfTrue(n.getBeginLine(), n.getBeginColumn(), token, arg, condition);
    }

    private boolean addIfTrue(Position position, TokenJava token, List<Token> arg, boolean condition) {
        if (condition) {
            addToken(position.line, position.column, token, arg);
        }
        return false;
    }

    private boolean addIfTrue(int line, int column, TokenJava token, List<Token> arg, boolean condition) {
        return addIfTrue(new Position(line, column), token, arg, condition);
    }

    private void addToken(int beginLine, int beginColumn, TokenJava token, List<Token> arg) {
        addToken(beginLine, beginColumn, token.getTokenValue(), token.getType(), arg);
    }

    private void addToken(int beginLine, int beginColumn, String token, List<Token> arg) {
        addToken(beginLine, beginColumn, token, fr.sf.once.model.Type.VALUE, arg);
    }

    protected void addToken(int beginLine, int beginColumn, String token, fr.sf.once.model.Type type, List<Token> arg) {
        Token tokenToAdd = null;
        if (fr.sf.once.model.Type.BREAK.is(type)) {
            tokenToAdd = new Token(new Location(fileName, beginLine, beginColumn), "", type) {
                public int getEndingColumn() {
                    return getStartingColumn();
                }
            };            
        } else {
            tokenToAdd = new Token(new Location(fileName, beginLine, beginColumn), token, type);
        }

        if (!arg.isEmpty()) {
            Token lastToken = getLastToken(arg);

            if (lastToken.getLocation().getFileName().equals(fileName)) {
                int tailleToken = lastToken.getType().is(fr.sf.once.model.Type.BREAK) ? 0 : lastToken.getTokenValue().length();
//                if (lastToken.getLigneDebut() > beginLine) {
//                    LOG.error("Nouveau token avant le précédent");
//                    LOG.error("Dernier:" + lastToken.format());
//                    LOG.error("Nouveau:" + tokenToAdd.format());
//                } else if (lastToken.getLigneDebut() == beginLine && lastToken.getColonneDebut() + tailleToken > beginColumn) {
//                    LOG.error("Nouveau token avant le précédent");
//                    LOG.error("Dernier:" + lastToken.format());
//                    LOG.error("Nouveau:" + tokenToAdd.format());
//                }
            }
        }
        arg.add(tokenToAdd);
    }

    private void addToken(Node n, String token, List<Token> arg) {
        addToken(n.getBeginLine(), n.getBeginColumn(), token, arg);
    }

    private void addToken(Node n, String token, fr.sf.once.model.Type type, List<Token> arg) {
        addToken(n.getBeginLine(), n.getBeginColumn(), token, type, arg);
    }

    private void addToken(Node n, TokenJava token, List<Token> arg) {
        addToken(n.getBeginLine(), n.getBeginColumn(), token, arg);
    }

    private void addToken(Position position, TokenJava token, List<Token> arg) {
        addToken(position.line, position.column, token, arg);
    }

    private void addToken(Position position, String token, List<Token> arg) {
        addToken(position.line, position.column, token, arg);
    }

    private void addToken(Position position, String token, fr.sf.once.model.Type type, List<Token> arg) {
        addToken(position.line, position.column, token, type, arg);
    }

    private void addModifier(Node n, int modifier, List<Token> arg) {
        if (modifier != 0) {
            String[] modifiers = Modifier.toString(modifier).split(" ");
            int column = n.getBeginColumn();
            for (String modifierText : modifiers) {
                addToken(n.getBeginLine(), column, modifierText, arg);
                column += modifierText.length() + 1;
            }
        }
    }

    private void addParameterList(List<? extends Node> parameterList, List<Token> arg) {
        if (parameterList != null) {
            boolean first = true;
            for (Node n : parameterList) {
                first = addIfTrue(avantToken(n, TokenJava.PARAMETER_SEPARATOR), TokenJava.PARAMETER_SEPARATOR, arg, !first);
                n.accept(this, arg);
            }
        }
    }

    static class Position {

        private final int line;
        private final int column;

        public Position(int line, int column) {
            this.line = line;
            this.column = column;
        }
    }

    private Position position(Node node) {
        return new Position(node.getBeginLine(), node.getBeginColumn());
    }

    private Position finNode(Node node) {
        return new Position(node.getEndLine(), node.getEndColumn());
    }

    private Position nextNode(Node node) {
        return new Position(node.getEndLine(), node.getEndColumn() + 1);
    }

    private Position afterWithSpace(Node node) {
        // EndColumn is the position of the last character of the token.
        // +1 to have the position after.
        // +1 to add a space.
        return new Position(node.getEndLine(), node.getEndColumn() + 2);
    }

    /**
     * Position just after the token (no space).
     * @param arg
     * @return
     */
    private Position endOfToken(List<Token> arg) {
        return endOfToken(getLastToken(arg));
    }

    private Position endOfToken(Token token) {
        return new Position(token.getStartingLine(), token.getEndingColumn());
    }
    
    /**
     * Position after the token with space.
     * @param arg
     * @return
     */
    private Position nextToken(List<Token> arg) {       
        Token lastToken = getLastToken(arg);
        if (fr.sf.once.model.Type.BREAK.is(lastToken.getType())) {
            
        }
        return nextToken(lastToken);
    }
    
    private Position nextToken(Token token) {
        return new Position(token.getStartingLine(), token.getEndingColumn() + 1);
    }    
    
    private Position finToken(Node n, Token token) {
        return new Position(n.getBeginLine(), n.getBeginColumn() + token.getTokenValue().length());
    }

    private Position avantToken(Node n, Token token) {
        return new Position(n.getBeginLine(), n.getBeginColumn() - token.getTokenValue().length() - 1);
    }

    public static final Token NO_TOKEN = new Token(new Location("", 0, 0),"", fr.sf.once.model.Type.NOT_SIGNIFICANT);
    private Token getLastToken(List<Token> arg) {
        if (arg.isEmpty()) {
            return NO_TOKEN;
        } else {
            return arg.get(arg.size()-1);
        }
    }
    
    
    private void acceptList(List<? extends Node> nodeList, List<Token> arg) {        
        for (Node node : notNull(nodeList)) {
            node.accept(this, arg);
        }
    }
    
    private static final Node EMPTY_NODE = new Node() {

        @Override
        public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
            return null;
        }

        @Override
        public <A> void accept(VoidVisitor<A> v, A arg) {
        }

    };
    
    private <T> List<T> notNull(List<T> list) {
        return (list != null) ? list : Collections.<T> emptyList();
    }

    private <T extends Node> Node notNull(T node) {
        return (node != null) ? node : EMPTY_NODE;
    }
    
    private <T> boolean isNotEmpty(List<T> list) {
        return list != null && !list.isEmpty();
    }
}
