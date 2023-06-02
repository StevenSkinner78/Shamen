package gov.doc.isu.shamen.convertor;

import static gov.doc.isu.dwarf.resources.Constants.EMPTY_STRING;
import static gov.doc.isu.shamen.resources.AppConstants.BLANK_CODE;
import static gov.doc.isu.shamen.resources.AppConstants.SHAMEN_DEFAULT_LABEL;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import gov.doc.isu.dwarf.exception.BaseException;
import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.dwarf.model.ColumnModel;
import gov.doc.isu.dwarf.resources.Constants;
import gov.doc.isu.dwarf.taglib.displaytag.util.TagConstants;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.dwarf.util.StringUtil;
import gov.doc.isu.shamen.ejb.beans.view.ApplicationStatusUpdaterBeanLocal;
import gov.doc.isu.shamen.interfaces.Scheduleable;
import gov.doc.isu.shamen.jms.models.JmsRunStatus;
import gov.doc.isu.shamen.jpa.entity.ApplicationEntity;
import gov.doc.isu.shamen.jpa.entity.ApplicationInstanceEntity;
import gov.doc.isu.shamen.jpa.entity.ApplicationStatusCodeEntity;
import gov.doc.isu.shamen.jpa.entity.ApplicationTypeCodeEntity;
import gov.doc.isu.shamen.jpa.entity.AuthorizedUserEntity;
import gov.doc.isu.shamen.jpa.entity.BatchAppCollectionEntity;
import gov.doc.isu.shamen.jpa.entity.BatchAppEntity;
import gov.doc.isu.shamen.jpa.entity.BatchTypeCodeEntity;
import gov.doc.isu.shamen.jpa.entity.CommonEntity;
import gov.doc.isu.shamen.jpa.entity.ControllerEntity;
import gov.doc.isu.shamen.jpa.entity.ControllerStatusCodeEntity;
import gov.doc.isu.shamen.jpa.entity.FrequencyCodeEntity;
import gov.doc.isu.shamen.jpa.entity.FrequencyTypeCodeEntity;
import gov.doc.isu.shamen.jpa.entity.RepeatCodeEntity;
import gov.doc.isu.shamen.jpa.entity.ResultCodeEntity;
import gov.doc.isu.shamen.jpa.entity.RunStatusEntity;
import gov.doc.isu.shamen.jpa.entity.ScheduleEntity;
import gov.doc.isu.shamen.jpa.entity.StatusCodeEntity;
import gov.doc.isu.shamen.jpa.entity.SystemEntity;
import gov.doc.isu.shamen.models.ApplicationInstanceModel;
import gov.doc.isu.shamen.models.ApplicationModel;
import gov.doc.isu.shamen.models.AuthorizedUserModel;
import gov.doc.isu.shamen.models.BatchAppCollectionModel;
import gov.doc.isu.shamen.models.BatchAppModel;
import gov.doc.isu.shamen.models.ControllerModel;
import gov.doc.isu.shamen.models.RunStatusModel;
import gov.doc.isu.shamen.models.ScheduleModel;
import gov.doc.isu.shamen.models.SystemModel;
import gov.doc.isu.shamen.resources.AppConstants;
import gov.doc.isu.shamen.util.RunStatusComparator;
import gov.doc.isu.shamen.util.ShamenUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Class used to convert Shamen Objects
 *
 * @author Steven Skinner JCCC
 */
public class ObjectConvertor {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.convertor.ObjectConvertor");

    /**
     * This method is used to convert an {@link AuthorizedUserEntity} to an {@link AuthorizedUserModel}.
     *
     * @param entity
     *        The AuthorizedUserEntity object to convert.
     * @return The converted model.
     */
    public static AuthorizedUserModel toAuthorizedUserBusiness(AuthorizedUserEntity entity) {
        log.debug("Entering toAuthorizedUserBusiness. Incoming parameter is: " + String.valueOf(entity));
        if(log.isTraceEnabled()){
            log.debug("This method is used to convert an AuthorizedUserEntity to an AuthorizedUserModel.");
        }// end if
        AuthorizedUserModel model = null;
        if(null != entity){
            model = new AuthorizedUserModel();
            model.setSystems(new ArrayList<SystemModel>());
            model.setBatchApps(new ArrayList<BatchAppModel>());
            model.setApps(new ArrayList<ApplicationModel>());
            model.setFirstName(null != entity.getFirstName() ? entity.getFirstName().trim() : "");
            model.setLastName(null != entity.getLastName() ? entity.getLastName().trim() : "");
            model.setUserID(null != entity.getUserID() ? entity.getUserID().trim() : "");
            model.setEmailInd(null != entity.getEmailInd() ? entity.getEmailInd() : "N");
            model.setAuthority(entity.getAuthority());
            model.setUserRefId(entity.getUserRefId());
            if(null != entity.getCommon()){
                model.setCreateTime(entity.getCommon().getCreateTime());
                model.setCreateUserRefId(entity.getCommon().getCreateUserRefId());
                model.setUpdateUserRefId(entity.getCommon().getUpdateUserRefId());
                model.setUpdateTime(entity.getCommon().getUpdateTime());
                model.setDeleteIndicator(entity.getCommon().getDeleteIndicator());
            }else{
                log.debug("The common entity model in the authorized user entity model is null. No values exist for the create/update user reference ID/timestamp or delete indicator. Unable to convert.");
            }// end if/else
        }else{
            log.warn("The authorized user entity to convert is null. Nothing to convert! This method will return null.");
        }// end if/else
        log.debug("Exiting toAuthorizedUserBusiness. Return value is: " + String.valueOf(model));
        return model;
    }// end toAuthorizedUserBusiness

    /**
     * This method is used to convert an List of {@link AuthorizedUserEntity} to a List of {@link AuthorizedUserModel}.
     *
     * @param entities
     *        The List<AuthorizedUserEntity> object to convert.
     * @return The converted List.
     */
    public static List<AuthorizedUserModel> toAuthorizedUserBusinessList(List<AuthorizedUserEntity> entities) {
        log.debug("Entering toAuthorizedUserBusinessList. Incoming parameter size is: " + entities == null ? "0" : entities.size());
        log.debug("This method is used to convert an  List of AuthorizedUserEntity to an  List of AuthorizedUserModel.");
        log.debug("The incoming parameter is: " + String.valueOf(entities));
        AuthorizedUserModel model = null;
        ColumnModel column = null;
        List<ColumnModel> columns = null;
        List<AuthorizedUserModel> resultList = new ArrayList<AuthorizedUserModel>();
        if(!AppUtil.isEmpty(entities)){
            for(int i = 0, j = entities.size();i < j;i++){
                model = new AuthorizedUserModel();
                columns = new ArrayList<ColumnModel>();
                model.setColumnData(columns);
                model.setFirstName(null != entities.get(i).getFirstName() ? entities.get(i).getFirstName().trim() : "");
                model.setLastName(null != entities.get(i).getLastName() ? entities.get(i).getLastName().trim() : "");
                model.setUserID(null != entities.get(i).getUserID() ? entities.get(i).getUserID().trim() : "");
                model.setEmailInd(null != entities.get(i).getEmailInd() ? entities.get(i).getEmailInd() : "N");
                model.setAuthority(entities.get(i).getAuthority());
                model.setUserRefId(entities.get(i).getUserRefId());

                column = new ColumnModel();
                column.setSortable(true);
                column.put("isSelect", false);
                column.setColumnName("Last Name");
                column.setColumnValue(model.getLastName());
                column.put("paramId", "userRefId");
                column.put("styleClass", "form-control enableWithRestrict");
                column.setColumnNumber(1L);
                column.put("errorStyleClass", "form-control enableWithRestrict is-invalid");
                column.put("boxSize", "25");
                column.setMaxLength(35);
                column.put("style", "width:25%;");
                column.put("paramProperty", "userRefId");
                column.put("url", "/userDetailAction.do?method=editView");
                columns.add(column);

                column = new ColumnModel();
                column.setSortable(true);
                column.put("isSelect", false);
                column.setColumnName("First Name");
                column.setColumnNumber(2L);
                column.put("boxSize", "25");
                column.setMaxLength(35);
                column.put("style", "width:25%;");
                column.setColumnValue(model.getFirstName());
                column.put("styleClass", "form-control enableWithRestrict");
                column.put("errorStyleClass", "form-control enableWithRestrict is-invalid");
                columns.add(column);

                column = new ColumnModel();
                column.setSortable(true);
                column.put("isSelect", false);
                column.setColumnName("User Id");
                column.setColumnValue(model.getUserID());
                column.put("boxSize", "15");
                column.setMaxLength(35);
                column.put("style", "width:15%;");
                column.put("styleClass", "form-control enableWithRestrict");
                column.setColumnNumber(3L);
                column.put("errorStyleClass", "form-control enableWithRestrict is-invalid");
                columns.add(column);

                column = new ColumnModel();
                column.setSortable(true);
                column.put("isSelect", true);
                column.put("listName", "emailIndList");
                column.put("style", "width:11%;");
                column.put("styleClass", "custom-select enableWithRestrict");
                column.setColumnName("Email");
                column.setColumnNumber(4L);
                column.setColumnValue(AppUtil.getYesNoIndicatorDescription(model.getEmailInd()));
                column.setType(TagConstants.ALPHA_NUMERIC_SORT);
                columns.add(column);

                column = new ColumnModel();
                column.setSortable(true);
                column.put("isSelect", true);
                column.put("style", "width:14%;");
                column.put("listName", "authorityList");
                column.put("styleClass", "custom-select enableWithRestrict");
                column.setColumnName("Authority");
                column.setColumnValue(StringUtil.getAuthorityDesc(model.getAuthority()));
                columns.add(column);

                if(null != entities.get(i).getCommon()){
                    model.setCreateTime(entities.get(i).getCommon().getCreateTime());
                    model.setCreateUserRefId(entities.get(i).getCommon().getCreateUserRefId());
                    model.setUpdateUserRefId(entities.get(i).getCommon().getUpdateUserRefId());
                    model.setUpdateTime(entities.get(i).getCommon().getUpdateTime());
                    model.setDeleteIndicator(entities.get(i).getCommon().getDeleteIndicator());
                }else{
                    log.debug("The common entity model in the authorized user entity model is null. No values exist for the create/update user reference ID/timestamp or delete indicator. Unable to convert.");
                }// end if/else
                resultList.add(model);
            }// end for
        }else{
            log.warn("The authorized user entity to convert is null. Nothing to convert! This method will return null.");
        }// end if/else
        log.debug("Converted value of list is: " + String.valueOf(resultList));
        log.debug("Exiting toAuthorizedUserBusiness. Return list size is: " + resultList == null ? "0" : resultList.size());
        return resultList;
    }// end toAuthorizedUserBusiness

    /**
     * This method is used to convert an List of{@link AuthorizedUserEntity} to an List {@link CodeModel}.
     *
     * @param entityList
     *        The List<AuthorizedUserEntity> to convert.
     * @return The converted model.
     */
    public static List<CodeModel> toAuthorizedUserCodeList(List<AuthorizedUserEntity> entityList) {
        log.debug("Entering toAuthorizedUserCodeList. Incoming parameter size is: " + entityList == null ? "0" : entityList.size());
        log.debug("This method is used to convert an List of AuthorizedUserEntity to an List CodeModel.");
        log.debug("The incoming parameter is: " + String.valueOf(entityList));
        List<CodeModel> resultList = new ArrayList<CodeModel>();
        resultList.add(new CodeModel(Constants.ZERO_STRING, SHAMEN_DEFAULT_LABEL));
        AuthorizedUserEntity model = null;
        if(!AppUtil.isEmpty(entityList)){
            Iterator<AuthorizedUserEntity> it = entityList.iterator();
            while(it.hasNext()){
                model = it.next();
                if(model.getUserRefId() != 99L){
                    resultList.add(new CodeModel(String.valueOf(model.getUserRefId()), model.getFullName()));
                }// end if
            }// end while
        }else{
            log.warn("The authorized user list to convert is null. Nothing to convert!");
        }// end if/else
        log.debug("Converted value of list is: " + String.valueOf(resultList));
        log.debug("Exiting toAuthorizedUserCodeList. Return list size is: " + resultList == null ? "0" : resultList.size());
        return resultList;
    }// end toAuthorizedUserCodeList

    /**
     * This method is used to convert an {@link AuthorizedUserModel} to an {@link AuthorizedUserEntity}.
     *
     * @param model
     *        The AuthorizedUserModel object to convert.
     * @return The converted entity.
     */
    public static AuthorizedUserEntity toAuthorizedUserPersist(AuthorizedUserModel model) {
        log.debug("Entering toAuthorizedUserPersist. Incoming parameter is: " + String.valueOf(model));
        log.debug("This method is used to convert an AuthorizedUserModel to an AuthorizedUserEntity.");
        AuthorizedUserEntity entity = null;
        if(null != model){
            entity = new AuthorizedUserEntity();
            entity.setFirstName(null != model.getFirstName() ? model.getFirstName().trim() : "");
            entity.setLastName(null != model.getLastName() ? model.getLastName().trim() : "");
            entity.setUserID(null != model.getUserID() ? model.getUserID().trim() : "");
            entity.setUserRefId(model.getUserRefId());
            entity.setEmailInd(null != model.getEmailInd() ? model.getEmailInd() : "N");
            entity.setAuthority(null != model.getAuthority() ? model.getAuthority() : "USER");
            entity.setCommon(new CommonEntity());
            entity.getCommon().setCreateTime(model.getCreateTime() != null ? model.getCreateTime() : AppUtil.getSQLTimestamp());
            entity.getCommon().setCreateUserRefId(model.getCreateUserRefId());
            entity.getCommon().setUpdateUserRefId(model.getUpdateUserRefId());
            entity.getCommon().setUpdateTime(AppUtil.getSQLTimestamp());
            entity.getCommon().setDeleteIndicator(StringUtil.isNullOrEmpty(model.getDeleteIndicator()) ? "N" : model.getDeleteIndicator());
        }else{
            log.warn("The authorized user entity to convert is null. Nothing to convert! This method will return null.");
        }// end if/else
        log.debug("Exiting toAuthorizedUserPersist. Return value is: " + String.valueOf(entity));
        return entity;
    }// end method

