package fr.sf.once;

import java.util.ArrayList;
import java.util.List;

public class CopieMethode {
    public List<String> referenceMethod(String param) {
        String[] splitArray = param.split(",");
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < splitArray.length; i++) {
            result.add(splitArray[i].trim());
        }
        return result;
    }
    
    public List<String> methodWithSeveralRename(String string) {
        String[] stringArray = string.split(",");
        List<String> resultList = new ArrayList<String>();
        for (int index = 0; index < stringArray.length; index++) {
            resultList.add(stringArray[index].trim());
        }
        return resultList;
    }
    
    public List<String> methodWithAVariableNamedLikeAMethod(String string) {
        String[] split = string.split(",");
        List<String> resultList = new ArrayList<String>();
        for (int index = 0; index < split.length; index++) {
            resultList.add(split[index].trim());
        }
        return resultList;
    }
}
