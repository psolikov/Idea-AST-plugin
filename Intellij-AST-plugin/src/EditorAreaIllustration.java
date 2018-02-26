import com.intellij.lang.ASTNode;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.StringUtils;

/**
 * Action that prints some characteristics of current method e.g. AST, number of lines etc.
 */
public class EditorAreaIllustration extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        int caret = editor.getCaretModel().getOffset();
        PsiMethod method = null;

        if (psiFile != null) {
            method = PsiTreeUtil.getParentOfType(psiFile.findElementAt(caret), PsiMethod.class);
        }

        PsiElement[] variables = PsiTreeUtil.collectElements(method,
                e -> e instanceof PsiLocalVariable);

        int sum = 0;
        for (PsiElement element : variables) {
            sum += element.getText().split("\\s+")[1].length();
        }

        if (method != null) {
            StringBuilder output = recurseTree(method.getNode(), "");

            Messages.showInfoMessage(String.valueOf(variables.length),
                    "Number of Local Variables");
            Messages.showInfoMessage(String.valueOf(sum / variables.length),
                    "Average Local Variable Length");
            Messages.showInfoMessage(String.valueOf(
                    StringUtils.countMatches(method.getBody().getText(), "\n") + 1),
                    "Number of Lines");
            Messages.showInfoMessage(String.valueOf(output),
                    "Method's AST");
        } else {
            Messages.showInfoMessage("You are outside any methods",
                    "Caret Parameters Inside The Editor");
        }

    }

    private StringBuilder recurseTree(ASTNode obj, String indent) {
        StringBuilder output = new StringBuilder();
        output.append(indent).append(obj.toString());

        for (ASTNode child : obj.getChildren(null)) {
            output.append("\n");
            output.append(recurseTree(child, indent + "--"));
        }

        return output;
    }

    @Override
    public void update(AnActionEvent e) {
    }
}