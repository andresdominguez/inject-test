package com.karateca.injecttest;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.io.File;

public class InjectTestActionTest extends LightCodeInsightFixtureTestCase {

  @Override
  protected String getTestDataPath() {
    return "testdata";
  }

  public void testInjectsService() {
    myFixture.configureByFiles("inject-no-params-es5-before.js");

    myFixture.performEditorAction("com.karateca.injecttest.InjectTestAction");

    myFixture.checkResultByFile(
        "inject-no-params-es5-before.js",
        "inject-no-params-es5-after.js", false);
  }
}
