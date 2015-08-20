package za.co.knonchalant.plugins.nodoc;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

public class CommentItem
{
  private final PsiClass clazz;
  private final PsiMethod method;
  private String comment;

  /**
   * Constructor
   * @param clazz the clazz
   * @param method the method
   * @param comment the comment
   **/
  public CommentItem(PsiClass clazz, PsiMethod method, String comment)
  {
    this.clazz = clazz;
    this.method = method;
    this.comment = comment;
  }

  /**
   * Get clazz
   * @return the clazz
   **/
  public PsiClass getClazz()
  {
    return clazz;
  }

  /**
   * Get method
   * @return the method
   **/
  public PsiMethod getMethod()
  {
    return method;
  }

  /**
   * Get comment
   * @return the comment
   **/
  public String getComment()
  {
    return comment;
  }
}
