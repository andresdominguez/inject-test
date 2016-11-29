package com.karateca.injecttest;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSFunction;
import com.intellij.lang.javascript.psi.JSFunctionExpression;
import com.intellij.lang.javascript.psi.JSParameterList;
import com.intellij.lang.javascript.psi.impl.JSFunctionExpressionImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

public class InjectTestAction extends AnAction {

  @Override
  public void actionPerformed(AnActionEvent e) {
    PsiElement element = e.getData(PlatformDataKeys.PSI_ELEMENT);

    JSFunction parentOfType = PsiTreeUtil.getParentOfType(element, JSFunction.class);
    JSFunctionExpression one = PsiTreeUtil.getParentOfType(element, JSFunctionExpression.class);
    JSFunctionExpressionImpl teo = PsiTreeUtil.getParentOfType(element, JSFunctionExpressionImpl.class);

    JSCallExpression injectElement = findInjectElement(element);
    if (injectElement == null) {
      return;
    }

    JSParameterList parameterList = PsiTreeUtil.findChildOfType(injectElement, JSParameterList.class);

    System.out.println(element);
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
