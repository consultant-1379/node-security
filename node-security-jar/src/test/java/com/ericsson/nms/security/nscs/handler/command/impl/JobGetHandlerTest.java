package com.ericsson.nms.security.nscs.handler.command.impl;

import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.types.JobGetCommand;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsGetJobResponseBuilder;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.jobs.dto.JobDto;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;

@RunWith(MockitoJUnitRunner.class)
public class JobGetHandlerTest {

    @InjectMocks
    JobGetHandler beanUnderTest;

    @InjectMocks
    NscsGetJobResponseBuilder responseBuilder;

    @Mock
    JobGetCommand command;

    @Mock
    CommandContext context;

    @Mock
    NscsJobCacheHandler cacheHandler;

    @Spy
    Logger logger = LoggerFactory.getLogger(JobGetHandler.class);

    List<JobDto> jobs;

    JobDto jobDto;

    JobStatusRecord jobRecord;
    DateFormat df;

    @Before
    public void setup() {

        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jobs = new ArrayList<JobDto>();

        final Calendar c = Calendar.getInstance();
        c.set(2016, 9, 6);

        try {

            final String uuid = "11ed9ac1-49ce-40dc-bab0-3f347092da6c";

            jobRecord = new JobStatusRecord();
            jobRecord.setUserId("administrator");
            jobRecord.setCommandId(NscsCommandType.GET_JOB.name());
            jobRecord.setStartDate(c.getTime());
            jobRecord.setJobId(UUID.fromString(uuid));
            jobRecord.setGlobalStatus(JobGlobalStatusEnum.RUNNING);

            jobDto = new JobDto(jobRecord, new ArrayList<WfResult>());
            jobs.add(jobDto);

        } catch (final Exception e) {
            logger.error(e.getMessage());
        }

    }

    @Test
    public void process() {
        Mockito.when(command.getValueString(Mockito.anyString())).thenReturn("*");
        Mockito.when(cacheHandler.getAllJobs(Mockito.anyList())).thenReturn(jobs);
        final NscsCommandResponse response = beanUnderTest.process(command, context);
        assertTrue("Should be of name value pair response type", response.isNameMultipleValueResponseType());

        final NscsNameMultipleValueCommandResponse commandResponse = ((NscsNameMultipleValueCommandResponse) response);
        final Iterator<NscsNameMultipleValueCommandResponse.Entry> iterator = commandResponse.iterator();
        final NscsNameMultipleValueCommandResponse.Entry row = iterator.next();
        assertTrue(row.getName().equals("Job Id"));
        final String[] columns = row.getValues();
        final List<String> columnsList = Arrays.asList(columns);
        assertTrue(columnsList.toString().equals(
                "[Command Id, Job User, Job Status, Job Start Date, Job End Date, Node Name, Workflow Status, Workflow Start Date, Workflow Duration, Workflow Details, Workflow Result]"));

        final NscsNameMultipleValueCommandResponse.Entry content = iterator.next();
        assertTrue(content.getName().equals("11ed9ac1-49ce-40dc-bab0-3f347092da6c"));

        final String[] values = content.getValues();
        final String startDate = values[3];

        final List valuesList = Arrays.asList(values);

        assertTrue(valuesList.toString().equals("[GET_JOB, administrator, RUNNING, " + startDate + ", N/A, N/A, N/A, N/A, N/A, N/A, N/A]"));
    }

    @Test
    public void process1() {
        Mockito.when(command.getJobList()).thenReturn(Arrays.asList("11ed9ac1-49ce-40dc-bab0-3f347092da6c", "21ed9ac1-49ce-40dc-bab0-3f347092da6e"));
        Mockito.when(command.getValueString(Mockito.anyString())).thenReturn("11ed9ac1-49ce-40dc-bab0-3f347092da6c");
        Mockito.when(cacheHandler.getJob((UUID) Mockito.anyObject(), Mockito.anyList())).thenReturn(jobDto);
        final NscsCommandResponse response = beanUnderTest.process(command, context);
        assertTrue("Should be of name value pair response type", response.isNameMultipleValueResponseType());

        final NscsNameMultipleValueCommandResponse commandResponse = ((NscsNameMultipleValueCommandResponse) response);
        final Iterator<NscsNameMultipleValueCommandResponse.Entry> iterator = commandResponse.iterator();
        final NscsNameMultipleValueCommandResponse.Entry row = iterator.next();
        assertTrue(row.getName().equals("Job Id"));
        final String[] columns = row.getValues();
        final List<String> columnsList = Arrays.asList(columns);
        assertTrue(columnsList.toString().equals(
                "[Command Id, Job User, Job Status, Job Start Date, Job End Date, Node Name, Workflow Status, Workflow Start Date, Workflow Duration, Workflow Details, Workflow Result]"));

        final NscsNameMultipleValueCommandResponse.Entry content = iterator.next();
        assertTrue(content.getName().equals("11ed9ac1-49ce-40dc-bab0-3f347092da6c"));

        final String[] values = content.getValues();
        final String startDate = values[3];
        final List valuesList = Arrays.asList(values);
        assertTrue(valuesList.toString().equals("[GET_JOB, administrator, RUNNING, " + startDate + ", N/A, N/A, N/A, N/A, N/A, N/A, N/A]"));

    }

}
