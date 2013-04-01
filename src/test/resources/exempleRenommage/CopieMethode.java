package fr.sf.once;

import java.util.ArrayList;
import java.util.List;

public class CopieMethode {
    public List<String> firstMethod(String param) {
        String[] splitArray = param.split(",");
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < splitArray.length; i++) {
            result.add(splitArray[i].trim());
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
