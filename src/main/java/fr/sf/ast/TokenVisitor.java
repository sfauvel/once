package fr.sf.ast;

import japa.parser.ast.BlockComment;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.LineComment;
import japa.parser.ast.Node;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.AnnotationDeclaration;
import japa.parser.ast.body.AnnotationMemberDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EmptyMemberDeclaration;
import japa.parser.ast.body.EmptyTypeDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.InitializerDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.CharLiteralExpr;
import japa.parser.ast.expr.ClassExpr;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.InstanceOfExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.IntegerLiteralMinValueExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.LongLiteralMinValueExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.SuperExpr;
import japa.parser.ast.expr.ThisExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.EmptyStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.LabeledStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.SwitchEntryStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.SynchronizedStmt;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.TypeDeclarationStmt;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.type.VoidType;
import japa.parser.ast.type.WildcardType;
import japa.parser.ast.visitor.VoidVisitor;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.sf.once.Localisation;
import fr.sf.once.MethodLocalisation;
import fr.sf.once.Token;

public class TokenVisitor implements VoidVisitor<List<Token>> {

    static final Logger logSortie = Logger.getLogger("SORTIE");
    private final String fileName;
    private final List<MethodLocalisation> methodList;

    public TokenVisitor() {
        this("");
    }

    public TokenVisitor(String fileName) {
        this(fileName, new ArrayList<MethodLocalisation>());
    }

    public TokenVisitor(String fileName, List<MethodLocalisation> methodList) {
        this.fileName = fileName;
        this.methodList = methodList;
    }

    public void genericVisit(Node n, List<Token> arg) {
    }

    public void visit(AnnotationDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        if (n.getMembers() != null) {
            for (BodyDeclaration member : n.getMembers()) {
                member.accept(this, arg);
            }
        }
    }

    public void visit(AnnotationMemberDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        n.getType().accept(this, arg);
        if (n.getDefaultValue() != null) {
            n.getDefaultValue().accept(this, arg);
        }
    }

    public void visit(ArrayAccessExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getName().accept(this, arg);
        addToken(n, TokenJava.TABLEAU_OUVRANT, arg);
        n.getIndex().accept(this, arg);
        addToken(n, TokenJava.TABLEAU_FERMANT, arg);

    }

