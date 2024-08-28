/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.ejb.resources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.laad.service.ResourcesBean;
import com.ericsson.oss.itpf.sdk.resources.Resource;

@RunWith(MockitoJUnitRunner.class)
public class FileResourcesBeanTest {
	
	@Mock
	private ResourcesBean mockResourcesBean;
	
	@Mock
	private Resource resource;
	
	@Spy
    private Logger logger = LoggerFactory.getLogger(FileResourcesBean.class);
	
	@InjectMocks
    private FileResourcesBean fileResourcesBean;

	@Test
	public void testWriteFileSystemResource() {
		final String fileLocation = "/tmp/smrs/file.txt";
		final byte[] content = "THIS_IS_A_TEST_CONTENT".getBytes();		
		Mockito.when(mockResourcesBean.getFileSystemResource(Mockito.anyString())).thenReturn(resource);
		fileResourcesBean.writeFileSystemResource(fileLocation, content);
		Mockito.verify(resource).write(Mockito.any(byte[].class), Mockito.anyBoolean());
	}

}
