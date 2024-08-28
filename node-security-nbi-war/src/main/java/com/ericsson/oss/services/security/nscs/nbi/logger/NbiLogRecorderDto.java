/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2024
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi.logger;

import java.io.Serializable;
import java.util.Date;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class NbiLogRecorderDto extends RestLogRecorder implements Serializable {

    private static final long serialVersionUID = 5928758224858725938L;

    private Date startDate;

    public NbiLogRecorderDto() {
        super();
        startDate = new Date();
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return new Date(startDate.getTime());
    }

}
