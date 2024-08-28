/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ericsson.nms.security.nscs.usermanagment;

import java.util.Set;

import com.ericsson.nms.security.nscs.laad.LAADData;

/**
 * 
 * @author enatbol
 */
public interface UserManagmentService {
    char[] getUserPassword(final String user) throws Exception;

    Set<LAADData> getLaadData();
}
