package fr.sf.once;

import java.util.ArrayList;
import java.util.List;

/**
 * When we use the ComparateurSimpleSansString, we find only one redondance between the methods:
 * referenceMethod and methodWithDifferenceOnlyOnString
 */
public class CopieMethode {
    public List<String> referenceMethod(String param) {
        String[] split = param.split(",");
        List<String> result = new ArrayList<String>();
        System.out.println("My first message");
        for (int i = 0; i < split.length; i++) {
            result.add(split[i].trim());
        }
        return result;
    }
    
    public List<String> methodWithDifferenceOnlyOnString(String string) {
        String[] split = param.split(",");
        List<String> result = new ArrayList<String>();
        System.out.println("Another message");
        for (int i = 0; i < split.length; i++) {
            result.add(split[i].trim());
        }
        return result;
    }
    
    public List<String> methodWithSeveralRename(String string) {
        String[] stringArray = string.split(",");
        List<String> resultList = new ArrayList<String>();
        System.out.println("Error message");
        for (int index = 0; index < stringArray.length; index++) {
            resultList.add(stringArray[index].trim());
        }
        return resultList;
    }
}
