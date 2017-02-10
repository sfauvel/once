package fr.sf.once.launcher;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.sf.once.comparator.BasicComparator;
import fr.sf.once.comparator.CodeComparator;

public class OnceConfigurationTest {
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	@Test
	public void should_use_default_source_directory_when_no_args() throws Exception {
		OnceConfiguration properties = OnceConfiguration.load(
				new String[] {}, 
				createFile());

		Assertions.assertThat(properties.getSourceDir()).isEqualTo(".");
	}

	@Test
	public void should_use_source_directory_given_into_parameters() throws Exception {
		OnceConfiguration properties = OnceConfiguration.load(
				new String[] {"./src"},
				createFile());
		
		Assertions.assertThat(properties.getSourceDir()).isEqualTo("./src");
	}
	
	@Test
	public void should_use_source_directory_given_into_properties_file_when_no_parameter() throws Exception {
		OnceConfiguration properties = OnceConfiguration.load(
				new String[] {}, 
				createFile(OnceConfiguration.OnceProperty.SRC_DIR.getKey() + "=./src"));
		
		Assertions.assertThat(properties.getSourceDir()).isEqualTo("./src");
	}

	@Test
	public void should_use_source_directory_given_into_parameters_behind_this_one_in_properties_file() throws Exception {
		OnceConfiguration properties = OnceConfiguration.load(
				new String[] {"./argSrc"}, 
				createFile());
		
		Assertions.assertThat(properties.getSourceDir()).isEqualTo("./argSrc");
	}
	
	private File createFile(String... properties) throws IOException, FileNotFoundException {
		File propertiesFile = temporaryFolder.newFile("tmp.properties");
		PrintWriter printWriter = new PrintWriter(propertiesFile);
		for (String property : properties) {			
			printWriter.format(property);
		}
		printWriter.close();
		return propertiesFile;
	}
	
	@Test
    public void testName() throws Exception {
	    Object obj = Class.forName(BasicComparator.class.getName());
	    Class<? extends CodeComparator> clazz = (Class<? extends CodeComparator> ) obj; 
	    System.out.println(obj);
    }
	

}
