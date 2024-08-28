package com.ericsson.nms.security.nscs.data;

import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;

/**
* Class that represents a Managed Object in the data store.
* @author emaynes.
*/
public class MoObject extends PoObject{
    private final ManagedObject mo;

    /**
     * Constructs an instance of MoObject given a Persistent Object instance
     * @param poObject a PoObject instance
     * @see com.ericsson.nms.security.nscs.data.PoObject
     */
    public MoObject(final PoObject poObject) {
        super(poObject.getPo());
        if ( ! poObject.isMo() ){
            throw new IllegalArgumentException(String.format("%s/%s is not a Managed Object.", poObject.getNamespace(), poObject.getType()));
        }
        this.mo = (ManagedObject) poObject.getPo();
    }

    MoObject(final ManagedObject mo) {
        super(mo);
        this.mo = mo;
    }

    public String getFdn() {
        return this.mo.getFdn();
    }

}
