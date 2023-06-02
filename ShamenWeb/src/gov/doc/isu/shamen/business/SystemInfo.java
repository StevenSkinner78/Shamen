package gov.doc.isu.shamen.business;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import gov.doc.isu.dwarf.exception.BaseException;
import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.shamen.convertor.ObjectConvertor;
import gov.doc.isu.shamen.ejb.beans.view.ApplicationStatusUpdaterBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.SystemBeanLocal;
import gov.doc.isu.shamen.ejb.util.EJBConstants;
import gov.doc.isu.shamen.jpa.entity.SystemEntity;
import gov.doc.isu.shamen.models.ApplicationModel;
import gov.doc.isu.shamen.models.BatchAppModel;
import gov.doc.isu.shamen.models.StatModel;
import gov.doc.isu.shamen.models.SystemModel;
import gov.doc.isu.shamen.models.SystemStatsModel;
import net.sf.json.JSONArray;

/**
 * This is the processor class for SystemInfo.
 *
 * @author <strong>Shane Duncan</strong>
 */
public class SystemInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.business.SystemInfo");
    private static final SystemInfo INSTANCE = new SystemInfo();

    /**
     * This constructor made private to block instantiating this class.
     */
    private SystemInfo() {
        super();
    }// end constructor

    /**
     * This method returns the private static final instance variable to enforce the Singleton pattern.
     *
     * @return An instance of this class.
     */
    public static SystemInfo getInstance() {
        log.debug("Returning the static instance of the SystemInfo class.");
        return INSTANCE;
    }// end getInstance

    /**
     * This method is called to use a JNDI lookup for accessing the {@link SystemBeanLocal}.
     *
     * @return An instance of the <code>SystemBeanLocal</code>.
     * @throws BaseException
     *         An exception if the <code>SystemBeanLocal</code> class cannot be accessed.
     */
    public SystemBeanLocal getBean() throws BaseException {
        log.debug("Entering SystemInfo.getBean().");
        log.debug("This method is called to use a JNDI lookup for accessing the SystemBeanLocal.");
        SystemBeanLocal bean;
        try{
            Context context = new InitialContext();
            bean = (SystemBeanLocal) context.lookup("java:global/ShamenEAR/ShamenEJB/SystemBean!gov.doc.isu.shamen.ejb.beans.view.SystemBeanLocal");
        }catch(NamingException e){
            log.error("An exception occurred while attempting to access the SystemBean from the ShamenServiceBean. Message is: " + e.getMessage());
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting SystemInfo.getBean. Return value is: " + String.valueOf(bean));
        return bean;
    }// end getBean

    /**
     * Loads a List of {@link SystemModel} for display.
     *
     * @return list of systems
     * @throws BaseException
     *         if an exception occurred
     */
    public List<SystemModel> getSystemList() throws BaseException {
        log.debug("Entering SystemInfo.getSystemList().");
        log.debug("This method is used to load a List of SystemModel for display.");
        List<SystemModel> list = new ArrayList<SystemModel>();
        try{
            list = ObjectConvertor.toSystemListBusinessFromObj(getBean().getSystemList());
        }catch(Exception e){
            log.error("An exception occurred while getting system list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch

        log.debug("Exiting SystemInfo.getSystemList systemList.size=" + (AppUtil.isEmpty(list) ? "empty" : list.size()));
        return list;
    } // end getSystemList

    /**
     * This method calculates ALL systems statistics using the already loaded statistics from each system.
     *
     * @param systemsList
     *        the list of System Models
     * @return system
     * @throws BaseException
     *         if an exception occurred
     */
    public SystemModel calculateAllSystemsStats(List<SystemModel> systemsList) throws BaseException {
        log.debug("Entering SystemInfo.calculateAllSystemsStats().");
        log.debug("This method calculates ALL systems statistics using the already loaded statistics from each system.");
        SystemStatsModel systemStats = new SystemStatsModel();
        StatModel webAppStats = new StatModel();
        StatModel batchAppStats = new StatModel();
        StatModel totalStats = new StatModel();
        if(systemsList != null){
            for(int i = 0, j = systemsList.size();i < j;i++){
                SystemStatsModel sysStatModel = systemsList.get(i).getStat();
                // perform calculations for all web apps
                webAppStats.setNiceApps(webAppStats.getNiceApps() + sysStatModel.getWebAppStats().getNiceApps());
                webAppStats.setNaughtyApps(webAppStats.getNaughtyApps() + sysStatModel.getWebAppStats().getNaughtyApps());
                webAppStats.setInfoApps(webAppStats.getInfoApps() + sysStatModel.getWebAppStats().getInfoApps());
                webAppStats.setSuspendedApps(webAppStats.getSuspendedApps() + sysStatModel.getWebAppStats().getSuspendedApps());
                webAppStats.setActiveApps(webAppStats.getActiveApps() + sysStatModel.getWebAppStats().getActiveApps());
                webAppStats.setUnreportableApps(webAppStats.getUnreportableApps() + sysStatModel.getWebAppStats().getUnreportableApps());
                webAppStats.setTotalApps(webAppStats.getTotalApps() + sysStatModel.getWebAppStats().getTotalApps());

                // perform calculations for all batch apps
                batchAppStats.setNiceApps(batchAppStats.getNiceApps() + sysStatModel.getBatchStats().getNiceApps());
                batchAppStats.setNaughtyApps(batchAppStats.getNaughtyApps() + sysStatModel.getBatchStats().getNaughtyApps());
                batchAppStats.setUnreportableApps(batchAppStats.getUnreportableApps() + sysStatModel.getBatchStats().getUnreportableApps());
                batchAppStats.setPendingApps(batchAppStats.getPendingApps() + sysStatModel.getBatchStats().getPendingApps());
                batchAppStats.setOffScheduleApps(batchAppStats.getOffScheduleApps() + sysStatModel.getBatchStats().getOffScheduleApps());
                batchAppStats.setRunningApps(batchAppStats.getRunningApps() + sysStatModel.getBatchStats().getRunningApps());
                batchAppStats.setInActiveApps(batchAppStats.getInActiveApps() + sysStatModel.getBatchStats().getInActiveApps());
                batchAppStats.setNoScheduleApps(batchAppStats.getNoScheduleApps() + sysStatModel.getBatchStats().getNoScheduleApps());

                batchAppStats.setTotalApps(batchAppStats.getTotalApps() + sysStatModel.getBatchStats().getTotalApps());

                // perform calculations for all apps
                totalStats.setNiceApps(totalStats.getNiceApps() + sysStatModel.getAllStats().getNiceApps());
                totalStats.setNaughtyApps(totalStats.getNaughtyApps() + sysStatModel.getAllStats().getNaughtyApps());
                totalStats.setUnreportableApps(totalStats.getUnreportableApps() + sysStatModel.getAllStats().getUnreportableApps());
                totalStats.setTotalApps(totalStats.getTotalApps() + sysStatModel.getAllStats().getTotalApps());
            }// end for
        }// end if
        systemStats.setWebAppStats(webAppStats);
        systemStats.setBatchStats(batchAppStats);
        systemStats.setAllStats(totalStats);
        SystemModel system = new SystemModel("All");
        system.setStat(systemStats);
        system.setSystemRefId(0L);
        log.debug("Exiting SystemInfo.calculateAllSystemsStats with: " + String.valueOf(system));
        return system;
    } // end calculateAllSystemsStats

    /**
     * Loads list of {@link SystemModel} with stats for display. This was separated for efficiency
     *
     * @return systemsList
     * @throws BaseException
     *         if an exception occurred
     */
    public List<SystemModel> getSystemListWithStats() throws BaseException {
        log.debug("Entering SystemInfo.getSystemListWithStats().");
        log.debug("This method is used to load a list of SystemModels with full statistics for display.");
        List<SystemModel> systemsList = getSystemList();
        List<SystemModel> finalList = new ArrayList<SystemModel>();

        if(systemsList != null){
            // Get all the batch jobs and collections with their stats.
            List<BatchAppModel> batchApps = BatchAppInfo.getInstance().getBatchList();
            batchApps.addAll(BatchAppInfo.getInstance().getBatchCollectionList());
            // Get list of applications for all systems
            List<ApplicationModel> webApps = ApplicationInfo.getInstance().getApplicationList();
            // Loop through all systems and calculate their stats
            for(int i = 0, j = systemsList.size();i < j;i++){
                SystemModel finalSystem = systemsList.get(i);
                finalSystem.setStat(calculateSystemStats(finalSystem, webApps, batchApps));
                finalList.add(finalSystem);
            }// end for
        }// end if

        log.debug("Exiting SystemInfo.getSystemListWithStats with: " + String.valueOf(finalList));
        return finalList;
    } // end getSystemListWithStats

    /**
     * This method calculates the stats for a particular system
     *
     * @param system
     *        the system model
     * @param webApps
     *        list of web apps
     * @param batchApps
     *        list of batch apps
     * @return SystemStatsModel
     * @throws BaseException
     *         if an exception occurred
     */
    public static SystemStatsModel calculateSystemStats(SystemModel system, List<ApplicationModel> webApps, List<BatchAppModel> batchApps) throws BaseException {
        log.debug("Entering SystemInfo.calculateSystemStats().");
        log.debug("This method calculates the stats for a particular system.");
        SystemStatsModel systemStats = new SystemStatsModel();

        // perform calculations for all web apps
        systemStats.setWebAppStats(setWebAppStats(system, webApps));

        // perform calculations for all batch apps(it's a little more complex)
        SystemStatsModel batchStats = setBatchAppStats(system, batchApps);
        systemStats.setBatchStats(batchStats.getBatchStats());
        systemStats.setBatchStates(batchStats.getBatchStates());
        system.setStat(systemStats);

        // perform calculations for all apps
        system.getStat().setAllStats(setAllStats(system));
        log.debug("Exiting SystemInfo.calculateSystemStats() with: " + String.valueOf(system.getStat()));
        return system.getStat();
    }// end calculateSystemStats

    /**
     * This method does statistical analysis for a particular system's web applications
     *
     * @param system
     *        current system
     * @param apps
     *        list of applications
     * @return StatModel
     */
    public static StatModel setWebAppStats(SystemModel system, List<ApplicationModel> apps) {
        log.debug("Entering SystemInfo.setWebAppStats().");
        log.debug("This method does statistical analysis for a particular system's web applications.");
        log.debug("Get WebApp statistics for: " + String.valueOf(system));
        StatModel appStats = new StatModel();
        int naughtyApps = 0;
        int totalApps = 0;
        int suspendedApps = 0;
        int infoApps = 0;
        int activeApps = 0;
        int unreportableApps = 0;
        if(apps != null && !apps.isEmpty()){
            for(int i = 0, j = apps.size();i < j;i++){
                // Make sure app is for current system
                if(apps.get(i).getSystem() != null && system.getSystemRefId().equals(apps.get(i).getSystem().getSystemRefId())){
                    // if unresponsive, it is naughty
                    if(ApplicationStatusUpdaterBeanLocal.STATUS_UNRESPONSIVE.equals(apps.get(i).getStatusInd())){
                        naughtyApps = naughtyApps + 1;
                    }else if(ApplicationStatusUpdaterBeanLocal.STATUS_SUSPENDED.equals(apps.get(i).getStatusInd())){
                        // suspended
                        suspendedApps = suspendedApps + 1;
                    }else if(ApplicationStatusUpdaterBeanLocal.STATUS_INFORMATION.equals(apps.get(i).getStatusInd())){
                        // information
                        infoApps = infoApps + 1;
                    }else if(ApplicationStatusUpdaterBeanLocal.STATUS_ACTIVE.equals(apps.get(i).getStatusInd())){
                        // active
                        activeApps = activeApps + 1;
                    }else if(ApplicationStatusUpdaterBeanLocal.STATUS_AWAITING_RESPONSE.equals(apps.get(i).getStatusInd())){
                        // unreportable
                        unreportableApps = unreportableApps + 1;
                    }
                    // add total
                    totalApps = totalApps + 1;
                }// end if
            }// end if
        }// end if
        appStats.setNaughtyApps(naughtyApps);
        appStats.setNiceApps(0);
        appStats.setTotalApps(totalApps);
        appStats.setActiveApps(activeApps);
        appStats.setInfoApps(infoApps);
        appStats.setSuspendedApps(suspendedApps);
        appStats.setUnreportableApps(unreportableApps);
        log.debug("Exiting SystemInfo.setWebAppStats().");
        return appStats;
    }// end setWebAppStats

    /**
     * This method does statistical analysis for a particular system's batch applications
     *
     * @param system
     *        current system
     * @param batchApps
     *        the list of batch apps
     * @return SystemStatsModel
     * @throws BaseException
     *         if an exception occurred
     */
    public static SystemStatsModel setBatchAppStats(SystemModel system, List<BatchAppModel> batchApps) throws BaseException {
        log.debug("Entering SystemInfo.setBatchAppStats().");
        log.debug("This method does statistical analysis for a particular system's batch applications.");
        log.debug("Get BatchApp statistics for: " + String.valueOf(system));
        SystemStatsModel sysStats = new SystemStatsModel();
        StatModel appStats = new StatModel();
        int naughtyApps = 0;
        int niceApps = 0;
        int unreportableApps = 0;
        int totalApps = 0;
        int offSchedule = 0;
        int running = 0;
        int pending = 0;
        int inactive = 0;
        int noSchedule = 0;
        Boolean appStateIsGood = false;

        if(batchApps != null && !batchApps.isEmpty()){
            for(int i = 0, j = batchApps.size();i < j;i++){
                // Make sure app is for current system
                if(batchApps.get(i).getSystem() != null && system.getSystemRefId().equals(batchApps.get(i).getSystem().getSystemRefId())){
                    // if unresponsive, it is naughty
                    if(batchApps.get(i).getLastRunStatus().contains(EJBConstants.BATCH_STATUS_UNSUCCESSFUL)){
                        naughtyApps = naughtyApps + 1;
                        totalApps = totalApps + 1;
                        appStateIsGood = false;
                    }else if(batchApps.get(i).getLastRunStatus().contains(EJBConstants.BATCH_STATUS_PENDING)){
                        pending = pending + 1;
                        totalApps = totalApps + 1;
                        appStateIsGood = true;
                    }else if(batchApps.get(i).getLastRunStatus().contains(EJBConstants.BATCH_STATUS_RUNNING)){
                        running = running + 1;
                        totalApps = totalApps + 1;
                        appStateIsGood = true;
                    }else if(batchApps.get(i).getLastRunStatus().contains(EJBConstants.BATCH_STATUS_OFF_SCHEDULE)){
                        offSchedule = offSchedule + 1;
                        totalApps = totalApps + 1;
                        appStateIsGood = false;
                    }else if(batchApps.get(i).getLastRunStatus().contains(EJBConstants.BATCH_STATUS_SUCCESSFUL)){
                        niceApps = niceApps + 1;
                        totalApps = totalApps + 1;
                        appStateIsGood = true;
                    }else if(batchApps.get(i).getLastRunStatus().contains(EJBConstants.BATCH_STATUS_INACTIVE)){
                        inactive = inactive + 1;
                        appStateIsGood = true;
                        totalApps = totalApps + 1;
                    }else if(batchApps.get(i).getLastRunStatus().contains(EJBConstants.BATCH_STATUS_NO_SCHEDULE)){
                        noSchedule = noSchedule + 1;
                        appStateIsGood = true;
                    }else{
                        unreportableApps = unreportableApps + 1;
                        totalApps = totalApps + 1;
                    }// end if-else
                     // add total

                    // hold batch app state
                    sysStats.getBatchStates().put(batchApps.get(i).getBatchRefId(), appStateIsGood);
                }// end if
            }// end if
        }// end if
        appStats.setNaughtyApps(naughtyApps);
        appStats.setNiceApps(niceApps);
        appStats.setTotalApps(totalApps);
        appStats.setUnreportableApps(unreportableApps);
        appStats.setPendingApps(pending);
        appStats.setRunningApps(running);
        appStats.setOffScheduleApps(offSchedule);
        appStats.setInActiveApps(inactive);
        appStats.setNoScheduleApps(noSchedule);
        sysStats.setBatchStats(appStats);
        log.debug("Exiting SystemInfo.setBatchAppStats().");
        return sysStats;
    }// end setBatchAppStats

    /**
     * This method does statistical analysis for all of a particular system's applications
     *
     * @param system
     *        current system
     * @return StatModel
     */
    public static StatModel setAllStats(SystemModel system) {
        log.debug("Entering SystemInfo.setBatchAppStats().");
        log.debug("This method does statistical analysis for all of a particular system's applications.");
        log.debug("Get Total statistics for: " + String.valueOf(system));
        StatModel appStats = new StatModel();
        SystemStatsModel sysStat = system.getStat();
        // count naughties
        int naughtyApps = sysStat.getBatchStats().getNaughtyApps() + sysStat.getWebAppStats().getNaughtyApps();
        // count nices
        int niceApps = sysStat.getBatchStats().getNiceApps() + sysStat.getWebAppStats().getNiceApps();
        // count Active(web apps)
        int activeApps = sysStat.getBatchStats().getActiveApps() + sysStat.getWebAppStats().getActiveApps();
        // count Infos(web apps)
        int infoApps = sysStat.getBatchStats().getInfoApps() + sysStat.getWebAppStats().getInfoApps();
        // count Suspended(web Apps)
        int suspendedApps = sysStat.getBatchStats().getSuspendedApps() + sysStat.getWebAppStats().getSuspendedApps();
        // count Pending(batch Apps)
        int pendingApps = sysStat.getBatchStats().getPendingApps() + sysStat.getWebAppStats().getPendingApps();
        // count running(batch Apps)
        int runningApps = sysStat.getBatchStats().getRunningApps() + sysStat.getWebAppStats().getRunningApps();
        // count off schedule(batch Apps)
        int offScheduleApps = sysStat.getBatchStats().getOffScheduleApps() + sysStat.getWebAppStats().getOffScheduleApps();
        // count Unreportable
        int unreportableApps = sysStat.getBatchStats().getUnreportableApps() + sysStat.getWebAppStats().getUnreportableApps();
        // count inactives
        int inactiveApps = sysStat.getBatchStats().getInActiveApps() + sysStat.getWebAppStats().getInActiveApps();
        // count noSchedule
        int noScheduleApps = sysStat.getBatchStats().getNoScheduleApps() + sysStat.getWebAppStats().getNoScheduleApps();
        // count totals
        int totalApps = sysStat.getBatchStats().getTotalApps() + sysStat.getWebAppStats().getTotalApps();
        appStats.setNaughtyApps(naughtyApps + offScheduleApps);
        appStats.setNiceApps(niceApps + infoApps + suspendedApps + activeApps + unreportableApps + pendingApps + runningApps + inactiveApps);
        appStats.setTotalApps(totalApps);
        appStats.setActiveApps(0);
        appStats.setInfoApps(0);
        appStats.setSuspendedApps(0);
        appStats.setUnreportableApps(0);
        appStats.setOffScheduleApps(0);
        appStats.setPendingApps(0);
        appStats.setRunningApps(0);
        appStats.setNoScheduleApps(0);
        appStats.setInActiveApps(0);
        log.debug("Exiting SystemInfo.setBatchAppStats().");
        return appStats;
    }// end setAllStats

    /**
     * Loads a List of {@link CodeModel} for display.
     *
     * @return list of systems
     * @throws BaseException
     *         if an exception occurred
     */
    public List<CodeModel> getSystemListAsCodeModel() throws BaseException {
        log.debug("Entering SystemInfo.getSystemListAsCodeModel().");
        log.debug("This method is used to load a List of systems as CodeModel for select box.");
        List<CodeModel> list = null;
        try{
            list = ObjectConvertor.convertSystemsToCodeModel(getSystemList());
        }catch(Exception e){
            log.error("An exception occurred while getting application list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting SystemInfo.getSystemListAsCodeModel list.size=" + (AppUtil.isEmpty(list) ? "empty" : list.size()));
        return list;
    } // end getSystemListAsCodeModel

    /**
     * Loads a {@link SystemModel} for display.
     *
     * @param refId
     *        the ref id of the system
     * @return system
     * @throws BaseException
     *         if an exception occurred
     */
    public SystemModel getSystem(Long refId) throws BaseException {
        log.debug("Entering SystemInfo.getSystem(). Incoming parameter is: " + String.valueOf(refId));
        log.debug("This method is used to load an SystemModel for edit.");
        SystemEntity entity = null;
        SystemModel model = new SystemModel();
        try{
            entity = (SystemEntity) getBean().findSystemByRefId(refId);
        }catch(Exception e){
            log.error("An exception occurred while getting system entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        try{
            if(null != entity){
                model = ObjectConvertor.toSystemBusiness(entity);
                model.setBatchApps(ObjectConvertor.toBatchAppListBusiness(entity.getBatchApps()));
                model.setApplications(ObjectConvertor.convertForApplicationList(entity.getApps()));
                model.setPointOfContact(ObjectConvertor.toAuthorizedUserBusiness(entity.getPointOfContact()));
                // Since special care needs to be afforded batch applications, calculate them here
                if(model.getBatchApps() != null && !model.getBatchApps().isEmpty()){
                    // Get all the batch jobs and collections with their stats.
                    List<BatchAppModel> batchApps = BatchAppInfo.getInstance().getBatchList();
                    batchApps.addAll(BatchAppInfo.getInstance().getBatchCollectionList());
                    model.setStat(calculateSystemStats(model, ApplicationInfo.getInstance().getApplicationList(), batchApps));
                    // loop through all the batch states and set them according to the stats
                    for(int i = 0, j = model.getBatchApps().size();i < j;i++){
                        model.getBatchApps().get(i).setAppState(model.getStat().getBatchStates().get(model.getBatchApps().get(i).getBatchRefId()));
                    }// end for
                }// end if
            }else{
                log.warn("The system entity in the database is null or empty. An new instatnce of system model will be returned.");
            }// end if/else
        }catch(Exception e){
            log.error("An exception occurred while converting system model. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting SystemInfo.getSystem system=" + String.valueOf(model));
        return model;
    }// end getSystem

    /**
     * Checks if is unassigned user.
     *
     * @param userRefId
     *        the user ref id
     * @return true, if is unassigned user
     * @throws BaseException
     *         the base exception
     */
    public boolean isUnassignedUser(Long userRefId) throws BaseException {
        log.debug("Entering isUnassignedUser");
        log.debug("Checks if is unassigned user.");
        log.debug("Entry parameters are: userRefId=" + String.valueOf(userRefId));
        boolean result = false;
        try{
            if(getBean().findSystemByRefId(1L).getPointOfContact().getUserRefId() == userRefId){
                result = true;
            }
        }catch(Exception e){
            log.error("An exception occurred while checking if the user is the assigned point of contact for the Unassigned Sytem entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting isUnassignedUser");
        return result;

    }

    /**
     * Saves new system
     *
     * @param model
     *        the system model to save
     * @throws BaseException
     *         if an exception occurred
     */
    public void saveSystem(SystemModel model) throws BaseException {
        log.debug("Entering SystemInfo.saveSystem(). Incoming parameter is: " + (model == null ? "null" : model.toString()));
        log.debug("This method is used to add an SystemModel.");
        model.setDeleteIndicator("N");
        SystemEntity entity = ObjectConvertor.toSystemPersist(model);
        try{
            getBean().create(entity);
        }catch(Exception e){
            log.error("An exception occurred while saving an system entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting SystemInfo.saveSystem");
    } // end saveSystem

    /**
     * Updates a existing system record
     *
     * @param model
     *        the system model to save
     * @throws BaseException
     *         if an exception occurred
     */
    public void updateSystem(SystemModel model) throws BaseException {
        log.debug("Entering SystemInfo.updateSystem(). Incoming parameter is: " + String.valueOf(model));
        log.debug("This method is used to Update an existing System record.");
        SystemEntity entity = ObjectConvertor.toSystemPersist(model);
        try{
            getBean().update(entity);
        }catch(Exception e){
            log.error("An exception occurred while updating an system entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting SystemInfo.updateSystem");
    } // end updateSystem

    /**
     * Delete a existing system record
     *
     * @param systemRefId
     *        the refId of system to delete.
     * @param updateUserRefId
     *        the refid of user deleting record
     * @throws BaseException
     *         if an exception occurred
     */
    public void deleteSystem(Long systemRefId, Long updateUserRefId) throws BaseException {
        log.debug("Entering SystemInfo.deleteSystem(). Incoming parameters are: systemRefId=" + String.valueOf(systemRefId) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        log.debug("This method is used to delete an System record.");
        try{
            getBean().deleteSystem(systemRefId, updateUserRefId);
            // getSystemStatusBean().removeSystemFromMap(systemRefId);
        }catch(Exception e){
            log.error("An exception occurred while deleting an system entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting SystemInfo.deleteSystem");
    } // end deleteSystem

    /**
     * This method is used to load a List of systems and their children for display.
     *
     * @return JSONArray
     * @throws BaseException
     *         if an exception occurred
     */
    public JSONArray getSystemsListForChart() throws BaseException {
        log.debug("Entering SystemInfo.getSystemsListForChart().");
        log.debug("This method is used to load a List of systems and their children for display.");
        List<SystemModel> list = new ArrayList<SystemModel>();
        List<SystemEntity> entityList = new ArrayList<SystemEntity>();
        JSONArray array = null;
        SystemEntity entity = null;
        try{
            entityList = getBean().getSystemListWithChildren();
            for(int i = 0, j = entityList.size();i < j;i++){
                entity = entityList.get(i);
                if(null != entity){
                    SystemModel model = ObjectConvertor.toSystemBusiness(entity);
                    model.setBatchApps(ObjectConvertor.toBatchAppListBusiness(entity.getBatchApps()));
                    model.setApplications(ObjectConvertor.convertForApplicationList(entity.getApps()));
                    list.add(model);
                }
            }
        }catch(Exception e){
            log.error("An exception occurred while retrieving system list. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        if(null != list && !list.isEmpty()){
            array = ObjectConvertor.convertToSystemData(list);
        }else{
            log.warn("The system list in the database is null or empty. An empty list of system models will be returned.");
        }// end if/else
        log.debug("Exiting SystemInfo.getSystemsListForChart systemList.size=" + (AppUtil.isEmpty(array) ? "empty" : array.size()));
        return array;
    } // end getSystemsListForChart

    /**
     * This method is used to load a system and its children for display.
     *
     * @param model
     *        the SystemModel
     * @return JSONArray
     * @throws BaseException
     *         if an exception occurred
     */
    public JSONArray getSystemChart(SystemModel model) throws BaseException {
        log.debug("Entering SystemInfo.getSystemChart().");
        log.debug("This method is used to load a system chart and its children for display.");
        JSONArray array = null;
        if(null != model){
            array = ObjectConvertor.convertToSystemData(model);
        }// end if
        log.debug("Exiting SystemInfo.getSystemChart array.size=" + (AppUtil.isEmpty(array) ? "empty" : array.size()));
        return array;
    } // end getSystemChart

    /**
     * This method is used to replace the use of a compiler-generated <code>readResolve()</code> method which is called from the <code>readObject()</code> method within the serialization machinery.
     * <p>
     * The <code>readObject()</code> method will create a new instance of the serialized class and call the <code>readResolve()</code> thereafter. By providing this method, the singleton pattern will not be violated with an additional instance of the class since this method returns the singleton instance.
     *
     * @return The singleton instance of this class
     * @throws ObjectStreamException
     *         An exception if the serialization machinery fails on this method.
     */
    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }// end readResolve

} // end SystemInfo
