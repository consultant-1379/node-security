/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.usermanagment;

import java.util.HashSet;
import java.util.Set;

import com.ericsson.nms.security.nscs.laad.LAADData;

/**
 * <p>Provides access to User information</p>
 * @author enatbol
 */
public class UserManagementServiceImpl implements UserManagmentService {

    /**
     * Fetches current user password
     * @param user
     * @return
     * @throws Exception
     */
    @Override
    public char[] getUserPassword(final String user) throws Exception {
        final String password = "This is a top secret user managment password.....shhhh!";
        return password.toCharArray();
    }

    //	//@Override
    //		public Set<LAADData> getLaadData() {
    //		Set<LAADData> set = new HashSet<>();
    //		char[] password = "passwordHash".toCharArray();
    //		String taskProfile[] = {"Do Stuff", "Do more stuff", "Yet more stuff"};
    //		LAADData laadData = new LAADData("Captain_Ericsson", password, taskProfile);
    //		set.add(laadData);
    //		laadData = new LAADData("Super_Ericsson_Dude", password, taskProfile);
    //		set.add(laadData);
    //		return set;
    //	}

    /**
     * Fetches current LAAD information to be distributed to nodes
     * @return
     */
    @Override
    public Set<LAADData> getLaadData() {
        final Set<LAADData> set = new HashSet<>();
        final char[] password = "passwordHash".toCharArray();
        final String taskProfile[] = { "Do Stuff", "Do more stuff", "Yet more stuff" };
        LAADData laadData = new LAADData("Captain_Ericsson", password, taskProfile);
        set.add(laadData);
        laadData = new LAADData("Super_Ericsson_Dude", password, taskProfile);
        set.add(laadData);
        return set;
    }

}
