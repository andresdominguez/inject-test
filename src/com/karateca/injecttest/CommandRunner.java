package com.karateca.injecttest;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;

class CommandRunner {
  static void runCommand(Project project, final Runnable runnable) {
    CommandProcessor.getInstance().executeCommand(project, new Runnable() {
      @Override
      public void run() {
        ApplicationManager.getApplication().runWriteAction(runnable);
      }
    }, "inject test", null);
  }
}
