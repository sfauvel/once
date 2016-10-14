package fr.sf.once.core;

import static fr.sf.once.test.UtilsToken.createUnmodifiableTokenList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import fr.sf.once.comparator.CodeComparator;
import fr.sf.once.comparator.ComparatorWithSubstitution;
import fr.sf.once.model.Code;
import fr.sf.once.model.Redundancy;
import fr.sf.once.test.UtilsToken;

public class RedundancyAggregatorTest {
    @Test
    public void should_create_a_code_with_duplication() throws Exception {
        Configuration configuration = new Configuration()
                .withTailleMin(3);

        Code code = new Code(createUnmodifiableTokenList(
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
                "A", "B", "C", "D", "E", "X", "Y", "F", "G", "H", "I", "Z"));
        ManagerToken managerToken = new ManagerToken(code);
        List<Redundancy> redundancy = managerToken.getRedondance(configuration);

        assertThat(redundancy).hasSize(2);
        Redundancy red1 = redundancy.get(0);
        Redundancy red2 = redundancy.get(1);
        UtilsToken.display(red1);
        UtilsToken.display(red2);

        Code newCode = new RedundancyAggregator(code).aggregate(red1, red2);
        
        String[] expectedTokens = new String[] {
                "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "A", "B", "C", "D", "E", "F", "G", "H", "I"};

        assertThat(newCode.getSize()).isEqualTo(expectedTokens.length);

        for (int i = 0; i < expectedTokens.length; i++) {
            assertThat(newCode.getToken(i).getValeurToken()).isEqualTo(expectedTokens[i]);
        }

    }
    
    @Test
    public void should_create_a_code_with_duplication_on_non_consecutive_code() throws Exception {
        Configuration configuration = new Configuration()
                .withTailleMin(3);

        Code code = new Code(createUnmodifiableTokenList(
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
                "F", "G", "H", "I", "X", "Y", "A", "B", "C", "D", "E", "Z"));
        ManagerToken managerToken = new ManagerToken(code);
        List<Redundancy> redundancy = managerToken.getRedondance(configuration);

        assertThat(redundancy).hasSize(2);
        Redundancy red1 = redundancy.get(0);
        Redundancy red2 = redundancy.get(1);
        UtilsToken.display(red1);
        UtilsToken.display(red2);

        Code newCode = new RedundancyAggregator(code).aggregate(red1, red2);
        
        String[] expectedTokens = new String[] {
                "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "F", "G", "H", "I", "A", "B", "C", "D", "E"};

        assertThat(newCode.getSize()).isEqualTo(expectedTokens.length);

        for (int i = 0; i < expectedTokens.length; i++) {
            assertThat(newCode.getToken(i).getValeurToken()).isEqualTo(expectedTokens[i]);
        }

    }
    
    @Test
    public void should_notfind_a_unique_duplication() throws Exception {

        Configuration configuration = new Configuration(ComparatorWithSubstitution.class)
                .withTailleMin(3);
        
        Code code = new Code(createUnmodifiableTokenList(
                "A", "B", "C", "D", "E", "F", "G", "A", "I",
                "A", "B", "C", "D", "E", "F", "G", "H", "I"));
        ManagerToken managerToken = new ManagerToken(code);
        List<Redundancy> redundancy = managerToken.getRedondance(configuration);

     //   assertThat(redundancy).hasSize(2);
//        Redundancy red1 = redundancy.get(0);
//        Redundancy red2 = redundancy.get(1);
//        UtilsToken.display(red1);
//        UtilsToken.display(red2);

        for (int i = 0; i < redundancy.size(); i++) {
            UtilsToken.display(redundancy.get(i));
        }
    }
}