    /**
     * This method is used to convert an {@link BatchAppEntity} to an {@link BatchAppModel}.
     *
     * @param entity
     *        The BatchAppEntity object to convert.
     * @return The converted model.
     */
    public static BatchAppModel toBatchAppBusiness(BatchAppEntity entity) {
        log.debug("Entering toBatchAppBusiness. Incoming parameter is: " + String.valueOf(entity));
        log.debug("This method is used to convert an BatchAppEntity to an BatchAppModel.");
        BatchAppModel model = new BatchAppModel();
        if(entity != null){
            model.setBatchRefId(entity.getBatchAppRefId());
            model.setName(entity.getBatchName() == null ? "" : entity.getBatchName());
            model.setFileLocation(entity.getFileLocation() == null ? "" : entity.getFileLocation());
            model.setFileName(entity.getFileName() == null ? "" : entity.getFileName());
            model.setDescription(entity.getDescription() == null ? "" : entity.getDescription());
            model.setType(entity.getBatchType() == null ? "" : entity.getBatchType().getBatchTypeCd());
            model.setTypeDesc(entity.getBatchType() == null ? "" : entity.getBatchType().getBatchTypeDesc());
            if(null != entity.getCommon()){
                model.setCreateTime(entity.getCommon().getCreateTime());
                model.setCreateUserRefId(entity.getCommon().getCreateUserRefId());
                model.setUpdateUserRefId(entity.getCommon().getUpdateUserRefId());
                model.setUpdateTime(entity.getCommon().getUpdateTime());
                model.setDeleteIndicator(entity.getCommon().getDeleteIndicator());
            }else{
                log.debug("The common entity model in the authorized user entity model is null. No values exist for the create/update user reference ID/timestamp or delete indicator. Unable to convert.");
            }// end if/else
        }// end if
        log.debug("Exiting toBatchAppBusiness. Return value is: " + String.valueOf(model));
        return model;
    }// end toBatchAppBusiness

    /**
     * This method is used to convert an {@link BatchAppEntity} to an {@link BatchAppModel}.
     *
     * @param entity
     *        The BatchAppEntity object to convert.
     * @return The converted model.
     */
    public static BatchAppModel toBatchCollectionBusiness(BatchAppEntity entity) {
        log.debug("Entering toBatchAppBusiness. Incoming parameter is: " + String.valueOf(entity));
        log.debug("This method is used to convert an BatchAppEntity to an BatchAppModel.");
        BatchAppModel model = new BatchAppModel();
        if(entity != null){
            model.setBatchRefId(entity.getBatchAppRefId());
            model.setName(entity.getBatchName() == null ? "" : entity.getBatchName());
            model.setFileLocation("");
            model.setFileName("");
            model.setDescription(entity.getDescription() == null ? "" : entity.getDescription());
            model.setType(AppConstants.BATCH_APP_TYPE_CD_COLLECTION);
            model.setTypeDesc("Collection");
            if(!AppUtil.isEmpty(entity.getBatchCollection())){
                for(int i = 0, j = entity.getBatchCollection().size();i < j;i++){
                    model.getBatchAppCollection().add(new BatchAppCollectionModel(entity.getBatchCollection().get(i).getCollectionRefId(), new BatchAppModel(entity.getBatchCollection().get(i).getMainBatchApp().getBatchAppRefId()), new BatchAppModel(entity.getBatchCollection().get(i).getAssocBatchApp().getBatchAppRefId()), entity.getBatchCollection().get(i).getRunSeq()));
                }// end for
            }// end if
            if(null != entity.getCommon()){
                model.setCreateTime(entity.getCommon().getCreateTime());
                model.setCreateUserRefId(entity.getCommon().getCreateUserRefId());
                model.setUpdateUserRefId(entity.getCommon().getUpdateUserRefId());
                model.setUpdateTime(entity.getCommon().getUpdateTime());
                model.setDeleteIndicator(entity.getCommon().getDeleteIndicator());
            }else{
                log.debug("The common entity model in the authorized user entity model is null. No values exist for the create/update user reference ID/timestamp or delete indicator. Unable to convert.");
            }// end if/else
        }// end if
        log.debug("Exiting toBatchAppBusiness. Return value is: " + String.valueOf(model));
        return model;
    }// end toBatchCollectionBusiness

    /**
     * This method is used to convert a list of {@link BatchAppEntity} to a list of {@link BatchAppModel}.
     *
     * @param entities
     *        The List<BatchAppEntity> objects to convert.
     * @return The converted list.
     * @throws BaseException
     *         if an exception occurred
     */
    public static List<BatchAppModel> toBatchAppListBusiness(List<BatchAppEntity> entities) throws BaseException {
        log.debug("Entering toBatchAppListBusiness. Incoming parameter size is: " + entities == null ? "0" : entities.size());
        log.debug("This method is used to convert an list of BatchAppEntity to an list of BatchAppModel.");
        log.debug("The incoming parameter is: " + String.valueOf(entities));
        List<BatchAppModel> modelList = new ArrayList<BatchAppModel>();
        BatchAppModel model = null;
        if(entities != null && !entities.isEmpty()){
            for(int i = 0;i < entities.size();i++){
                model = new BatchAppModel();
                model.setBatchRefId(entities.get(i).getBatchAppRefId());
                model.setName(entities.get(i).getBatchName() == null ? "" : entities.get(i).getBatchName());
                model.setFileName(entities.get(i).getFileName() == null ? "" : entities.get(i).getFileName());
                model.setPartOfCollection(entities.get(i).getPartOfCollection());
                if(entities.get(i).getController() != null){
                    ControllerModel cont = new ControllerModel(entities.get(i).getController().getControllerName());
                    if(entities.get(i).getController().getControllerStatusCodeEntity() != null){
                        cont.setStatus(entities.get(i).getController().getControllerStatusCodeEntity().getControllerStatusDesc());
                        cont.setStatusCd(entities.get(i).getController().getControllerStatusCodeEntity().getControllerStatusCd());
                    }// end if
                    model.setController(cont);
                }else{
                    model.setController(new ControllerModel());
                }// end if/else
                model.setApplication(entities.get(i).getApplication() == null ? null : toApplicationBusiness(entities.get(i).getApplication()));
                model.setType(entities.get(i).getBatchType() == null ? "" : entities.get(i).getBatchType().getBatchTypeCd());
                model.setTypeDesc(entities.get(i).getBatchType() == null ? "" : entities.get(i).getBatchType().getBatchTypeDesc());
                for(ScheduleEntity schedultEntity : entities.get(i).getSchedule()){
                    if(null != schedultEntity.getLastRunStatus()){
                        model.getLastRunStatus().add(schedultEntity.getLastRunStatus().getResult().getResultCd());
                    }
                }// end for
                model.setExecutionCount(entities.get(i).getExecutionCount() == null ? 0L : entities.get(i).getExecutionCount());
                model.setSystem(ObjectConvertor.toSystemBusiness(entities.get(i).getSystem()));
                model.setPointOfContact(ObjectConvertor.toAuthorizedUserBusiness(entities.get(i).getPointOfContact()));
                ScheduleModel scheduleModel = new ScheduleModel(null, "", "", "", "");
                if(!AppUtil.isEmpty(entities.get(i).getSchedule())){
                    if(entities.get(i).getSchedule().size() == 1){
                        ScheduleEntity schEnt = entities.get(i).getSchedule().get(0);
                        schEnt.setBatchApp(entities.get(i));
                        scheduleModel = toScheduleBusiness(schEnt);
                    }else{
                        for(ScheduleEntity schedultEntity : entities.get(i).getSchedule()){
                            if(schedultEntity.getActiveInd().equalsIgnoreCase("Y")){
                                scheduleModel.setScheduleRefId(0L);
                                scheduleModel.setActiveInd("Y");
                                scheduleModel.getLastRunStatus().setResultCd("MUL");
                                scheduleModel.setFrequencyDesc("Multiple Schedules");
                                break;
                            }// end if
                        }// end for
                    }// end if/else
                }// end if
                model.setScheduleModel(scheduleModel);

                if(null != entities.get(i).getCommon()){
                    model.setCreateTime(entities.get(i).getCommon().getCreateTime());
                    model.setCreateUserRefId(entities.get(i).getCommon().getCreateUserRefId());
                    model.setUpdateUserRefId(entities.get(i).getCommon().getUpdateUserRefId());
                    model.setUpdateTime(entities.get(i).getCommon().getUpdateTime());
                    model.setDeleteIndicator(entities.get(i).getCommon().getDeleteIndicator());
                }else{
                    log.debug("The common entity model in the batch app entity model is null. No values exist for the create/update user reference ID/timestamp or delete indicator. Unable to convert.");
                }// end if/else

                modelList.add(model);
            }// end for
        }// end if
        log.debug("Converted value of list is: " + String.valueOf(modelList));
        log.debug("Exiting toBatchAppListBusiness. Return list size is: " + modelList == null ? "0" : modelList.size());
        return modelList;
    }// end toBatchAppListBusiness

    /**
     * This method is used to convert a list of {@link BatchAppEntity} to a list of {@link BatchAppModel}.
     *
     * @param entities
     *        The List<BatchAppEntity> objects to convert.
     * @return The converted list.
     * @throws BaseException
     *         if an exception occurred
     */
    public static List<BatchAppModel> convertForBatchAppList(List<BatchAppEntity> entities) throws BaseException {
        log.debug("Entering convertForBatchAppList. Incoming parameter size is: " + entities == null ? "0" : entities.size());
        log.debug("This method is used to convert an list of BatchAppEntity to an list of BatchAppModel.");
        log.debug("The incoming parameter is: " + String.valueOf(entities));
        List<BatchAppModel> modelList = new ArrayList<BatchAppModel>();
        BatchAppModel model = null;
        if(entities != null && !entities.isEmpty()){
            for(int i = 0;i < entities.size();i++){
                model = new BatchAppModel();
                model.setBatchRefId(entities.get(i).getBatchAppRefId());
                model.setName(entities.get(i).getBatchName() == null ? "" : entities.get(i).getBatchName());
                model.setPartOfCollection(entities.get(i).getPartOfCollection());
                if(entities.get(i).getController() != null){
                    ControllerModel cont = new ControllerModel(entities.get(i).getController().getControllerName());
                    if(entities.get(i).getController().getControllerStatusCodeEntity() != null){
                        cont.setStatus(entities.get(i).getController().getControllerStatusCodeEntity().getControllerStatusDesc());
                        cont.setStatusCd(entities.get(i).getController().getControllerStatusCodeEntity().getControllerStatusCd());
                    }// end if
                    model.setController(cont);
                }else{
                    model.setController(null);
                }// end if/else
                model.setType(entities.get(i).getBatchType() == null ? "" : entities.get(i).getBatchType().getBatchTypeCd());
                model.setTypeDesc(entities.get(i).getBatchType() == null ? "" : entities.get(i).getBatchType().getBatchTypeDesc());
                model.setExecutionCount(entities.get(i).getExecutionCount() == null ? 0L : entities.get(i).getExecutionCount());
                for(ScheduleEntity schedultEntity : entities.get(i).getSchedule()){
                    if(null != schedultEntity.getLastRunStatus()){
                        model.getLastRunStatus().add(schedultEntity.getLastRunStatus().getResult().getResultCd());
                    }
                }// end for
                model.setSystem(ObjectConvertor.toSystemBusiness(entities.get(i).getSystem()));
                model.setPointOfContact(ObjectConvertor.toAuthorizedUserBusiness(entities.get(i).getPointOfContact()));
                ScheduleModel scheduleModel = new ScheduleModel(null, "", "", "", "");
                if(!AppUtil.isEmpty(entities.get(i).getSchedule())){
                    if(entities.get(i).getSchedule().size() == 1){
                        ScheduleEntity schEnt = entities.get(i).getSchedule().get(0);
                        schEnt.setBatchApp(entities.get(i));
                        scheduleModel = toScheduleBusiness(schEnt);
                    }else{
                        for(ScheduleEntity schedultEntity : entities.get(i).getSchedule()){
                            if(schedultEntity.getActiveInd().equalsIgnoreCase("Y")){
                                scheduleModel.setScheduleRefId(0L);
                                scheduleModel.setActiveInd("Y");
                                scheduleModel.getLastRunStatus().setResultCd("MUL");
                                break;
                            }// end if
                        }// end for
                    }// end if/else
                }// end if
                model.setScheduleModel(scheduleModel);
                buildBatchAppDisplayValues(model);
                modelList.add(model);
            }// end for
        }// end if
        log.debug("Converted value of list is: " + String.valueOf(modelList));
        log.debug("Exiting convertForBatchAppList. Return list size is: " + modelList == null ? "0" : modelList.size());
        return modelList;
    }// end convertForBatchAppList