    public void visit(ArrayCreationExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.NEW, arg);
        n.getType().accept(this, arg);
        if (n.getDimensions() != null) {
            for (Expression dim : n.getDimensions()) {
                addToken(n, TokenJava.TABLEAU_OUVRANT, arg);
                dim.accept(this, arg);
                addToken(n, TokenJava.TABLEAU_FERMANT, arg);
            }
        } else {
            addToken(n, TokenJava.TABLEAU_OUVRANT, arg);
            addToken(n, TokenJava.TABLEAU_FERMANT, arg);
            n.getInitializer().accept(this, arg);
        }

    }

    public void visit(ArrayInitializerExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.ACCOLADE_OUVRANTE, arg);
        addParameterList(n.getValues(), arg);

        addToken(n, TokenJava.ACCOLADE_FERMANTE, arg);

    }

    public void visit(AssertStmt n, List<Token> arg) {
        genericVisit(n, arg);
        n.getCheck().accept(this, arg);
        if (n.getMessage() != null) {
            n.getMessage().accept(this, arg);
        }
    }

    public void visit(AssignExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getTarget().accept(this, arg);
        addToken(nextNode(n.getTarget()), TokenJava.AFFECTATION, arg);
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
        addToken(n, TokenJava.ACCOLADE_OUVRANTE, arg);
        if (n.getStmts() != null) {
            for (Statement s : n.getStmts()) {
                s.accept(this, arg);
            }
        }
        addToken(finNode(n), TokenJava.ACCOLADE_FERMANTE, arg);
    }

    public void visit(BooleanLiteralExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, n.toString(), arg);
    }

    public void visit(BreakStmt n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.BREAK, arg);
        addToken(n, TokenJava.FIN_INSTRUCTION, arg);
    }

    public void visit(CastExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.PARENTHESE_OUVRANTE, arg);
        n.getType().accept(this, arg);
        addToken(nextNode(n.getType()), TokenJava.PARENTHESE_FERMANTE, arg);
        n.getExpr().accept(this, arg);
    }

    public void visit(CatchClause n, List<Token> arg) {
        genericVisit(n, arg);

        addToken(avantToken(n.getExcept(), TokenJava.PARENTHESE_OUVRANTE), TokenJava.PARENTHESE_OUVRANTE, arg);
        n.getExcept().accept(this, arg);
        addToken(nextNode(n.getExcept()), TokenJava.PARENTHESE_FERMANTE, arg);
        n.getCatchBlock().accept(this, arg);
    }

    public void visit(CharLiteralExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, "'" + n.getValue() + "'", arg);
    }

    public void visit(ClassExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getType().accept(this, arg);

        addToken(n, TokenJava.POINT, arg);
        addToken(n, TokenJava.CLASS, arg);
    }

    public void visit(ClassOrInterfaceDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }

        addModifier(n, n.getModifiers(), arg);
        TokenJava typeObjet = n.isInterface() ? TokenJava.INTERFACE : TokenJava.CLASS;
        addToken(n, typeObjet, arg);
        addToken(finToken(n, typeObjet), n.getName(), arg);

        if (n.getTypeParameters() != null) {
            for (TypeParameter t : n.getTypeParameters()) {
                t.accept(this, arg);
            }
        }
        if (n.getExtends() != null) {
            for (ClassOrInterfaceType c : n.getExtends()) {
                addToken(avantToken(c, TokenJava.EXTENDS), TokenJava.EXTENDS, arg);
                c.accept(this, arg);
            }
        }

        if (n.getImplements() != null) {
            addToken(n, TokenJava.IMPLEMENTS, arg);
            boolean first = true;
            for (ClassOrInterfaceType c : n.getImplements()) {
                first = addIfTrue(n, TokenJava.SEPARATEUR_PARAMETRE, arg, !first);
                c.accept(this, arg);
            }
        }

        if (n.getMembers() != null && !n.getMembers().isEmpty()) {
            BodyDeclaration bodyDeclaration = n.getMembers().get(0);
            addToken(bodyDeclaration.getBeginLine(), bodyDeclaration.getBeginColumn() - 1, TokenJava.ACCOLADE_OUVRANTE, arg);
        } else {
            addToken(n.getEndLine(), n.getEndColumn() - 1, TokenJava.ACCOLADE_OUVRANTE, arg);

        }
        if (n.getMembers() != null) {
            for (BodyDeclaration member : n.getMembers()) {
                member.accept(this, arg);
            }
        }
        addToken(finNode(n), TokenJava.ACCOLADE_FERMANTE, arg);
    }

    public void visit(ClassOrInterfaceType n, List<Token> arg) {
        genericVisit(n, arg);
        if (n.getScope() != null) {
            n.getScope().accept(this, arg);
        }
        addToken(n, n.getName(), arg);
        if (n.getTypeArgs() != null) {

            addToken(n, TokenJava.GENERIQUE_OUVRANTE, arg);
            for (Type t : n.getTypeArgs()) {
                t.accept(this, arg);
            }
            addToken(n, TokenJava.GENERIQUE_FERMANTE, arg);
        }
    }

    public void visit(CompilationUnit n, List<Token> arg) {
        genericVisit(n, arg);
        if (n.getPackage() != null) {
            n.getPackage().accept(this, arg);
        }
        if (n.getImports() != null) {
            for (ImportDeclaration i : n.getImports()) {
                i.accept(this, arg);
            }
        }
        if (n.getTypes() != null) {
            for (TypeDeclaration typeDeclaration : n.getTypes()) {
                typeDeclaration.accept(this, arg);
            }
        }
    }

    public void visit(ConditionalExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getCondition().accept(this, arg);
        n.getThenExpr().accept(this, arg);
        n.getElseExpr().accept(this, arg);
    }

    public void visit(ConstructorDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }

        addModifier(n, n.getModifiers(), arg);

        addToken(n, n.getName(), arg);

        addToken(n, TokenJava.PARENTHESE_OUVRANTE, arg);

        if (n.getTypeParameters() != null) {
            for (TypeParameter t : n.getTypeParameters()) {
                t.accept(this, arg);
            }
        }
        if (n.getParameters() != null) {
            for (Parameter p : n.getParameters()) {
                p.accept(this, arg);
            }
        }

        addToken(n, TokenJava.PARENTHESE_FERMANTE, arg);

        if (n.getThrows() != null) {
            for (NameExpr name : n.getThrows()) {
                name.accept(this, arg);
            }
        }
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
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
    }

    public void visit(EmptyStmt n, List<Token> arg) {
        genericVisit(n, arg);
    }

    public void visit(EmptyTypeDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
    }

    public void visit(EnclosedExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.PARENTHESE_OUVRANTE, arg);
        n.getInner().accept(this, arg);
        addToken(finNode(n), TokenJava.PARENTHESE_FERMANTE, arg);
    }

    public void visit(EnumConstantDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        if (n.getArgs() != null) {
            for (Expression e : n.getArgs()) {
                e.accept(this, arg);
            }
        }
        if (n.getClassBody() != null) {
            for (BodyDeclaration member : n.getClassBody()) {
                member.accept(this, arg);
            }
        }
    }

    public void visit(EnumDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        if (n.getImplements() != null) {
            for (ClassOrInterfaceType c : n.getImplements()) {
                c.accept(this, arg);
            }
        }
        if (n.getEntries() != null) {
            for (EnumConstantDeclaration e : n.getEntries()) {
                e.accept(this, arg);
            }
        }
        if (n.getMembers() != null) {
            for (BodyDeclaration member : n.getMembers()) {
                member.accept(this, arg);
            }
        }
    }

    public void visit(ExplicitConstructorInvocationStmt n, List<Token> arg) {
        genericVisit(n, arg);
        if (!n.isThis()) {
            if (n.getExpr() != null) {
                n.getExpr().accept(this, arg);
            }
        }
        if (n.getTypeArgs() != null) {
            for (Type t : n.getTypeArgs()) {
                t.accept(this, arg);
            }
        }
        if (n.getArgs() != null) {
            for (Expression e : n.getArgs()) {
                e.accept(this, arg);
            }
        }
    }

    public void visit(ExpressionStmt n, List<Token> arg) {
        genericVisit(n, arg);
        n.getExpression().accept(this, arg);
        addToken(finNode(n), TokenJava.FIN_INSTRUCTION, arg);
    }

    public void visit(FieldAccessExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getScope().accept(this, arg);
        addToken(n, TokenJava.POINT, arg);
        addToken(n, n.getField(), arg);
    }

    public void visit(FieldDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        addModifier(n, n.getModifiers(), arg);
        n.getType().accept(this, arg);
        addParameterList(n.getVariables(), arg);
        addToken(finNode(n), TokenJava.FIN_INSTRUCTION, arg);
    }

    public void visit(ForeachStmt n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.FOR, arg);
        addToken(n, TokenJava.PARENTHESE_OUVRANTE, arg);
        n.getVariable().accept(this, arg);
        addToken(n, TokenJava.PARCOURS_LISTE, arg);
        n.getIterable().accept(this, arg);
        addToken(n, TokenJava.PARENTHESE_FERMANTE, arg);
        n.getBody().accept(this, arg);
    }

    public void visit(ForStmt n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.FOR, arg);

        Position currentPosition = finToken(n, TokenJava.FOR);
        addToken(currentPosition, TokenJava.PARENTHESE_OUVRANTE, arg);

        currentPosition = new Position(currentPosition.line, currentPosition.column + TokenJava.PARENTHESE_OUVRANTE.getValeurToken().length());

        if (n.getInit() != null) {
            for (Expression e : n.getInit()) {
                e.accept(this, arg);
                currentPosition = nextNode(e);
            }
        }
        addToken(currentPosition, TokenJava.FIN_INSTRUCTION, arg);

        if (n.getCompare() != null) {
            n.getCompare().accept(this, arg);
            currentPosition = nextNode(n.getCompare());
        }
        addToken(currentPosition, TokenJava.FIN_INSTRUCTION, arg);

        if (n.getUpdate() != null) {
            for (Expression e : n.getUpdate()) {
                e.accept(this, arg);
                currentPosition = nextNode(e);
            }
        }
        addToken(currentPosition, TokenJava.PARENTHESE_FERMANTE, arg);

        n.getBody().accept(this, arg);
    }

    public void visit(IfStmt n, List<Token> arg) {
        genericVisit(n, arg);

        addToken(n, TokenJava.IF, arg);
        addToken(finToken(n, TokenJava.IF), TokenJava.PARENTHESE_OUVRANTE, arg);
        n.getCondition().accept(this, arg);
        addToken(nextNode(n.getCondition()), TokenJava.PARENTHESE_FERMANTE, arg);
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
        addToken(finNode(n), TokenJava.FIN_INSTRUCTION, arg);
    }

    public void visit(InitializerDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }

        addIfTrue(n, TokenJava.STATIC, arg, n.isStatic());
        n.getBlock().accept(this, arg);
    }

    public void visit(InstanceOfExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getExpr().accept(this, arg);
        addToken(n, TokenJava.INSTANCE_OF, arg);
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
            addToken(currentPosition, TokenJava.POINT, arg);
            currentPosition = new Position(currentPosition.line, currentPosition.column + 1);
        }

        addToken(currentPosition, n.getName(), arg);
        currentPosition = new Position(currentPosition.line, currentPosition.column + n.getName().length());

        addToken(currentPosition, TokenJava.PARENTHESE_OUVRANTE, arg);
        if (n.getTypeArgs() != null) {
            for (Type t : n.getTypeArgs()) {
                t.accept(this, arg);
            }
        }
        addParameterList(n.getArgs(), arg);

        addToken(finNode(n), TokenJava.PARENTHESE_FERMANTE, arg);
    }

    public void visit(MethodDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        if (n.getTypeParameters() != null) {
            for (TypeParameter t : n.getTypeParameters()) {
                t.accept(this, arg);
            }
        }

        addModifier(n, n.getModifiers(), arg);

        n.getType().accept(this, arg);

        addToken(nextNode(n.getType()), n.getName(), arg);

        int line = n.getType().getEndLine();
        int column = n.getType().getEndColumn() + n.getName().length() + 1;
        column++;
        addToken(line, column, TokenJava.PARENTHESE_OUVRANTE, arg);

        if (n.getParameters() != null) {
            boolean first = true;
            Position lastPostion = null;
            for (Parameter p : n.getParameters()) {
                first = addIfTrue(lastPostion, TokenJava.SEPARATEUR_PARAMETRE, arg, !first);
                p.accept(this, arg);
                line = p.getEndLine();
                column = p.getEndColumn();
                lastPostion = nextNode(p);
            }
        }
        column++;
        addToken(line, column, TokenJava.PARENTHESE_FERMANTE, arg);

        if (n.getThrows() != null && !n.getThrows().isEmpty()) {
            addToken(avantToken(n.getThrows().get(0), TokenJava.THROWS), TokenJava.THROWS, arg);
            addParameterList(n.getThrows(), arg);
        }
        if (n.getBody() != null) {
            MethodLocalisation methodLocalisation = new MethodLocalisation();
            methodLocalisation.setMethodName(n.getName());
            Position startPosition = position(n.getBody());
            methodLocalisation.setLocalisationDebut(new Localisation(fileName, startPosition.line, startPosition.column));
            addToken(startPosition, TokenJava.METHOD_LIMIT, arg);
            n.getBody().accept(this, arg);
            Position endPosition = nextNode(n.getBody());
            methodLocalisation.setLocalisationFin(new Localisation(fileName, endPosition.line, endPosition.column));
            addToken(endPosition, TokenJava.METHOD_LIMIT, arg);
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
        if (n.getPairs() != null) {
            for (MemberValuePair m : n.getPairs()) {
                m.accept(this, arg);
            }
        }
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
            addToken(n, TokenJava.POINT, arg);
        }

        if (n.getTypeArgs() != null) {
            for (Type t : n.getTypeArgs()) {
                t.accept(this, arg);
            }
        }
        n.getType().accept(this, arg);
        addToken(nextNode(n.getType()), TokenJava.PARENTHESE_OUVRANTE, arg);

        addParameterList(n.getArgs(), arg);

        if (n.getAnonymousClassBody() != null) {
            for (BodyDeclaration member : n.getAnonymousClassBody()) {
                member.accept(this, arg);
            }
        }
        addToken(finNode(n), TokenJava.PARENTHESE_FERMANTE, arg);

    }

    public void visit(PackageDeclaration n, List<Token> arg) {
        genericVisit(n, arg);
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        addToken(n, TokenJava.PACKAGE, arg);
        n.getName().accept(this, arg);
        addToken(finNode(n), TokenJava.FIN_INSTRUCTION, arg);
    }

    public void visit(Parameter n, List<Token> arg) {
        genericVisit(n, arg);
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
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
        addToken(n.getEndLine(), n.getEndColumn() - n.getName().length(), TokenJava.POINT, arg);
        addToken(n.getEndLine(), n.getEndColumn() - n.getName().length() + 1, n.getName(), arg);
    }

    public void visit(ReferenceType n, List<Token> arg) {
        genericVisit(n, arg);
        n.getType().accept(this, arg);
        int arrayCount = n.getArrayCount();
        for (int i = 0; i < arrayCount; i++) {
            addToken(n, TokenJava.TABLEAU_OUVRANT, arg);
            addToken(n, TokenJava.TABLEAU_FERMANT, arg);
        }
    }

    public void visit(ReturnStmt n, List<Token> arg) {
        genericVisit(n, arg);

        addToken(n, TokenJava.RETURN, arg);
        if (n.getExpr() != null) {
            n.getExpr().accept(this, arg);
        }

        addToken(finNode(n), TokenJava.FIN_INSTRUCTION, arg);
    }

    public void visit(SingleMemberAnnotationExpr n, List<Token> arg) {
        genericVisit(n, arg);
        n.getName().accept(this, arg);
        n.getMemberValue().accept(this, arg);
    }

    public void visit(StringLiteralExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, "\"" + n.getValue() + "\"", arg);

    }

    public void visit(SuperExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.SUPER, arg);

        if (n.getClassExpr() != null) {
            n.getClassExpr().accept(this, arg);
        }
    }

    public void visit(SwitchEntryStmt n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.CASE, arg);
        if (n.getLabel() != null) {
            n.getLabel().accept(this, arg);
        }
        addToken(n, TokenJava.CASE_SEPARATEUR, arg);
        if (n.getStmts() != null) {
            for (Statement s : n.getStmts()) {
                s.accept(this, arg);
            }
        }
    }

    public void visit(SwitchStmt n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.SWITCH, arg);
        addToken(n, TokenJava.PARENTHESE_OUVRANTE, arg);
        n.getSelector().accept(this, arg);
        addToken(n, TokenJava.PARENTHESE_FERMANTE, arg);

        addToken(n, TokenJava.ACCOLADE_OUVRANTE, arg);
        if (n.getEntries() != null) {
            for (SwitchEntryStmt e : n.getEntries()) {
                e.accept(this, arg);
            }
        }
        addToken(n, TokenJava.ACCOLADE_FERMANTE, arg);
    }

    public void visit(SynchronizedStmt n, List<Token> arg) {
        genericVisit(n, arg);
        n.getExpr().accept(this, arg);
        n.getBlock().accept(this, arg);

    }

    public void visit(ThisExpr n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.THIS, arg);
        if (n.getClassExpr() != null) {
            n.getClassExpr().accept(this, arg);
        }
    }

    public void visit(ThrowStmt n, List<Token> arg) {
        genericVisit(n, arg);
        n.getExpr().accept(this, arg);
    }

    public void visit(TryStmt n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.TRY, arg);

        n.getTryBlock().accept(this, arg);
        if (n.getCatchs() != null) {
            for (CatchClause c : n.getCatchs()) {
                addToken(c, TokenJava.CATCH, arg);
                c.accept(this, arg);
            }
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
        if (n.getTypeBound() != null) {
            for (ClassOrInterfaceType c : n.getTypeBound()) {
                c.accept(this, arg);
            }
        }
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
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, arg);
            }
        }
        n.getType().accept(this, arg);
        addParameterList(n.getVars(), arg);

    }

    public void visit(VariableDeclarator n, List<Token> arg) {
        genericVisit(n, arg);
        n.getId().accept(this, arg);
        if (n.getInit() != null) {
            addToken(nextNode(n.getId()), TokenJava.AFFECTATION, arg);
            n.getInit().accept(this, arg);
        }
    }

    public void visit(VariableDeclaratorId n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, n.getName(), arg);
    }

    public void visit(VoidType n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.VOID, arg);
    }

    public void visit(WhileStmt n, List<Token> arg) {
        genericVisit(n, arg);
        addToken(n, TokenJava.WHILE, arg);
        addToken(finToken(n, TokenJava.WHILE), TokenJava.PARENTHESE_OUVRANTE, arg);
        n.getCondition().accept(this, arg);
        addToken(nextNode(n.getCondition()), TokenJava.PARENTHESE_FERMANTE, arg);
        n.getBody().accept(this, arg);
    }

    public void visit(WildcardType n, List<Token> arg) {
        genericVisit(n, arg);
        if (n.getExtends() != null) {
            n.getExtends().accept(this, arg);
        }
        if (n.getSuper() != null) {
            n.getSuper().accept(this, arg);
        }
    }

    private void addOperator(Node n, List<Token> arg, BinaryExpr.Operator operator) {
        SymboleOperator symbole = SymboleOperator.valueOf(operator.toString());
        addToken(n, symbole.toString(), arg);
    }

    private void addOperator(Position position, List<Token> arg, BinaryExpr.Operator operator) {
        SymboleOperator symbole = SymboleOperator.valueOf(operator.toString());
        addToken(position, symbole.toString(), arg);
    }

    private boolean isPostOperator(UnaryExpr.Operator operator) {
        SymboleUnaryExpr symbole = SymboleUnaryExpr.valueOf(operator.toString());
        return symbole.isPost;
    }

    private void addUnaryExpr(Position position, List<Token> arg, UnaryExpr.Operator operator) {
        SymboleUnaryExpr symbole = SymboleUnaryExpr.valueOf(operator.toString());
        addToken(position, symbole.toString(), arg);
    }

    public static enum SymboleOperator {
        or("||"),
        and("&&"),
        binOr("|"),
        binAnd("&"),
        xor("^"),
        equals("=="),
        notEquals("!="),
        less("<"),
        greater(">"),
        lessEquals("<="),
        greaterEquals(">="),
        lShift("<<"),
        rSignedShift(">>"),
        rUnsignedShift(">>>"),
        plus("+"),
        minus("-"),
        times("*"),
        divide("/"),
        remainder("%");

        private final String symbole;

        SymboleOperator(String symbole) {
            this.symbole = symbole;
        }

        public String toString() {
            return symbole;
        }

    }

    public static enum SymboleUnaryExpr {
        positive("+", false),
        negative("-", false),
        preIncrement("++", false),
        preDecrement("--", false),
        not("!", false),
        inverse("~", false),
        posIncrement("++", true),
        posDecrement("--", true);

        private final String symbole;
        private final boolean isPost;

        SymboleUnaryExpr(String symbole, boolean isPost) {
            this.symbole = symbole;
            this.isPost = isPost;
        }

        public String toString() {
            return symbole;
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

    private int currentLine = 0;
    private int currentColumn = 0;

    private void addToken(int beginLine, int beginColumn, TokenJava token, List<Token> arg) {
        addToken(beginLine, beginColumn, token.getValeurToken(), token.getType(), arg);
    }

    private void addToken(int beginLine, int beginColumn, String token, List<Token> arg) {
        addToken(beginLine, beginColumn, token, fr.sf.once.Token.Type.VALEUR, arg);
    }

    protected void addToken(int beginLine, int beginColumn, String token, fr.sf.once.Token.Type type, List<Token> arg) {
        currentLine = beginLine;
        Token token2 = new Token(new Localisation(fileName, beginLine, beginColumn), token, type);
        arg.add(token2);
    }

    private void addToken(Node n, String token, List<Token> arg) {
        addToken(n.getBeginLine(), n.getBeginColumn(), token, arg);
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

    private void addModifier(Node n, int modifier, List<Token> arg) {
        if (modifier != 0) {
            addToken(n, Modifier.toString(modifier), arg);
        }
    }

    private void addParameterList(List<? extends Node> parameterList, List<Token> arg) {
        if (parameterList != null) {
            boolean first = true;
            for (Node n : parameterList) {
                first = addIfTrue(avantToken(n, TokenJava.SEPARATEUR_PARAMETRE), TokenJava.SEPARATEUR_PARAMETRE, arg, !first);
                n.accept(this, arg);
            }
        }
    }

    class Position {

        final int line;
        final int column;

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

    private Position finToken(Node n, Token token) {
        return new Position(n.getBeginLine(), n.getBeginColumn() + token.getValeurToken().length());
    }

    private Position finToken(Node n, String tokenValue) {
        return new Position(n.getBeginLine(), n.getBeginColumn() + tokenValue.length());
    }

    private Position avantToken(Node n, Token token) {
        return new Position(n.getBeginLine(), n.getBeginColumn() - token.getValeurToken().length() - 1);
    }
}
