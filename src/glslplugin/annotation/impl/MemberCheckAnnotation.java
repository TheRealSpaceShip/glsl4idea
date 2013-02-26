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

import com.intellij.lang.annotation.AnnotationHolder;
import glslplugin.annotation.Annotator;
import glslplugin.lang.elements.GLSLIdentifier;
import glslplugin.lang.elements.expressions.GLSLFieldSelectionExpression;
import glslplugin.lang.elements.types.GLSLStructType;
import glslplugin.lang.elements.types.GLSLType;

public class MemberCheckAnnotation implements Annotator<GLSLFieldSelectionExpression> {
    public void annotate(GLSLFieldSelectionExpression expr, AnnotationHolder holder) {
        GLSLType leftHandType = expr.getLeftHandExpression().getType();

        if (leftHandType instanceof GLSLStructType) {
            GLSLIdentifier memberIdentifier = expr.getMemberIdentifier();
            GLSLType memberType = leftHandType.getMembers().get(memberIdentifier.getIdentifierName());
            if (memberType == null) {
                holder.createErrorAnnotation(memberIdentifier, "Unknown member for " + leftHandType.getTypename());
            }

        }

    }
}
