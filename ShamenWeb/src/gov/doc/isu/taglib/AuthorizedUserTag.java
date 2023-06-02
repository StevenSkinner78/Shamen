package gov.doc.isu.taglib;

import java.io.IOException;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseFieldTag;
import org.apache.struts.taglib.html.CheckboxTag;
import org.apache.struts.taglib.html.OptionTag;
import org.apache.struts.taglib.html.SelectTag;

import gov.doc.isu.dwarf.util.StringUtil;
import gov.doc.isu.shamen.form.AuthorizedUserForm;

/**
 * Builds Authorized User Detail. Detail consist of input box for User Id, Last Name, and First Name and a drop down box for Authority. Tag also writes javascript function that handle on change event for Authority that changes which description displays based on selected authority.
 *
 * @author Steven L. Skinner SLS00#IS
 */
public class AuthorizedUserTag extends BaseFieldTag {

    /**
     *
     */
    private static final long serialVersionUID = 6817852106481137278L;
    private static Logger log = Logger.getLogger(AuthorizedUserTag.class);
    private static final String CSS_CLASS = " class=\"";
    private static final String LABEL_FOR = " for=\"";
    private static final String ID = " id=\"";
    private static final String DOUBLE_QUOTE = "\"";
    private static final String DIV_ROW = "<div class=\"row\">";
    private static final String DIV_START = "<div";
    private static final String DIV_END = "</div>";
    private static final String LABEL_START = "<label";
    private static final String LABEL_END = "</label>";
    private static final String DIV_COL_6 = "<div class=\"col-lg-6\">";
    private static final String SAMP_START = "<samp>";
    private static final String SAMP_END = "</samp>";
    private static final String CLOSE_TAG = ">";
    private static final String CSS_CLASS_FORM_GROUP = "form-group";
    private static final String CSS_CLASS_FOR_LABEL = "control-label";

    private String formName;
    private int levelSelect;

    /**
     * @return the formName
     */
    public String getFormName() {
        return formName;
    }

    /**
     * @param formName
     *        the formName to set
     */
    public void setFormName(final String formName) {
        this.formName = formName;
    }

    /**
     * @return the levelSelect
     */
    public int getLevelSelect() {
        return levelSelect;
    }

    /**
     * @param levelSelect
     *        the levelSelect to set
     */
    public void setLevelSelect(final int levelSelect) {
        this.levelSelect = levelSelect;
    }

    /**
     * Class Constructor
     */
    public AuthorizedUserTag() {

        super();
        this.type = "text";
        doReadonly = true;

    }

    /**
     * Constructs the Authorized User Detail
     *
     * @return int
     * @throws JspException
     *         if an JspException occurred
     */
    public int doStartTag() throws JspException {
        log.debug("Entering AuthorizedUserTag.doStartTag");
        HttpSession session = pageContext.getSession();
        /** Check the form in the request if not then try to find in the session */
        AuthorizedUserForm theForm = (AuthorizedUserForm) pageContext.getRequest().getAttribute(formName);

        if(theForm == null){
            theForm = (AuthorizedUserForm) session.getAttribute(formName);
        }// end if
        try{
            pageContext.getOut().print(DIV_ROW);
            renderLastName(theForm);
            renderFirstName(theForm);
            pageContext.getOut().print(DIV_END + DIV_ROW);
            renderUserId(theForm);
            renderAuthority(theForm);
            pageContext.getOut().print(DIV_END + "<div class=\"row mb-3\">");
            renderEmailInd(theForm);
            pageContext.getOut().print(constructAuthorityLevelDesc().toString());
            pageContext.getOut().print(DIV_END);
        }catch(JspException e){
            log.error("JspException While constructing the Authorized User Detail Tag: " + e.getMessage());
        }catch(IOException e){
            log.error("IOException While constructing the Authorized User Detail Tag: " + e.getMessage());
        }catch(Exception e){
            log.error("Exception While constructing the Authorized User Detail Tag: " + e.getMessage());
        }// end try
        log.debug("Exiting AuthorizedUserTag.doStartTag");
        return EVAL_PAGE;
    }// end method

