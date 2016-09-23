package fr.sf.once.launcher;

import java.io.File;
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
		File propertiesFile = temporaryFolder.newFile("tmp.properties");
		String[] args = new String[] {};
		
		OnceProperties properties = Launcher.loadConfiguration(args, propertiesFile);

		Assertions.assertThat(properties.getSourceDir()).isEqualTo(OnceProperties.DEFAULT_SOURCE_DIR);
	}

	@Test
	public void should_use_source_directory_given_into_parameters() throws Exception {
		File propertiesFile = temporaryFolder.newFile("tmp.properties");
		String[] args = new String[] {"./src"};
		
		OnceProperties properties = Launcher.loadConfiguration(args, propertiesFile);
		
		Assertions.assertThat(properties.getSourceDir()).isEqualTo("./src");
	}
	
	@Test
	public void should_use_source_directory_given_into_properties_file_when_no_parameter() throws Exception {
		File propertiesFile = temporaryFolder.newFile("tmp.properties");
		PrintWriter printWriter = new PrintWriter(propertiesFile);
		printWriter.format("%s=%s", OnceProperties.SRC_DIR, "./src");
		printWriter.close();
		String[] args = new String[] {};
		
		OnceProperties properties = Launcher.loadConfiguration(args, propertiesFile);
		
		Assertions.assertThat(properties.getSourceDir()).isEqualTo("./src");
	}
	
	@Test
	public void should_use_source_directory_given_into_parameters_behind_this_one_in_properties_file() throws Exception {
		File propertiesFile = temporaryFolder.newFile("tmp.properties");
		PrintWriter printWriter = new PrintWriter(propertiesFile);
		printWriter.format("%s=%s", OnceProperties.SRC_DIR, "./src");
		printWriter.close();
		String[] args = new String[] {"./argSrc"};
		
		OnceProperties properties = Launcher.loadConfiguration(args, propertiesFile);
		
		Assertions.assertThat(properties.getSourceDir()).isEqualTo("./argSrc");
	}
}
