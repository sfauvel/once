package fr.sf.ast;

import java.util.ArrayList;
import java.util.List;

public class PageTestAst {

   public void maMethode() {
       List<String> liste = new ArrayList<String>();
       
       for (String message : liste) {
           System.out.println(message.trim());
       }
   }
}
