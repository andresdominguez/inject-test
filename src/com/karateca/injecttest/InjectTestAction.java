package com.karateca.injecttest;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSParameterList;
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

public class InjectTestAction extends CommonAction {

  @Override
  public void actionPerformed(AnActionEvent e) {
    Project project = getEventProject(e);
    Editor editor = e.getData(PlatformDataKeys.EDITOR);
    PsiFile file = e.getData(PlatformDataKeys.PSI_FILE);
    Caret caret = e.getData(PlatformDataKeys.CARET);
    if (project == null || editor == null || file == null || caret == null) {
      return;
    }

    final String selectedText = getSelectedText(caret);
    final String serviceNameWithUnderscore = String.format("_%s_", selectedText);
    if (selectedText == null) {
      showHint(editor, "Please select a string with the name of the service");
      return;
    }

    PsiElement elementAtCaret = findElementAtCaret(e);
    JSCallExpression injectElement = findInjectElement(elementAtCaret);
    JSParameterList parameterList = PsiTreeUtil.findChildOfType(injectElement, JSParameterList.class);

    if (parameterList == null) {
      showHint(editor, "Can't find parameters in inject function");
      return;
    }

    TextRange parameterListTextRange = parameterList.getTextRange();
    Document document = editor.getDocument();
    String paramListString = document.getText(parameterListTextRange);
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
    sb.insert(sb.length() - 1, serviceNameWithUnderscore);

    CommandRunner.runCommand(project, () -> {
      // Assign the variable <selection> = _<selection>_;
      document.insertString(caret.getSelectionEnd(), String.format(" = %s;", serviceNameWithUnderscore));

      // Add the dependency parameter list.
      document.replaceString(parameterListTextRange.getStartOffset(), parameterListTextRange.getEndOffset(), sb.toString());

      // Add the variable.
      new AddVarAction().actionPerformed(e);
    });
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
