package paxexam;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import paxexam.bundle.persistence.MainService;
import paxexam.bundle.persistence.impl.MainServiceImpl;

import java.io.File;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

/**
 * Created by lasombra on 24/01/2014.
 */

@RunWith(PaxExam.class)
public class MainTest
{
	@Configuration
	public Option[] config()
	{
		MavenArtifactUrlReference karafUrl = maven()
				.groupId("org.apache.karaf")
				.artifactId("apache-karaf")
				.version("2.3.0")
				.type("tar.gz");
		MavenUrlReference karafStandardRepo = maven()
				.groupId("org.apache.karaf.features")
				.artifactId("org.apache.karaf.features.core")
				.version("2.3.0")
				.classifier("features")
				.type("xml");

		return new Option[]
		{
			karafDistributionConfiguration()
				.frameworkUrl(karafUrl)
				.unpackDirectory(new File("target/exam"))
				.useDeployFolder(false)
				.karafVersion("2.3.0"),
			keepRuntimeFolder(),
			features(karafStandardRepo, "scr"),
			mavenBundle()
				.groupId("pax-exam")
				.artifactId("bundle")
				.versionAsInProject().start(),
			logLevel(LogLevelOption.LogLevel.ERROR)
		};
	}

	@Test
	public void testDatabaseAccess()
	{
		MainService service = new MainServiceImpl();
		service.warmUp();
		service.writeDatabase();
		service.readDatabase();
	}
}