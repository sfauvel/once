package fr.sf.once.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import fr.sf.once.comparator.SubstitutionStringList;

public class SubstitutionStringListTestTest {
    
    
    private SubstitutionStringList substitutionList;

    @Before
    public void initialiseObjetSousTest() {
        substitutionList = new SubstitutionStringList();
    }
    
    @Test
    public void testAjouterUnElementSurListeVide() {
        assertThat(substitutionList.getPosition("B")).isEqualTo(0);
    }

    @Test
    public void testAjouterDeuxElementsSurListe() {
        substitutionList.getPosition("A");
        assertThat(substitutionList.getPosition("B")).isEqualTo(1);
    }


    @Test
    public void testAjouterElementDejaPresent() {
        substitutionList.getPosition("A");
        substitutionList.getPosition("B");
        substitutionList.getPosition("C");

        assertThat(substitutionList.getPosition("B")).isEqualTo(1);
    }
    
    @Test
    public void testAjouterPremierToken() {
        assertThat(substitutionList.getPosition("A")).isEqualTo(0);
        assertThat(substitutionList.getPosition("A")).isEqualTo(0);
    }

    @Test
    public void testAjouterSecondToken() {
        assertThat(substitutionList.getPosition("A")).isEqualTo(0);
        assertThat(substitutionList.getPosition("B")).isEqualTo(1);
        assertThat(substitutionList.getPosition("B")).isEqualTo(1);
    }
    
    @Test
    public void should_duplicate_substitution_list_when_duplicate() {
        substitutionList.getPosition("A");
        substitutionList.getPosition("B");
        substitutionList.getPosition("C");

        SubstitutionStringList duplicate = new SubstitutionStringList(substitutionList);
        
        // Check in reverse order to verify the values are the same
        assertThat(duplicate.getPosition("C")).isEqualTo(2);
        assertThat(duplicate.getPosition("B")).isEqualTo(1);
        assertThat(duplicate.getPosition("A")).isEqualTo(0);
    }
}
