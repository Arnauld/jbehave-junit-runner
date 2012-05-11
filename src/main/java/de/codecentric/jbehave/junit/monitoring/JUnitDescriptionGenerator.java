package de.codecentric.jbehave.junit.monitoring;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.StepCandidate;
import org.jbehave.core.steps.StepType;
import org.junit.runner.Description;

public class JUnitDescriptionGenerator {

    public static final String EXAMPLE_DESCRIPTION_PREFIX = "Example: ";

	public static final String SCENARIO_DESCRIPTION_PREFIX = "Scenario: ";

	DescriptionTextUniquefier uniq = new DescriptionTextUniquefier();

    private int testCases;

	private List<StepCandidate> allCandidates = new ArrayList<StepCandidate>();
    
    public JUnitDescriptionGenerator(List<CandidateSteps> candidateSteps) {
		for (CandidateSteps candidateStep : candidateSteps) {
			allCandidates.addAll(candidateStep.listCandidates());
		}
	}

	public Description createDescriptionFrom(Story story) {
	    Description storyDescription = Description.createSuiteDescription(getJunitSafeString(story.getName()));
	    List<Scenario> scenarios = story.getScenarios();
	    for (Scenario scenario : scenarios) {
	        storyDescription.addChild(createDescriptionFrom(scenario));
	    }
	    return storyDescription;
	}

	public Description createDescriptionFrom(Scenario scenario) {
        Description scenarioDescription = Description.createSuiteDescription(SCENARIO_DESCRIPTION_PREFIX + getJunitSafeString(scenario.getTitle()));
        if (hasGivenStories(scenario)) {
        	insertGivenStories(scenario, scenarioDescription);
        }
        
        if (hasExamples(scenario)) {
            insertDescriptionForExamples(scenario, scenarioDescription);
        } else {
            addScenarioSteps(scenario, scenarioDescription);
        }
        return scenarioDescription;
    }

	private boolean hasGivenStories(Scenario scenario) {
		return !scenario.getGivenStories().getPaths().isEmpty();
	}

	private void insertGivenStories(Scenario scenario,
			Description scenarioDescription) {
		for (String path: scenario.getGivenStories().getPaths()) {
			String name = path.substring(path.lastIndexOf("/")+1, path.length());
			scenarioDescription.addChild(
					Description.createSuiteDescription(getJunitSafeString(name))
			);
			testCases++;
		}
	}

	private boolean hasExamples(Scenario scenario) {
		ExamplesTable examplesTable = scenario.getExamplesTable();
		return examplesTable!= null && examplesTable.getRowCount() > 0;
	}

	private void insertDescriptionForExamples(Scenario scenario,
			Description scenarioDescription) {
		ExamplesTable examplesTable = scenario.getExamplesTable();
		int rowCount = examplesTable.getRowCount();
		for (int i = 1; i <= rowCount; i++) {
		    Description exampleRowDescription = Description.createSuiteDescription(EXAMPLE_DESCRIPTION_PREFIX + examplesTable.getRow(i-1), (Annotation[]) null);
		    scenarioDescription.addChild(exampleRowDescription);
		    addScenarioSteps(scenario, exampleRowDescription);
		}
	}

	private void addScenarioSteps(Scenario scenario, Description description) {
		List<String> steps = scenario.getSteps();
		addSteps(description, steps);
	}

	private void addSteps(Description description, List<String> steps) {
		String previousNonAndStep = null;
		for (String stringStep : steps) {
			for (StepCandidate step : allCandidates) {
				if (step.matches(stringStep, previousNonAndStep)) {
					if (step.getStepType() != StepType.AND) {
						previousNonAndStep = step.getStartingWord() + " ";
					}
					if (stringStep.indexOf('\n') != -1) {
						stringStep = stringStep.substring(0, stringStep.indexOf('\n'));
					}
					Description testDescription;
					String[] composedSteps = step.composedSteps();
					if (composedSteps!=null && composedSteps.length>0) {
						testDescription = Description.createSuiteDescription(getJunitSafeString(stringStep));
						addSteps(testDescription, Arrays.asList(composedSteps));
					} else {
						testCases++;
						// JUnit and the Eclipse JUnit view needs to be touched/fixed in order to make the JUnit view
						// jump to the corresponding test method accordingly. For now we have to live, that we end up in 
						// the correct class.
						testDescription = Description.createTestDescription(step.getStepsInstance().getClass(), getJunitSafeString(stringStep));
					}
					description.addChild(testDescription);
					continue;
				}
			}
		}
	}

    public String getJunitSafeString(String string) {
        return uniq.getUniqueDescription(string.replaceAll("\r", "\n").replaceAll("\n{2,}", "\n").replaceAll("\n", ", ").replaceAll("[\\(\\)]", "|"));
    }

	public int getTestCases() {
		return testCases;
	}

}
