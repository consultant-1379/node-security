/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ericsson.nms.security.nscs.laad.service;

import javax.inject.Singleton;

import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.itpf.sdk.resources.Resources;

/**
 * Utility class to access filesystem resource.
 * @author enatbol
 */
@Singleton
public class ResourcesBean {
    public Resource getFileSystemResource(final String fileLocation) {
        return Resources.getFileSystemResource(fileLocation);
    }
}
