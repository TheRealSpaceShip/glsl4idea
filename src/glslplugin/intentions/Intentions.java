/*
 *     Copyright 2010 Jean-Paul Balabanian and Yngve Devik Hammersland
 *
 *     This file is part of glsl4idea.
 *
 *     Glsl4idea is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     Glsl4idea is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with glsl4idea.  If not, see <http://www.gnu.org/licenses/>.
 */

package glslplugin.intentions;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import glslplugin.GLSLSupportLoader;
import glslplugin.intentions.vectorcomponents.VectorComponentsPredicate;
import glslplugin.lang.elements.GLSLElement;
import glslplugin.lang.elements.GLSLIdentifier;
import glslplugin.lang.elements.declarations.GLSLDeclarator;
import glslplugin.lang.elements.declarations.GLSLVariableDeclaration;
import glslplugin.lang.parser.GLSLFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Intentions extends PsiElementBaseIntentionAction {
    private VectorComponentsPredicate predicate;
    private Editor editor;

    public Intentions(VectorComponentsPredicate predicate) {
        this.predicate = predicate;
    }

    protected abstract void processIntention(PsiElement element);

    public boolean isAvailable(@NotNull Project project, Editor editor, @Nullable PsiElement element) {
        while (element != null) {
            if (predicate.satisfiedBy(element)) {
                return true;
            } else {
                element = element.getParent();
                if (element instanceof PsiFile) {
                    break;
                }
            }
        }
        return false;
    }

    protected GLSLIdentifier replaceIdentifierElement(GLSLIdentifier identifier, String value) {
        GLSLIdentifier newIdentifier = createIdentifierFromText(identifier, value);
        return (GLSLIdentifier) replaceExpression(identifier, newIdentifier);
    }

    private GLSLIdentifier createIdentifierFromText(GLSLIdentifier identifier, String value) {

        String completeFragment = "float " + value + ";";
        PsiElement newFile = createExpressionFromText(identifier, completeFragment);
        GLSLDeclarator[] glslDeclarators = ((GLSLVariableDeclaration) newFile.getFirstChild()).getDeclarators();
        return glslDeclarators[0].getIdentifier();
    }

    protected PsiElement replaceExpression(PsiElement element, String expression) {
        PsiElement newElement = createExpressionFromText(element, expression);

        final ASTNode newExpressionNode = newElement.getNode();
        final ASTNode oldExpressionNode = element.getNode();
        final PsiElement parentNode = element.getParent();
        final ASTNode grandParentNode = parentNode.getNode();

        if (!(grandParentNode == null || oldExpressionNode == null || newExpressionNode == null)) {
            grandParentNode.replaceChild(oldExpressionNode, newExpressionNode);
            reformat(parentNode);
            return newExpressionNode.getPsi();
        }
        return null;
    }

    protected GLSLElement replaceExpression(GLSLElement from, GLSLElement to) {

        final ASTNode newExpressionNode = to.getNode();
        final ASTNode oldExpressionNode = from.getNode();
        final PsiElement parentNode = from.getParent();
        final ASTNode grandParentNode = parentNode.getNode();

        if (!(grandParentNode == null || oldExpressionNode == null || newExpressionNode == null)) {
            grandParentNode.replaceChild(oldExpressionNode, newExpressionNode);
            reformat(parentNode);
            return (GLSLElement) newExpressionNode.getPsi();
        }
        return null;
    }

    protected PsiElement createExpressionFromText(PsiElement element, String expression) {
        String name = "dummy.glsl";
        //todo: parser must be able to handle this somehow...
        PsiFile dummyFile = PsiFileFactory.getInstance(element.getProject()).createFileFromText(name, GLSLSupportLoader.GLSL.getLanguage(), expression);
        return dummyFile.getFirstChild();
    }

    public static void reformat(PsiElement statement) {
        if (statement.getContainingFile() instanceof GLSLFile) {
            CodeStyleManager.getInstance(statement.getManager()).reformat(statement);
        }
    }

    @Nullable
    PsiElement findMatchingElement(PsiFile file, Editor editor) {
        final CaretModel caretModel = editor.getCaretModel();
        final int position = caretModel.getOffset();
        PsiElement element = file.findElementAt(position);
        return findMatchingElement(element);
    }

    @Nullable
    PsiElement findMatchingElement(@Nullable PsiElement element) {
        while (element != null) {
            if (predicate.satisfiedBy(element)) {
                return element;
            } else {
                element = element.getParent();
                if (element instanceof PsiFile) {
                    break;
                }
            }
        }
        return null;
    }

    private static boolean isFileReadOnly(Project project, PsiFile file) {
        final VirtualFile virtualFile = file.getVirtualFile();
        final ReadonlyStatusHandler readonlyStatusHandler = ReadonlyStatusHandler.getInstance(project);
        final ReadonlyStatusHandler.OperationStatus operationStatus = readonlyStatusHandler.ensureFilesWritable(virtualFile);
        return operationStatus.hasReadonlyFiles();
    }

//    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
//        this.editor = editor;
//
//        if (isFileReadOnly(project, file)) {
//            return;
//        }
//        final PsiElement element = findMatchingElement(file, editor);
//        if (element == null) {
//            return;
//        }
//        processIntention(element);
//    }

    protected Editor getEditor() {
        return editor;
    }
}
