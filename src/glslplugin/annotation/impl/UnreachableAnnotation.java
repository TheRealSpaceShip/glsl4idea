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

package glslplugin.annotation.impl;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import glslplugin.annotation.Annotator;
import glslplugin.lang.elements.GLSLElement;
import glslplugin.lang.elements.statements.*;

import java.awt.*;

public class UnreachableAnnotation implements Annotator<GLSLStatement> {
    private TextAttributesKey strikeThrough;

    public UnreachableAnnotation() {
        TextAttributes textAttributes = new TextAttributes(Color.GRAY, null, Color.RED, EffectType.STRIKEOUT, Font.ITALIC);
        strikeThrough = TextAttributesKey.createTextAttributesKey("Unreachable", textAttributes);
    }

    public void annotate(GLSLStatement expr, AnnotationHolder holder) {
        //todo: not unreachables...
        if (expr instanceof GLSLBreakStatement || expr instanceof GLSLContinueStatement) {
            GLSLElement parent = expr.findParentByClasses(GLSLDoStatement.class, GLSLForStatement.class, GLSLWhileStatement.class);
            if(parent == null) {
                holder.createErrorAnnotation(expr, "Must be in a loop!");
                return;
            }
        }

        if (expr instanceof GLSLBreakStatement || expr instanceof GLSLContinueStatement || expr instanceof GLSLDiscardStatement || expr instanceof GLSLReturnStatement) {

            PsiElement element = expr.getNextSibling();
            while(element != null) {
                if(element instanceof GLSLElement) {
                    Annotation annotation = holder.createWarningAnnotation(element, "Unreachable expression");
                    annotation.setTextAttributes(strikeThrough);
                }

                element = element.getNextSibling();
            }

        }

    }
}
