package com.ericsson.nms.security.nscs.api.command;

import java.io.Serializable;
import java.util.*;

/**
 * <p>
 * A subclass of NscsCommandResponse representing a list of name/value pairs .
 * </p>
 * @author emaynes
 */
public class NscsNameValueCommandResponse extends NscsCommandResponse implements Iterable<NscsNameValueCommandResponse.Entry> {

    private static final long serialVersionUID = -1673990240029588019L;

    private final List<Entry> pairs = new LinkedList<>();

    public NscsNameValueCommandResponse add(final String name, final String value) {
        pairs.add(new Entry(name, value));
        return this;
    }

    /**
     * Always returns NscsCommandResponseType.NAME_VALUE
     * 
     * @return NscsCommandResponseType.NAME_VALUE
     */
    @Override
    public NscsCommandResponseType getResponseType() {
        return NscsCommandResponseType.NAME_VALUE;
    }

    /**
     * @return a Iterator of com.ericsson.nms.security.nscs.api.command.NscsNameValueCommandResponse.Entry, where each Entry is a name-value pair.
     */
    @Override
    public Iterator<Entry> iterator() {
        return pairs.iterator();
    }

    /**
     * @return the number of name/value pairs in this response
     */
    public int size() {
        return pairs.size();
    }

    /**
     * @return true if this response has no name/value pair in it.
     */
    public boolean isEmpty() {
        return pairs.isEmpty();
    }

    /**
     * Represents a Name/Value pair
     */
    public class Entry implements Serializable {
        private static final long serialVersionUID = 4092596821283304666L;

        private final String name;
        private final String value;

        public Entry(final String name, final String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    public class EntryComparator implements Comparator<Entry> {

        @Override
        public int compare(final Entry arg0, final Entry arg1) {

            final int value1 = arg0.getValue().compareTo(arg1.getValue());

            if (value1 == 0) {
                final int value2 = arg0.getName().compareTo(arg1.getName());
                if (value2 != 0) {
                    return value2;
                }
            }
            return value1;
        }
    }
}
