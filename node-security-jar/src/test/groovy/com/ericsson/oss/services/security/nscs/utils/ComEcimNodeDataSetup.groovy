/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.utils

import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject

class ComEcimNodeDataSetup extends NodeDataSetup {

    private ManagedObject systemFunctions
    private ManagedObject secM
    private ManagedObject userManagement
    private ManagedObject ldapAuthenticationMethod
    private ManagedObject ldap
    private platform = "ECIM"

    def createNodeWithMeContext(String neType, String ossModelIdentity, String nodeName) {
        createNodeWithMeContext(neType, ossModelIdentity, nodeName, platform)
    }

    def createNodeWithManagedElement(String neType, String ossModelIdentity, String nodeName) {
        createNodeWithManagedElement(neType, ossModelIdentity, nodeName, platform, getTopNs(neType))
    }

    def createManagedElementUnderMeContext(String neType, String nodeName) {
        createManagedElementUnderMeContext(neType, nodeName, getTopNs(neType))
    }

    def createSystemFunctionsUnderManagedElement() {
        systemFunctions = runtimeConfigurableDps.addManagedObject()
                .parent(managedElement)
                .namespace(RADIONODE_TOP_NS)
                .type("SystemFunctions")
                .name("1")
                .build()
    }

    def createSecMUnderSystemFunctions() {
        secM = runtimeConfigurableDps.addManagedObject()
                .parent(systemFunctions)
                .namespace("RcsSecM")
                .type("SecM")
                .name("1")
                .build()
    }

    def createUserManagementUnderSecM() {
        userManagement = runtimeConfigurableDps.addManagedObject()
                .parent(secM)
                .namespace("RcsSecM")
                .type("UserManagement")
                .name("1")
                .build()
    }

    def createLdapAuthenticationMethodUnderUserManagement() {
        ldapAuthenticationMethod = runtimeConfigurableDps.addManagedObject()
                .parent(userManagement)
                .namespace("RcsLdapAuthentication")
                .type("LdapAuthenticationMethod")
                .name("1")
                .build()
    }

    def createLdapUnderLdapAuthenticationMethod(String bindDn) {
        ldap = runtimeConfigurableDps.addManagedObject()
                .parent(ldapAuthenticationMethod)
                .namespace("RcsLdapAuthentication")
                .type("Ldap")
                .name("1")
                .addAttribute("bindDn", bindDn)
                .build()
    }

    private String getTopNs(String neType) {
        if (RADIONODE_TARGET_TYPE == neType) {
            return RADIONODE_TOP_NS
        }
        return null
    }
}
