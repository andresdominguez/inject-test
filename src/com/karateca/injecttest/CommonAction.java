package com.karateca.injecttest;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.lang.javascript.psi.JSBlockStatement;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class CommonAction extends AnAction {
  @Nullable
  String getSelectedText(AnActionEvent e) {
    Caret caret = e.getData(PlatformDataKeys.CARET);
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

  JSBlockStatement findDescribeBody(PsiElement element) {
    // Find the top level describe.
    JSExpressionStatement describeExpression = null;
    JSExpressionStatement parent = PsiTreeUtil.getParentOfType(element, JSExpressionStatement.class);
    while (parent != null) {
      if (parent.getText().startsWith("describe")) {
        describeExpression = parent;
        break;
      }
      parent = PsiTreeUtil.getParentOfType(parent, JSExpressionStatement.class);
    }

    // Try to find the body of describe function.
    return PsiTreeUtil.findChildOfType(describeExpression, JSBlockStatement.class);
  }

  @NotNull
  Document getDocument(AnActionEvent e) {
    return e.getData(PlatformDataKeys.EDITOR).getDocument();
  }

  @Nullable
  PsiElement findElementAtCaret(AnActionEvent e) {
    Editor editor = e.getData(PlatformDataKeys.EDITOR);
    PsiFile file = e.getData(PlatformDataKeys.PSI_FILE);

    if (editor == null || file == null) {
      return null;
    }

    return file.findElementAt(editor.getCaretModel().getOffset());
  }
}
