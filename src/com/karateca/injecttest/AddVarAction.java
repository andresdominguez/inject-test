package com.karateca.injecttest;

import com.intellij.lang.javascript.psi.JSBlockStatement;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class AddVarAction extends CommonAction {
  @Override
  public void actionPerformed(AnActionEvent e) {
    Project project = getEventProject(e);
    Editor editor = e.getData(PlatformDataKeys.EDITOR);
    PsiFile file = e.getData(PlatformDataKeys.PSI_FILE);
    Caret caret = e.getData(PlatformDataKeys.CARET);
    if (project == null || editor == null || file == null || caret == null) {
      return;
    }

    final PsiElement elementAtCaret = findElementAtCaret(editor, file);
    JSBlockStatement jsBlockStatement = findDescribeBody(elementAtCaret);
    String selectedText = getSelectedText(caret);

    // Add the variable.
    TextRange jsBlockStatementTextRange = jsBlockStatement.getTextRange();
    int offset = jsBlockStatementTextRange.getStartOffset() + 2;
    editor.getDocument().insertString(offset, String.format("\n  let %s;\n\n", selectedText));
  }
}
