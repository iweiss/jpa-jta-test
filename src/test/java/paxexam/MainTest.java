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

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

/**
 * Created by Ingo Weiss on 24/01/2014.
 */

@RunWith(PaxExam.class)
public class MainTest
{
	final String hibernateVersion = "4.2.7.Final";
	final String antlrVersion = "2.7.7";
	final String javassistVersion = "3.18.1-GA";

	@Configuration
	public Option[] config()
	{
		MavenArtifactUrlReference karafUrl = maven()
				.groupId("org.apache.karaf")
				.artifactId("apache-karaf")
				.version("2.3.0")
				.type("tar.gz");

		MavenUrlReference karafStandardRepo = maven()
				.groupId("org.apache.karaf.assemblies.features")
				.artifactId("enterprise")
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
			features(karafStandardRepo, "scr", "transaction", "jndi", "jpa"),
//			provision(hibernate, antlr, javassist),
			// Test database bundle
			mavenBundle("org.hsqldb", "hsqldb", "2.3.1"),

			// Hibernate pseudo-feature
			bundle("mvn:org.apache.geronimo.specs/geronimo-jta_1.1_spec/1.1.1").startLevel(30),
			bundle("mvn:org.hibernate.javax.persistence/hibernate-jpa-2.1-api/1.0.0.Final").startLevel(30),
			bundle("mvn:org.apache.geronimo.specs/geronimo-servlet_3.0_spec/1.0").startLevel(30),
			bundle("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.antlr/2.7.7_5"),
			bundle("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.ant/1.8.2_2"),
			bundle("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.dom4j/1.6.1_5"),
			bundle("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.serp/1.14.1_1"),
			bundle("mvn:com.fasterxml/classmate/0.5.4"),
			bundle("mvn:org.javassist/javassist/3.18.1-GA"),
			bundle("mvn:org.jboss.spec.javax.security.jacc/jboss-jacc-api_1.4_spec/1.0.2.Final"),
			bundle("wrap:mvn:org.jboss/jandex/1.0.3.Final"),
			bundle("mvn:org.jboss.logging/jboss-logging/3.1.0.GA"),
			bundle("mvn:org.hibernate.common/hibernate-commons-annotations/4.0.2.Final"),
			bundle("mvn:org.hibernate/hibernate-core/4.2.7.Final"),
			bundle("mvn:org.hibernate/hibernate-entitymanager/4.2.7.Final"),
			bundle("mvn:org.hibernate/hibernate-osgi/4.2.7.Final"),
			logLevel(LogLevelOption.LogLevel.WARN),
			mavenBundle()
				.groupId("pax-exam")
				.artifactId("bundle")
				.version("1.0-SNAPSHOT").start()
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