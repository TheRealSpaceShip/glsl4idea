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
package glslplugin.lang.scanner;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static glslplugin.lang.elements.GLSLTokenTypes.*;
import java.util.List;
import java.util.ArrayList;

%%

%class GLSLFlexLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType

%{
%}


DIGIT               = [0-9]
OCTAL_DIGIT         = [0-7]
HEX_DIGIT           = [0-9A-Fa-f]
NON_DIGIT           = [_a-zA-Z]

WHITE_SPACE         = [ \n\r\t\f]

IDENTIFIER          = {NON_DIGIT}({NON_DIGIT} | {DIGIT})*

INTEGER_CONSTANT    = {DECIMAL_CONSTANT} | {HEX_CONSTANT} | {OCTAL_CONSTANT}
DECIMAL_CONSTANT    = (0|([1-9]({DIGIT})*))
HEX_CONSTANT        = 0[Xx]({HEX_DIGIT})*
OCTAL_CONSTANT      = 0({OCTAL_DIGIT})*

FLOATING_CONSTANT   = (({FLOATING_CONSTANT1})|({FLOATING_CONSTANT2})|({FLOATING_CONSTANT3})|({FLOATING_CONSTANT4})) ([fF])?
FLOATING_CONSTANT1  = ({DIGIT})+"."({DIGIT})*({EXPONENT_PART})?
FLOATING_CONSTANT2  = "."({DIGIT})+({EXPONENT_PART})?
FLOATING_CONSTANT3  = ({DIGIT})+({EXPONENT_PART})
FLOATING_CONSTANT4  = ({DIGIT})+
EXPONENT_PART       = [Ee]["+""-"]?({DIGIT})*

LINE_COMMENT        = "//"[^\r\n]*
BLOCK_COMMENT       = "/*"([^"*"]|("*"+[^"*""/"]))*("*"+"/")?

%%
/**
 * LEXICAL RULES:
 */

true                    {return BOOL_CONSTANT; }
false                   {return BOOL_CONSTANT; }

/* GLSL types */
void                    {return VOID_TYPE; }
float                   {return FLOAT_TYPE; }
int                     {return INT_TYPE; }
bool                    {return BOOL_TYPE; }
vec2                    {return VEC2_TYPE; }
vec3                    {return VEC3_TYPE; }
vec4                    {return VEC4_TYPE; }
ivec2                   {return IVEC2_TYPE; }
ivec3                   {return IVEC3_TYPE; }
ivec4                   {return IVEC4_TYPE; }
bvec2                   {return BVEC2_TYPE; }
bvec3                   {return BVEC3_TYPE; }
bvec4                   {return BVEC4_TYPE; }
mat2                    {return MAT2_TYPE; }
mat3                    {return MAT3_TYPE; }
mat4                    {return MAT4_TYPE; }
mat2x2                  {return MAT2X2_TYPE; }
mat2x3                  {return MAT2X3_TYPE; }
mat2x4                  {return MAT2X4_TYPE; }
mat3x2                  {return MAT3X2_TYPE; }
mat3x3                  {return MAT3X3_TYPE; }
mat3x4                  {return MAT3X4_TYPE; }
mat4x2                  {return MAT4X2_TYPE; }
mat4x3                  {return MAT4X3_TYPE; }
mat4x4                  {return MAT4X4_TYPE; }
sampler1D               {return SAMPLER1D_TYPE; }
sampler2D               {return SAMPLER2D_TYPE; }
sampler3D               {return SAMPLER3D_TYPE; }
samplerCube             {return SAMPLERCUBE_TYPE; }
sampler1DShadow         {return SAMPLER1DSHADOW_TYPE; }
sampler2DShadow         {return SAMPLER2DSHADOW_TYPE; }

/* GLSL STORAGE QUALIFIERS */
//note: these are declared separately to better support error handling
//      for example swapping varying and centroid should not cause lexer failure.
//      nor should the "wrong" whitespace separation do.
const                   {return CONST_KEYWORD; }
attribute               {return ATTRIBUTE_KEYWORD; }
uniform                 {return UNIFORM_KEYWORD; }
varying                 {return VARYING_KEYWORD; }
centroid                {return CENTROID_KEYWORD; }
invariant               {return INVARIANT_KEYWORD;}

/* GLSL PARAMETER QUALIFIER */
in                      {return IN_KEYWORD; }
out                     {return OUT_KEYWORD; }
inout                   {return INOUT_KEYWORD; }

/* GLSL ITERATION FLOW_KEYWORDS */
while                   {return WHILE_KEYWORD; }
do                      {return DO_KEYWORD; }
for                     {return FOR_KEYWORD; }

/* GLSL JUMP FLOW_KEYWORDS */
break                   {return BREAK_JUMP_STATEMENT; }
continue                {return CONTINUE_JUMP_STATEMENT; }
return                  {return RETURN_JUMP_STATEMENT; }
discard                 {return DISCARD_JUMP_STATEMENT; }

struct                  {return STRUCT; }

/* GLSL SELECTION FLOW_KEYWORDS */
if                      {return IF_KEYWORD; }
else                    {return ELSE_KEYWORD; }


/* GLSL Symbols */
"{"                     {return LEFT_BRACE; }
"}"                     {return RIGHT_BRACE; }
"["                     {return LEFT_BRACKET; }
"]"                     {return RIGHT_BRACKET; }
"("                     {return LEFT_PAREN; }
")"                     {return RIGHT_PAREN; }

"="                     {return EQUAL; }
"*="                    {return MUL_ASSIGN; }
"/="                    {return DIV_ASSIGN; }
"+="                    {return ADD_ASSIGN; }
"-="                    {return SUB_ASSIGN; }
"+"                     {return PLUS; }
"-"                     {return DASH; }
"/"                     {return SLASH; }
"*"                     {return STAR; }

"--"                    {return DEC_OP; }
"++"                    {return INC_OP; }

"=="                    {return EQ_OP; }
"<"                     {return LEFT_ANGLE; }
">"                     {return RIGHT_ANGLE; }
">="                    {return GE_OP; }
"<="                    {return LE_OP; }
"!="                    {return NE_OP; }
"&&"                    {return AND_OP; }
"||"                    {return OR_OP; }
"^^"                    {return XOR_OP; }

"?"                     {return QUESTION; }
":"                     {return COLON; }
"!"                     {return BANG; }
"."                     {return DOT; }
";"                     {return SEMICOLON; }
","                     {return COMMA; }

{WHITE_SPACE}+          {return WHITE_SPACE;}

#{WHITE_SPACE}*version[^\n\r]* {return COMPILER_DIRECTIVE_VERSION;}
#{WHITE_SPACE}*extension[^\n\r]* {return COMPILER_DIRECTIVE_EXTENSION;}
#{WHITE_SPACE}*pragma[^\n\r]* {return COMPILER_DIRECTIVE_PRAGMA;}
#{WHITE_SPACE}*[^\n\r]* {return COMPILER_DIRECTIVE_OTHER;}

{IDENTIFIER}            {return IDENTIFIER;}

{INTEGER_CONSTANT}      {return INTEGER_CONSTANT; }
{FLOATING_CONSTANT}     {return FLOAT_CONSTANT; }
{LINE_COMMENT}          {return COMMENT_LINE; }
{BLOCK_COMMENT}         {return COMMENT_BLOCK; }
.                       {return UNKNOWN; }
