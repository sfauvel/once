package fr.sf.once.launcher;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.sf.once.comparator.ComparateurAvecSubstitutionEtType;

public class LauncherITest {
    @Test
    public void testName() throws Exception {
        Launcher launcher = new Launcher()
                .withSource("src/test/resources/exemple", "UTF-8")
                .withComparator(ComparateurAvecSubstitutionEtType.class)
                .withMinimalSize(10);
        launcher.execute();
    }
}
