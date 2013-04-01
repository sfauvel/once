package fr.sf.once;

import java.util.ArrayList;
import java.util.List;

/**
 * La variable split porte le même nom que la méthode. L'algorithme n'arrivera pas à faire matché les deux algos.
 */
public class CopieMethode {
    public List<String> firstMethod(String param) {
        String[] split = param.split(",");
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < split.length; i++) {
            result.add(split[i].trim());
        }
        return result;
    }
    
    public List<String> secondMethod(String string) {
        String[] stringArray = string.split(",");
        List<String> resultList = new ArrayList<String>();
        for (int index = 0; index < stringArray.length; index++) {
            resultList.add(stringArray[index].trim());
        }
        return resultList;
    }
}