    /**
     * This method builds the Column Model and Column Model List of Batch Apps for Dwarf Display table.
     *
     * @param model
     *        BatchAppModel
     */
    private static void buildBatchAppDisplayValues(BatchAppModel model) {
        log.debug("Entering buildBatchAppDisplayValues");
        log.debug("This method builds the Column Model and Column Model List of Batch Apps for Dwarf Display table.");
        log.debug("Entry parameters are: model=" + String.valueOf(model));
        List<ColumnModel> column = new ArrayList<ColumnModel>();
        ColumnModel col = new ColumnModel();

        col.setSortable(true);
        col.setMaxLength(80);
        col.put("isSelect", false);
        col.setColumnValue(model.getName());
        col.setColumnNumber(0L);

        col.put("styleClass", "form-control enableWithRestrict");
        col.put("errorStyleClass", "form-control enableWithRestrict is-invalid");
        col.put("paramId", "batchRefId");
        col.put("paramProperty", "batchRefId");
        col.put("boxSize", "40");
        col.put("class", "enableWithRestrict");
        col.setType(TagConstants.ALPHA_NUMERIC_SORT);
        if(!"Collection".equalsIgnoreCase(model.getTypeDesc())){
            col.setColumnName("Batch App Name");
            col.put("url", "/batchDetailAction.do?method=editBatch&caller=batchList");
        }else{
            col.setColumnName("Collection Name");
            col.put("url", "/batchCollectionDetailAction.do?method=editBatch&caller=batchCollectionList");
        }// end if/else
        column.add(col);

        col = new ColumnModel();
        col.setSortable(true);
        col.put("isSelect", false);
        col.setColumnName("Execution Count");
        col.setColumnValue(model.getExecutionCount());
        col.setColumnNumber(3L);
        col.put("styleClass", "form-control enableWithRestrict");
        col.put("errorStyleClass", "form-control enableWithRestrict is-invalid");
        col.put("boxSize", "10");
        col.put("style", "width:12%");
        col.setType(TagConstants.ALPHA_NUMERIC_SORT);
        column.add(col);

        if(!"Collection".equalsIgnoreCase(model.getTypeDesc())){
            col = new ColumnModel();
            col.setSortable(true);
            col.put("isSelect", true);
            col.setColumnName("Type");
            col.put("listName", "batchTypes");
            col.setColumnNumber(4L);
            col.setColumnValue(model.getTypeDesc());
            col.put("styleClass", "custom-select enableWithRestrict");
            col.setType(TagConstants.ALPHA_NUMERIC_SORT);
            column.add(col);
        }// end if

        col = new ColumnModel();
        col.setSortable(true);
        col.put("isSelect", true);
        col.put("listName", "systemList");
        col.put("styleClass", "custom-select enableWithRestrict");
        col.setColumnName("System");
        col.setColumnNumber(1L);
        col.setMaxLength(23);
        col.setColumnValue(model.getSystem() == null ? "" : model.getSystem().getName());
        col.setType(TagConstants.ALPHA_NUMERIC_SORT);
        column.add(col);
        if(model.getController() == null){
            model.setAssignedController(false);
        }// end if

        col = new ColumnModel();
        col.setSortable(true);
        col.put("isSelect", true);
        col.put("listName", "pocList");
        col.setColumnName("Point of Contact");
        col.setMaxLength(45);
        col.setColumnValue(model.getPointOfContact().getFullName());
        col.setColumnNumber(2L);
        col.put("styleClass", "custom-select enableWithRestrict");
        col.setType(TagConstants.ALPHA_NUMERIC_SORT);
        column.add(col);

        model.setColumnData(column);
        log.debug("Exiting buildBatchAppDisplayValues");
    }// end buildBatchAppDisplayValues

    /**
     * This method is used to convert an {@link BatchAppEntity} to an {@link BatchAppModel}.
     *
     * @param entity
     *        The ControllerEntity object to convert.
     * @return The converted model.
     */
    public static ControllerModel toControllerBusiness(ControllerEntity entity) {
        log.debug("Entering toControllerBusiness. Incoming parameter is: " + String.valueOf(entity));
        log.debug("This method is used to convert an BatchAppEntity to an BatchAppModel.");
        ControllerModel model = new ControllerModel();
        if(entity != null){
            model.setControllerRefId(entity.getControllerRefId());
            model.setAddress(entity.getControllerAddress() == null ? "" : entity.getControllerAddress());
            model.setName(entity.getControllerName() == null ? "" : entity.getControllerName());
            model.setDefaultAddress(entity.getDefaultAddress() == null ? "" : entity.getDefaultAddress());
            model.setStatus(entity.getControllerStatusCodeEntity().getControllerStatusDesc());
            if(null != entity.getCommon()){
                model.setCreateTime(entity.getCommon().getCreateTime());
                model.setCreateUserRefId(entity.getCommon().getCreateUserRefId());
                model.setUpdateUserRefId(entity.getCommon().getUpdateUserRefId());
                model.setUpdateTime(entity.getCommon().getUpdateTime());
                model.setDeleteIndicator(entity.getCommon().getDeleteIndicator());
            }else{
                log.debug("The common entity model in the authorized user entity model is null. No values exist for the create/update user reference ID/timestamp or delete indicator. Unable to convert.");
            }// end if/else
        }// end if
        log.debug("Exiting toControllerBusiness. Return value is: " + String.valueOf(model));
        return model;
    }// end toControllerBusiness

    /**
     * This method is used to convert an {@link SystemEntity} to a {@link SystemModel}.
     *
     * @param entity
     *        The SystemEntity object to convert.
     * @return The converted model.
     */
    public static SystemModel toSystemBusiness(SystemEntity entity) {
        log.debug("Entering toSystemBusiness. Incoming parameter is: " + String.valueOf(entity));
        log.debug("This method is used to convert an BatchAppEntity to an BatchAppModel.");
        SystemModel model = new SystemModel();
        if(entity != null){
            model.setSystemRefId(entity.getSystemRefId());
            model.setName(entity.getSystemName() == null ? "" : entity.getSystemName());
            model.setSystemDesc(entity.getSystemDesc() == null ? "" : entity.getSystemDesc());
            if(null != entity.getCommon()){
                model.setCreateTime(entity.getCommon().getCreateTime());
                model.setCreateUserRefId(entity.getCommon().getCreateUserRefId());
                model.setUpdateUserRefId(entity.getCommon().getUpdateUserRefId());
                model.setUpdateTime(entity.getCommon().getUpdateTime());
                model.setDeleteIndicator(entity.getCommon().getDeleteIndicator());
            }else{
                log.debug("The common entity model in the authorized user entity model is null. No values exist for the create/update user reference ID/timestamp or delete indicator. Unable to convert.");
            }// end if/else
        }// end if
        log.debug("Exiting toSystemBusiness. Return value is: " + String.valueOf(model));
        return model;
    }// end toSystemBusiness

    /**
     * This method is used to convert an {@link RunStatusEntity} to an {@link RunStatusModel}.
     *
     * @param entity
     *        The RunStatusEntity object to convert.
     * @return The converted model.
     */
    public static RunStatusModel toRunStatusBusiness(RunStatusEntity entity) {
        log.debug("Entering toRunStatusBusiness. Incoming parameter is: " + String.valueOf(entity));
        log.debug("This method is used to convert an RunStatusEntity to an RunStatusModel.");
        RunStatusModel model = new RunStatusModel();
        if(entity != null){
            model.setRunStatusRefId(entity.getRunStatusRefId());
            model.setScheduleRefId(entity.getSchedule() == null ? 0L : entity.getSchedule().getScheduleRefId());
            model.setMainBatchAppRefId(entity.getMainBatchAppRefId());
            model.setStart(ShamenUtil.getFormattedDateTimeAsString(entity.getStartTime()));
            model.setStop(ShamenUtil.getFormattedDateTimeAsString(entity.getStopTime()));
            if(!StringUtil.isNullOrEmpty(model.getStop())){
                model.setDuration(ShamenUtil.getTimeDifference(model.getStart(), model.getStop()));
                model.setDurationInMinutes(ShamenUtil.getTimeDifferenceInMinutes(model.getStart(), model.getStop()));
            }else{
                model.setDuration("");
            }// end if/else
            model.setStatusCd(entity.getStatus() == null ? "" : entity.getStatus().getStatusCd());
            model.setStatusDesc(entity.getStatus() == null ? "" : entity.getStatus().getStatusDesc());
            model.setResultCd(entity.getResult() == null ? "" : entity.getResult().getResultCd());
            model.setResultDesc(entity.getResult() == null ? "" : entity.getResult().getResultDesc());
            model.setResultDetail(entity.getResultDetail() == null ? "" : entity.getResultDetail());
            model.setDescription(entity.getDescription() == null ? "" : entity.getDescription());
            model.setFromScheduleInd(entity.isFromSchedule());
            model.setUserName(entity.getCreatedBy() != null ? entity.getCreatedBy().getFullName() : "");
            if(null != entity.getCommon()){
                model.setCreateTime(entity.getCommon().getCreateTime());
                model.setCreateUserRefId(entity.getCommon().getCreateUserRefId());
                model.setUpdateUserRefId(entity.getCommon().getUpdateUserRefId());
                model.setUpdateTime(entity.getCommon().getUpdateTime());
                model.setDeleteIndicator(entity.getCommon().getDeleteIndicator());
            }else{
                log.debug("The common entity model in the authorized user entity model is null. No values exist for the create/update user reference ID/timestamp or delete indicator. Unable to convert.");
            }// end if/else
        }// end if
        log.debug("Exiting toRunStatusBusiness. Return value is: " + String.valueOf(model));
        return model;
    }// end toRunStatusBusiness

    /**
     * This method is used to convert an list of {@link Object} to an list of {@link RunStatusModel}.
     *
     * @param list
     *        List<Object> from query result set The list of entity object to convert.
     * @return The converted list.
     */
    public static List<RunStatusModel> toRunStatusListBusiness(List<Object> list) {
        log.debug("Entering toRunStatusListBusiness. Incoming parameter size is: " + list == null ? "0" : list.size());
        log.debug("This method is used to convert an list of Object to an list of RunStatusModel.");
        log.debug("The incoming parameter is: " + String.valueOf(list));
        List<RunStatusModel> modelList = new ArrayList<RunStatusModel>();
        RunStatusModel model = null;
        if(list != null && !list.isEmpty()){
            for(int i = 0, j = list.size();i < j;i++){
                Object[] element = (Object[]) list.get(i);
                model = new RunStatusModel();
                model.setRunStatusRefId(Long.valueOf(element[0].toString()));
                model.setStart(ShamenUtil.getFormattedDateTimeAsString((Timestamp) element[1]));
                model.setStartTs((Timestamp) element[1]);
                model.setStop(ShamenUtil.getFormattedDateTimeAsString((Timestamp) element[2]));
                model.setStatusCd((String) (element[3] == null ? "" : element[3]));
                model.setStatusDesc((String) (element[4] == null ? "" : element[4]));
                model.setResultDesc((String) (element[5] == null ? "" : element[5]));
                model.setBatchApp(new BatchAppModel());
                model.getBatchApp().setName((String) element[6]);
                model.setFromScheduleInd((element[7] != null ? true : false));
                model.getBatchApp().setBatchRefId(Long.valueOf(element[8].toString()));
                if(!StringUtil.isNullOrEmpty(model.getStop())){
                    model.setDuration(ShamenUtil.getTimeDifference(model.getStart(), model.getStop()));
                    model.setDurationInMinutes(ShamenUtil.getTimeDifferenceInMinutes(model.getStart(), model.getStop()));
                }else{
                    model.setDuration("");
                }// end if-else

                String userName = "";
                if(element[9] != null && element[10] != null){
                    userName = String.valueOf(element[10]).trim();
                    if(!String.valueOf(element[9]).equalsIgnoreCase("")){
                        userName += ", " + element[9];
                    }// end if
                }// end if
                model.setUserName(userName);
                model.setMainBatchAppRefId(element[11] != null ? Long.valueOf(element[11].toString()) : null);
                model.setDescription((element[12] != null ? element[12].toString() : ""));
                model.setRunNumber((element[13] != null ? Long.valueOf(element[13].toString()) : null));
                model.getBatchApp().setType((String) (element[14] == null ? "" : element[14]));
                model.getBatchApp().setSystem(new SystemModel());
                model.getBatchApp().getSystem().setSystemRefId((element[15] != null ? Long.valueOf(element[15].toString()) : null));
                model.setResultCd((String) (element[16] == null ? "" : element[16]));
                model.setCreateUserRefId(element[17] != null && Long.valueOf(element[17].toString()) == 99L ? 99L : 0L);
                model.setResultDetail("");
                if(element[17] != null && Long.valueOf(element[17].toString()) == 99L && model.getStatusCd().equalsIgnoreCase("STD")){
                    model.setResultDetail(element[18].toString());
                }// end if
                modelList.add(model);
            }// end for
        }// end if
        log.debug("Converted value of list is: " + String.valueOf(modelList));
        log.debug("Exiting toRunStatusBusiness. Return list size is: " + modelList == null ? "0" : modelList.size());
        return modelList;
    }// end toRunStatusListBusiness

    /**
     * This method is used to convert a list of {@link Object} to a list of {@link RunStatusModel}. It is used for multiple batch job display on the most recent run status screen.
     *
     * @param list
     *        The lList<Object> to convert.
     * @return The converted list.
     */
    public static List<RunStatusModel> toRunStatusListBusinessFromObj(List<Object> list) {
        log.debug("Entering toRunStatusListBusinessFromObj. Incoming parameter size is: " + list == null ? "0" : list.size());
        log.debug("This method is used to convert an list of Object to an list of RunStatusModel.");
        log.debug("The incoming parameter is: " + String.valueOf(list));
        List<RunStatusModel> modelList = new ArrayList<RunStatusModel>();
        List<RunStatusModel> finalList = new ArrayList<RunStatusModel>();
        Map<Long, RunStatusModel> collectionList = new TreeMap<Long, RunStatusModel>();
        modelList = toRunStatusListBusiness(list);
        if(modelList != null && !modelList.isEmpty()){
            // consolidate the collections into one job
            for(int i = 0, j = modelList.size();i < j;i++){
                if(modelList.get(i).getMainBatchAppRefId() == null){
                    // hold this record to compare against children
                    collectionList.put(modelList.get(i).getBatchApp().getBatchRefId(), modelList.get(i));
                    finalList.add(modelList.get(i));
                }else{
                    // loop through and add this collection member to the right parent collection
                    for(int h = 0, k = finalList.size();h < k;h++){
                        if(finalList.get(h).getBatchApp().getBatchRefId().equals(modelList.get(i).getMainBatchAppRefId())){
                            // only show this child record if its parent collection's startTs is less than its own
                            RunStatusModel runStatusModel = collectionList.get(modelList.get(i).getMainBatchAppRefId());
                            Long runNbr = (runStatusModel != null ? runStatusModel.getRunNumber() : null);
                            if(runNbr.equals(modelList.get(i).getRunNumber())){
                                finalList.get(h).getCollectionMembers().add(modelList.get(i));
                            }// end if
                        }// end if
                    }// end for
                }// end if-else
            }// end for
            Collections.sort(finalList, Collections.reverseOrder(new RunStatusComparator()));
        }// end if
        log.debug("Converted value of list is: " + String.valueOf(finalList));
        log.debug("Exiting toRunStatusListBusinessFromObj. Return list size is: " + finalList == null ? "0" : finalList.size());
        return finalList;
    }// end toRunStatusListBusinessFromObj

