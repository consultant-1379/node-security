package com.ericsson.nms.security.nscs.handler.command.utility;

import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.enums.WfStatusEnum;
import com.ericsson.oss.services.jobs.dto.JobDto;

@RunWith(MockitoJUnitRunner.class)
public class NscsGetJobResponseBuilderTest {

    @InjectMocks
    NscsGetJobResponseBuilder beanUnderTest;

    @Spy
    Logger logger = LoggerFactory.getLogger(getClass());

    WfResult result;
    JobStatusRecord jobInfo;
    JobDto jobDto;
    DateFormat df;

    @Before
    public void before() {

        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        result = new WfResult();
        result.setWfId("1234");
        result.setNodeName("LTE03ERBS00003");
        result.setStatus(WfStatusEnum.RUNNING);
        result.setMessage("test");
        result.setWfWakeId(UUID.randomUUID());
    }

    @Test
    public void formatWfRowNA() {

        final Map<String, String> returnedMap = beanUnderTest.formatWfRow(new WfResult());
        boolean allNA = true;

        for (final Map.Entry<String, String> e : returnedMap.entrySet()) {
            if (e.getValue() != "N/A") {
                allNA = false;
                break;
            }
        }
        assertTrue(allNA);
    }

    @Test
    public void formatWfRow() {
        final Map<String, String> returnedMap = beanUnderTest.formatWfRow(result);
        final Date startDate = result.getStartDate();
        final String returnedMapStr = returnedMap.toString();

        assertTrue(returnedMapStr.contains("Node Name=LTE03ERBS00003"));
        assertTrue(returnedMapStr.contains("Workflow Status=RUNNING"));
        assertTrue(returnedMapStr.contains("Workflow Start Date=" + df.format(startDate)));
        assertTrue(returnedMapStr.contains("Workflow Duration=N/A"));
        assertTrue(returnedMapStr.contains("Workflow Details=" + result.getMessage()));
        assertTrue(returnedMapStr.contains("Workflow Result=N/A"));
    }

    @Test
    public void formatJobRecordNA() {

        jobInfo = new JobStatusRecord();
        jobInfo.setUserId("administrator");
        jobInfo.setCommandId(NscsCommandType.GET_JOB.name());
        jobInfo.setGlobalStatus(JobGlobalStatusEnum.RUNNING);

        final List<WfResult> wfResultList = new ArrayList<WfResult>();
        wfResultList.add(result);
        jobDto = new JobDto(jobInfo, wfResultList);

        final Date startDate = jobInfo.getStartDate();
        final Date wfStartDate = result.getStartDate();
        final String[] returnedArr = beanUnderTest.formatJobRow(jobDto, wfResultList.get(0));
        final List<String> list = Arrays.asList(returnedArr);
        assertTrue(list.toString().equals("[GET_JOB, administrator, RUNNING, " + df.format(startDate) + ", N/A, LTE03ERBS00003, RUNNING, "
                + df.format(wfStartDate) + ", N/A, " + result.getMessage() + ", N/A]"));
    }

    @Test
    public void formatRunningJobRecord() {

        jobInfo = new JobStatusRecord();
        jobInfo.setUserId("administrator");
        jobInfo.setCommandId(NscsCommandType.GET_JOB.name());
        jobInfo.setGlobalStatus(JobGlobalStatusEnum.RUNNING);

        final List<WfResult> wfResultList = new ArrayList<WfResult>();
        wfResultList.add(result);
        jobDto = new JobDto(jobInfo, wfResultList);

        final String[] returnedArr = beanUnderTest.formatJobRow(jobDto, wfResultList.get(0));
        final Date jobStartDate = jobInfo.getStartDate();
        final Date wfStartDate = result.getStartDate();
        final List<String> list = Arrays.asList(returnedArr);

        assertTrue(list.toString().equals("[GET_JOB, administrator, RUNNING, " + df.format(jobStartDate) + ", N/A, LTE03ERBS00003, RUNNING, "
                + df.format(wfStartDate) + ", N/A, " + result.getMessage() + ", N/A]"));
    }

