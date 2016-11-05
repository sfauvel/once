package fr.sf.once.report;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fr.sf.once.core.RedundancyFinder;
import fr.sf.once.model.Code;
import fr.sf.once.model.MethodDefinition;
import fr.sf.once.model.Redundancy;

public class ReportingMethodPercentageTest {

    private final RedundancyFinder EMPTY_MANAGER = new RedundancyFinder(new Code());

    @Test
    public void purcentage_redundancy_is_0_when_there_is_no_redundency() {
        List<Redundancy> redundancyList = new ArrayList<Redundancy>();
        
        ReportingMethodPercentage report = new ReportingMethodPercentage(EMPTY_MANAGER, redundancyList);
        MethodDefinition methodA = new MethodDefinition("fileA", "methodA", 1, 100);
        MethodDefinition methodB = new MethodDefinition("fileA", "methodB", 101, 200);
        assertThat(report.getPercentageBetween(methodA, methodB)).isEqualTo(0);
    }
    
    @Test
    public void with_no_redundancy_between_methods_purcentage_is_0() {
        final int DUPLICATED_TOKEN_NUMBER = 20;
        Redundancy redundancy = new Redundancy(null, DUPLICATED_TOKEN_NUMBER, Arrays.asList(5, 1005));
        
        ReportingMethodPercentage report = new ReportingMethodPercentage(EMPTY_MANAGER, Arrays.<Redundancy>asList(redundancy));
        
        MethodDefinition methodA = new MethodDefinition("fileA", "methodA", 1, 100);
        MethodDefinition methodB = new MethodDefinition("fileA", "methodB", 101, 300);
        assertThat(report.getPercentageBetween(methodA, methodB)).isEqualTo(0);
    }
    
    @Test
    public void with_one_redundancy_purcentage_of_duplication_is_redundancy_size_divided_by_method_size() {
        final int DUPLICATED_TOKEN_NUMBER = 20;
        Redundancy redundancy = new Redundancy(null, DUPLICATED_TOKEN_NUMBER, Arrays.asList(5, 105));
        
        ReportingMethodPercentage report = new ReportingMethodPercentage(EMPTY_MANAGER, Arrays.<Redundancy>asList(redundancy));
        
        MethodDefinition methodA = new MethodDefinition("fileA", "methodA", 1, 100);
        MethodDefinition methodB = new MethodDefinition("fileA", "methodB", 101, 300);
        assertThat(report.getPercentageBetween(methodA, methodB)).isEqualTo(20);
        assertThat(report.getPercentageBetween(methodB, methodA)).isEqualTo(10);
    }
    
    @Test
    public void with_several_duplication_between_method_total_purcentage_is_sum_of_purcentage() {
        final int DUPLICATED_TOKEN_NUMBER = 20;
        List<Redundancy> redundancyList = Arrays.<Redundancy>asList(
                new Redundancy(null, DUPLICATED_TOKEN_NUMBER, Arrays.asList(5, 105)),
                new Redundancy(null, DUPLICATED_TOKEN_NUMBER, Arrays.asList(50, 160)));
        
        ReportingMethodPercentage report = new ReportingMethodPercentage(EMPTY_MANAGER, redundancyList);
        
        MethodDefinition methodA = new MethodDefinition("fileA", "methodA", 1, 100);
        MethodDefinition methodB = new MethodDefinition("fileA", "methodB", 101, 300);
        assertThat(report.getPercentageBetween(methodA, methodB)).isEqualTo(40);
        assertThat(report.getPercentageBetween(methodB, methodA)).isEqualTo(20);
    }
}
