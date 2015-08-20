package za.co.knonchalant.plugins.nodoc;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;

import java.util.ArrayList;
import java.util.List;

public class TextBoxes extends AnAction {
    /**
     * Action performed
     *
     * @param event the event
     */
    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getRequiredData(CommonDataKeys.PROJECT);
        PsiFile file = event.getData(LangDataKeys.PSI_FILE);

        if (file instanceof PsiJavaFile) {
            processFile(project, (PsiJavaFile) file);
        }
    }

    /**
     * Process file
     * @param project the project
     * @param file the file
     **/
    private void processFile(Project project, PsiJavaFile file) {
        List<List<CommentItem>> items = new ArrayList<List<CommentItem>>();

        for (PsiClass clazz : file.getClasses()) {
            List<CommentItem> classItems = new ArrayList<CommentItem>();
            items.add(classItems);

            for (PsiMethod method : clazz.getMethods()) {
                PsiDocComment comment = method.getDocComment();
                if (comment == null) {
                    classItems.add(new CommentItem(clazz, method, ProduceComment.produceCommentFor(method)));
                }
            }
        }

        insertAllCommentsIntoProject(project, items);
    }

    /**
     * Insert all comments into project
     *
     * @param project  the project
     * @param comments the comments
     */
    private void insertAllCommentsIntoProject(final Project project, final List<List<CommentItem>> comments) {
        final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();

        Runnable runnable = new Runnable() {
            public void run() {
                for (List<CommentItem> list : comments) {
                    for (CommentItem commentItem : list) {
                        PsiComment commentFromText = factory.createCommentFromText(commentItem.getComment(), commentItem.getMethod());
                        commentItem.getClazz().addBefore(commentFromText, commentItem.getMethod());
                    }
                }
            }
        };

        WriteCommandAction.runWriteCommandAction(project, runnable);
    }
}

