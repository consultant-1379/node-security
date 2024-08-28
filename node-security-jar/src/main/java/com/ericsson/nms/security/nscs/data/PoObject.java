package com.ericsson.nms.security.nscs.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;

/**
 * Class that represents a Persistent Object in the data store.
 * @author emaynes.
 */
public class PoObject {
    private final PersistenceObject po;

    PoObject(final PersistenceObject po) {
        this.po = po;
    }

    /**
     * Gets the Persistent Object namespace
     * @return namespace name
     */
    public String getNamespace() {
        return this.po.getNamespace();
    }

    /**
     * Gets the Type of this Persistent Object instance
     * @return
     */
    public String getType() {
        return this.po.getType();
    }

    /**
     * Gets the version of this persistence object's type.
     * @return
     */
    public String getVersion() {
        return this.po.getVersion();
    }

    /**
     * Gets a Persistent Object of the given type and namespace whichi is
     * associated with this instance
     * @param namespace namespace of the associated Persistent Object
     * @param type type of the associated Persistent Object
     * @return a PoObject instance or null if a matching associated persistent object
     * could not be found
     */
    public PoObject getAssociationOfType(final String namespace, final String type){
        PoObject found = null;
        for (Collection<PersistenceObject> persistenceObjects : getAssociations().values()) {
            for (PersistenceObject object : persistenceObjects) {
                if ( namespace.equals(object.getNamespace()) && type.equals(object.getType())) {
                    found = new PoObject(object);
                }
            }
        }

        return found;
    }

    /**
     * Gets a Persistent Object which is associated with this instance for the given endpoint.
     * 
     * @param endpointName
     *            the name of the endpoint.
     * @return a PoObject instance or null if a matching associated persistent object could not be found
     */
    public PoObject getAssociationOfEndpoint(final String endpointName) {
        PoObject found = null;
        Collection<PersistenceObject> persistenceObjects = getAssociations(endpointName);
        if (persistenceObjects != null && !persistenceObjects.isEmpty()) {
            final Iterator<PersistenceObject> iterator = persistenceObjects.iterator();
            if (iterator.hasNext()) {
                PersistenceObject object = iterator.next();
                found = new PoObject(object);
            }
        }

        return found;
    }

    PersistenceObject getPo() {
        return po;
    }

    /**
     * Checks if this Persistent Object is also a Managed Object.
     * @return true it this is also a Managed Object
     */
    public boolean isMo(){
        return (this.po instanceof ManagedObject);
    }

    /**
     * Gets a MoObject type of reference to this Persistent Object.
     * @return a new MoObject instance representing this persistent object
     * @throws java.lang.IllegalArgumentException if this PoObject cannot me converted to a
     * MoObject
     */
    public MoObject toMoObject(){
        return new MoObject(this);
    }

    /**
     * Gets the value of an attribute
     * @param attName name of the attribute
     * @param <T> Type of the attribute value
     * @return the value of the attribute. This value can be null.
     * @throws com.ericsson.oss.itpf.datalayer.dps.exception.model.NotDefinedInModelException
     * if there is no attribute with the supplied name on this persistence object
     */
    public <T> T getAttribute(final String attName) {
        return this.po.getAttribute(attName);
    }

    private Map<String, Collection<PersistenceObject>> getAssociations(){
        return this.po.getAssociations();
    }

    private Collection<PersistenceObject> getAssociations(final String endpointName) {
        return this.po.getAssociations(endpointName);
    }
}