    /**
     * This method is used to convert a list of {@link Object} to a list of {@link RunStatusModel}. It is used for single batch job display on the batch app detail.
     *
     * @param list
     *        The List<Object> to convert.
     * @return The converted list.
     */
    public static List<RunStatusModel> toRunStatusListBusinessFromObjForCollectionDetail(List<Object> list) {
        log.debug("Entering toRunStatusListBusinessFromObjForCollectionDetail. Incoming parameter size is: " + list == null ? "0" : list.size());
        log.debug("This method is used to convert an list of Object to an list of RunStatusModel.");
        log.debug("The incoming parameter is: " + String.valueOf(list));
        List<RunStatusModel> modelList = new ArrayList<RunStatusModel>();
        List<RunStatusModel> finalList = new ArrayList<RunStatusModel>();
        Map<Long, RunStatusModel> collectionList = new TreeMap<Long, RunStatusModel>();
        modelList = toRunStatusListBusiness(list);
        if(modelList != null && !modelList.isEmpty()){
            // consolidate the collections into one job
            Long holdRunNbr = 0L;
            for(int i = 0, j = modelList.size();i < j;i++){
                if(!modelList.get(i).getRunNumber().equals(holdRunNbr)){
                    holdRunNbr = modelList.get(i).getRunNumber();
                    // hold this record to compare against children
                    collectionList.put(modelList.get(i).getRunNumber(), modelList.get(i));

                    finalList.add(modelList.get(i));
                }else{
                    // loop through and add this collection member to the right parent collection
                    for(int h = 0, k = finalList.size();h < k;h++){
                        if(finalList.get(h).getRunNumber().equals(modelList.get(i).getRunNumber())){
                            // only show this child record if its parent collection's startTs is less than its own
                            RunStatusModel runStatusModel = collectionList.get(modelList.get(i).getRunNumber());
                            Long runNbr = (runStatusModel != null ? runStatusModel.getRunNumber() : null);
                            if(runNbr.equals(modelList.get(i).getRunNumber())){
                                finalList.get(h).getCollectionMembers().add(modelList.get(i));
                            }// end if
                        }// end if
                    }// end for
                }// end if-else
            }// end for
        }// end if
         // Once the collections are split, properly order their children. This was done separately because the logic just gets too hairy. This was a complicated sort
         // specifically for the run statuses on the batch collection detail screen.
        for(int i = 0, j = finalList.size();i < j;i++){
            RunStatusModel col = finalList.get(i);
            RunStatusModel mbr;
            Long holdBatchAppRefId = 0L;
            ArrayList<RunStatusModel> sortList = new ArrayList<RunStatusModel>();
            ArrayList<RunStatusModel> sortedList = new ArrayList<RunStatusModel>();
            int holdIndex = 0;
            for(int k = 0, l = col.getCollectionMembers().size();k < l;k++){
                mbr = col.getCollectionMembers().get(k);
                if(!holdBatchAppRefId.equals(mbr.getBatchApp().getBatchRefId())){
                    holdBatchAppRefId = mbr.getBatchApp().getBatchRefId();
                    // if something was there, then load to output list.
                    if(!sortList.isEmpty()){
                        // add the done first.
                        if(JmsRunStatus.STATUS_DONE.equals(sortList.get(holdIndex - 1).getStatusCd())){
                            RunStatusModel doneRun = null;
                            try{
                                RunStatusModel doneRunFirst = (RunStatusModel) sortList.get(holdIndex - 1).clone();
                                doneRunFirst.setBatchApp((BatchAppModel) doneRunFirst.getBatchApp().clone());
                                // doneRunFirst.getBatchApp().setName(new String(doneRunFirst.getBatchApp().getName().substring(2)));
                                sortedList.add(doneRunFirst);
                                doneRun = (RunStatusModel) sortList.get(holdIndex - 1);
                                doneRun.setStart(new String(doneRun.getStop()));
                                doneRun.setStop("");
                                doneRun.setDuration("");
                                doneRun.setDurationInMinutes("");
                            }catch(CloneNotSupportedException e){
                                log.warn("Unable to clone the runstatus.", e);
                            }// end try/catch
                        }// end if
                        holdIndex = 0;
                        // add the rest sorted ascending
                        sortedList.addAll(sortList);
                        sortList.clear();
                    }// end if
                }// end if
                holdIndex = holdIndex + 1;
                sortList.add(mbr);
            }// end for
            if(!sortList.isEmpty()){
                // add the done first.
                if(JmsRunStatus.STATUS_DONE.equals(sortList.get(0).getResultCd())){
                    sortedList.add(sortList.get(0));
                }// end if
                sortedList.addAll(sortList);
            }// end if
            finalList.get(i).setCollectionMembers(sortedList);
        }// end for

        Collections.sort(finalList, Collections.reverseOrder(new RunStatusComparator()));
        log.debug("Converted value of list is: " + String.valueOf(finalList));
        log.debug("Exiting toRunStatusListBusinessFromObjForCollectionDetail. Return list size is: " + finalList == null ? "0" : finalList.size());
        return finalList;
    }// end toRunStatusListBusinessFromObj

    /**
     * This method is used to convert a list of {@link Object} to a list of {@link RunStatusModel}. It is used for single batch job display on the batch app detail.
     *
     * @param list
     *        The List<Object> to convert.
     * @return The converted list.
     */
    public static List<RunStatusModel> toRunStatusListBusinessFromObjForDetail(List<Object> list) {
        log.debug("Entering toRunStatusListBusinessFromObjForDetail. Incoming parameter size is: " + list == null ? "0" : list.size());
        log.debug("This method is used to convert an list of Object to an list of RunStatusModel. It is used for single batch job display on the batch app detail.");
        log.debug("The incoming parameter is: " + String.valueOf(list));
        List<RunStatusModel> modelList = new ArrayList<RunStatusModel>();
        List<RunStatusModel> finalList = new ArrayList<RunStatusModel>();
        Map<Long, RunStatusModel> collectionList = new TreeMap<Long, RunStatusModel>();
        modelList = toRunStatusListBusiness(list);
        if(modelList != null && !modelList.isEmpty()){
            // consolidate the statuses into one job
            for(int i = 0, j = modelList.size();i < j;i++){
                if(!collectionList.containsKey(modelList.get(i).getRunNumber())){
                    // hold this record to compare against children
                    collectionList.put(modelList.get(i).getRunNumber(), modelList.get(i));
                }else{
                    collectionList.get(modelList.get(i).getRunNumber()).getCollectionMembers().add(modelList.get(i));
                }// end if-else
            }// end for
            Iterator<RunStatusModel> it = collectionList.values().iterator();
            while(it.hasNext()){
                finalList.add(it.next());
            }// end while
        }// end if
        Collections.sort(finalList, Collections.reverseOrder(new RunStatusComparator()));
        log.debug("Converted value of list is: " + String.valueOf(finalList));
        log.debug("Exiting toRunStatusListBusinessFromObjForDetail. Return list size is: " + finalList == null ? "0" : finalList.size());
        return finalList;
    }// end toRunStatusListBusinessFromObj

    // /**
    // * This method builds the Column Model and Column Model List of Run Statuses for Dwarf Display table.
    // *
    // * @param model
    // * RunStatusModel
    // */
    // private static void buildRunStatusDisplayValues(RunStatusModel model) {
    // log.debug("Entering buildRunStatusDisplayValues");
    // log.debug("This method builds the Column Model and Column Model List of Run Statuses for Dwarf Display table.");
    // log.debug("Entry parameters are: model=" + String.valueOf(model));
    // List<ColumnModel> column = new ArrayList<ColumnModel>();
    // ColumnModel col = new ColumnModel();
    //
    // col.setSortable(true);
    // col.setMaxLength(10);
    // col.put("isSelect", false);
    // col.setColumnName("Batch App Name");
    // col.setColumnValue(model.getStatusDesc()));
    // col.setColumnNumber(0L);
    //
    // col.put("styleClass", "form-control enableWithRestrict");
    // col.put("errorStyleClass", "form-control enableWithRestrict is-invalid");
    // col.put("boxSize", "10");
    // col.put("class", "enableWithRestrict");
    // col.setType(TagConstants.ALPHA_NUMERIC_SORT);
    // column.add(col);
    //
    // col = new ColumnModel();
    // col.setSortable(true);
    // col.put("isSelect", false);
    // col.setColumnName("Execution Count");
    // col.setColumnValue(model.getExecutionCount());
    // col.setColumnNumber(3L);
    // col.put("styleClass", "form-control enableWithRestrict");
    // col.put("errorStyleClass", "form-control enableWithRestrict is-invalid");
    // col.put("boxSize", "10");
    // col.put("style", "width:12%");
    // col.setType(TagConstants.ALPHA_NUMERIC_SORT);
    // column.add(col);
    //
    // if(!"Collection".equalsIgnoreCase(model.getTypeDesc())){
    // col = new ColumnModel();
    // col.setSortable(true);
    // col.put("isSelect", true);
    // col.setColumnName("Type");
    // col.put("listName", "batchTypes");
    // col.setColumnNumber(4L);
    // col.setColumnValue(model.getTypeDesc());
    // col.put("styleClass", "custom-select enableWithRestrict");
    // col.setType(TagConstants.ALPHA_NUMERIC_SORT);
    // column.add(col);
    // }// end if
    //
    // col = new ColumnModel();
    // col.setSortable(true);
    // col.put("isSelect", true);
    // col.put("listName", "systemList");
    // col.put("styleClass", "custom-select enableWithRestrict");
    // col.setColumnName("System");
    // col.setColumnNumber(1L);
    // col.setMaxLength(23);
    // col.setColumnValue(model.getSystem() == null ? "" : model.getSystem().getName());
    // col.setType(TagConstants.ALPHA_NUMERIC_SORT);
    // column.add(col);
    // if(model.getController() == null){
    // model.setAssignedController(false);
    // }// end if
    //
    // col = new ColumnModel();
    // col.setSortable(true);
    // col.put("isSelect", true);
    // col.put("listName", "pocList");
    // col.setColumnName("Point of Contact");
    // col.setMaxLength(45);
    // col.setColumnValue(model.getPointOfContact().getFullName());
    // col.setColumnNumber(2L);
    // col.put("styleClass", "custom-select enableWithRestrict");
    // col.setType(TagConstants.ALPHA_NUMERIC_SORT);
    // column.add(col);
    //
    // model.setColumnData(column);
    // log.debug("Exiting buildRunStatusDisplayValues");
    // }// end buildRunStatusDisplayValues

    /**
     * This method is used to convert a list of {@link Object} to a list of {@link RunStatusModel}. It is used for batch job duration per execution display on the batch app detail.
     *
     * @param list
     *        The List<Object> to convert.
     * @return The converted list.
     */
    public static List<RunStatusModel> toRunStatusListForGraph(List<Object> list) {
        log.debug("Entering toRunStatusListBusinessFromObjForDetail. Incoming parameter size is: " + list == null ? "0" : list.size());
        log.debug("This method is used to convert an list of Object to an list of RunStatusModel. It is used for batch job duration per execution display on the batch app detail.");
        log.debug("The incoming parameter is: " + String.valueOf(list));
        List<RunStatusModel> modelList = new ArrayList<RunStatusModel>();
        RunStatusModel model = null;
        List<RunStatusModel> finalList = new ArrayList<RunStatusModel>();
        Map<Long, RunStatusModel> collectionList = new TreeMap<Long, RunStatusModel>();
        if(list != null && !list.isEmpty()){
            for(int i = 0, j = list.size();i < j;i++){
                Object[] element = (Object[]) list.get(i);
                model = new RunStatusModel();
                model.setRunStatusRefId(Long.valueOf(element[0].toString()));
                model.setStart(ShamenUtil.getFormattedDateTimeAsString((Timestamp) element[1]));
                model.setStartTs((Timestamp) element[1]);
                model.setStop(ShamenUtil.getFormattedDateTimeAsString((Timestamp) element[2]));
                model.setStatusCd((String) (element[3] == null ? "" : element[3]));
                if(!StringUtil.isNullOrEmpty(model.getStop())){
                    model.setDuration(ShamenUtil.getTimeDifference(model.getStart(), model.getStop()));
                    model.setDurationInMinutes(ShamenUtil.getTimeDifferenceInMinutes(model.getStart(), model.getStop()));
                }else{
                    model.setDuration("");
                }// end if-else
                model.setResultCd((String) (element[4] == null ? "" : element[4]));
                model.setRunNumber((element[5] != null ? Long.valueOf(element[5].toString()) : null));
                modelList.add(model);
            }// end for
        }// end if

        if(modelList != null && !modelList.isEmpty()){
            // consolidate the statuses into one job
            for(int i = 0, j = modelList.size();i < j;i++){
                if(!collectionList.containsKey(modelList.get(i).getRunNumber())){
                    // hold this record to compare against children
                    collectionList.put(modelList.get(i).getRunNumber(), modelList.get(i));
                }else{
                    collectionList.get(modelList.get(i).getRunNumber()).getCollectionMembers().add(modelList.get(i));
                }// end if-else
            }// end for
            Iterator<RunStatusModel> it = collectionList.values().iterator();
            while(it.hasNext()){
                finalList.add(it.next());
            }// end while
        }// end if
        Collections.sort(finalList, new RunStatusComparator());
        log.debug("Converted value of list is: " + String.valueOf(finalList));
        log.debug("Exiting toRunStatusListBusinessFromObjForDetail. Return list size is: " + finalList == null ? "0" : finalList.size());
        return finalList;
    }// end toRunStatusListBusinessFromObj

    /**
     * This method is used to convert an list of {@link Object} to an list of {@link ControllerModel}.
     *
     * @param list
     *        The List<Object> to convert.
     * @return The converted list.
     */
    public static List<ControllerModel> toControllerListBusinessFromObj(List<Object> list) {
        log.debug("Entering toControllerListBusinessFromObj. Incoming parameter size is: " + list == null ? "0" : list.size());
        log.debug("This method is used to convert an list of Object to an list of ControllerModel.");
        log.debug("The incoming parameter is: " + String.valueOf(list));
        List<ControllerModel> modelList = new ArrayList<ControllerModel>();
        ControllerModel model = null;
        if(list != null && !list.isEmpty()){
            for(int i = 0, j = list.size();i < j;i++){
                Object[] element = (Object[]) list.get(i);
                model = new ControllerModel();
                model.setControllerRefId(Long.valueOf(element[0].toString()));
                model.setAddress(element[1].toString());
                model.setName(element[2].toString());
                model.setStatusCd(element[3].toString());
                model.setStatus(element[4].toString());
                model.setDefaultAddress(element[5].toString());
                model.setBatchApps(new ArrayList<BatchAppModel>());
                modelList.add(model);
            }// end for
        }// end if
        log.debug("Converted value of list is: " + String.valueOf(modelList));
        log.debug("Exiting toControllerListBusinessFromObj. Return list size is: " + modelList == null ? "0" : modelList.size());
        return modelList;
    }// end toBatchAppListBusinessFromObj

