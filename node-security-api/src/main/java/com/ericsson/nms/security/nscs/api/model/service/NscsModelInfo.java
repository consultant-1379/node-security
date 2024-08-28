package com.ericsson.nms.security.nscs.api.model.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Auxiliary class containing model information.
 *
 */
public class NscsModelInfo implements Serializable {

    private static final long serialVersionUID = 6630088954679792388L;

    /**
     * The schema name.
     */
    private String schema;

    /**
     * The model namespace.
     */
    private String namespace;

    /**
     * The model name.
     */
    private String name;

    /**
     * The model version.
     */
    private String version;

    /**
     * The member names.
     */
    private Collection<String> memberNames;

    /**
     * The actions (K=action name, V=action parameters)
     */
    private Map<String, Collection<String>> actions;

    public NscsModelInfo(final String schema, final String namespace, final String name, final String version) {
        super();
        this.schema = schema;
        this.namespace = namespace;
        this.name = name;
        this.version = version;
        this.memberNames = null;
        this.actions = null;
    }

    public NscsModelInfo(final String schema, final String namespace, final String name, final String version, final Collection<String> memberNames) {
        super();
        this.schema = schema;
        this.namespace = namespace;
        this.name = name;
        this.version = version;
        this.memberNames = memberNames;
        this.actions = null;
    }

    /**
     * @param schema  the schema
     * @param namespace the namespace
     * @param name  the name
     * @param version  the version
     * @param memberNames the memberNames
     * @param actions the actions
     */
    public NscsModelInfo(final String schema, final String namespace, final String name, final String version, final Collection<String> memberNames,
            final Map<String, Collection<String>> actions) {
        super();
        this.schema = schema;
        this.namespace = namespace;
        this.name = name;
        this.version = version;
        this.memberNames = memberNames;
        this.actions = actions;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(final String schema) {
        this.schema = schema;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * @return the memberNames
     */
    public Collection<String> getMemberNames() {
        return memberNames;
    }

    /**
     * @param memberNames
     *            the memberNames to set
     */
    public void setMemberNames(final Collection<String> memberNames) {
        this.memberNames = memberNames;
    }

    /**
     * @return the actions
     */
    public Map<String, Collection<String>> getActions() {
        return actions;
    }

    /**
     * @param actions
     *            the actions to set
     */
    public void setActions(final Map<String, Collection<String>> actions) {
        this.actions = actions;
    }

}
