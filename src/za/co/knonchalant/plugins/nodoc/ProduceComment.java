package za.co.knonchalant.plugins.nodoc;

import com.intellij.psi.*;

public class ProduceComment
{
  private static final String COMMENT = "/**\n" +
          " * %s\n" +
          " %s" + // @return
          " **/";

  /**
   * Produce comment for
   *
   * @param method the method
   * @return the result
   */
  public static String produceCommentFor(PsiMethod method)
  {
    StringBuilder mainComment = new StringBuilder();
    StringBuilder returnComment = new StringBuilder();
    String name = method.getName();

    if (name.equals("toString"))
    {
      makeToStringComment(method, mainComment, returnComment);
    }
    else if (name.startsWith("is"))
    {
      makeBooleanGetterComment(method, mainComment, returnComment, name);
    }
    else
    {
      makeStandardComment(method, mainComment, returnComment, name);
    }

    return String.format(COMMENT, mainComment.toString(), returnComment.toString());
  }

  /**
   * Make standard comment
   * @param method the method
   * @param mainComment the main comment
   * @param returnComment the return comment
   * @param name the name
   **/
  private static void makeStandardComment(PsiMethod method, StringBuilder mainComment, StringBuilder returnComment, String name)
  {
    PsiParameterList parameterList = method.getParameterList();

    if (name.startsWith("get") && parameterList.getParametersCount() == 0)
    {
      String value = getValue(name);
      if (method.getReturnType() != null && method.getReturnType() != PsiType.VOID)
      {
        returnComment.append(" * @return the ").append(value).append("\n");
      }
    }
    else if (name.startsWith("set") && !name.startsWith("setup") && parameterList.getParametersCount() == 1)
    {
      String value = getValue(name);
      if (parameterList.getParametersCount() == 1)
      {
        PsiParameter psiParameter = parameterList.getParameters()[0];
        returnComment.append(" * @param ");
        returnComment.append(psiParameter.getName());
        returnComment.append(" the new value for ").append(value).append("\n");
      }
    }
    else
    {
      if (parameterList.getParametersCount() != 0)
      {
        for (int i = 0; i < parameterList.getParametersCount(); i++)
        {
          PsiParameter psiParameter = parameterList.getParameters()[i];
          returnComment.append(" * @param ");
          returnComment.append(psiParameter.getName());

          String variableName = splitCamelCase(psiParameter.getName()).toLowerCase();
          String typeName = psiParameter.getType().getPresentableText();
          returnComment.append(" the ");

          if (typeName.toLowerCase().startsWith(variableName))
          {
            returnComment.append(splitCamelCase(typeName).toLowerCase()).append("\n");
          }
          else
          {
            returnComment.append(variableName).append("\n");
          }
        }
      }
      if (method.getReturnType() != null && method.getReturnType() != PsiType.VOID)
      {
        returnComment.append(" * @return the result\n");
      }
    }
    if (!method.isConstructor())
    {
      mainComment.append(splitCamelCase(name));
    }
    else
    {
      mainComment.append("Constructor");
    }
  }

  /**
   * Make boolean getter comment
   * @param method the method
   * @param mainComment the main comment
   * @param returnComment the return comment
   * @param name the name
   **/
  private static void makeBooleanGetterComment(PsiMethod method, StringBuilder mainComment, StringBuilder returnComment, String name)
  {
    String value = splitCamelCase(name).toLowerCase();
    mainComment.append("Checks if this ").append(value);

    if (method.getReturnType() != null && method.getReturnType() != PsiType.VOID)
    {
      returnComment.append(" * @return true if this ").append(value).append("\n");
    }
  }

  /**
   * Make to string comment
   * @param method the method
   * @param mainComment the main comment
   * @param returnComment the return comment
   **/
  private static void makeToStringComment(PsiMethod method, StringBuilder mainComment, StringBuilder returnComment)
  {
    PsiElement parent = method.getParent();
    if (parent instanceof PsiClass)
    {
      PsiClass psiClass = (PsiClass) parent;
      mainComment.append("Returns the string representation of ").append(psiClass.getName());
      returnComment.append(" * @return textual representation\n");
    }
  }

  /**
   * Get value
   *
   * @param name the name
   * @return the result
   */
  private static String getValue(String name)
  {
    return splitCamelCase(name.substring(3)).toLowerCase();
  }

  /**
   * Split camel case
   *
   * @param s the string
   * @return the result
   */
  static String splitCamelCase(String s)
  {
    String s1 = s.replaceAll(String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])", "(?<=[A-Za-z])(?=[^A-Za-z])"), " ");
    s1 = s1.substring(0, 1).toUpperCase() + s1.substring(1).toLowerCase();
    return s1;
  }
}