    /**
     * This method is used to convert an list of {@link Object} to an list of {@link SystemModel}.
     *
     * @param list
     *        The List<Object> to convert.
     * @return The converted list.
     */
    public static List<SystemModel> toSystemListBusinessFromObj(List<Object> list) {
        log.debug("Entering toSystemListBusinessFromObj. Incoming parameter size is: " + list == null ? "0" : list.size());
        log.debug("This method is used to convert an list of Object to an list of SystemModel.");
        log.debug("The incoming parameter is: " + String.valueOf(list));
        List<SystemModel> modelList = new ArrayList<SystemModel>();
        SystemModel model = null;
        if(list != null && !list.isEmpty()){
            for(int i = 0, j = list.size();i < j;i++){
                Object[] element = (Object[]) list.get(i);
                model = new SystemModel();
                model.setSystemRefId(Long.valueOf(element[0].toString()));
                model.setName(element[1].toString());
                model.setSystemDesc(element[2].toString());
                model.setNbrApplications(element[3] != null ? Integer.valueOf(element[3].toString()) : 0);
                model.setNbrBatchApps(element[4] != null ? Integer.valueOf(element[4].toString()) : 0);
                model.setNbrTotal(model.getNbrApplications() + model.getNbrBatchApps());
                model.setPointOfContact(new AuthorizedUserModel(element[5] != null ? element[5].toString() : "", element[6] != null ? element[6].toString() : ""));
                modelList.add(model);
            }// end for
        }// end if
        log.debug("Converted value of list is: " + String.valueOf(modelList));
        log.debug("Exiting toSystemListBusinessFromObj. Return list size is: " + modelList == null ? "0" : modelList.size());
        return modelList;
    }// end toSystemListBusinessFromObj

    /**
     * This method is used to convert an list of {@link RunStatusEntity} to an list of {@link RunStatusModel}.
     *
     * @param entities
     *        The list of RunStatusEntity object to convert.
     * @return The converted list.
     */
    public static List<RunStatusModel> toRunStatusForDisplayBusiness(List<RunStatusEntity> entities) {
        log.debug("Entering toRunStatusForDisplayBusiness. Incoming parameter size is: " + entities == null ? "0" : entities.size());
        log.debug("This method is used to convert an list of RunStatusEntity to an list of RunStatusModel.");
        log.debug("The incoming parameter is: " + String.valueOf(entities));
        List<RunStatusModel> modelList = new ArrayList<RunStatusModel>();
        RunStatusModel model = null;
        if(entities != null && !entities.isEmpty()){
            for(int i = 0;i < entities.size();i++){
                model = new RunStatusModel();
                model.setBatchApp(new BatchAppModel(entities.get(i).getBatchApp().getBatchAppRefId()));
                model.getBatchApp().setName(entities.get(i).getBatchApp().getBatchName());
                model.getBatchApp().setType(entities.get(i).getBatchApp().getBatchType().getBatchTypeCd());
                model.setRunStatusRefId(entities.get(i).getRunStatusRefId());
                model.setStart(ShamenUtil.getFormattedDateTimeAsString(entities.get(i).getStartTime()));
                model.setStop(ShamenUtil.getFormattedDateTimeAsString(entities.get(i).getStopTime()));
                if(!StringUtil.isNullOrEmpty(model.getStop())){
                    model.setDuration(ShamenUtil.getTimeDifference(model.getStart(), model.getStop()));
                    model.setDurationInMinutes(ShamenUtil.getTimeDifferenceInMinutes(model.getStart(), model.getStop()));
                }else{
                    model.setDuration("");
                }// end if/else
                model.setStatusDesc(entities.get(i).getStatus() == null ? "" : entities.get(i).getStatus().getStatusDesc());
                model.setResultDesc(entities.get(i).getResult() == null ? "" : entities.get(i).getResult().getResultDesc());
                model.setFromScheduleInd(entities.get(i).isFromSchedule());
                model.setUserName(entities.get(i).getCreatedBy() != null ? entities.get(i).getCreatedBy().getFullName() : "");
                model.setCreateTime(entities.get(i).getCommon().getCreateTime());
                modelList.add(model);
            }// end for
            Collections.sort(modelList, Collections.reverseOrder(new RunStatusComparator()));
        }// end if
        log.debug("Converted value of list is: " + String.valueOf(modelList));
        log.debug("Exiting toRunStatusForDisplayBusiness. Return list size is: " + modelList == null ? "0" : modelList.size());
        return modelList;
    }// end toRunStatusForDisplayBusiness

    /**
     * This method is used to convert an {@link ScheduleEntity} to an {@link ScheduleModel}.
     *
     * @param entity
     *        The ScheduleEntity object to convert.
     * @return The converted model.
     */
    public static ScheduleModel toScheduleBusiness(ScheduleEntity entity) {
        log.debug("Entering toScheduleBusiness. Incoming parameter is: " + String.valueOf(entity));
        log.debug("This method is used to convert an ScheduleEntity to an ScheduleModel.");
        ScheduleModel model = new ScheduleModel();
        if(entity != null){
            model.setBatchApp(new BatchAppModel(entity.getBatchApp().getBatchAppRefId()));
            model.setScheduleRefId(entity.getScheduleRefId() == null ? 0L : entity.getScheduleRefId());
            model.setStartTime(ShamenUtil.getTimeAsString(entity.getStartTime()));
            model.setSchedultStartDt(AppUtil.getDateAsString(entity.getScheduleStartDt()));
            model.setRecur(entity.getRecurNo() == null ? 0L : entity.getRecurNo());
            model.setRepeatCd(entity.getRepeat() == null ? "" : entity.getRepeat().getRepeatCd());
            model.setRepeatDesc(entity.getRepeat() == null ? "" : entity.getRepeat().getRepeatDesc());
            model.setFrequencyCd(entity.getFrequency() == null ? "" : entity.getFrequency().getFrequencyCd());
            model.setFrequencyDesc(entity.getFrequency() == null ? "" : entity.getFrequency().getFrequencyDesc());
            model.setFrequencyTypeCd(entity.getFrequencyType() == null ? "" : entity.getFrequencyType().getFrequencyTypeCd());
            model.setFrequencyTypeDesc(entity.getFrequencyType() == null ? "" : entity.getFrequencyType().getFrequencyTypeDesc());
            String[] weekDays = StringUtil.isNullOrEmpty(entity.getWeekdays()) ? null : entity.getWeekdays().split(",");
            String[] weekNo = StringUtil.isNullOrEmpty(entity.getWeekNumber()) ? null : entity.getWeekNumber().split(",");
            String[] dayNo = StringUtil.isNullOrEmpty(entity.getDayNo()) ? null : entity.getDayNo().split(",");
            model.setWeekdays(weekDays);
            model.setWeekNumber(weekNo);
            model.setDayNumber(dayNo);
            model.setActiveInd(StringUtil.isNullOrEmpty(entity.getActiveInd()) ? "N" : entity.getActiveInd());
            model.setLastRunStatus(entity.getLastRunStatus() == null ? new RunStatusModel() : toRunStatusBusiness(entity.getLastRunStatus()));
            if(null != entity.getCommon()){
                model.setCreateTime(entity.getCommon().getCreateTime());
                model.setCreateUserRefId(entity.getCommon().getCreateUserRefId());
                model.setUpdateUserRefId(entity.getCommon().getUpdateUserRefId());
                model.setUpdateTime(entity.getCommon().getUpdateTime());
                model.setDeleteIndicator(entity.getCommon().getDeleteIndicator());
            }else{
                log.debug("The common entity model in the schedule entity model is null. No values exist for the create/update user reference ID/timestamp or delete indicator. Unable to convert.");
            }// end if/else
        }// end if
        log.debug("Exiting toScheduleBusiness. Return value is: " + String.valueOf(model));
        return model;
    }// end toScheduleBusiness

    /**
     * This method is used to convert an {@link ControllerModel} to an {@link ControllerEntity}.
     *
     * @param model
     *        The model object to convert.
     * @return The converted entity.
     */
    public static ControllerEntity toControllerPersist(ControllerModel model) {
        log.debug("Entering toControllerPersist. Incoming parameter is: " + String.valueOf(model));
        log.debug("This method is used to convert an ControllerModel to an ControllerEntity.");
        ControllerEntity entity = null;
        if(model != null){
            entity = new ControllerEntity();
            entity.setControllerRefId(model.getControllerRefId());
            entity.setControllerAddress(model.getAddress());
            entity.setControllerName(model.getName());
            entity.setDefaultAddress(model.getDefaultAddress() != null ? model.getDefaultAddress() : EMPTY_STRING);
            entity.setCommon(new CommonEntity());
            entity.getCommon().setCreateTime(model.getCreateTime() != null ? model.getCreateTime() : AppUtil.getSQLTimestamp());
            entity.getCommon().setCreateUserRefId(model.getCreateUserRefId());
            entity.getCommon().setUpdateUserRefId(model.getUpdateUserRefId());
            entity.getCommon().setUpdateTime(AppUtil.getSQLTimestamp());
            entity.getCommon().setDeleteIndicator(StringUtil.isNullOrEmpty(model.getDeleteIndicator()) ? "N" : model.getDeleteIndicator());
            if(entity.getControllerStatusCodeEntity() == null){
                entity.setControllerStatusCodeEntity(new ControllerStatusCodeEntity("UNR", ""));
            }else{
                entity.getControllerStatusCodeEntity().setControllerStatusCd(model.getStatus());
            }// end if/else
        }// end if
        log.debug("Exiting toControllerPersist. Return value is: " + String.valueOf(entity));
        return entity;
    }// end toControllerPersist

    /**
     * This method is used to convert an {@link SystemModel} to an {@link SystemEntity}.
     *
     * @param model
     *        The model object to convert.
     * @return The converted entity.
     */
    public static SystemEntity toSystemPersist(SystemModel model) {
        log.debug("Entering toSystemPersist. Incoming parameter is: " + String.valueOf(model));
        log.debug("This method is used to convert an SystemModel to an SystemEntity.");
        SystemEntity entity = null;
        if(model != null){
            entity = new SystemEntity();
            entity.setSystemRefId(model.getSystemRefId());
            entity.setSystemName(model.getName());
            entity.setSystemDesc(model.getSystemDesc());
            // Convert the point of contact.
            if(model.getPointOfContact() != null && !model.getPointOfContact().getUserRefId().equals(0L)){
                entity.setPointOfContact(new AuthorizedUserEntity(model.getPointOfContact().getUserRefId()));
            }// end if
            entity.setCommon(new CommonEntity());
            entity.getCommon().setCreateTime(model.getCreateTime() != null ? model.getCreateTime() : AppUtil.getSQLTimestamp());
            entity.getCommon().setCreateUserRefId(model.getCreateUserRefId());
            entity.getCommon().setUpdateUserRefId(model.getUpdateUserRefId());
            entity.getCommon().setUpdateTime(AppUtil.getSQLTimestamp());
            entity.getCommon().setDeleteIndicator(StringUtil.isNullOrEmpty(model.getDeleteIndicator()) ? "N" : model.getDeleteIndicator());
        }// end if
        log.debug("Exiting toSystemPersist. Return value is: " + String.valueOf(entity));
        return entity;
    }// end toSystemPersist

    /**
     * This method is used to convert an {@link BatchAppModel} to an {@link BatchAppEntity}.
     *
     * @param model
     *        The entity object to convert.
     * @return The converted model.
     */
    public static BatchAppEntity toBatchAppPersist(BatchAppModel model) {
        log.debug("Entering toBatchAppPersist. Incoming parameter is: " + String.valueOf(model));
        log.debug("This method is used to convert an BatchAppModel to an BatchAppEntity.");
        BatchAppEntity entity = new BatchAppEntity();
        if(model != null){
            entity.setBatchAppRefId(model.getBatchRefId());
            entity.setBatchName(model.getName());
            entity.setFileLocation(model.getFileLocation());
            entity.setFileName(model.getFileName());
            entity.setDescription(model.getDescription());
            entity.setBatchType(new BatchTypeCodeEntity(model.getType(), model.getTypeDesc()));
            entity.setController(new ControllerEntity(model.getController().getControllerRefId()));
            // Convert the system.
            if(model.getSystem() != null && !model.getSystem().getSystemRefId().equals(0L)){
                entity.setSystem(new SystemEntity(model.getSystem().getSystemRefId()));
            }// end if
             // Convert the Application.
            if(model.getApplication() != null && !(model.getApplication().getApplicationRefId() == null) && !model.getApplication().getApplicationRefId().equals(0L)){
                entity.setApplication(new ApplicationEntity(model.getApplication().getApplicationRefId()));
            }else{
                entity.setApplication(null);
            }// end if
             // Convert the point of contact.
            if(model.getPointOfContact() != null && !model.getPointOfContact().getUserRefId().equals(0L)){
                entity.setPointOfContact(new AuthorizedUserEntity(model.getPointOfContact().getUserRefId()));
            }// end if
            entity.setCommon(new CommonEntity());
            entity.getCommon().setCreateTime(model.getCreateTime() != null ? model.getCreateTime() : AppUtil.getSQLTimestamp());
            entity.getCommon().setCreateUserRefId(model.getCreateUserRefId());
            entity.getCommon().setUpdateUserRefId(model.getUpdateUserRefId());
            entity.getCommon().setUpdateTime(AppUtil.getSQLTimestamp());
            entity.getCommon().setDeleteIndicator(StringUtil.isNullOrEmpty(model.getDeleteIndicator()) ? "N" : model.getDeleteIndicator());
        }// end if
        log.debug("Exiting toBatchAppPersist. Return value is: " + String.valueOf(model));
        return entity;
    }// end toBatchAppPersist