    /**
     * Renders a fully formed &lt;input&gt; element and label for Authorized User Id. Error Tag used by struts validator is added at the end of input box.
     *
     * @param theForm
     *        {@link AuthorizedUserForm} is the form passed in and used for authorized user detail.
     * @throws JspException
     *         If a jsp exception occurred
     * @throws IOException
     *         If an input or output exception occurred
     */
    protected void renderUserId(AuthorizedUserForm theForm) throws JspException, IOException {
        log.debug(" entering AuthorizedUserTag.renderUserId");
        StringBuffer sb = new StringBuffer();
        String message = TagUtils.getInstance().message(pageContext, null, null, "prompt.authorizedUser.userID", null);
        sb.append(DIV_COL_6);
        sb.append(DIV_START).append(CSS_CLASS).append(CSS_CLASS_FORM_GROUP).append(DOUBLE_QUOTE).append(CLOSE_TAG);
        sb.append(LABEL_START).append(LABEL_FOR).append("userID").append(DOUBLE_QUOTE).append(CSS_CLASS).append(CSS_CLASS_FOR_LABEL).append(DOUBLE_QUOTE).append(CLOSE_TAG).append(message).append(LABEL_END);
        pageContext.getOut().write(sb.toString());
        this.setProperty("authUser.userID");
        this.setStyleId("userID");
        this.setStyleClass("form-control");
        this.setMaxlength("30");
        this.setErrorStyleClass("form-control is-invalid");
        this.setErrorKey("org.apache.struts.action.ERROR");
        this.setValue(theForm.getAuthUser().getUserID());
        TagUtils.getInstance().write(pageContext, this.renderInputElement());
        pageContext.getOut().write(DIV_END);
        pageContext.getOut().write(DIV_END);
        log.debug(" exiting AuthorizedUserTag.renderUserId");
    }

    /**
     * Renders a fully formed &lt;input&gt; element and label for Authorized User Last Name. Error Tag used by struts validator is added at the end of input box.
     *
     * @param theForm
     *        {@link AuthorizedUserForm} is the form passed in and used for authorized user detail.
     * @throws JspException
     *         If a jsp exception occurred
     * @throws IOException
     *         If an input or output exception occurred
     */
    protected void renderLastName(AuthorizedUserForm theForm) throws JspException, IOException {
        log.debug(" entering AuthorizedUserTag.renderLastName");
        StringBuffer sb = new StringBuffer();
        String message = TagUtils.getInstance().message(pageContext, null, null, "prompt.authorizedUser.lastName", null);
        sb.append(DIV_COL_6);
        sb.append(DIV_START).append(CSS_CLASS).append(CSS_CLASS_FORM_GROUP).append(DOUBLE_QUOTE).append(CLOSE_TAG);
        sb.append(LABEL_START).append(LABEL_FOR).append("lastName").append(DOUBLE_QUOTE).append(CSS_CLASS).append(CSS_CLASS_FOR_LABEL).append(DOUBLE_QUOTE).append(CLOSE_TAG).append(message).append(LABEL_END);
        pageContext.getOut().write(sb.toString());
        this.setProperty("authUser.lastName");
        this.setStyleId("lastName");
        this.setStyleClass("form-control");
        this.setMaxlength("35");
        this.setErrorStyleClass("form-control is-invalid");
        this.setErrorKey("org.apache.struts.action.ERROR");
        this.setValue(theForm.getAuthUser().getLastName());
        TagUtils.getInstance().write(pageContext, this.renderInputElement());
        pageContext.getOut().write(DIV_END);
        pageContext.getOut().write(DIV_END);
        log.debug(" exiting AuthorizedUserTag.renderLastName");
    }

    /**
     * Renders a fully formed &lt;input&gt; element and label for Authorized First Name. Error Tag used by struts validator is added at the end of input box.
     *
     * @param theForm
     *        {@link AuthorizedUserForm} is the form passed in and used for authorized user detail.
     * @throws JspException
     *         If a jsp exception occurred
     * @throws IOException
     *         If an input or output exception occurred
     */
    protected void renderFirstName(AuthorizedUserForm theForm) throws JspException, IOException {
        log.debug(" entering AuthorizedUserTag.renderFirstName");
        StringBuffer sb = new StringBuffer();
        String message = TagUtils.getInstance().message(pageContext, null, null, "prompt.authorizedUser.firstName", null);
        sb.append(DIV_COL_6);
        sb.append(DIV_START).append(CSS_CLASS).append(CSS_CLASS_FORM_GROUP).append(DOUBLE_QUOTE).append(CLOSE_TAG);
        sb.append(LABEL_START).append(LABEL_FOR).append("firstName").append(DOUBLE_QUOTE).append(CSS_CLASS).append(CSS_CLASS_FOR_LABEL).append(DOUBLE_QUOTE).append(CLOSE_TAG).append(message).append(LABEL_END);
        pageContext.getOut().write(sb.toString());
        this.setProperty("authUser.firstName");
        this.setStyleId("firstName");
        this.setMaxlength("35");
        this.setStyleClass("form-control");
        this.setErrorStyleClass("form-control is-invalid");
        this.setErrorKey("org.apache.struts.action.ERROR");
        this.setValue(theForm.getAuthUser().getFirstName());
        TagUtils.getInstance().write(pageContext, this.renderInputElement());
        pageContext.getOut().write(DIV_END);
        pageContext.getOut().write(DIV_END);
        log.debug(" exiting AuthorizedUserTag.renderFirstName");

    }

