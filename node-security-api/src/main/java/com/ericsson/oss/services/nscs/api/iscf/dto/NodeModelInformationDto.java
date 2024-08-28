/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.api.iscf.dto;

import java.io.Serializable;

import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;

public class NodeModelInformationDto implements Serializable {

    private static final long serialVersionUID = 5442495552748753378L;

    private String targetCategory = null;
    private String targetType = null;
    private ModelIdentifierType modelIdentifierType = null;
    private String targetModelIdentifier = null;

    public NodeModelInformationDto() {
    }

    public NodeModelInformationDto(String targetCategory, String targetType, ModelIdentifierType modelIdentifierType, String targetModelIdentifier) {
        super();
        this.targetCategory = targetCategory;
        this.targetType = targetType;
        this.modelIdentifierType = modelIdentifierType;
        this.targetModelIdentifier = targetModelIdentifier;
    }

    /**
     * @return the targetCategory
     */
    public String getTargetCategory() {
        return targetCategory;
    }

    /**
     * @param targetCategory
     *            the targetCategory to set
     */
    public void setTargetCategory(final String targetCategory) {
        this.targetCategory = targetCategory;
    }

    /**
     * @return the targetType
     */
    public String getTargetType() {
        return targetType;
    }

    /**
     * @param targetType
     *            the targetType to set
     */
    public void setTargetType(final String targetType) {
        this.targetType = targetType;
    }

    /**
     * @return the modelIdentifierType
     */
    public ModelIdentifierType getModelIdentifierType() {
        return modelIdentifierType;
    }

    /**
     * @param modelIdentifierType
     *            the modelIdentifierType to set
     */
    public void setModelIdentifierType(final ModelIdentifierType modelIdentifierType) {
        this.modelIdentifierType = modelIdentifierType;
    }

    /**
     * @return the targetModelIdentifier
     */
    public String getTargetModelIdentifier() {
        return targetModelIdentifier;
    }

    /**
     * @param targetModelIdentifier
     *            the targetModelIdentifier to set
     */
    public void setTargetModelIdentifier(final String targetModelIdentifier) {
        this.targetModelIdentifier = targetModelIdentifier;
    }
}
