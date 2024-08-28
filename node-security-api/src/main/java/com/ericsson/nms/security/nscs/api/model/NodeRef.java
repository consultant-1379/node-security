package com.ericsson.nms.security.nscs.api.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;

/**
 * Implementation of {@link com.ericsson.nms.security.nscs.api.model.NodeReference}.
 *
 * @author emaynes.
 * @see com.ericsson.nms.security.nscs.api.model.NodeReference
 */
public class NodeRef implements NodeReference {
    private static final long serialVersionUID = -3211291821964947555L;

    private static final String VNFM_TYPE = "VirtualNetworkFunctionManager";
    private static final String NFVO_TYPE = "NetworkFunctionVirtualizationOrchestrator";
    private static final String VIM_TYPE = "VirtualInfrastructureManager";
    private static final String CIM_TYPE = "CloudInfrastructureManager";
    private static final String NETWORK_ELEMENT_TYPE = "NetworkElement";
    private static final String SUB_NETWORK_TYPE = "SubNetwork";
    private static final String ME_CONTEXT_TYPE = "MeContext";
    private static final String MANAGEMENT_SYSTEM_TYPE = "ManagementSystem";
    private static final String MANAGED_ELEMENT_TYPE = "ManagedElement";
    private static final String FDN_REGEX_TEMPLATE = "%s\\s*=\\s*([^,]+)(,|$)";
    private static final String ROOT_MO_FDN_REGEX_TEMPLATE = "(^|,)" + FDN_REGEX_TEMPLATE;
    private static final Pattern SUB_NETWORK_PATTERN = Pattern.compile(String.format(FDN_REGEX_TEMPLATE, SUB_NETWORK_TYPE), Pattern.CASE_INSENSITIVE);
    private String name;
    private String fdn;

    /**
     * Map associating the type with the specific pattern. Note that iteration on it doesn't grant order!
     */
    private static final Map<String, Pattern> TYPE_PATTERN_MAP = new HashMap<String, Pattern>() {
        private static final long serialVersionUID = 1L;

        {
            put(NETWORK_ELEMENT_TYPE, Pattern.compile(String.format(ROOT_MO_FDN_REGEX_TEMPLATE, NETWORK_ELEMENT_TYPE), Pattern.CASE_INSENSITIVE));
            put(ME_CONTEXT_TYPE, Pattern.compile(String.format(ROOT_MO_FDN_REGEX_TEMPLATE, ME_CONTEXT_TYPE), Pattern.CASE_INSENSITIVE));
            put(MANAGED_ELEMENT_TYPE, Pattern.compile(String.format(ROOT_MO_FDN_REGEX_TEMPLATE, MANAGED_ELEMENT_TYPE), Pattern.CASE_INSENSITIVE));
            put(VNFM_TYPE, Pattern.compile(String.format(ROOT_MO_FDN_REGEX_TEMPLATE, VNFM_TYPE), Pattern.CASE_INSENSITIVE));
            put(NFVO_TYPE, Pattern.compile(String.format(ROOT_MO_FDN_REGEX_TEMPLATE, NFVO_TYPE), Pattern.CASE_INSENSITIVE));
            put(VIM_TYPE, Pattern.compile(String.format(ROOT_MO_FDN_REGEX_TEMPLATE, VIM_TYPE), Pattern.CASE_INSENSITIVE));
            put(CIM_TYPE, Pattern.compile(String.format(ROOT_MO_FDN_REGEX_TEMPLATE, CIM_TYPE), Pattern.CASE_INSENSITIVE));
            put(MANAGEMENT_SYSTEM_TYPE, Pattern.compile(String.format(ROOT_MO_FDN_REGEX_TEMPLATE, MANAGEMENT_SYSTEM_TYPE), Pattern.CASE_INSENSITIVE));
        }
    };

    public static class TypePattern {

        private final String type;
        private final Pattern pattern;

        public TypePattern(final String type) {
            this.type = type;
            this.pattern = TYPE_PATTERN_MAP.get(type);
        }

        /**
         * @return the type
         */
        public String getType() {
            return this.type;
        }

        /**
         * @return the pattern
         */
        public Pattern getPattern() {
            return this.pattern;
        }
    }

    /**
     * Ordered list of type patterns. Note that the matching order is fundamental to distinguish, for example, between FDNs like
     * MeContext=nodeName,ManagedElement=1 and ManagedElement=nodeName
     */
    private static final Deque<TypePattern> TYPE_PATTERN_DEQUE = new LinkedList<TypePattern>() {
        private static final long serialVersionUID = 1L;

        {
            // Don't change the order of insertion in the list!
            add(new TypePattern(NETWORK_ELEMENT_TYPE));
            add(new TypePattern(ME_CONTEXT_TYPE));
            add(new TypePattern(MANAGED_ELEMENT_TYPE));
            add(new TypePattern(VNFM_TYPE));
            add(new TypePattern(NFVO_TYPE));
            add(new TypePattern(VIM_TYPE));
            add(new TypePattern(CIM_TYPE));
            add(new TypePattern(MANAGEMENT_SYSTEM_TYPE));
        }
    };