    /**
     * This method is used to convert an {@link BatchAppModel} to an {@link BatchAppEntity}.
     *
     * @param model
     *        The entity object to convert.
     * @return The converted model.
     */
    public static BatchAppEntity toBatchCollectionPersist(BatchAppModel model) {
        log.debug("Entering toBatchAppPersist. Incoming parameter is: " + String.valueOf(model));
        log.debug("This method is used to convert an BatchAppModel to an BatchAppEntity.");
        BatchAppEntity entity = new BatchAppEntity();
        if(model != null){
            entity.setBatchAppRefId(model.getBatchRefId());
            entity.setBatchName(model.getName());
            entity.setFileLocation("");
            entity.setFileName("");
            entity.setDescription(model.getDescription());
            entity.setBatchType(new BatchTypeCodeEntity("COL", ""));
            entity.setController(new ControllerEntity(model.getController().getControllerRefId()));
            // Convert the system.
            if(model.getSystem() != null && !model.getSystem().getSystemRefId().equals(0L)){
                entity.setSystem(new SystemEntity(model.getSystem().getSystemRefId()));
            }// end if
             // Convert the Application.
            if(model.getApplication() != null && !model.getApplication().getApplicationRefId().equals(0L)){
                entity.setApplication(new ApplicationEntity(model.getApplication().getApplicationRefId()));
            }// end if
            if(model.getPointOfContact() != null && !model.getPointOfContact().getUserRefId().equals(0L)){
                entity.setPointOfContact(new AuthorizedUserEntity(model.getPointOfContact().getUserRefId()));
            }// end if
            entity.setCommon(new CommonEntity());
            entity.getCommon().setCreateTime(model.getCreateTime() != null ? model.getCreateTime() : AppUtil.getSQLTimestamp());
            entity.getCommon().setCreateUserRefId(model.getCreateUserRefId());
            entity.getCommon().setUpdateUserRefId(model.getUpdateUserRefId());
            entity.getCommon().setUpdateTime(AppUtil.getSQLTimestamp());
            entity.getCommon().setDeleteIndicator(StringUtil.isNullOrEmpty(model.getDeleteIndicator()) ? "N" : model.getDeleteIndicator());
            if(!AppUtil.isEmpty(model.getBatchAppCollection())){
                entity.setBatchCollection(new ArrayList<BatchAppCollectionEntity>());
                CommonEntity comm = null;

                for(int i = 0, j = model.getBatchAppCollection().size();i < j;i++){
                    comm = new CommonEntity();
                    comm.setCreateTime(model.getCreateTime() != null ? model.getCreateTime() : AppUtil.getSQLTimestamp());
                    comm.setCreateUserRefId(model.getCreateUserRefId());
                    comm.setUpdateUserRefId(model.getUpdateUserRefId());
                    comm.setUpdateTime(model.getBatchAppCollection().get(i).getCreateTime() != null ? model.getBatchAppCollection().get(i).getUpdateTime() : AppUtil.getSQLTimestamp());
                    comm.setDeleteIndicator(StringUtil.isNullOrEmpty(model.getBatchAppCollection().get(i).getDeleteIndicator()) ? "N" : model.getBatchAppCollection().get(i).getDeleteIndicator());
                    entity.getBatchCollection().add(new BatchAppCollectionEntity(model.getBatchAppCollection().get(i).getCollectionRefId(), entity, new BatchAppEntity(model.getBatchAppCollection().get(i).getAssocBatchApp().getBatchRefId()), model.getBatchAppCollection().get(i).getRunSeq(), comm));
                }// end for
            }// end if
        }// end if
        log.debug("Exiting toBatchAppPersist. Return value is: " + String.valueOf(model));
        return entity;
    }// end toBatchCollectionPersist

    /**
     * This method is used to convert an {@link ScheduleModel} to an {@link ScheduleEntity}.
     *
     * @param model
     *        The model object to convert.
     * @return The converted entity.
     */
    public static ScheduleEntity toSchedulePersist(ScheduleModel model) {
        log.debug("Entering toSchedulePersist. Incoming parameter is: " + String.valueOf(model));
        log.debug("This method is used to convert an ScheduleModel to an ScheduleEntity.");
        ScheduleEntity entity = new ScheduleEntity();
        entity.setBatchApp(new BatchAppEntity());
        if(model != null){
            if(EMPTY_STRING.equalsIgnoreCase(model.getFrequencyCd())){
                entity = null;
            }else{
                entity.setScheduleRefId(model.getScheduleRefId());
                entity.setStartTime(ShamenUtil.getSQLTime(model.getStartTime()));
                entity.setScheduleStartDt(AppUtil.getSQLDate(model.getSchedultStartDt()));
                entity.setRecurNo(model.getRecur());
                entity.setFrequency(StringUtil.isNullOrEmpty(model.getFrequencyCd()) ? new FrequencyCodeEntity(BLANK_CODE, EMPTY_STRING) : new FrequencyCodeEntity(model.getFrequencyCd(), EMPTY_STRING));
                entity.setRepeat(StringUtil.isNullOrEmpty(model.getRepeatCd()) ? new RepeatCodeEntity(BLANK_CODE, EMPTY_STRING) : new RepeatCodeEntity(model.getRepeatCd(), EMPTY_STRING));
                entity.setFrequencyType(StringUtil.isNullOrEmpty(model.getFrequencyTypeCd()) ? new FrequencyTypeCodeEntity(BLANK_CODE, EMPTY_STRING) : new FrequencyTypeCodeEntity(model.getFrequencyTypeCd(), EMPTY_STRING));
                entity.setWeekdays(StringUtil.collapseArray(model.getWeekdays()));
                entity.setWeekNumber(StringUtil.collapseArray(model.getWeekNumber()));
                entity.setDayNo(StringUtil.collapseArray(model.getDayNumber()));
                entity.setActiveInd(model.getActiveInd());
                entity.setCommon(new CommonEntity());
                entity.getCommon().setCreateTime(AppUtil.getSQLTimestamp());
                entity.getCommon().setCreateUserRefId(model.getCreateUserRefId());
                entity.getCommon().setUpdateUserRefId(model.getUpdateUserRefId());
                entity.getCommon().setUpdateTime(AppUtil.getSQLTimestamp());
                entity.getCommon().setDeleteIndicator(StringUtil.isNullOrEmpty(model.getDeleteIndicator()) ? "N" : model.getDeleteIndicator());
            }// end if/else
        }// end if
        log.debug("Exiting toSchedulePersist. Return value is: " + String.valueOf(entity));
        return entity;
    }// end toSchedulePersist

    /**
     * This method is used to convert an {@link RunStatusModel} to an {@link RunStatusEntity}.
     *
     * @param model
     *        The model object to convert.
     * @return The converted entity.
     */
    public static RunStatusEntity toRunStatusPersist(RunStatusModel model) {
        log.debug("Entering toRunStatusPersist. Incoming parameter is: " + String.valueOf(model));
        log.trace("This method is used to convert a RunStatusModel to a RunStatusEntity.");
        RunStatusEntity entity = new RunStatusEntity();
        if(model != null){
            entity.setRunStatusRefId(model.getRunStatusRefId());
            entity.setRunNumber(model.getRunNumber());
            entity.setBatchApp((model.getBatchApp() != null ? new BatchAppEntity(model.getBatchApp().getBatchRefId()) : null));
            entity.setSchedule(model.getScheduleRefId() == null ? null : new ScheduleEntity(model.getScheduleRefId()));
            entity.setStartTime(ShamenUtil.getSqlTimestamp(model.getStart()));
            entity.setStopTime(ShamenUtil.getSqlTimestamp(model.getStop()));
            entity.setStatus(model.getStatusCd() == null ? new StatusCodeEntity() : new StatusCodeEntity(model.getStatusCd(), ""));
            entity.setResult(model.getResultCd() == null ? new ResultCodeEntity(BLANK_CODE, EMPTY_STRING) : new ResultCodeEntity(model.getResultCd(), ""));
            entity.setResultDetail(model.getResultDetail() == null ? "" : model.getResultDetail());
            entity.setDescription(model.getDescription() == null ? "" : model.getDescription());
            entity.setCommon(new CommonEntity());
            entity.getCommon().setCreateTime(model.getCreateTime() != null ? model.getCreateTime() : AppUtil.getSQLTimestamp());
            entity.getCommon().setCreateUserRefId(model.getCreateUserRefId());
            entity.getCommon().setUpdateUserRefId(model.getUpdateUserRefId());
            entity.getCommon().setUpdateTime(AppUtil.getSQLTimestamp());
            entity.getCommon().setDeleteIndicator(StringUtil.isNullOrEmpty(model.getDeleteIndicator()) ? "N" : model.getDeleteIndicator());
        }// end if
        log.debug("Exiting toBusiness. Return value is: " + String.valueOf(entity));
        return entity;
    }// end toRunStatusPersist

    /**
     * This method is used to convert a list {@link Object} to a list {@link ControllerModel}.
     *
     * @param objectList
     *        The list object to convert.
     * @return The converted list.
     */
    public static List<ControllerModel> convertControllerToCodeModel(List<Object> objectList) {
        log.debug("Entering convertControllerToCodeModel. Incoming list size is: " + objectList == null ? "0" : objectList.size());
        log.debug("This method is used to convert an Object list to a list of ControllerModel.");
        log.debug("The incoming parameter is: " + String.valueOf(objectList));
        List<ControllerModel> codeList = new ArrayList<ControllerModel>();
        codeList.add(new ControllerModel(Constants.ZERO_STRING, SHAMEN_DEFAULT_LABEL));
        ControllerModel model = null;
        if(objectList != null && !objectList.isEmpty()){
            for(int i = 0, j = objectList.size();i < j;i++){
                Object[] element = (Object[]) objectList.get(i);
                model = new ControllerModel(String.valueOf(element[0]), String.valueOf(element[1]));
                model.setDefaultAddress(String.valueOf(element[2]));
                codeList.add(model);
            }// end for
        }// end if
        log.debug("Converted value of list is: " + String.valueOf(codeList));
        log.debug("Exiting convertControllerToCodeModel. Return list size is: " + codeList == null ? "0" : codeList.size());
        return codeList;
    }// end convertControllerToCodeModel

    /**
     * This method is used to convert a list {@link BatchAppEntity} to a list {@link CodeModel}.
     *
     * @param modelList
     *        The list object to convert.
     * @return The converted list.
     */
    public static List<CodeModel> convertBatchToCodeModel(List<BatchAppEntity> modelList) {
        log.debug("Entering convertBatchToCodeModel. Incoming list size is: " + modelList == null ? "0" : modelList.size());
        log.debug("This method is used to convert alist of BatchAppEntity to an list of CodeModel.");
        log.debug("The incoming parameter is: " + String.valueOf(modelList));
        List<CodeModel> codeList = new ArrayList<CodeModel>();

        if(modelList != null && !modelList.isEmpty()){
            for(int i = 0, j = modelList.size();i < j;i++){
                if(!modelList.get(i).getBatchType().getBatchTypeCd().equalsIgnoreCase(AppConstants.BATCH_APP_TYPE_CD_COLLECTION) && modelList.get(i).getBatchType().getBatchTypeCd().equalsIgnoreCase("GE2") && AppUtil.isEmpty(modelList.get(i).getSchedule())){
                    codeList.add(new CodeModel(String.valueOf(modelList.get(i).getBatchAppRefId()), modelList.get(i).getBatchName()));
                }// end if
            }// end for
        }// end if
        log.debug("Converted value of list is: " + String.valueOf(codeList));
        log.debug("Exiting convertBatchToCodeModel. Return list size is: " + codeList == null ? "0" : codeList.size());
        return codeList;
    }// end convertBatchToCodeModel

    /**
     * This method is used to convert a list {@link SystemModel} to a list {@link CodeModel}.
     *
     * @param modelList
     *        The list object to convert.
     * @return The converted list.
     */
    public static List<CodeModel> convertSystemsToCodeModel(List<SystemModel> modelList) {
        log.debug("Entering convertSystemsToCodeModel. Incoming list size is: " + modelList == null ? "0" : modelList.size());
        log.debug("This method is used to convert a list of SystemEntity to a list of CodeModel.");
        log.debug("The incoming parameter is: " + String.valueOf(modelList));
        List<CodeModel> codeList = new ArrayList<CodeModel>();
        codeList.add(new CodeModel(Constants.ZERO_STRING, SHAMEN_DEFAULT_LABEL));
        if(modelList != null && !modelList.isEmpty()){
            for(int i = 0, j = modelList.size();i < j;i++){
                codeList.add(new CodeModel(String.valueOf(modelList.get(i).getSystemRefId()), modelList.get(i).getName()));
            }// end for
        }// enf if
        log.debug("Converted value of list is: " + String.valueOf(codeList));
        log.debug("Exiting convertSystemsToCodeModel. Return list size is: " + codeList == null ? "0" : codeList.size());
        return codeList;
    }// end convertSystemsToCodeModel

    /**
     * This method is used to convert a list {@link Object} to a list {@link CodeModel}.
     *
     * @param objectList
     *        The list object to convert.
     * @return The converted list.
     */
    public static List<CodeModel> convertApplicationToCodeModel(List<Object> objectList) {
        log.debug("Entering convertApplicationToCodeModel. Incoming list size is: " + objectList == null ? "0" : objectList.size());
        log.debug("This method is used to convert a list of ApplicationModel to a list of CodeModel.");
        log.debug("The incoming parameter is: " + String.valueOf(objectList));
        List<CodeModel> codeList = new ArrayList<CodeModel>();
        codeList.add(new CodeModel(Constants.ZERO_STRING, SHAMEN_DEFAULT_LABEL));

        if(objectList != null && !objectList.isEmpty()){
            for(int i = 0, j = objectList.size();i < j;i++){
                Object[] element = (Object[]) objectList.get(i);
                codeList.add(new CodeModel(String.valueOf(element[0]), (String.valueOf(element[1]) + " - " + String.valueOf(element[2]))));
            }// end for
        }// end if
        log.debug("Converted value of list is: " + String.valueOf(codeList));
        log.debug("Exiting convertApplicationToCodeModel. Return list size is: " + codeList == null ? "0" : codeList.size());
        return codeList;
    }// end convertApplicationToCodeModel

