package com.karateca.injecttest;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

public class InjectTestActionTest extends LightCodeInsightFixtureTestCase {

  @Override
  protected String getTestDataPath() {
    return "testdata";
  }

  public void testInjectsES5NoParams() {
    // inject() has no params.
    runActionTest("inject-no-params-es5-before.js", "inject-no-params-es5-after.js");
  }

  public void testInjectES5WithParams() {
    // inject() has params.
    runActionTest("inject-with-params-es5-before.js", "inject-with-params-es5-after.js");
  }

  public void testIgnoresWithoutSelection() {
    // No selection changes nothing.
    String beforeAndAfter = "inject-with-params-es5-after.js";
    runActionTest(beforeAndAfter, beforeAndAfter);
  }

  public void testIgnoresFileWithoutinjectFunction() {
    // No inject() changes nothing.
    String beforeAndAfter = "no-inject-function.js";
    runActionTest(beforeAndAfter, beforeAndAfter);
  }

  private void runActionTest(String fileBefore, String fileAfter) {
    myFixture.configureByFiles(fileBefore);

    myFixture.performEditorAction("com.karateca.injecttest.InjectTestAction");

    myFixture.checkResultByFile(fileBefore, fileAfter, false);
  }
}