    /**
     * Creates a new NodeRef instance.
     * <p>
     * This constructor accepts a name or FDN. If simple name is provided, this implementation assumes that FDN will be 'NetworkElement=[simplename]'.
     * If FDN is provided then the node name will be <B>extracted</B> from the FDN.
     * </p>
     * <p>
     * This constructor accepts even a child FDN like 'MeContext=ERBS_002,ManagedElement=1,Security=1', in this case getName() will return 'ERBS_002'
     * and getFdn() will return 'MeContext=ERBS_002', or 'ManagedElement=RadioNode01,SystemFunctions=1', in this case getName() will return
     * 'RadioNode01' and getFdn() will return 'ManagedElement=RadioNode01'; the rest will be ignored.
     * </p>
     *
     * @param nameOrFdn
     *            String containing the node simple name or the node FDN
     */
    public NodeRef(final String nameOrFdn) {
        Objects.requireNonNull(nameOrFdn, "Node name or FDN can't be null.");
        if (nameOrFdn.isEmpty()) {
            throw new InvalidArgumentValueException("Can't create a Node reference. Not a valid FDN : empty string");
        }
        if (nameOrFdn.contains("=") || nameOrFdn.contains(",")) {
            initializeWithFdn(nameOrFdn);
        } else {
            initializeWithName(nameOrFdn);
        }
    }

    private final void initializeWithName(final String name) {
        this.name = name;
        this.fdn = toFdn(NETWORK_ELEMENT_TYPE, name);
    }

    private final String toFdn(final String... typeNamePair) {
        final StringBuilder fdn = new StringBuilder();
        for (int i = 0; i < typeNamePair.length; i++) {
            if (fdn.length() > 0) {
                fdn.append(",");
            }
            fdn.append(String.format("%s=%s", typeNamePair[i], typeNamePair[++i]));
        }
        return fdn.toString();
    }

    private final void initializeWithFdn(final String fdn) {
        String name = null;
        String type = null;

        final Deque<TypePattern> patterns = new LinkedList<>(TYPE_PATTERN_DEQUE);
        while (!patterns.isEmpty()) {
            final TypePattern typePattern = patterns.pop();
            final Pattern pattern = typePattern.getPattern();
            final Matcher matcher = pattern.matcher(fdn);
            if (matcher.find()) {
                name = matcher.group(2);
                type = typePattern.getType();
                break;
            }
        }

        if (name != null && type != null) {
            final Matcher matcher = SUB_NETWORK_PATTERN.matcher(fdn);
            final StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                sb.append(toFdn(SUB_NETWORK_TYPE, matcher.group(1)));
                sb.append(",");
            }
            sb.append(toFdn(type, name));
            this.fdn = sb.toString();
            this.name = name;
        } else {
            throw new InvalidArgumentValueException(String.format("Can't create a Node reference. Not a valid FDN : %s", fdn));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return the node simple name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * @return the FDN
     */
    @Override
    public String getFdn() {
        return fdn;
    }

    @Override
    public String toString() {
        return String.format("NodeRef{fdn='%s'}", fdn);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodeRef)) {
            return false;
        }

        final NodeRef nodeRef = (NodeRef) o;

        return (fdn.equals(nodeRef.fdn) || name.equals(nodeRef.name));

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + fdn.hashCode();
        return result;
    }

    /**
     * Utility method to convert a collection of node names or FDN in the String format to a List of NodeReferences
     *
     * @param nodes
     *            String list of node names or FDNs
     * @return List of NodeReference
     */
    public static List<NodeReference> from(final Collection<String> nodes) {
        if (nodes == null) {
            return null;
        }

        final List<NodeReference> nodesRef = new LinkedList<>();
        for (final String node : nodes) {
            nodesRef.add(new NodeRef(node));
        }

        return nodesRef;
    }

    /**
     * Utility method to convert an array of node names or FDN in the String format to a List of NodeReferences
     *
     * @param nodes
     *            String array of node names or FDNs
     * @return List of NodeReference
     */
    public static List<NodeReference> from(final String... nodes) {
        return from(Arrays.asList(nodes));
    }

    /**
     * Utility method to transform a collection of NodeReference into a list of node names in the String format
     *
     * @param nodes
     *            Collection of NodeReference
     * @return String list of node names
     */
    public static List<String> toNames(final Collection<? extends NodeReference> nodes) {
        if (nodes == null) {
            return null;
        }

        final List<String> names = new LinkedList<>();
        for (final NodeReference node : nodes) {
            names.add(node.getName());
        }

        return names;
    }

    /**
     * Utility method to transform a collection of NodeReference into a list of node FDNs in the String format
     *
     * @param nodes
     *            Collection of NodeReference
     * @return String list of node FDNs
     */
    public static List<String> toFdns(final Collection<? extends NodeReference> nodes) {
        if (nodes == null) {
            return null;
        }

        final List<String> fdns = new LinkedList<>();
        for (final NodeReference node : nodes) {
            fdns.add(node.getFdn());
        }

        return fdns;
    }
}
