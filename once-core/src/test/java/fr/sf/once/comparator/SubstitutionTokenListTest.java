package fr.sf.once.comparator;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import fr.sf.once.comparator.SubstitutionTokenList;
import fr.sf.once.model.Token;
import fr.sf.once.model.Type;

/**
 * Liste gérant les subsitutions.
 */
public class SubstitutionTokenListTest  {

        private final Token tokenA = new Token(null, "A", Type.VALEUR);
        private final Token tokenB = new Token(null, "B", Type.VALEUR);
        private final Token tokenC = new Token(null, "C", Type.VALEUR);
        private SubstitutionTokenList substitutionList;

        @Before
        public void initialiseObjetSousTest() {
            substitutionList = new SubstitutionTokenList();
        }
        
        @Test
        public void should_return_the_same_position_when_search_another_instance_of_the_same_token() {
            assertThat(substitutionList.getPosition(new Token(null, tokenA.getValeurToken(), Type.VALEUR))).isEqualTo(0);
            Token otherTokenReferenceWithSameValue = new Token(null, tokenA.getValeurToken(), tokenA.getType());
            assertThat(substitutionList.getPosition(otherTokenReferenceWithSameValue)).isEqualTo(0);
        }

        @Test
        public void testAjouterTokenTypeDifferent() {
            final Token tokenATypeBreak = new Token(null, "A", Type.BREAK);
            assertThat(tokenA.getType()).isNotEqualTo(tokenATypeBreak.getType());
            assertThat(substitutionList.getPosition(tokenA)).isEqualTo(0);
            assertThat(substitutionList.getPosition(tokenATypeBreak)).isEqualTo(1);
        }
        
}
