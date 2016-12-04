package com.karateca.injecttest;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.io.File;

public class InjectTestActionTest extends LightCodeInsightFixtureTestCase {

  @Override
  protected String getTestDataPath() {
    return new File("testdata/").getPath();
  }

  public void testInjectsService() {
    myFixture.configureByFiles("file-to-inject-before.js");

    myFixture.performEditorAction("com.karateca.injecttest.InjectTestAction");

    myFixture.checkResultByFile(
        "file-to-inject-before.js",
        "file-to-inject-after.js", false);
  }
}
