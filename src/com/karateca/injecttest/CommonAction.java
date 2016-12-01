package com.karateca.injecttest;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSParameterList;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

abstract class CommonAction extends AnAction {
  void runCommand(Project project, final Runnable runnable) {
    CommandProcessor.getInstance().executeCommand(project, () -> ApplicationManager.getApplication().runWriteAction(runnable), "inject test", null);
  }

  @Nullable
  String getSelectedText(Caret caret) {
    if (caret == null) {
      return null;
    }

    if (caret.getSelectedText() == null || caret.getSelectedText().trim().length() == 0) {
      return null;
    }
    return caret.getSelectedText().trim();
  }

  void showHint(Editor editor, String s) {
    HintManager.getInstance().showErrorHint(editor, s);
  }

  @Nullable
  <T extends PsiElement> T findChildrenByTypeWithText(PsiElement element, Class<? extends T> aClass, String text) {
    return PsiTreeUtil.findChildrenOfType(element, aClass)
        .stream()
        .filter(o -> o.getText().startsWith(text))
        .findFirst()
        .orElse(null);
  }

  @Nullable
  JSParameterList findInjectablesList(PsiFile file) {
    // Look for "inject(() => ..._)".
    JSCallExpression inject = findChildrenByTypeWithText(file, JSCallExpression.class, "inject");

    // Now find the parameter list (the list of injectables).
    return PsiTreeUtil.findChildOfType(inject, JSParameterList.class);
  }
}
