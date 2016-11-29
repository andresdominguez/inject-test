package com.karateca.injecttest;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSParameterList;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

public class InjectTestAction extends AnAction {

  @Override
  public void actionPerformed(AnActionEvent e) {
    Project project = getEventProject(e);
    Editor editor = e.getData(PlatformDataKeys.EDITOR);
    PsiFile file = e.getData(PlatformDataKeys.PSI_FILE);
    if (project == null || editor == null || file == null) {
      return;
    }

    final String selectedText = getSelectedText(e);
    if (selectedText == null) {
      showHint(editor, "Please select a string with the name of the service");
      return;
    }

    PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
    JSCallExpression injectElement = findInjectElement(element);
    JSParameterList parameterList = PsiTreeUtil.findChildOfType(injectElement, JSParameterList.class);

    if (parameterList == null) {
      showHint(editor, "Can't find parameters in inject function");
      return;
    }

    TextRange textRange = parameterList.getTextRange();
    Document document = editor.getDocument();
    String paramListString = document.getText(textRange);
    final StringBuilder sb = new StringBuilder(paramListString);

    // Does the para list has parens?
    if (!paramListString.endsWith(")")) {
      sb.insert(0, "(");
      sb.append(")");
    }

    // Add comma if this is the second+ parameter.
    if (parameterList.getChildren().length > 0) {
      sb.insert(sb.length() - 1, ", ");
    }

    // Add the service with _name_;
    sb.insert(sb.length() - 1, String.format("_%s_", selectedText));

    CommandRunner.runCommand(project, () -> {
      document.replaceString(textRange.getStartOffset(), textRange.getEndOffset(), sb.toString());
    });
  }

  @Nullable
  private String getSelectedText(AnActionEvent e) {
    Caret caret = e.getData(PlatformDataKeys.CARET);
    if (caret == null) {
      return null;
    }

    if (caret.getSelectedText() == null || caret.getSelectedText().trim().length() == 0) {
      return null;
    }
    return caret.getSelectedText().trim();
  }

  private void showHint(Editor editor, String s) {
    HintManager.getInstance().showErrorHint(editor, s);
  }

  @Nullable
  private JSCallExpression findInjectElement(PsiElement element) {
    JSCallExpression expr = PsiTreeUtil.getParentOfType(element, JSCallExpression.class);
    while (expr != null) {
      if (expr.getText().startsWith("inject")) {
        return expr;
      }
      expr = PsiTreeUtil.getParentOfType(expr, JSCallExpression.class);
    }
    return null;
  }
}
