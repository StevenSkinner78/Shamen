package gov.doc.isu.shamen.action;

import static gov.doc.isu.dwarf.resources.Constants.EMPTY_STRING;
import static gov.doc.isu.dwarf.resources.Constants.FAILURE;
import static gov.doc.isu.dwarf.resources.Constants.KEY;
import static gov.doc.isu.dwarf.resources.Constants.LIST;
import static gov.doc.isu.dwarf.resources.Constants.VAL;
import static gov.doc.isu.shamen.resources.AppConstants.DEFAULT_NEW_LINE;
import static gov.doc.isu.shamen.resources.AppConstants.PDF_TYPE;
import static gov.doc.isu.shamen.resources.AppConstants.TXT_TYPE;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;

import gov.doc.isu.dwarf.print.Footer;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.dwarf.util.StringUtil;
import gov.doc.isu.shamen.business.BatchAppInfo;
import gov.doc.isu.shamen.form.RunStatusForm;
import gov.doc.isu.shamen.models.RunStatusModel;
import gov.doc.isu.shamen.util.ReportUtil;
import gov.doc.isu.shamen.util.ShamenUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Action class that handles all request for the Run Status List Screen
 * 
 * @author <strong>Steven Skinner</strong>
 */
public class RunStatusAction extends ShamenDispatchAction {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.action.RunStatusAction");

    /**
     * This method gets the list of run statuses.
     * 
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering RunStatusAction.list");
        ActionForward forward = mapping.findForward(LIST);
        try{
            RunStatusForm theForm = (RunStatusForm) form;
            theForm.setShowDetail(false);
            BatchAppInfo batchInfo = BatchAppInfo.getInstance();
            theForm.setRunStatuses(batchInfo.getRunStatusList());
            // wait(3000);
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in RunStatusAction.list. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting RunStatusAction.list");
        return forward;
    }// end list

    /**
     * This method generates a pdf for the result detail.
     * 
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward viewResultDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering RunStatusAction.viewResultDetail");
        try{
            RunStatusForm theForm = (RunStatusForm) form;
            theForm.setShowDetail(true);
            Long refId = 0L;
            String type = "";
            if(request.getParameter("runStatusRefId") != null){
                refId = Long.valueOf(request.getParameter("runStatusRefId"));
            }// end if
            if(request.getParameter("type") != null){
                type = request.getParameter("type");
            }// end if
            BatchAppInfo batchInfo = BatchAppInfo.getInstance();
            RunStatusModel model = batchInfo.getRunStatusResultDetail(refId);
            String fileName = ShamenUtil.formatFileName(model.getStart(), model.getBatchApp().getName(), " Result_Detail");
            if(PDF_TYPE.equalsIgnoreCase(type)){
                response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".pdf\"");
                response.setContentType("application/pdf");
                Document document = new Document(PageSize.A4.rotate(), 40, 40, 20, 40);
                PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
                document.addCreationDate();
                writer.setPageEvent(new Footer("", "", getResources(request).getMessage("export.footer")));
                document.open();
                document.add(ReportUtil.getDocumentHeader(StringUtils.upperCase("Run Status Result Detail"), model.getBatchApp().getName()));
                document.add(ReportUtil.getData(model.getResultDetail().split(DEFAULT_NEW_LINE)));
                document.close();
            }else if(TXT_TYPE.equalsIgnoreCase(type)){
                response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\"");
                response.setContentType("text/plain");
                PrintWriter out = response.getWriter();
                out.println(EMPTY_STRING.equalsIgnoreCase(model.getResultDetail()) ? "NO RESULT DETAIL FOUND" : model.getResultDetail());
                out.close();
            }
        }catch(Exception e){
            log.error("Exception occurred in RunStatusAction.viewResultDetail. ", e);
            return mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting RunStatusAction.viewResultDetail");
        return null;
    }// end viewResultDetail

    /**
     * Ajax call to load the statuses of controllers.
     * 
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return null null
     * @throws Exception
     *         Exception
     */
    public ActionForward loadStatuses(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        log.debug("Entering RunStatusAction.loadStatuses");
        RunStatusForm theForm = (RunStatusForm) form;
        theForm.setShowDetail(false);
        BatchAppInfo batchInfo = BatchAppInfo.getInstance();
        List<RunStatusModel> statusList = batchInfo.getRunStatusList();
        JSONArray jsnArray = new JSONArray();
        if(AppUtil.isEmpty(statusList)){
            statusList = new ArrayList<RunStatusModel>();
        }// end if
        String runs = request.getParameter("runNumbers");
        String[] numbers = runs.split(",");
        // while(hasProcessing){
        for(RunStatusModel object : statusList){
            JSONObject jo = new JSONObject();
            if(StringUtil.arrayContains(object.getRunNumber().toString(), numbers) && "DON".equalsIgnoreCase(object.getStatusCd())){
                jo.put(KEY, object.getRunNumber());
                jo.put(VAL, object.getStatusCd());
                jo.put("statusDesc", object.getStatusDesc());
                if(StringUtil.isNullOrEmpty(object.getDescription())){
                    jo.put("details", object.getResultDesc());
                }else{
                    jo.put("details", object.getDescription());
                }
                jo.put("strtTime", object.getStart());
                jo.put("stopTime", object.getStop());
                jo.put("dura", object.getDuration());
                jo.put("runStatusRefId", object.getRunStatusRefId());
                jsnArray.add(jo);

            }else if(StringUtil.arrayContains(object.getRunNumber().toString(), numbers) && !"DON".equalsIgnoreCase(object.getStatusCd())){

                jo.put(KEY, object.getRunNumber());
                jo.put(VAL, object.getStatusCd());
                jo.put("statusDesc", object.getStatusDesc());
                if(StringUtil.isNullOrEmpty(object.getDescription())){
                    jo.put("details", object.getResultDesc());
                }else{
                    jo.put("details", object.getDescription());
                }
                jsnArray.add(jo);

//            }else if(!StringUtil.arrayContains(object.getRunNumber().toString(), numbers) && !"DON".equalsIgnoreCase(object.getStatusCd())){
//                jo.put(KEY, object.getRunNumber());
//                jo.put(VAL, object.getStatusCd());
//                jo.put("statusDesc", object.getStatusDesc());
//                if(StringUtil.isNullOrEmpty(object.getDescription())){
//                    jo.put("details", object.getResultDesc());
//                }else{
//                    jo.put("details", object.getDescription());
//                }
//                jsnArray.add(jo);
            }

        }
        response.getWriter().println(jsnArray);
        response.flushBuffer();
        // }
        log.debug("Exiting ControllerAction.loadStatuses");
        return null;
    }// end loadStatuses
}// end RunStatusAction
