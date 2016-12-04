package com.karateca.injecttest;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

public class InjectTestActionTest extends LightCodeInsightFixtureTestCase {

  @Override
  protected String getTestDataPath() {
    return "testdata";
  }

  private void whenYouRunTheAction() {
    myFixture.performEditorAction("com.karateca.injecttest.InjectTestAction");
  }

  public void testInjectsES5NoParams() {
    myFixture.configureByFiles("inject-no-params-es5-before.js");

    whenYouRunTheAction();

    myFixture.checkResultByFile(
        "inject-no-params-es5-before.js",
        "inject-no-params-es5-after.js", false);
  }

  public void testInjectES5WithParams() {
    // Given that inject() has params.
    myFixture.configureByFile("inject-with-params-es5-before.js");

    whenYouRunTheAction();

    myFixture.checkResultByFile(
        "inject-with-params-es5-before.js",
        "inject-with-params-es5-after.js", false);
  }
}