    /**
     * This method is used to convert a list of {@link ApplicationEntity} to a list of {@link ApplicationModel}.
     *
     * @param entities
     *        The list of entity objects to convert.
     * @return The converted list.
     * @throws BaseException
     *         if an exception occurred
     */
    public static List<ApplicationModel> convertForApplicationList(List<ApplicationEntity> entities) throws BaseException {
        log.debug("Entering convertForApplicationList. Incoming list size is: " + entities == null ? "0" : String.valueOf(entities));
        log.debug("This method is used to convert an list of ApplicationEntity to a list of ApplicationModel.");
        log.debug("The incoming parameter is: " + String.valueOf(entities));
        List<ApplicationModel> modelList = new ArrayList<ApplicationModel>();
        List<ColumnModel> column = null;
        ColumnModel col = null;
        ApplicationModel model = null;
        if(entities != null && !entities.isEmpty()){
            for(int i = 0, j = entities.size();i < j;i++){
                model = new ApplicationModel();
                column = new ArrayList<ColumnModel>();
                model.setApplicationRefId(entities.get(i).getApplicationRefId());
                model.setApplicationName(entities.get(i).getApplicationName() == null ? "" : entities.get(i).getApplicationName());
                model.setApplicationTypeDesc(entities.get(i).getApplicationType().getApplicationTypeDesc() == null ? "" : entities.get(i).getApplicationType().getApplicationTypeDesc());
                model.setApplicationAddress(entities.get(i).getApplicationAddress() == null ? "" : entities.get(i).getApplicationAddress());
                model.setRequestStatusInd(entities.get(i).getApplicationStatus().getApplicationStatusCd() == null ? "" : entities.get(i).getApplicationStatus().getApplicationStatusCd());
                model.setRequestStatusIndDesc(entities.get(i).getApplicationStatus().getApplicationStatusDesc() == null ? "" : entities.get(i).getApplicationStatus().getApplicationStatusDesc());
                model.setShowApplicationNotification(StringUtil.isNullOrEmpty(entities.get(i).getShowApplicationNotification()) ? "N" : entities.get(i).getShowApplicationNotification());
                model.setStatusIndDesc("");
                model.setSystem(toSystemBusiness(entities.get(i).getSystem()));
                model.setPointOfContact(toAuthorizedUserBusiness(entities.get(i).getPointOfContact()));
                // if any batch apps are associated to this web app, then convert them as well.
                if(entities.get(i).getBatchApps() != null){
                    model.setBatchApps(new ArrayList<BatchAppModel>());
                    for(int j2 = 0, j3 = entities.get(i).getBatchApps().size();j2 < j3;j2++){
                        model.getBatchApps().add(toBatchAppBusiness(entities.get(i).getBatchApps().get(j2)));
                    }// end for
                }// end for
                 // Set the status for the overall application and load the instances.
                String statusInd = ApplicationStatusUpdaterBeanLocal.STATUS_UNRESPONSIVE;
                String holdStatus = "";
                List<ApplicationInstanceEntity> appInstances = entities.get(i).getApplicationInstances();
                model.setAppInstances(new ArrayList<ApplicationInstanceModel>());
                if(appInstances != null){
                    for(int k = 0, l = appInstances.size();k < l;k++){
                        model.getAppInstances().add(toApplicationInstanceBusiness(appInstances.get(k)));
                        if(ApplicationStatusUpdaterBeanLocal.STATUS_UNRESPONSIVE.equals(appInstances.get(k).getVerifiedApplicationStatusCd()) || (!appInstances.get(k).getVerifiedApplicationStatusCd().equals(model.getRequestStatusInd()) && !ApplicationStatusUpdaterBeanLocal.STATUS_AWAITING_RESPONSE.equals(appInstances.get(k).getVerifiedApplicationStatusCd()))){
                            statusInd = ApplicationStatusUpdaterBeanLocal.STATUS_UNRESPONSIVE;
                            break;
                        }else{
                            statusInd = appInstances.get(k).getVerifiedApplicationStatusCd();
                            if(holdStatus.equals("")){
                                holdStatus = appInstances.get(k).getVerifiedApplicationStatusCd();
                            }else{
                                if(!holdStatus.equals(appInstances.get(k).getVerifiedApplicationStatusCd())){
                                    statusInd = ApplicationStatusUpdaterBeanLocal.STATUS_UNRESPONSIVE;
                                    break;
                                }// end if
                            }// end if/else
                        }// end if/else
                    }// end for
                }// end if
                model.setStatusInd(statusInd);

                col = new ColumnModel();
                col.setSortable(true);
                col.setMaxLength(45);
                col.put("isSelect", false);
                col.setColumnName("Web App Name");
                col.setColumnValue(model.getApplicationName());
                col.setColumnNumber(1L);
                col.put("styleClass", "form-control enableWithRestrict");
                col.put("errorStyleClass", "form-control enableWithRestrict is-invalid");
                col.put("boxSize", "40");
                col.put("style", "width:35%");
                col.put("paramId", "applicationRefId");
                col.put("paramProperty", "applicationRefId");
                col.put("class", "enableWithRestrict");
                col.put("url", "/applicationDetailAction.do?method=editApplication&caller=applicationList");
                col.setType(TagConstants.ALPHA_NUMERIC_SORT);
                column.add(col);

                col = new ColumnModel();
                col.setSortable(true);
                col.put("boxSize", "20");
                col.put("style", "width:20%");
                col.setColumnNumber(2L);
                col.put("styleClass", "form-control enableWithRestrict");
                col.put("errorStyleClass", "form-control enableWithRestrict is-invalid");
                col.setMaxLength(35);
                col.put("isSelect", false);
                col.setColumnName("Environment");
                col.setColumnValue(model.getApplicationAddress());
                col.setType(TagConstants.ALPHA_NUMERIC_SORT);
                column.add(col);

                col = new ColumnModel();
                col.setSortable(true);
                col.put("isSelect", true);
                col.put("listName", "statusIndList");
                col.setColumnName("Requested Status");
                col.setColumnValue(model.getRequestStatusIndDesc());
                col.put("styleClass", "custom-select enableWithRestrict");
                col.setType(TagConstants.ALPHA_NUMERIC_SORT);
                column.add(col);

                col = new ColumnModel();
                col.setSortable(true);
                col.put("isSelect", true);
                col.put("listName", "pocList");
                col.setColumnNumber(4L);
                col.put("styleClass", "custom-select enableWithRestrict");
                col.setColumnName("Point of Contact");
                col.setColumnValue(model.getPointOfContact().getFullName());
                col.setType(TagConstants.ALPHA_NUMERIC_SORT);
                column.add(col);

                model.setColumnData(column);
                modelList.add(model);
            }// end for
        }// end if
        log.debug("Converted value of list is: " + String.valueOf(modelList));
        log.debug("Exiting convertForApplicationList. Return list size is: " + modelList == null ? "0" : modelList.size());
        return modelList;
    }// end convertForApplicationList

    /**
     * This method is used to convert an {@link ApplicationEntity} to an {@link ApplicationModel}.
     *
     * @param entity
     *        The entity object to convert.
     * @return model The converted model.
     * @throws BaseException
     *         if an exception occurred
     */
    public static ApplicationModel toApplicationBusiness(ApplicationEntity entity) throws BaseException {
        log.debug("Entering ObjectConvertor.toApplicationBusiness(). Parameter: entity=" + null != String.valueOf(entity) ? String.valueOf(entity) : null);
        log.debug("This method is used to convert an ApplicationEntity to an ApplicationModel.");
        ApplicationModel model = null;
        if(entity != null){
            model = new ApplicationModel();
            model.setAppInstances(new ArrayList<ApplicationInstanceModel>());
            model.setApplicationRefId(entity.getApplicationRefId());
            model.setApplicationName(entity.getApplicationName() == null ? "" : entity.getApplicationName());
            model.setApplicationType("WEB");
            model.setApplicationTypeDesc("");
            model.setApplicationAddress(entity.getApplicationAddress() == null ? "" : entity.getApplicationAddress());
            model.setRequestStatusInd(entity.getApplicationStatus() == null || entity.getApplicationStatus().getApplicationStatusCd() == null ? "" : entity.getApplicationStatus().getApplicationStatusCd());
            model.setRequestStatusIndDesc(entity.getApplicationStatus() == null || entity.getApplicationStatus().getApplicationStatusDesc() == null ? "" : entity.getApplicationStatus().getApplicationStatusDesc());
            model.setShowApplicationNotification(StringUtil.isNullOrEmpty(entity.getShowApplicationNotification()) ? "N" : entity.getShowApplicationNotification());
            model.setApplicationNotificationDesc(StringUtil.isNullOrEmpty(entity.getApplicationNotificationDesc()) ? "" : ShamenUtil.htmlBreak2TextBreak(entity.getApplicationNotificationDesc()));
            // model.setStatusInd(entity.getVerifiedApplicationStatusCd());
            // model.setStatusIndDesc(info.getApplicationStatusDesc(entity.getVerifiedApplicationStatusCd()));
            model.setStatusComment(entity.getStatusComment() == null ? "" : ShamenUtil.htmlBreak2TextBreak(entity.getStatusComment()));
            // model.setEncryptionKey((entity.getEncryptionKey() != null ? new String(entity.getEncryptionKey()) : null));
            // set the app instances list
            if(!AppUtil.isEmpty(entity.getApplicationInstances())){
                for(int i = 0;i < entity.getApplicationInstances().size();i++){
                    model.getAppInstances().add(toApplicationInstanceBusiness(entity.getApplicationInstances().get(i)));
                }// end for
            }// end if
            if(null != entity.getCommon()){
                model.setCreateTime(entity.getCommon().getCreateTime());
                model.setCreateUserRefId(entity.getCommon().getCreateUserRefId());
                model.setUpdateUserRefId(entity.getCommon().getUpdateUserRefId());
                model.setUpdateTime(entity.getCommon().getUpdateTime());
                model.setDeleteIndicator(entity.getCommon().getDeleteIndicator());
            }else{
                log.debug("The common entity model in the authorized user entity model is null. No values exist for the create/update user reference ID/timestamp or delete indicator. Unable to convert.");
            }// end if/else
        }// end if
        log.debug("Exiting ObjectConvertor.convertForApplicationList(). Return: model=" + null != String.valueOf(model) ? String.valueOf(model) : null);
        return model;
    }// end toApplicationBusiness

    /**
     * This method is used to convert an {@link ApplicationInstanceEntity} to an {@link ApplicationInstanceModel}.
     *
     * @param entity
     *        The entity object to convert.
     * @return model The converted model.
     * @throws BaseException
     *         if an exception occurred
     */
    public static ApplicationInstanceModel toApplicationInstanceBusiness(ApplicationInstanceEntity entity) throws BaseException {
        log.debug("Entering ObjectConvertor.toApplicationBusiness(). Parameter: entity=" + null != String.valueOf(entity) ? String.valueOf(entity) : null);
        log.debug("This method is used to convert an ApplicationInstanceEntity to an ApplicationInstanceModel.");
        ApplicationInstanceModel model = null;
        if(entity != null){
            model = new ApplicationInstanceModel();
            model.setApplicationInstanceRefId(entity.getApplicationInstanceRefId());
            model.setApplicationInstanceName(entity.getApplicationInstanceName());
            model.setInstantiationTs(ShamenUtil.getFormattedDateTimeAsString(entity.getInstantiationTs()));
            model.setStatus(entity.getVerifiedApplicationStatusCd());
            model.setVerifiedVersionNm(entity.getVerifiedVersionNm() == null ? "" : entity.getVerifiedVersionNm());
            model.setVerifiedBuildNm(entity.getVerifiedBuildNm() == null ? "" : entity.getVerifiedBuildNm());
            model.setVerifiedEarNm(entity.getVerifiedEarNm() == null ? "" : entity.getVerifiedEarNm());
            model.setVerifiedAddtnlInfo(entity.getVerifiedAddtnlInfo() == null ? "" : entity.getVerifiedAddtnlInfo());
        }// end if
        log.debug("Exiting ObjectConvertor.convertForApplicationList(). Return: model=" + null != String.valueOf(model) ? String.valueOf(model) : null);
        return model;
    }// end toApplicationInstanceBusiness

    /**
     * This method is used to convert an {@link ApplicationModel} to an {@link ApplicationEntity}.
     *
     * @param model
     *        The model object to convert.
     * @return The converted entity.
     */
    public static ApplicationEntity toApplicationPersist(ApplicationModel model) {
        log.debug("Entering toApplicationPersist");
        log.debug("This method is used to convert an ApplicationModel to an ApplicationEntity.");
        log.debug("Entry parameters are: model=" + String.valueOf(model));
        ApplicationEntity entity = new ApplicationEntity();
        if(model != null){
            entity.setApplicationRefId(model.getApplicationRefId());
            entity.setApplicationAddress(model.getApplicationAddress());
            entity.setApplicationName(model.getApplicationName());
            entity.setShowApplicationNotification(StringUtil.isNullOrEmpty(model.getShowApplicationNotification()) ? "N" : model.getShowApplicationNotification());
            entity.setApplicationNotificationDesc(StringUtil.isNullOrEmpty(model.getApplicationNotificationDesc()) ? "" : ShamenUtil.textBreak2HtmlBreak(model.getApplicationNotificationDesc()));
            entity.setApplicationType(new ApplicationTypeCodeEntity("WEB", ""));
            entity.setApplicationStatus(new ApplicationStatusCodeEntity(model.getRequestStatusInd(), model.getRequestStatusIndDesc()));
            // Convert the system.
            if(model.getSystem() != null && !model.getSystem().getSystemRefId().equals(0L)){
                entity.setSystem(new SystemEntity(model.getSystem().getSystemRefId()));
            }// end if
             // Convert the point of contact.
            if(model.getPointOfContact() != null && !model.getPointOfContact().getUserRefId().equals(0L)){
                entity.setPointOfContact(new AuthorizedUserEntity(model.getPointOfContact().getUserRefId()));
            }// end if
            log.debug("Checking if statusInd and requestStatusInd are both available and match. Removing comment if true.");
            if((!model.getRequestStatusInd().equalsIgnoreCase(ApplicationStatusUpdaterBeanLocal.STATUS_SUSPENDED) && !model.getRequestStatusInd().equalsIgnoreCase(ApplicationStatusUpdaterBeanLocal.STATUS_INFORMATION)) && (null != model.getStatusInd() && null != model.getRequestStatusInd())){
                if(model.getStatusInd().equals(model.getRequestStatusInd())){
                    model.setStatusComment("");
                }// end if
            }// end if

            entity.setStatusComment(model.getStatusComment() != null ? ShamenUtil.textBreak2HtmlBreak(model.getStatusComment()) : "");
            entity.setCommon(new CommonEntity());
            entity.getCommon().setCreateTime(model.getCreateTime() != null ? model.getCreateTime() : AppUtil.getSQLTimestamp());
            entity.getCommon().setCreateUserRefId(model.getCreateUserRefId());
            entity.getCommon().setUpdateUserRefId(model.getUpdateUserRefId());
            entity.getCommon().setUpdateTime(AppUtil.getSQLTimestamp());
            entity.getCommon().setDeleteIndicator(StringUtil.isNullOrEmpty(model.getDeleteIndicator()) ? "N" : model.getDeleteIndicator());
        }// end if
        log.debug("Return value is: entity=" + String.valueOf(entity));
        log.debug("Exiting toApplicationPersist");
        return entity;
    }// end toApplicationPersist

