package com.ericsson.nms.security.nscs.dps.eventhandling;

import java.io.Serializable;

public class NscsDPSEvent implements Serializable {

    private static final long serialVersionUID = 391418010844529280L;

    private final String moType;
    private final String moNamespace;
    private final String attributeName;
    private final boolean isWorkflowNotificable;
    private final boolean isCacheNotificable;
    private final boolean isPrivacyNotificable;
    private final String wfEventName;

    /**
     * @param moType the ModelObject (MO) type
     * @param moNamespace the MO namespace
     * @param attributeName the name attribute
     * @param isWorkflowNotificable flag enabled if the object is notifiable to workflow
     * @param isCacheNotificable flag enabled if the object is notifiable to cache
     */
    public NscsDPSEvent(final String moType, final String moNamespace, final String attributeName, final boolean isWorkflowNotificable,
            final boolean isCacheNotificable) {
        super();
        this.moType = moType;
        this.moNamespace = moNamespace;
        this.attributeName = attributeName;
        this.isWorkflowNotificable = isWorkflowNotificable;
        this.isCacheNotificable = isCacheNotificable;
        this.isPrivacyNotificable = false;
        if (this.isWorkflowNotificable) {
            this.wfEventName = "ATT" + this.attributeName + "Change";
        } else {
            this.wfEventName = "";
        }
    }

    /**
     * @param moType the ModelObject (MO) type
     * @param moNamespace the MO namespace
     * @param attributeName the name attribute
     * @param isWorkflowNotificable flag enabled if the object is notifiable to workflow
     * @param isCacheNotificable flag enabled if the object is notifiable to cache
     * @param isPrivacyNotificable flag enabled if the object is notifiable to privacy functions
     */
    public NscsDPSEvent(final String moType, final String moNamespace, final String attributeName, final boolean isWorkflowNotificable,
                        final boolean isCacheNotificable,final boolean isPrivacyNotificable) {
        super();
        this.moType = moType;
        this.moNamespace = moNamespace;
        this.attributeName = attributeName;
        this.isWorkflowNotificable = isWorkflowNotificable;
        this.isCacheNotificable = isCacheNotificable;
        this.isPrivacyNotificable = isPrivacyNotificable;
        if (this.isWorkflowNotificable) {
            this.wfEventName = "ATT" + this.attributeName + "Change";
        } else {
            this.wfEventName = "";
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributeName == null) ? 0 : attributeName.hashCode());
        result = prime * result + (isCacheNotificable ? 1231 : 1237);
        result = prime * result + (isWorkflowNotificable ? 1231 : 1237);
        result = prime * result + (isPrivacyNotificable ? 1231 : 1237);
        result = prime * result + ((moNamespace == null) ? 0 : moNamespace.hashCode());
        result = prime * result + ((moType == null) ? 0 : moType.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NscsDPSEvent other = (NscsDPSEvent) obj;
        if (attributeName == null) {
            if (other.attributeName != null) {
                return false;
            }
        } else if (!attributeName.equals(other.attributeName)) {
            return false;
        }
        if (isCacheNotificable != other.isCacheNotificable) {
            return false;
        }
        if (isWorkflowNotificable != other.isWorkflowNotificable) {
            return false;
        }
        if (isPrivacyNotificable != other.isPrivacyNotificable) {
            return false;
        }
        if (moNamespace == null) {
            if (other.moNamespace != null) {
                return false;
            }
        } else if (!moNamespace.equals(other.moNamespace)) {
            return false;
        }
        if (moType == null) {
            if (other.moType != null) {
                return false;
            }
        } else if (!moType.equals(other.moType)) {
            return false;
        }
        return true;
    }

    public String getMoType() {
        return moType;
    }

    public String getMoNamespace() {
        return moNamespace;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public boolean isWorkflowNotificable() {
        return isWorkflowNotificable;
    }

    public boolean isCacheNotificable() {
        return isCacheNotificable;
    }

    public boolean isPrivacyNotificable() {
        return isPrivacyNotificable;
    }

    /**
     * @return wfEventName (Format: "ATTattributeNameChange")
     */
    public String getWfEventName() {
        return wfEventName;
    }

    @Override
    public String toString() {
        return String.format(
                "NscsDPSEvent " + "	moType: [%s]" + " moNamespace: [%s]" + " attributeName: [%s]" + " isWorkflowNotificable: [%s]"
                        + " isCacheNotificable: [%s]" + " isPrivacyNotificable: [%s]" + " wfEventName: [%s]",
                moType, moNamespace, attributeName, isWorkflowNotificable, isCacheNotificable, isPrivacyNotificable, wfEventName);

    }

}
