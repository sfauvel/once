package fr.sf.once;

import java.util.ArrayList;
import java.util.List;

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
        String[] split = param.split(",");
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < split.length; i++) {
            result.add(split[i].trim());
        }
        return result;
    }
}
