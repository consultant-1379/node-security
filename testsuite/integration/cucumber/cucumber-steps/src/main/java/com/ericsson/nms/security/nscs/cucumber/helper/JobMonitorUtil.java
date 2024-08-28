/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cucumber.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.cucumber.helper.laad.LaadTestDataConstants;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;

/**
 * This class is used to monitor work flow job status.
 * @author xnagsow
 *
 */
public class JobMonitorUtil {

    @Inject
    private EServiceProducer eServiceProducer;

    private static final Logger log = LoggerFactory.getLogger(JobMonitorUtil.class);

    public String getJobStatus(final Command getJobStatuscommand) {
        final String jobResponseMessage = eServiceProducer.getCommandHandler().execute(getJobStatuscommand).getResponseDto().toString();
        log.info("Job status: {}", jobResponseMessage);
        return jobResponseMessage;
    }

    public String getJobCommandString(final String response) {
        String c = null;
        final Pattern p = Pattern.compile(LaadTestDataConstants.JOB_ID_PATTERN);
        final Matcher m = p.matcher(response);
        if (m.find()) {
            c = m.group(0);
        } else {
            log.error("Job ID not found");
        }
        if (c != null) {
            return "job get -j " + c;
        }
        return c;
    }

    public boolean isWorkflowStarted(final Command getJobStatuscommand) {
        int i = 1;
        do {
            final String jobResponseMessage = eServiceProducer.getCommandHandler().execute(getJobStatuscommand).getResponseDto().toString();
            if (!jobResponseMessage.contains("PENDING")) {
                return true;
            } else {
                sleep(2000);
            }
            i++;
        } while (i <= 40);
        return false;
    }

    public void sleep(final long timeInMillis) {
        try {
            log.info("Sleeping for {} milliseconds", timeInMillis);
            Thread.sleep(timeInMillis);
            log.info("Thread paused for {} milliseconds", timeInMillis);
        } catch (final InterruptedException e) {
            log.error("Caught InterruptedException:  {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