    /**
     * This method used to convert a map of values to a JSON array for Calendar display.
     *
     * @param dataMap
     *        the Map<Scheduleable, List<Timestamp>> to convert
     * @return JSONArray
     */
    public static JSONArray convertToCalendarData(Map<Scheduleable, List<Timestamp>> dataMap) {
        log.debug("Entering convertToCalendarData");
        log.debug("This method used to convert a map of values to a JSON array for Calendar display.");
        log.debug("Entry parameters are: dataMap=" + String.valueOf(dataMap));
        JSONArray array = new JSONArray();
        JSONObject obj = null;
        Iterator<Entry<Scheduleable, List<Timestamp>>> it = dataMap.entrySet().iterator();
        List<Timestamp> list = null;
        Map.Entry<Scheduleable, List<Timestamp>> entry = null;
        Scheduleable sch = null;
        Timestamp time = null;
        while(it.hasNext()){
            entry = it.next();
            sch = entry.getKey();
            list = entry.getValue();
            if(!AppUtil.isEmpty(list)){
                Iterator<Timestamp> iter = list.iterator();
                while(iter.hasNext()){
                    obj = new JSONObject();
                    time = iter.next();
                    obj.put("id", sch.getSchedule().get(0).getBatchAppRefId());
                    obj.put("title", sch.getName());
                    obj.put("start", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(time));
                    obj.put("editable", false);
                    if("STD".equalsIgnoreCase(sch.getType())){
                        obj.put("url", "/batchDetailAction.do?method=editBatch&caller=calendar&batchRefId=" + sch.getSchedule().get(0).getBatchAppRefId());
                    }else{
                        obj.put("url", "/batchCollectionDetailAction.do?method=editBatch&caller=calendar&batchRefId=" + sch.getSchedule().get(0).getBatchAppRefId());
                        obj.put("color", "#1adba2");
                    }// end if/else
                    array.add(obj);
                }// end while
            }// end if
        }// end while
        log.debug("Return value is: array=" + String.valueOf(array));
        log.debug("Exiting convertToCalendarData");
        return array;
    }// end convertToCalendarData

    /**
     * This method used to convert data to a JSON array for the users info graphic display.
     *
     * @param userData
     *        the List<AuthorizedUserModel> to convert
     * @param labels
     *        String[] of labels to use as the key
     * @return JSONArray
     */
    public static JSONArray convertToUserData(List<AuthorizedUserModel> userData, String[] labels) {
        log.debug("Entering convertToUserData");
        log.debug("This method used to convert data to a JSON array for the users info graphic display.");
        log.debug("Entry parameters are: userData=" + String.valueOf(userData) + ", labels=" + Arrays.toString(labels));
        JSONArray array = new JSONArray();
        JSONObject obj = null;
        AuthorizedUserModel model = null;
        Iterator<AuthorizedUserModel> it = userData.iterator();

        while(it.hasNext()){
            obj = new JSONObject();
            model = it.next();
            int num = 0;
            String bb = "";
            if(StringUtil.arrayContains("System", labels)){
                num += model.getNumOfSystems();
                bb += model.getNumOfSystems();
            }// end if
            if(StringUtil.arrayContains("BatchApps", labels)){
                num += model.getNumOfBatchApps();
                if(!StringUtil.isNullOrEmpty(bb)){
                    bb += ",";
                }// end if
                bb += model.getNumOfBatchApps();
            }// end if
            if(StringUtil.arrayContains("WebApps", labels)){
                num += model.getNumOfWebApps();
                if(!StringUtil.isNullOrEmpty(bb)){
                    bb += ",";
                }// end if
                bb += model.getNumOfWebApps();
            }// end if
            if(num != 0){
                Integer[] hold = ShamenUtil.convertStringArraytoIntegerArray(bb.split(","));
                obj.put("label", model.getFullNameAbbr());
                obj.put("values", JSONArray.fromObject(hold));
                array.add(obj);
            }// end if
        }// end while
        obj = new JSONObject();
        log.debug("Return value is: array=" + String.valueOf(array));
        log.debug("Exiting convertToUserData");
        return array;
    }// end convertToUserData

    /**
     * This method converts a list of system models into a JSON array.
     *
     * @param systemData
     *        the List<SystemModel> to convert
     * @return JSONArray
     */
    public static JSONArray convertToSystemData(List<SystemModel> systemData) {
        log.debug("Entering convertToSystemData");
        log.debug("This method converts a list of system models into a JSON array.");
        log.debug("Entry parameters are: systemData=" + String.valueOf(systemData));
        SystemModel model = null;
        Iterator<SystemModel> it = systemData.iterator();
        JSONArray systemArray = new JSONArray();
        List<Long> collectionMembersToIgnore;
        // loop and load each system
        while(it.hasNext()){
            JSONObject systemObj = new JSONObject();
            model = it.next();
            systemObj.put("id", "system" + model.getSystemRefId());
            systemObj.put("name", model.getName());
            systemObj.put("data", "{}");
            JSONArray applicationArray = new JSONArray();
            // loop and load each application
            if(model.getApplications() != null){
                for(int i = 0, j = model.getApplications().size();i < j;i++){
                    JSONObject applicationObj = new JSONObject();
                    JSONArray batchArray = new JSONArray();
                    applicationObj.put("id", "webApp" + model.getApplications().get(i).getApplicationRefId());
                    applicationObj.put("name", model.getApplications().get(i).getApplicationName() + "-" + model.getApplications().get(i).getApplicationAddress());
                    applicationObj.put("data", "{}");
                    // loop and load each batch application
                    if(model.getApplications().get(i).getBatchApps() != null){
                        collectionMembersToIgnore = getCollectionMembers(model.getBatchApps());
                        for(int k = 0, l = model.getApplications().get(i).getBatchApps().size();k < l;k++){
                            JSONObject batchObj = loadBatchAppForTreeChart(model.getApplications().get(i).getBatchApps().get(k), "batchAppB", collectionMembersToIgnore);
                            if(batchObj != null){
                                batchArray.add(batchObj);
                            }// end if
                        }// end for
                    }// end if
                    applicationObj.put("children", batchArray);
                    applicationArray.add(applicationObj);
                }// end for
            }// end if
             // load the system level batch apps
            if(model.getBatchApps() != null){
                collectionMembersToIgnore = getCollectionMembers(model.getBatchApps());
                for(int i = 0, j = model.getBatchApps().size();i < j;i++){
                    ApplicationModel app = model.getBatchApps().get(i).getApplication();
                    // only show at this level if the batch app is not assigned to a web app. This is only for system level batch apps.
                    if(!(app != null && app.getApplicationRefId() != null)){
                        // only load batch apps that are not part of a collection.
                        JSONObject batchObj = loadBatchAppForTreeChart(model.getBatchApps().get(i), "batchApp", collectionMembersToIgnore);
                        if(batchObj != null){
                            applicationArray.add(batchObj);
                        }// end if
                    }// end if
                }// end for
            }// end if
            systemObj.put("children", applicationArray);
            systemArray.add(systemObj);
        }// end while
        log.debug("Return value is: systemArray=" + String.valueOf(systemArray));
        log.debug("Exiting convertToSystemData");
        return systemArray;
    }// end convertToSystemData

    /**
     * This method converts a system model into a JSON array.
     *
     * @param systemData
     *        the SystemModel to convert
     * @return JSONArray
     */
    public static JSONArray convertToSystemData(SystemModel systemData) {
        log.debug("Entering convertToSystemData");
        log.debug("This method converts a system model into a JSON array.");
        log.debug("Entry parameters are: systemData=" + String.valueOf(systemData));
        List<Long> collectionMembersToIgnore;
        JSONArray systemArray = new JSONArray();
        // loop and load each application
        if(systemData.getApplications() != null){
            for(int i = 0, j = systemData.getApplications().size();i < j;i++){
                JSONObject applicationObj = new JSONObject();
                JSONArray batchArray = new JSONArray();
                applicationObj.put("id", "webApp" + systemData.getApplications().get(i).getApplicationRefId());
                applicationObj.put("name", systemData.getApplications().get(i).getApplicationName() + "-" + systemData.getApplications().get(i).getApplicationAddress());
                applicationObj.put("data", "{}");
                // loop and load each batch application
                if(systemData.getApplications().get(i).getBatchApps() != null){
                    collectionMembersToIgnore = getCollectionMembers(systemData.getBatchApps());
                    for(int k = 0, l = systemData.getApplications().get(i).getBatchApps().size();k < l;k++){
                        JSONObject batchObj = loadBatchAppForTreeChart(systemData.getApplications().get(i).getBatchApps().get(k), "batchAppB", collectionMembersToIgnore);
                        if(batchObj != null){
                            batchArray.add(batchObj);
                        }// end if
                    }// end for
                }// end if
                applicationObj.put("children", batchArray);
                systemArray.add(applicationObj);
            }// end for
        }// end if
         // load the system level batch apps
        if(systemData.getBatchApps() != null){
            collectionMembersToIgnore = getCollectionMembers(systemData.getBatchApps());
            for(int i = 0, j = systemData.getBatchApps().size();i < j;i++){
                ApplicationModel app = systemData.getBatchApps().get(i).getApplication();
                // only show at this level if the batch app is not assigned to a web app. This is only for system level batch apps.
                if(!(app != null && app.getApplicationRefId() != null)){
                    // only load batch apps that are not part of a collection.
                    JSONObject batchObj = loadBatchAppForTreeChart(systemData.getBatchApps().get(i), "batchApp", collectionMembersToIgnore);
                    if(batchObj != null){
                        systemArray.add(batchObj);
                    }// end if
                }// end if
            }// end for
        }// end if
        log.debug("Return value is: systemArray=" + String.valueOf(systemArray));
        log.debug("Exiting convertToSystemData");
        return systemArray;
    }// end convertToSystemData

    /**
     * This method loads a JSONObject for display in a chart. If it's a collection it will load it's children as well.
     *
     * @param batchApp
     *        the BatchAppModel
     * @param nodeId
     *        the nodeId
     * @param collectionMemberList
     *        the ref id list of collection members
     * @return JSONObject
     */
    private static JSONObject loadBatchAppForTreeChart(BatchAppModel batchApp, String nodeId, List<Long> collectionMemberList) {
        log.debug("Entering loadBatchAppForTreeChart");
        log.debug("This method loads a JSONObject for display in a chart. If it's a collection it will load it's children as well.");
        log.debug("Entry parameters are: batchApp=" + String.valueOf(batchApp) + ", nodeId=" + String.valueOf(nodeId) + ", collectionMemberList=" + String.valueOf(collectionMemberList));
        JSONObject batchObj = null;
        // only load batch apps that are not part of a collection.
        if(!collectionMemberList.contains(batchApp.getBatchRefId())){
            batchObj = new JSONObject();
            batchObj.put("id", nodeId + batchApp.getBatchRefId());
            batchObj.put("name", batchApp.getName());
            batchObj.put("data", "{}");
            // if this batch app is a collection, load all its members as children.
            if(AppConstants.BATCH_APP_TYPE_CD_COLLECTION.equals(batchApp.getType())){
                if(batchApp.getBatchAppCollection() != null && !batchApp.getBatchAppCollection().isEmpty()){
                    JSONArray collectionArray = new JSONArray();
                    for(int i = 0, j = batchApp.getBatchAppCollection().size();i < j;i++){
                        JSONObject collectionObj = loadBatchAppForTreeChart(batchApp.getBatchAppCollection().get(i).getAssocBatchApp(), "colMbr", new ArrayList<Long>());
                        if(collectionObj != null){
                            collectionArray.add(collectionObj);
                        }// end if
                    }// end for
                    batchObj.put("children", collectionArray);
                }// end if
            }else{
                batchObj.put("children", "{}");
            }// end if
        }// end if
        log.debug("Return value is: batchObj=" + String.valueOf(batchObj));
        log.debug("Exiting loadBatchAppForTreeChart");
        return batchObj;
    }// end loadBatchAppForTreeChart

    /**
     * This method gets all the collection members, if they exist, from a batch app.
     *
     * @param batchAppList
     *        a List<BatchAppModel> to convert
     * @return list of collection members
     */
    private static List<Long> getCollectionMembers(List<BatchAppModel> batchAppList) {
        log.debug("Entering getCollectionMembers. Incoming parameter list size is: " + batchAppList == null ? "0" : batchAppList.size());
        log.debug("This method gets all the collection members, if they exist, from a batch app.");
        log.debug("Entry parameters are: batchAppList=" + String.valueOf(batchAppList));
        List<Long> collectionList = new ArrayList<Long>();
        BatchAppModel batchApp = null;
        for(int k = 0, l = batchAppList.size();k < l;k++){
            batchApp = batchAppList.get(k);
            // check if the inner collection of batch apps is there
            if(batchApp.getBatchAppCollection() != null && !batchApp.getBatchAppCollection().isEmpty()){
                // loop through and add all collection members to the list
                for(int i = 0, j = batchApp.getBatchAppCollection().size();i < j;i++){
                    collectionList.add(batchApp.getBatchAppCollection().get(i).getAssocBatchApp().getBatchRefId());
                }// end for
            }// end if
        }// end if
        log.debug("Converted value of list is: " + String.valueOf(collectionList));
        log.debug("Exiting getCollectionMembers. Return list size is: " + collectionList == null ? "0" : collectionList.size());
        return collectionList;
    }// end getCollectionMembers
}// end class