    @Test
    public void formatCompletedJobRecord() {

        jobInfo = new JobStatusRecord();
        jobInfo.setUserId("administrator");
        jobInfo.setCommandId(NscsCommandType.GET_JOB.name());
        // set to RUNNING, this sets also jobStartDate
        jobInfo.setGlobalStatus(JobGlobalStatusEnum.RUNNING);
        // set to COMPLETED, this sets also jobEndDate
        jobInfo.setGlobalStatus(JobGlobalStatusEnum.COMPLETED);

        // set to RUNNING, this sets also wfStartDate
        result.setStatus(WfStatusEnum.RUNNING);
        // set to SUCCESS, this sets also wfEndDate
        result.setStatus(WfStatusEnum.SUCCESS);
        final List<WfResult> wfResultList = new ArrayList<WfResult>();
        wfResultList.add(result);
        jobDto = new JobDto(jobInfo, wfResultList);

        final String[] returnedArr = beanUnderTest.formatJobRow(jobDto, wfResultList.get(0));
        final Date jobStartDate = jobInfo.getStartDate();
        final Date jobEndDate = jobInfo.getEndDate();
        final Date wfStartDate = result.getStartDate();
        final Date wfEndDate = result.getEndDate();
        final long millis = wfEndDate.getTime() - wfStartDate.getTime();
        Date date = new Date(millis);
        final DateFormat dfDuration = new SimpleDateFormat("HH:mm:ss.SSS");
        dfDuration.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String durationStr = dfDuration.format(date);
        final List<String> list = Arrays.asList(returnedArr);
        assertTrue(list.toString().equals("[GET_JOB, administrator, COMPLETED, " + df.format(jobStartDate) + ", " + df.format(jobEndDate)
                + ", LTE03ERBS00003, SUCCESS, " + df.format(wfStartDate) + ", " + durationStr + ", " + result.getMessage() + ", Not Applicable]"));
    }

    @Test
    public void formatCompletedWithErrorsJobRecord() {

        jobInfo = new JobStatusRecord();
        jobInfo.setUserId("administrator");
        jobInfo.setCommandId(NscsCommandType.GET_JOB.name());
        // set to RUNNING, this sets also jobStartDate
        jobInfo.setGlobalStatus(JobGlobalStatusEnum.RUNNING);
        // set to COMPLETED, this sets also jobEndDate
        jobInfo.setGlobalStatus(JobGlobalStatusEnum.COMPLETED);

        // set to RUNNING, this sets also wfStartDate
        result.setStatus(WfStatusEnum.RUNNING);
        // set to ERROR, this sets also wfEndDate
        result.setStatus(WfStatusEnum.ERROR);
        final List<WfResult> wfResultList = new ArrayList<WfResult>();
        wfResultList.add(result);
        jobDto = new JobDto(jobInfo, wfResultList);

        final String[] returnedArr = beanUnderTest.formatJobRow(jobDto, wfResultList.get(0));
        final Date jobStartDate = jobInfo.getStartDate();
        final Date jobEndDate = jobInfo.getEndDate();
        final Date wfStartDate = result.getStartDate();
        final Date wfEndDate = result.getEndDate();
        final long millis = wfEndDate.getTime() - wfStartDate.getTime();
        Date date = new Date(millis);
        final DateFormat dfDuration = new SimpleDateFormat("HH:mm:ss.SSS");
        dfDuration.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String durationStr = dfDuration.format(date);
        final List<String> list = Arrays.asList(returnedArr);
        assertTrue(list.toString().equals("[GET_JOB, administrator, COMPLETED, " + df.format(jobStartDate) + ", " + df.format(jobEndDate)
                + ", LTE03ERBS00003, ERROR, " + df.format(wfStartDate) + ", " + durationStr + ", " + result.getMessage() + ", N/A]"));
    }

    @Test
    public void testDuration() {
        result.setStatus(WfStatusEnum.SUCCESS);
        final Map<String, String> returnedMap = beanUnderTest.formatWfRow(result);
        assertTrue(returnedMap.get("Workflow Duration") != "N/A");
    }
}
