package app.cbo.writings.tooling;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

public class HighLighter {


    public static void main(String[] args) throws FileNotFoundException {

        final String windows = "C:\\Users\\a118608\\OneDrive - Worldline\\Bureau\\HSM\\iaik_jce_full-5.63-eval\\demo\\src\\demo\\TestSignature.java";
        if(new File(windows).exists()) {
            new HighLighter().highlight(windows,
                    "c:\\WORK\\output.html");
        }
        else{
            new HighLighter().highlight(
                    "/home/a118608/workspace/poc-sourceweb/mockSW/src/main/java/aw/a118608/sw/mocksw/MockController.java",
            "/mnt/c/work/output.html");

        }

    }

    private void highlight(String filePath, String outputFile) throws FileNotFoundException {

        System.out.printf("Highlighting %s %n", filePath);
        var file = StaticJavaParser.parse(new File(filePath));

        try(var out = new PrintStream(new FileOutputStream(outputFile))){

            prepare(out);
            file.getChildNodes().forEach(firstLevel -> print(out, firstLevel));
            finish(out);
        }

    }

    private void prepare(PrintStream out) {
        out.println("<html><head><link href=\"black.css\" rel=\"stylesheet\"></head><body><code>");
    }

    private void finish(PrintStream out) {
        out.println("</code><body>");
    }

    private void print(PrintStream out, Node firstLevel) {

        if(firstLevel instanceof PackageDeclaration){
            this.printPackage(out, firstLevel);
            out.println("<hr/>");
        }else if(firstLevel instanceof ImportDeclaration importDeclaration){
            this.printImport(out, importDeclaration);
        }else if(firstLevel instanceof ClassOrInterfaceDeclaration classOrInterfaceDeclaration){
            out.println("<hr/>");
            this.classOrInterface(out, classOrInterfaceDeclaration);
        }
    }


    private void classOrInterface(PrintStream out, ClassOrInterfaceDeclaration classOrinterface) {
        if(classOrinterface.getComment().isPresent()){
            printComment(out, classOrinterface);

        }

        classOrinterface.getAnnotations().forEach(annotation -> this.printAnnotation(out, annotation));
        classOrinterface.getModifiers().forEach(modifier -> this.modifier(out, modifier.toString()));
        if(classOrinterface.isInterface()){
            keyword(out, "interface", "interface");
        } else {
            keyword(out, "class", "class");
        }
        out.print(classOrinterface.getNameAsString() );

        if(classOrinterface.getExtendedTypes().isNonEmpty()){
            keyword(out,  " extends", "extends");
            List<String> extended = classOrinterface.getExtendedTypes().stream().map(type -> type.getName().asString()).toList();
            out.print(String.join(", ", extended));

        }
        if(classOrinterface.getImplementedTypes().isNonEmpty()){
            keyword(out, " implements", "extends");
            List<String> implemented = classOrinterface.getImplementedTypes().stream().map(type -> type.getName().asString()).toList();
            out.print(String.join(", ", implemented));

        }
    }

    private void printAnnotation(PrintStream out, AnnotationExpr a){

        out.print("<p class='annotation'>");

        if(a instanceof SingleMemberAnnotationExpr single){
            Expression t = single.getMemberValue();
            out.print("@"+a.getName()+"(");
            this.printExpression(out, t);
            out.print(")");

        }else if(a instanceof NormalAnnotationExpr normal){
            out.print("@"+a.getName()+"(");
            if(normal.getPairs().isNonEmpty()){
                for(int i = 0; i < normal.getPairs().size() ; i++){
                    if(i >0){
                        out.print(", ");
                    }
                    var pair = normal.getPairs().get(i);
                    out.print(pair.getName()+" = ");
                    this.printExpression(out, pair.getValue());
                }
            }
            out.print(")");
        }else if(a instanceof MarkerAnnotationExpr){
            out.print("@"+a.getName());
        }else{
            System.err.println(a.getClass().getSimpleName()+" : "+a);
            out.print(a);
        }
        out.print("</p>");
    }

    private void printExpression(PrintStream out, Expression expr) {
        if(expr instanceof AnnotationExpr a){
            this.printAnnotation(out, a);
        }else if( expr instanceof StringLiteralExpr stringLiteralExpr){
            this.printString(out, stringLiteralExpr);
        }else if( expr instanceof BooleanLiteralExpr booleanLiteralExpr){
            this.printBoolean(out, booleanLiteralExpr);
        }else{

            System.err.println(expr.getClass().getSimpleName()+" : "+expr);
            out.print(expr.toString());
        }
    }

    private void printBoolean(PrintStream out, BooleanLiteralExpr booleanLiteralExpr) {
        out.print("<span class=\"boolean "+booleanLiteralExpr.getValue()+"\">"+booleanLiteralExpr.getValue()+"</span>");
        boolean b = true;
        boolean c = false;

    }

    private void printString(PrintStream out, StringLiteralExpr stringLiteralExpr) {
        out.print("<span class=\"string\">\""+stringLiteralExpr.asString()+"\"</span>");
    }

    private void printComment(PrintStream out, ClassOrInterfaceDeclaration firstLevel) {
        out.println("<p class='comment'>"+firstLevel.getComment().get()+"</p>");
    }

    private void printImport(PrintStream out, ImportDeclaration declaration){
        out.print("<p class='declaration import'>");
        if(declaration.isStatic()) {
            keyword(out, "import static", "import", "importStatic", "static");
        }else{
            keyword(out, "import", "import");
        }
        if(declaration.isAsterisk()) {
            out.println(declaration.getChildNodes().get(0).toString() + ".*;</p>");
        }else{
            out.println(declaration.getChildNodes().get(0).toString() + ";</p>");
        }
    }
    private void printPackage(PrintStream out, Node firstLevel ) {
        out.print("<p class='declaration package'>");
        keyword(out, "package", "package");
        out.println(firstLevel.getChildNodes().get(0).toString()+";</p>");
    }

    private void modifier(PrintStream out, String modifier){
        keyword(out, modifier, "modifier", modifier);
    }

    private void keyword(PrintStream out, String text, String... types){
        out.print("<span class='keyword "+String.join(" ", types)+"'>"+text+" </span>");
    }
}