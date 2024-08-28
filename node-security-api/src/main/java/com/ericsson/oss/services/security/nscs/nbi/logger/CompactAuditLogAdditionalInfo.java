/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2024
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi.logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompactAuditLogAdditionalInfo implements Serializable {

    private static final long serialVersionUID = 3743445633982574618L;

    private List<CompactAuditLogDetailResult> detailResult;
    private List<CompactAuditLogSummaryResult> summaryResult;
    private String errorDetail;

    public CompactAuditLogAdditionalInfo() {
        // empty constructor
    }

    /**
     * Get the detailResult list.
     * 
     * If detailResult is not null, return an unmodifiable list (wrapper over the modifiable list, not allowing modifications to it directly, but
     * reflecting underlying mutable list changes).
     * 
     * If detailResult is null, return an immutable empty list.
     * 
     * @return the detailResult.
     */
    public List<CompactAuditLogDetailResult> getDetailResult() {
        return detailResult == null ? Collections.emptyList() : Collections.unmodifiableList(detailResult);
    }

    /**
     * @param detailResult
     *            the detailResult to set
     */
    public void setDetailResult(final List<CompactAuditLogDetailResult> detailResult) {
        this.detailResult = new ArrayList<>(detailResult);
    }

    /**
     * Get the summaryResult list.
     * 
     * If summaryResult is not null, return an unmodifiable list (wrapper over the modifiable list, not allowing modifications to it directly, but
     * reflecting underlying mutable list changes).
     * 
     * If summaryResult is null, return an immutable empty list.
     * 
     * @return the summaryResult.
     */
    public List<CompactAuditLogSummaryResult> getSummaryResult() {
        return summaryResult == null ? Collections.emptyList() : Collections.unmodifiableList(summaryResult);
    }

    /**
     * @param summaryResult
     *            the detailResult to set
     */
    public void setSummaryResult(final List<CompactAuditLogSummaryResult> summaryResult) {
        this.summaryResult = new ArrayList<>(summaryResult);
    }

    /**
     * @return the errorDetail
     */
    public String getErrorDetail() {
        return errorDetail;
    }

    /**
     * @param errorDetail
     *            the errorDetail to set
     */
    public void setErrorDetail(final String errorDetail) {
        this.errorDetail = errorDetail;
    }

    /**
     * Add detail result for the create of a given entity.
     * 
     * @param id
     *            the entity identifier.
     */
    public void addEntityCreateDetailResult(final String id) {
        if (detailResult == null) {
            detailResult = new ArrayList<>();
        }
        final CompactAuditLogDetailResult detailRes = new CompactAuditLogDetailResult();
        detailRes.setOpType("create");
        detailRes.setId(id);
        detailResult.add(detailRes);
    }

    /**
     * Add detail result for the given attribute value changes (AVCs) of a given entity.
     * 
     * @param id
     *            the entity identifier.
     * @param avcs
     *            the attribute value changes (AVCs).
     */
    public void addEntityAttributeValueChangesDetailResult(final String id, final List<AttributeValueChange> avcs) {
        if (detailResult == null) {
            detailResult = new ArrayList<>();
        }
        final CompactAuditLogDetailResult detailRes = new CompactAuditLogDetailResult();
        detailRes.setOpType("update");
        detailRes.setId(id);
        final Map<String, Serializable> currentValues = new HashMap<>();
        final Map<String, Serializable> oldValues = new HashMap<>();
        for (final AttributeValueChange avc : avcs) {
            currentValues.put(avc.getAttribute(), avc.getCurrValue());
            oldValues.put(avc.getAttribute(), avc.getOldValue());
        }
        detailRes.setCurrentValues(currentValues);
        detailRes.setOldValues(oldValues);
        detailResult.add(detailRes);
    }

    /**
     * Add summary result for the execute operation of the given entity performed on entities of given type with the given result.
     * 
     * @param id
     *            the entity identifier.
     * @param entity
     *            the type of entities on which operation has been performed.
     * @param result
     *            the operation result.
     */
    public void addExecuteSummaryResult(final String id, final String entity, final Map<String, Serializable> result) {
        if (summaryResult == null) {
            summaryResult = new ArrayList<>();
        }
        final CompactAuditLogSummaryResult summaryRes = new CompactAuditLogSummaryResult();
        summaryRes.setOpType("execute");
        summaryRes.setId(id);
        summaryRes.setEntity(entity);
        summaryRes.setResult(result);
        summaryResult.add(summaryRes);
    }
}
