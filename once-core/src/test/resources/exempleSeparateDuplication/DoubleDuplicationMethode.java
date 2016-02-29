package fr.sf.once;

import java.util.ArrayList;
import java.util.List;

public class CopieMethode {
    interface Dao {
        
    }
    public List<String> firstMethod(String param) {
        String[] split = param.split(",");
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < split.length; i++) {
            result.add(split[i].trim());
        }
        
        Dao dao = new CustomerDao();
        List<ResultSet> results = dao.execute("select * from customer");
        for (ResultSet resultSet : results) {
            Customer customer = build(resultSet);
        }
        
        return result;
    }
    
    public List<String> secondMethod(String string) {
        String[] split = param.split(",");
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < split.length; i++) {
            result.add(split[i].trim());
        }

        Log.info("Get from the database");
        
        Dao dao = new CustomerDao();
        List<ResultSet> results = dao.execute("select * from customer");
        for (ResultSet resultSet : results) {
            Customer customer = build(resultSet);
        }
        
        return result;
    }
}