    /**
     * Generate an HTML %lt;checkbox&gt; element for Authorized User Email Indicator.
     *
     * @param theForm
     *        {@link AuthorizedUserForm} is the form passed in and used for authorized user detail.
     * @throws JspException
     *         If a jsp exception occurred
     * @throws IOException
     *         If an input or output exception occurred
     */
    protected void renderEmailInd(AuthorizedUserForm theForm) throws JspException, IOException {
        log.debug(" entering AuthorizedUserTag.renderEmailInd");
        StringBuffer sb = new StringBuffer();
        String message = TagUtils.getInstance().message(pageContext, null, null, "prompt.authorizedUser.emailInd", null);
        sb.append(DIV_COL_6);
        sb.append(DIV_START).append(CSS_CLASS).append("custom-control custom-toggle").append(DOUBLE_QUOTE).append(CLOSE_TAG);
        pageContext.getOut().write(sb.toString());
        CheckboxTag tag = new CheckboxTag();
        tag.setPageContext(pageContext);
        tag.setProperty("authUser.emailInd");
        tag.setStyleId("emailInd");
        tag.setStyleClass("custom-control-input");
        tag.setValue(!StringUtil.isNullOrEmpty(theForm.getAuthUser().getEmailInd()) ? theForm.getAuthUser().getEmailInd() : "N");
        tag.doStartTag();
        tag.doAfterBody();
        tag.doEndTag();
        sb = new StringBuffer();
        sb.append(LABEL_START).append(LABEL_FOR).append("emailInd").append(DOUBLE_QUOTE).append(CSS_CLASS).append("custom-control-label").append(DOUBLE_QUOTE).append(CLOSE_TAG).append(message).append(LABEL_END);
        pageContext.getOut().write(sb.toString());
        pageContext.getOut().write(DIV_END);
        pageContext.getOut().write(DIV_END);
        log.debug(" exiting AuthorizedUserTag.renderEmailInd");

    }

    /**
     * Generate an HTML %lt;select&gt; element for Authorized User Authority. Error Tag used by struts validator is added at the end of input box.
     *
     * @param theForm
     *        {@link AuthorizedUserForm} is the form passed in and used for authorized user detail.
     * @throws JspException
     *         If a jsp exception occurred
     * @throws IOException
     *         If an input or output exception occurred
     */
    protected void renderAuthority(AuthorizedUserForm theForm) throws JspException, IOException {
        log.debug(" entering AuthorizedUserTag.renderAuthority");
        StringBuffer sb = new StringBuffer();
        String message = TagUtils.getInstance().message(pageContext, null, null, "prompt.authorizedUser.authority", null);
        sb.append(DIV_COL_6);
        sb.append(DIV_START).append(CSS_CLASS).append(CSS_CLASS_FORM_GROUP).append(DOUBLE_QUOTE).append(CLOSE_TAG);
        sb.append(LABEL_START).append(LABEL_FOR).append("authority").append(DOUBLE_QUOTE).append(CSS_CLASS).append(CSS_CLASS_FOR_LABEL).append(DOUBLE_QUOTE).append(CLOSE_TAG).append(message).append(LABEL_END);
        pageContext.getOut().write(sb.toString());
        SelectTag tag = new SelectTag();
        tag.setPageContext(pageContext);
        tag.setProperty("authUser.authority");
        tag.setStyleId("authority");
        tag.setStyleClass("custom-select");
        tag.setOnchange("showAuthLevelDesc()");
        tag.setErrorStyleClass("custom-select is-invalid");
        tag.setErrorKey("org.apache.struts.action.ERROR");
        tag.setValue(theForm.getAuthUser().getAuthority());
        tag.doStartTag();
        log.debug(" start building authority level dropdown");
        renderOption(tag);
        tag.doAfterBody();
        tag.doEndTag();
        log.debug(" end building authority level dropdown");
        pageContext.getOut().write(DIV_END);
        pageContext.getOut().write(DIV_END);
        log.debug(" exiting AuthorizedUserTag.renderAuthority");

    }

