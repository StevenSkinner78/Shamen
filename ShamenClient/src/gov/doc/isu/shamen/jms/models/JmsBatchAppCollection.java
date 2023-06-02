/**
 * @(#)JmsBatchAppCollection.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED
 *                                CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You
 *                                acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.jms.models;

import java.util.List;

import gov.doc.isu.shamen.interfaces.Scheduleable;

/**
 * Model object to hold the Batch Application collection record. This is essentially a collection of batch jobs that run as a unit.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, July 20, 2016
 */
public class JmsBatchAppCollection extends Scheduleable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long mainBatchAppRefId;// This is the overall ref id
    private List<JmsBatchApp> batchApps;

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("JmsBatchAppCollection [mainBatchAppRefId=");
        builder.append(mainBatchAppRefId);
        builder.append(", batchApps=");
        builder.append(batchApps);
        builder.append("]");
        builder.append(super.toString());
        return builder.toString();
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Jul 20, 2016
     * @return the batchApps
     */
    public List<JmsBatchApp> getBatchApps() {
        return batchApps;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Jul 20, 2016
     * @param batchApps
     *        the batchApps to set
     */
    public void setBatchApps(List<JmsBatchApp> batchApps) {
        this.batchApps = batchApps;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Dec 8, 2016
     * @return the mainBatchApp
     */
    public Long getMainBatchAppRefId() {
        return mainBatchAppRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Dec 8, 2016
     * @param mainBatchApp
     *        the mainBatchApp to set
     */
    public void setMainBatchApp(Long mainBatchAppRefId) {
        this.mainBatchAppRefId = mainBatchAppRefId;
    }
}
