/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.jobs.dto;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Before;
import org.junit.Test;

import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;

public class JobDtoTest {

    final static JobStatusRecord jsr1 = new JobStatusRecord();
    final static WfResult wfRes1 = new WfResult();
    final static List<WfResult> wfList1 = new ArrayList<WfResult>();
    final static JobStatusRecord jsr2 = new JobStatusRecord();
    final static WfResult wfRes2 = new WfResult();
    final static List<WfResult> wfList2 = new ArrayList<WfResult>();
    static JobDto jobDto1 = null;
    static JobDto jobDto2 = null;

    @Before
    public void setup() {

        jsr1.setJobId(UUID.randomUUID());
        jsr1.setUserId("userId1000");
        jsr1.setCommandId("Command123");
        jsr1.setInsertDate(new Date());
        jsr1.setGlobalStatus(JobGlobalStatusEnum.PENDING);

        //set some jsr attribute
        wfRes1.setWfWakeId(UUID.nameUUIDFromBytes((jsr1.getJobId().toString() + "1").getBytes()));
        wfRes1.setTimestamp(jsr1.getInsertDate().getTime() + (new Date()).getTime());
        wfRes1.setJobId(jsr1.getJobId());
        jsr1.setNumOfTotWf(1);

        wfList1.add(wfRes1);

        jsr2.setJobId(UUID.randomUUID());
        jsr2.setUserId("userId10002");
        jsr2.setCommandId("Command1232");
        jsr2.setInsertDate(new Date(jsr1.getInsertDate().getTime() + 10));
        jsr2.setGlobalStatus(JobGlobalStatusEnum.PENDING);

        //set some jsr attribute
        wfRes2.setWfWakeId(UUID.nameUUIDFromBytes((jsr2.getJobId().toString() + "1").getBytes()));
        wfRes2.setTimestamp(jsr2.getInsertDate().getTime() + (new Date()).getTime());
        wfRes2.setJobId(jsr2.getJobId());
        jsr2.setNumOfTotWf(1);

        wfList2.add(wfRes2);
        jobDto1 = new JobDto(jsr1, wfList1);
        jobDto2 = new JobDto(jsr2, wfList2);

    }

    @Test
    public void testSorting() {

        final List<JobStatusRecord> jsrList = new ArrayList<JobStatusRecord>();
        jsrList.add(jsr2);
        jsrList.add(jsr1);

        Collections.sort(jsrList);
        assertTrue("Invalid sorting in JobStatusRecord", jsrList.get(0).getInsertDate().getTime() < jsrList.get(1).getInsertDate().getTime());

        final List<JobDto> jobdtoList = new ArrayList<JobDto>();
        jobdtoList.add(jobDto2);
        jobdtoList.add(jobDto1);

        Collections.sort(jobdtoList);
        assertTrue("Invalid sorting in JobDto", jobdtoList.get(0).getInsertDate().getTime() < jobdtoList.get(1).getInsertDate().getTime());
        assertTrue("Invalid JobStatusRecord in JobDto", jobdtoList.get(0).getJobId() == jsrList.get(0).getJobId());
    }

    @Test
    public void testDataconsistency() {

        assertTrue("Invalid job UUID", jobDto1.getJobId().equals(jsr1.getJobId()));
        assertTrue("Invalid userId", jobDto1.getUserId().equals(jsr1.getUserId()));
        assertTrue("Invalid CommandId", jobDto1.getCommandId().equals(jsr1.getCommandId()));
        assertTrue("Invalid InsertDate", jobDto1.getInsertDate().equals(jsr1.getInsertDate()));
        assertTrue("Invalid GlobalStatus", jobDto1.getGlobalStatus().equals(jsr1.getGlobalStatus()));
        assertTrue("Invalid NumOfTotWf", jobDto1.getNumOfTotWf() == jsr1.getNumOfTotWf());
        assertTrue("Invalid LastStartedWfId", jobDto1.getLastStartedWfId() == jsr1.getLastStartedWfId());
    }

    @Test
    public void testGlobalStatusPending() {

        final JobStatusRecord record = jsr2;
        record.setGlobalStatus(JobGlobalStatusEnum.PENDING);
        final JobDto jobDto = new JobDto(record, wfList2);

        assertNull("JobStatusRecord startDate must be null", record.getStartDate());
        assertNull("JobStatusRecord endDate must be null", record.getEndDate());
        assertNull("JobDto startDate must be null", jobDto.getStartDate());
        assertNull("JobDto endDate must be null", jobDto.getEndDate());

    }

    @Test
    public void testGlobalStatusRunning() {

        final JobStatusRecord record = jsr2;
        record.setGlobalStatus(JobGlobalStatusEnum.PENDING);
        record.setGlobalStatus(JobGlobalStatusEnum.RUNNING);
        final JobDto jobDto = new JobDto(record, wfList2);

        assertNotNull("JobStatusRecord startDate must not be null", record.getStartDate());
        assertNull("JobStatusRecord endDate must be null", record.getEndDate());
        assertNotNull("JobDto startDate must not be null", jobDto.getStartDate());
        assertNull("JobDto endDate must be null", jobDto.getEndDate());

    }

    @Test
    public void testGlobalStatusCompleted() {

        final JobStatusRecord record = jsr2;
        record.setGlobalStatus(JobGlobalStatusEnum.PENDING);
        record.setGlobalStatus(JobGlobalStatusEnum.RUNNING);
        record.setGlobalStatus(JobGlobalStatusEnum.COMPLETED);
        final JobDto jobDto = new JobDto(record, wfList2);

        assertNotNull("JobStatusRecord startDate must not be null", record.getStartDate());
        assertNotNull("JobStatusRecord endDate must not be null", record.getEndDate());
        assertNotNull("JobDto startDate must not be null", jobDto.getStartDate());
        assertNotNull("JobDto endDate must not be null", jobDto.getEndDate());

    }

}