    /**
     * Generate an HTML %lt;option&gt; element for Authorized User Authority.
     *
     * @param tag
     *        {@link SelectTag} parent tag for the options
     * @throws JspException
     *         If a jsp exception occurred
     * @throws IOException
     *         If an input or output exception occurred
     */
    protected void renderOption(SelectTag tag) throws JspException, IOException {
        log.debug(" entering AuthorizedUserTag.renderOption");
        OptionTag optionTag;
        optionTag = new OptionTag();
        optionTag.setPageContext(pageContext);
        optionTag.setParent(tag);
        optionTag.setValue("");
        log.debug("  add option=" + String.valueOf(optionTag.getValue()));
        optionTag.setKey("prompt.authorizedUser.authorityLevel.default");
        optionTag.doStartTag();
        optionTag.doEndTag();
        if(levelSelect == 0 || levelSelect == 2){
            optionTag = new OptionTag();
            optionTag.setPageContext(pageContext);
            optionTag.setParent(tag);
            optionTag.setValue("VIEW");
            log.debug("  add option=" + String.valueOf(optionTag.getValue()));
            optionTag.setKey("prompt.authorizedUser.authorityLevel.view");
            optionTag.doStartTag();
            optionTag.doEndTag();
        }// end if
        if(levelSelect == 0 || levelSelect == 1){
            optionTag = new OptionTag();
            optionTag.setPageContext(pageContext);
            optionTag.setParent(tag);
            optionTag.setValue("USER");
            log.debug("  add option=" + String.valueOf(optionTag.getValue()));
            optionTag.setKey("prompt.authorizedUser.authorityLevel.user");
            optionTag.doStartTag();
            optionTag.doEndTag();
        }// end if
        optionTag = new OptionTag();
        optionTag.setPageContext(pageContext);
        optionTag.setParent(tag);
        optionTag.setValue("ADMN");
        log.debug("  add option=" + String.valueOf(optionTag.getValue()));
        optionTag.setKey("prompt.authorizedUser.authorityLevel.admn");
        optionTag.doStartTag();
        optionTag.doEndTag();
        log.debug(" exiting AuthorizedUserTag.renderOption");
    }

    /**
     * Returns the string output of the authority levels description. Values displayed for user based on selected authority level.
     *
     * @return the string output of authority level description.
     * @throws JspException
     *         If a jsp exception occurred
     */
    private StringBuffer constructAuthorityLevelDesc() throws JspException {
        log.debug(" entering AuthorizedUserTag.constructAuthorityLevelDesc");
        String adminDesc = TagUtils.getInstance().message(pageContext, null, null, "prompt.authorizedUser.authority.admin.desc", null);
        String userDesc = TagUtils.getInstance().message(pageContext, null, null, "prompt.authorizedUser.authority.user.desc", null);
        String viewDesc = TagUtils.getInstance().message(pageContext, null, null, "prompt.authorizedUser.authority.view.desc", null);

        StringBuffer sb = new StringBuffer();
        sb.append(DIV_COL_6);
        sb.append(DIV_START).append(CSS_CLASS).append(CSS_CLASS_FORM_GROUP).append(DOUBLE_QUOTE).append(CLOSE_TAG);

        sb.append(DIV_START).append(ID).append("admnDesc").append(DOUBLE_QUOTE).append(CLOSE_TAG);
        sb.append("<h3 class=\"text-white\">Administrator</h3>");
        sb.append(SAMP_START).append(adminDesc).append(SAMP_END);
        sb.append(DIV_END);

        sb.append(DIV_START).append(ID).append("userDesc").append(DOUBLE_QUOTE).append(CLOSE_TAG);
        sb.append("<h3 class=\"text-white\">User</h3>");
        sb.append(SAMP_START).append(userDesc).append(SAMP_END);
        sb.append(DIV_END);

        sb.append(DIV_START).append(ID).append("viewDesc").append(DOUBLE_QUOTE).append(CLOSE_TAG);
        sb.append("<h3 class=\"text-white\">View</h3>");
        sb.append(SAMP_START).append(viewDesc).append(SAMP_END);
        sb.append(DIV_END);

        sb.append(DIV_END);
        sb.append(DIV_END);
        log.debug(" exiting AuthorizedUserTag.constructAuthorityLevelDesc");
        return sb;
    }

}
