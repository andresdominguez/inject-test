package com.karateca.injecttest;

import com.intellij.lang.javascript.psi.JSBlockStatement;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;

public class AddVarAction extends CommonAction {
  @Override
  public void actionPerformed(AnActionEvent e) {
    Document document = getDocument(e);
    JSBlockStatement jsBlockStatement = findDescribeBody(findElementAtCaret(e));
    String selectedText = getSelectedText(e);

    // Add the variable.
    TextRange jsBlockStatementTextRange = jsBlockStatement.getTextRange();
    int offset = jsBlockStatementTextRange.getStartOffset() + 2;
    document.insertString(offset, String.format("\n  let %s;\n\n", selectedText));
  }
}
