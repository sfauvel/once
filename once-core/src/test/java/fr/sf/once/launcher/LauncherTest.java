package fr.sf.once.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.sf.once.launcher.Launcher.OnceProperties;

public class LauncherTest {
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	@Test
	public void should_use_default_source_directory_when_no_args() throws Exception {
		OnceProperties properties = Launcher.OnceProperties.extractConfiguration(
				new String[] {}, 
				createFile());

		Assertions.assertThat(properties.getSourceDir()).isEqualTo(".");
	}

	@Test
	public void should_use_source_directory_given_into_parameters() throws Exception {
		OnceProperties properties = Launcher.OnceProperties.extractConfiguration(
				new String[] {"./src"},
				createFile());
		
		Assertions.assertThat(properties.getSourceDir()).isEqualTo("./src");
	}
	
	@Test
	public void should_use_source_directory_given_into_properties_file_when_no_parameter() throws Exception {
		OnceProperties properties = Launcher.OnceProperties.extractConfiguration(
				new String[] {}, 
				createFile(OnceProperties.Key.SRC_DIR + "=./src"));
		
		Assertions.assertThat(properties.getSourceDir()).isEqualTo("./src");
	}

	@Test
	public void should_use_source_directory_given_into_parameters_behind_this_one_in_properties_file() throws Exception {
		OnceProperties properties = Launcher.OnceProperties.extractConfiguration(
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
}
