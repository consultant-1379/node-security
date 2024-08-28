package com.ericsson.nms.security.nscs.api.command;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * A subclass of NscsCommandResponse representing a list of name/ multiple value pairs .
 * </p>
 *
 * Created with IntelliJ IDEA.
 * User: ediniku
 * Date: 01/10/14
 * Time: 12:59
 */
public class NscsNameMultipleValueCommandResponse extends NscsCommandResponse implements Iterable<NscsNameMultipleValueCommandResponse.Entry> {

    private static final long serialVersionUID = -2469395226834506479L;

    public static final String EMPTY_STRING = "";
    private final List<Entry> pairs = new LinkedList<>();
    private final int numberOfCoulmns;// No of Columns corresponding to each name
    private String responseTitle = EMPTY_STRING;// Table title to display in response
    private String additionalInformation = EMPTY_STRING;
    

    /**
     *
     * @param name the name
     * @param values Only first NUNMBER_OF_VALUES values will be considered rest of the values ignored
     * @return the NscsNameMultipleValueCommandResponse instance
     */
    public NscsNameMultipleValueCommandResponse add(final String name, final String[] values) {
        if(values.length == this.numberOfCoulmns){
            pairs.add(new Entry(name, values));
            return this;
        }else if(values.length > this.numberOfCoulmns){
            pairs.add((new Entry(name, Arrays.copyOfRange(values, 0 , numberOfCoulmns))));
            return this;
        }else{
            throw new IllegalArgumentException("Error: Number of Values provided is less than the Number of Columns defined for this response type");
        }

    }

    /**
     * @return a Iterator of com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse.Entry, where each Entry is a name- multiple value pair.
     */
    @Override
    public Iterator<Entry> iterator() {
        return pairs.iterator();
    }

    /**
     * Always returns NscsCommandResponseType.NAME_MULTIPLE_VALUE
     *
     * @return NscsCommandResponseType.NAME_MULTIPLE_VALUE
     */
    @Override
    public NscsCommandResponseType getResponseType() {
        return NscsCommandResponseType.NAME_MULTIPLE_VALUE;
    }

    /**
     * @return the number of value corresponding to each name in the response
     */
    public int getValueSize(){
        return numberOfCoulmns;
    }

    /**
     * @return the number of name/ multiple value pairs in this response
     */
    public int size() {
        return pairs.size();
    }

    /**
     * @return true if this response has no name/multiple value pair in it.
     */
    public boolean isEmpty() {
        return pairs.isEmpty();
    }
    
    /**
	 * @return the responseHeaderTitle
	 */
	public String getResponseTitle() {
		return responseTitle;
	}

	/**
	 * @param responseTitle the responseHeaderTitle to set
	 */
	public void setResponseHeaderTitle(final String responseTitle) {
		this.responseTitle = responseTitle;
	}

	public NscsNameMultipleValueCommandResponse(final int numberOfCoulmns){
         this.numberOfCoulmns = numberOfCoulmns;

    }
	

    /**
     * Method to get additionalInformation
	 * @return {@link String} the additionalInformation
	 */
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	/**
	 * Method to set additionalInformation
	 * @param additionalInformation :the additionalInformation to set
	 */
	public void setAdditionalInformation(final String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

    /**
     * Represents a Name/Multiple Value pair
     */
    public class Entry implements Serializable {


        private static final long serialVersionUID = -4191650207988637354L;
        private final String name;
        private final String[] values;

        public Entry(final String name, final String[] values) {
            this.name = name;
            this.values = values;
        }

        public String getName() {
            return name;
        }

        public String[] getValues() {
            return values;
        }
    }
}
