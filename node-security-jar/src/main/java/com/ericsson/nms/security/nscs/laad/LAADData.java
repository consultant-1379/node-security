/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.laad;

import java.util.Arrays;

/**
 * <p>Data structure containing required user information for
 * LAAD files generation</p>
 * @author enatbol
 */
public class LAADData {
    private String userId;
    private char[] passwordHash;
    private String[] taskProfiles;

    public LAADData() {

    }

    public LAADData(final String user, final char[] passwordHash, final String[] taskProfiles) {
        this.userId = user;
        this.passwordHash = passwordHash;
        this.taskProfiles = taskProfiles;
    }

    /**
     * Gets the user id
     * @return user id
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Gets user's password hash
     * @return password hash
     */
    public char[] getPasswordHash() {
        return this.passwordHash;
    }

    /**
     * Gets user's task profiles
     * @return String array of profiles names
     */
    public String[] getTaskProfiles() {
        return this.taskProfiles;
    }

    /**
     * Sets user id
     * @param userId String of user id
     */
    public void setUserId(final String userId) {
        this.userId = userId;
    }

    /**
     * Sets user's password hash
     * @param passwordHash String with password hash
     */
    public void setPasswordHash(final char[] passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Sets user's task profiles
     * @param taskProfiles String array of profiles names
     */
    public void setTaskProfiles(final String[] taskProfiles) {
        this.taskProfiles = taskProfiles;
    }

    @Override
    public String toString() {
        return this.userId + "/" + this.passwordHash + "/" + Arrays.toString(this.taskProfiles);
    }
}
