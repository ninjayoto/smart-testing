package org.arquillian.smart.testing.ftest.affected;

import java.util.List;
import org.arquillian.smart.testing.ftest.testbed.project.Project;
import org.arquillian.smart.testing.ftest.testbed.rules.GitClone;
import org.arquillian.smart.testing.ftest.testbed.rules.TestBed;
import org.arquillian.smart.testing.ftest.testbed.testresults.TestResult;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.arquillian.smart.testing.ftest.testbed.configuration.Mode.SELECTING;
import static org.arquillian.smart.testing.ftest.testbed.configuration.Strategy.AFFECTED;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangesOnDifferentModulesAffectedTestsSelectionExecutionFunctionalTest {

    @ClassRule
    public static final GitClone GIT_CLONE = new GitClone();

    @Rule
    public TestBed testBed = new TestBed(GIT_CLONE);

    @Test
    public void should_detect_changes_on_maven_modules_and_execute_test() {
        // given
        final Project project = testBed.getProject();

        project.configureSmartTesting()
            .executionOrder(AFFECTED)
            .inMode(SELECTING)
            .enable();

        final List<TestResult> expectedTestResults = project.applyAsCommits("Adds class in one module to affect test in another");

        // when
        final List<TestResult> actualTestResults = project
            .build()
                .options()
                    .withSystemProperties("git.commit", "HEAD", "git.previous.commit", "HEAD~")
                .configure()
            .run();

        // then
        assertThat(actualTestResults).hasSameSizeAs(expectedTestResults).containsAll(expectedTestResults);
    }

    @Test
    @Ignore("Example of debugging option as living documentation")
    public void should_detect_changes_on_maven_modules_and_execute_test_in_debug_mode() {
        // given
        final Project project = testBed.getProject();

        project.configureSmartTesting()
            .executionOrder(AFFECTED)
            .inMode(SELECTING)
            .enable();

        final List<TestResult> expectedTestResults = project.applyAsCommits("Adds class in one module to affect test in another");

        // when
        // tag::documentation_test_bed_debug[]
        final List<TestResult> actualTestResults = project
            .build()
            .options()
                .withSystemProperties("git.commit", "HEAD", "git.previous.commit", "HEAD~")
                .withDebugOutput()
                .withRemoteDebugging()
                .withRemoteSurefireDebugging()
            .configure()
            .run();
        // end::documentation_test_bed_debug[]

        // then
        assertThat(actualTestResults).hasSameSizeAs(expectedTestResults).containsAll(expectedTestResults);
    }

}
