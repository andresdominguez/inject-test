package com.karateca.injecttest;

import com.intellij.lang.javascript.psi.JSBlockStatement;
import com.intellij.lang.javascript.psi.JSExpressionStatement;
import com.intellij.lang.javascript.psi.JSParameterList;
import com.intellij.lang.javascript.psi.JSVarStatement;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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

    String selectedText = getSelectedText(caret);
    JSBlockStatement jsBlockStatement = findDescribeBody(file);
    JSParameterList inject = findInjectablesList(file);
    if (jsBlockStatement == null || inject == null) {
      return;
    }

    String textToInsert;
    int offset;

    // Look for vars or lets declared before the inject function.
    // If one is found then append the selected variable name.
    Optional<JSVarStatement> jsVarStatement = findVarWithLetInBody(jsBlockStatement, inject.getTextOffset());
    if (jsVarStatement.isPresent()) {
      // Add the variable at the end.
      offset = jsVarStatement.get().getTextRange().getEndOffset() - 1;
      textToInsert = ", " + selectedText;
    } else {
      // No var/let. Add a new let to the body.
      offset = jsBlockStatement.getTextRange().getStartOffset() + 2;
      textToInsert = String.format("\n  let %s;\n", selectedText);
    }

    runCommand(project, () -> editor.getDocument().insertString(offset, textToInsert));
  }

  private Optional<JSVarStatement> findVarWithLetInBody(JSBlockStatement jsBlockStatement, int endOffset) {
    return PsiTreeUtil.findChildrenOfType(jsBlockStatement, JSVarStatement.class)
        .stream()
        .filter(jsVarStatement -> {
          // Find var/let statements before the inject function.
          if (jsVarStatement.getTextOffset() > endOffset) {
            return false;
          }
          final String text = jsVarStatement.getText();
          return text.startsWith("var") || text.startsWith("let");
        })
        .findFirst();
  }

  @Nullable
  private JSBlockStatement findDescribeBody(PsiFile file) {
    // Find the top level describe.
    JSExpressionStatement describe = findChildrenByTypeWithText(file, JSExpressionStatement.class, "describe");

    // Try to find the body of describe function.
    return PsiTreeUtil.findChildOfType(describe, JSBlockStatement.class);
  }
}
