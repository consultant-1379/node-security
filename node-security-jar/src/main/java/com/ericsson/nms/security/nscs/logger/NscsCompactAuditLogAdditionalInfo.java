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
package com.ericsson.nms.security.nscs.logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NscsCompactAuditLogAdditionalInfo implements Serializable {

    private static final long serialVersionUID = -984907885175410095L;

    private List<Map<String, Serializable>> detailResult;
    private List<Map<String, Serializable>> summaryResult;
    private String errorDetail;

    public NscsCompactAuditLogAdditionalInfo() {
        // empty constructor
    }

    /**
     * @return the detailResult
     */
    public List<Map<String, Serializable>> getDetailResult() {
        final List<Map<String, Serializable>> newDetailResult = new ArrayList<>();
        if (detailResult != null) {
            for (final Map<String, Serializable> detail : detailResult) {
                final Map<String, Serializable> newDetail = new HashMap<>();
                for (Map.Entry<String, Serializable> detailEntry : detail.entrySet()) {
                    if (detailEntry.getValue() instanceof Map<?, ?>) {
                        @SuppressWarnings("unchecked")
                        final Map<String, Serializable> detailEntryValue = (Map<String, Serializable>) detailEntry.getValue();
                        final Map<String, Serializable> newDetailEntryValue = new HashMap<>(detailEntryValue);
                        newDetail.put(detailEntry.getKey(), (Serializable) newDetailEntryValue);
                    } else {
                        newDetail.put(detailEntry.getKey(), detailEntry.getValue());
                    }
                }
                newDetailResult.add(newDetail);
            }
        }
        return newDetailResult;
    }

    /**
     * @param detailResult
     *            the detailResult to set
     */
    public void setDetailResult(final List<Map<String, Serializable>> detailResult) {
        this.detailResult = new ArrayList<>();
        if (detailResult != null) {
            for (final Map<String, Serializable> detail : detailResult) {
                final Map<String, Serializable> newDetail = new HashMap<>();
                for (Map.Entry<String, Serializable> detailEntry : detail.entrySet()) {
                    if (detailEntry.getValue() instanceof Map<?, ?>) {
                        @SuppressWarnings("unchecked")
                        final Map<String, Serializable> detailEntryValue = (Map<String, Serializable>) detailEntry.getValue();
                        final Map<String, Serializable> newDetailEntryValue = new HashMap<>(detailEntryValue);
                        newDetail.put(detailEntry.getKey(), (Serializable) newDetailEntryValue);
                    } else {
                        newDetail.put(detailEntry.getKey(), detailEntry.getValue());
                    }
                }
                this.detailResult.add(newDetail);
            }
        }
    }

    /**
     * @return the summaryResult
     */
    public List<Map<String, Serializable>> getSummaryResult() {
        final List<Map<String, Serializable>> newSummaryResult = new ArrayList<>();
        if (summaryResult != null) {
            for (final Map<String, Serializable> summary : summaryResult) {
                final Map<String, Serializable> newSummary = new HashMap<>();
                for (Map.Entry<String, Serializable> summaryEntry : summary.entrySet()) {
                    if (summaryEntry.getValue() instanceof Map<?, ?>) {
                        @SuppressWarnings("unchecked")
                        final Map<String, Serializable> summaryEntryValue = (Map<String, Serializable>) summaryEntry.getValue();
                        final Map<String, Serializable> newSummaryEntryValue = new HashMap<>(summaryEntryValue);
                        newSummary.put(summaryEntry.getKey(), (Serializable) newSummaryEntryValue);
                    } else {
                        newSummary.put(summaryEntry.getKey(), summaryEntry.getValue());
                    }
                }
                newSummaryResult.add(newSummary);
            }
        }
        return newSummaryResult;
    }

    /**
     * @param summaryResult
     *            the summaryResult to set
     */
    public void setSummaryResult(final List<Map<String, Serializable>> summaryResult) {
        this.summaryResult = new ArrayList<>();
        if (summaryResult != null) {
            for (final Map<String, Serializable> summary : summaryResult) {
                final Map<String, Serializable> newSummary = new HashMap<>();
                for (Map.Entry<String, Serializable> summaryEntry : summary.entrySet()) {
                    if (summaryEntry.getValue() instanceof Map<?, ?>) {
                        @SuppressWarnings("unchecked")
                        final Map<String, Serializable> summaryEntryValue = (Map<String, Serializable>) summaryEntry.getValue();
                        final Map<String, Serializable> newSummaryEntryValue = new HashMap<>(summaryEntryValue);
                        newSummary.put(summaryEntry.getKey(), (Serializable) newSummaryEntryValue);
                    } else {
                        newSummary.put(summaryEntry.getKey(), summaryEntry.getValue());
                    }
                }
                this.summaryResult.add(newSummary);
            }
        }
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

}
