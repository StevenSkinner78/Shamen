package gov.doc.isu.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

/***
 * This method is used to forward users to a Credit Page that displays the programmer(s) that created the page or app the user is using.
 * 
 * @author <strong>Joshua Rudolph</strong> JCCC, Jun 28, 2016
 * @author <strong>Steve Skinnner</strong> JCCC, Jun 29, 2016
 */
public class CreditPageTag extends TagSupport {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static Logger log = Logger.getLogger("gov.doc.isu.taglib.CreditPageTag");
    private String path;
    private String imagePath;
    private String anchorClass;
    private String imageClass;

    /**
     * This method performs all neccessary function to compile tag.
     * 
     * @return Integer
     */
    @Override
    public int doStartTag() {
        log.debug("Entering CreditPageTag.doStartTag");
        String contextPath = ((HttpServletRequest) pageContext.getRequest()).getContextPath();
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("<a");
            if(null != anchorClass && !"".equalsIgnoreCase(anchorClass.trim())) {
                sb.append(" class=\"").append(anchorClass).append("\"");
            }
            sb.append(" href=\"");
            sb.append(contextPath);
            sb.append(getFormattedPath(path));
            sb.append("\" target=\"_blank\">");
            if(imagePath != null && !"".equalsIgnoreCase(imagePath.trim())){
                sb.append("<img title=\"About Shamen\"");
                sb.append(" id=\"info\" src=\"");
                sb.append(contextPath);
                sb.append(getFormattedPath(imagePath));
                sb.append("\"/>");
                sb.append("</a><span style=\"padding-left: 2px;padding-right: 2px;\"></span>");
            }else{
                sb.append("<i");
                if(imageClass != null && !"".equalsIgnoreCase(imageClass.trim())){
                    sb.append(" class=\"").append(imageClass).append("\"");
                }else{
                    sb.append(" class=\"fa fa-info-circle fa-fw\"");
                }
                sb.append("></i> About</a>");

            }
            pageContext.getOut().print(sb.toString());
        }catch(IOException e){
            log.error("IOException occurred while constructing the CreditPageTag", e);
        }catch(final Exception e){
            log.error("Exception occurred while constructing the CreditPageTag", e);
        }// end catch
        log.debug("Exiting CreditPageTag.doStartTag()");
        return EVAL_PAGE;
    }// end doStartTag

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }// end getPath

    /**
     * @param path
     *        the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }// end setPath

    /**
     * @return the imagePath
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * @param imagePath
     *        the imagePath to set
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * @return the anchorClass
     */
    public String getAnchorClass() {
        return anchorClass;
    }

    /**
     * @param anchorClass
     *        the anchorClass to set
     */
    public void setAnchorClass(String anchorClass) {
        this.anchorClass = anchorClass;
    }

    /**
     * @return the imageClass
     */
    public String getImageClass() {
        return imageClass;
    }

    /**
     * @param imageClass
     *        the imageClass to set
     */
    public void setImageClass(String imageClass) {
        this.imageClass = imageClass;
    }

    /**
     * Returns the value with page prepended with a "/" if it is not already.
     * 
     * @param paramPath
     *        The value for the path.
     * @return String
     */
    private String getFormattedPath(String paramPath) {
        log.debug("Entering CreditPageTag.getFormattedPath");
        log.debug("Paramaters: page=" + String.valueOf(paramPath));
        if(!paramPath.startsWith("/")){
            paramPath = "/" + paramPath;
        }// end else
        log.debug("Exiting CreditPageTag.getFormattedPath");
        return paramPath;
    }// end method
}
