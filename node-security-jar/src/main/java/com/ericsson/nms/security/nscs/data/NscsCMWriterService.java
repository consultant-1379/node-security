package com.ericsson.nms.security.nscs.data;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.api.exception.DataAccessSystemException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.exception.general.AlreadyDefinedException;
import com.ericsson.oss.itpf.datalayer.dps.exception.general.DelegateFailureException;
import com.ericsson.oss.itpf.datalayer.dps.exception.general.DpsIllegalStateException;
import com.ericsson.oss.itpf.datalayer.dps.exception.general.ObjectNotInContextException;
import com.ericsson.oss.itpf.datalayer.dps.exception.model.ModelConstraintViolationException;
import com.ericsson.oss.itpf.datalayer.dps.exception.model.NotDefinedInModelException;
import com.ericsson.oss.itpf.datalayer.dps.object.builder.ManagedObjectBuilder;
import com.ericsson.oss.itpf.datalayer.dps.object.builder.MibRootBuilder;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.cm.cmshared.dto.AttributeSpecification;
import com.ericsson.oss.services.cm.cmshared.dto.AttributeSpecificationContainer;
import com.ericsson.oss.services.cm.cmshared.dto.CmObjectSpecification;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.cm.cmshared.dto.StringifiedAttributeSpecifications;

/**
 * Auxiliary class to facilitate saving information into DPS
 *
 * Created by emaynes on 04/05/2014.
 */
public class NscsCMWriterService {

    @Inject
    private Logger logger;

    @EServiceRef
    private DataPersistenceService dataPersistenceService;

    /**
     * Creates the specified MibRoot
     *
     * @param parentFdn
     * @param specification
     * @throws DataAccessException
     *             if CmResponse getStatusCode() < 0
    */
    public ManagedObject createMibRoot(final String parentFdn, final CmObjectSpecification specification) {
        logger.debug("Creating MibRoot using DPS");
        final DataBucket dataBucket = dataPersistenceService.getLiveBucket();

        logger.debug("Got dataBucket");

        final String namespace = specification.getNamespace();
        final String namespaceVersion = specification.getNamespaceVersion();
        final String type = specification.getType();
        final AttributeSpecificationContainer attributeSpecificationContainer = specification.getAttributeSpecificationContainer();
        final String rootName = specification.getName();

        ManagedObject mo;
        try {
            final Map<String, Object> attributes = convertAttributeSpecificationToMapForDps(attributeSpecificationContainer);
            logger.debug("attributes[{}]", attributes);
            final MibRootBuilder mibRootBuilder = createPopulatedMibBuilder(dataBucket, namespace, type, namespaceVersion, rootName, attributes);
            if (parentFdn != null) {
                logger.debug("parentFdn[{}]", parentFdn);
                ManagedObject parentMo;
                parentMo = dataBucket.findMoByFdn(parentFdn);
                if (parentMo == null) {
                    throw new DataAccessException("Invalid parent MO : " + parentFdn);
                }
                mibRootBuilder.parent(parentMo);
            }

            logger.debug("calling mibRootBuilder.create()");
            mo = mibRootBuilder.create();
        } catch (DataAccessException de) {
            logger.error("Got DataAccessException[{}]", de.getMessage());
            throw de;
        } catch (Exception e) {
            logger.error("Got exception[{}]", e.getMessage());
            throw wrapException(e);
        }

        logger.debug("end of createMibRoot...");
        return mo;
    }

    /**
     * Creates the specified MibRoot
     *
     * @param parentMoFdn
     *            the FDN of parent MO
     * @param moType
     *            the type of the MO to create
     * @param moNamespace
     *            the namespace of the MO to create
     * @param moVersion
     *            the version of the MO to create
     * @param moName
     *            the name of the MO to create
     * @param moAttributes
     *            the attributes of the MO to create
     * @throws DataAccessSystemException
     *             if wrong (null) parameters are passed
     * @throws DataAccessException
     *             if involved MO can't be created
     */
    public ManagedObject createMibRoot(final String parentMoFdn, final String moType, final String moNamespace, final String moVersion, final String moName, final Map<String, Object> moAttributes) {
        logger.debug("createMib: start for parent[{}] type[{}] ns[{}] ver[{}] name[{}] attrs[{}]", parentMoFdn, moType, moNamespace, moVersion, moName, moAttributes);
        ManagedObject mo;
        if (parentMoFdn != null && moType != null && moNamespace != null && moVersion != null && moName != null) {
            NscsCMWriterService.WriterSpecificationBuilder specBuilder = withSpecification();
            specBuilder.setParent(parentMoFdn);
            specBuilder.setType(moType);
            specBuilder.setNamespace(moNamespace);
            specBuilder.setVersion(moVersion);
            specBuilder.setName(moName);
            if (moAttributes != null) {
                specBuilder.addAttributes(moAttributes);
            } else {
                logger.debug("createMo: skipping null attributes.");
            }
            try {
                mo = specBuilder.createMIBRoot();
            } catch (Exception e) {
                String errorMessage = String.format("createMo: caught exception[%s] message[%s]", e.getClass().toString(), e.getMessage());
                logger.error(errorMessage);
                throw new DataAccessException(errorMessage);
            }
        } else {
            String errorMessage = buildErrorMessages(parentMoFdn, moType, moNamespace, moVersion, moName);
            logger.error(errorMessage);
            throw new DataAccessSystemException(errorMessage);
        }
        logger.debug("createMo: successful end for parent[{}] type[{}] ns[{}] ver[{}] name[{}] attrs[{}]", parentMoFdn, moType, moNamespace, moVersion, moName, moAttributes);
        return mo;
    }

    /**
     * Creates the specified Mo
     *
     * @param parentFdn
     * @param specification
     * @throws DataAccessException
     *             if CmResponse getStatusCode() < 0
   */
    public ManagedObject createMo(final String parentFdn, final CmObjectSpecification specification) {

        logger.debug("Creating createMo using DPS");
        final DataBucket dataBucket = dataPersistenceService.getLiveBucket();

        logger.debug("Got dataBucket");

        final String type = specification.getType();
        final AttributeSpecificationContainer attributeSpecificationContainer = specification.getAttributeSpecificationContainer();
        final String rootName = specification.getName();

        ManagedObject mo;
        try {
            final Map<String, Object> attributes = convertAttributeSpecificationToMapForDps(attributeSpecificationContainer);
            logger.debug("attributes[{}]", attributes);
            final ManagedObjectBuilder managedObjectBuilder = createPopulatedMoBuilder(dataBucket, type, rootName, attributes);
            if (parentFdn != null) {
                ManagedObject parentMo;
                parentMo = dataBucket.findMoByFdn(parentFdn);
                if (parentMo == null) {
                    throw new DataAccessException("Invalid parent MO : " + parentFdn);
                }
                logger.debug("parentoMO fdn: {} name :{} isMibRoot: {}",parentMo.getFdn(), parentMo.getName(), parentMo.isMibRoot());
                managedObjectBuilder.parent(parentMo);
            }

            logger.debug("calling managedObjectBuilder.create()");
            mo = managedObjectBuilder.create();
        } catch (DataAccessException de) {
            logger.error("Got DataAccessException[{}]", de.getMessage());
            throw de;
        } catch (Exception e) {
            logger.error("Got exception[{}]", e.getMessage());
            throw wrapException(e);
        }

        logger.debug("end of createMo...");
        return mo;
    }

    /**
     * Create an MO of given type, namespace, version, name and with given
     * attributes as child of a parent MO specified by FDN.
     *
     * @param parentMoFdn
     *            the FDN of parent MO
     * @param moType
     *            the type of the MO to create
     * @param moNamespace
     *            the namespace of the MO to create
     * @param moVersion
     *            the version of the MO to create
     * @param moName
     *            the name of the MO to create
     * @param moAttributes
     *            the attributes of the MO to create
     * @throws DataAccessSystemException
     *             if wrong (null) parameters are passed
     * @throws DataAccessException
     *             if involved MO can't be created
     */
    public ManagedObject createMo(final String parentMoFdn, final String moType, final String moNamespace, final String moVersion, final String moName, final Map<String, Object> moAttributes) {
        logger.debug("createMo: start for parent[{}] type[{}] ns[{}] ver[{}] name[{}] attrs[{}]", parentMoFdn, moType, moNamespace, moVersion, moName, moAttributes);
        ManagedObject mo;
        if (parentMoFdn != null && moType != null && moNamespace != null && moVersion != null && moName != null) {
            NscsCMWriterService.WriterSpecificationBuilder specBuilder = withSpecification();
            specBuilder.setParent(parentMoFdn);
            specBuilder.setType(moType);
            specBuilder.setNamespace(moNamespace);
            specBuilder.setVersion(moVersion);
            specBuilder.setName(moName);
            if (moAttributes != null) {
                specBuilder.addAttributes(moAttributes);
            } else {
                logger.debug("createMo: skipping null attributes.");
            }
            try {
                mo = specBuilder.createMO();
            } catch (Exception e) {
                String errorMessage = String.format("createMo: caught exception[%s] message[%s]", e.getClass().toString(), e.getMessage());
                logger.error(errorMessage);
                throw new DataAccessException(errorMessage);
            }
        } else {
            String errorMessage = buildErrorMessages(parentMoFdn, moType, moNamespace, moVersion, moName);
            logger.error(errorMessage);
            throw new DataAccessSystemException(errorMessage);
        }
        logger.debug("createMo: successful end for parent[{}] type[{}] ns[{}] ver[{}] name[{}] attrs[{}]", parentMoFdn, moType, moNamespace, moVersion, moName, moAttributes);
        return mo;
    }

    /**
     * Add, for given MO specified by FDN, an association of given name to a
     * given sibling MO specified by FDN.
     *
     * @param moFdn
     *            the MO FDN
     * @param associationName
     *            the association name
     * @param siblingMoFdn
     *            the sibling MO FDN
     * @throws DataAccessSystemException
     *             if wrong (null) parameters are passed
     * @throws DataAccessException
     *             if involved MOs can't be found or the association can't be
     *             added
     */
    public void addAssociation(final String moFdn, final String associationName, final String siblingMoFdn) {
        logger.debug("Add association: start for fdn[{}] association[{}] siblingFdn[{}]", moFdn, associationName, siblingMoFdn);
        if (moFdn != null && associationName != null && siblingMoFdn != null) {
            final DataBucket dataBucket = dataPersistenceService.getLiveBucket();
            ManagedObject mo = dataBucket.findMoByFdn(moFdn);
            if (mo == null) {
                throw new DataAccessException("Add association: invalid MO FDN: " + moFdn);
            }
            ManagedObject siblingMo = dataBucket.findMoByFdn(siblingMoFdn);
            if (siblingMo == null) {
                throw new DataAccessException("Add association: invalid sibling MO FDN: " + siblingMoFdn);
            }
            try {
                mo.addAssociation(associationName, siblingMo);
            } catch (
                    ModelConstraintViolationException
                    | NotDefinedInModelException
                    | AlreadyDefinedException
                    | DelegateFailureException e) {
                StringBuilder builder = new StringBuilder();
                builder.append("Add Association: got exception ");
                builder.append(e.getClass().toString());
                builder.append(" [");
                builder.append(e.getMessage());
                builder.append("]");
                logger.error(builder.toString());
                throw new DataAccessException(builder.toString());
            }
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("Add Association: Wrong parameters: ");
            builder.append(moFdn != null ? "" : "null moFdn ");
            builder.append(associationName != null ? "" : "null associationName ");
            builder.append(siblingMoFdn != null ? "" : "null siblingMoFdn");
            logger.error(builder.toString());
            throw new DataAccessSystemException(builder.toString());
        }
        logger.debug("Add association: successful end for fdn[{}] association[{}] siblingFdn[{}]", moFdn, associationName, siblingMoFdn);
    }

    /**
     * Delete the given MO specified by FDN.
     *
     * @param moFdn
     * @throws DataAccessSystemException
     * @throws DataAccessException
     */
    public void deleteMo(final String moFdn) {
        logger.debug("deleteMo: start for FDN [{}]", moFdn);
        if (moFdn != null) {
            final DataBucket dataBucket = dataPersistenceService.getLiveBucket();
            ManagedObject mo = dataBucket.findMoByFdn(moFdn);
            if (mo == null) {
                final String errorMessage = "Delete MO : invalid MO FDN : " + moFdn;
                logger.error(errorMessage);
                throw new DataAccessException(errorMessage);
            }
            try {
                dataBucket.deletePo(mo);
            } catch (
                    ObjectNotInContextException
                    | DpsIllegalStateException
                    | DelegateFailureException e) {
                final String errorMessage = NscsLogger.stringifyException(e) + " deleting MO with FDN [" + moFdn + "]";
                logger.error(errorMessage);
                throw new DataAccessException(errorMessage);
            }
        } else {
            final String errorMessage = "Delete MO : wrong parameters : null moFdn";
            logger.error(errorMessage);
            throw new DataAccessSystemException(errorMessage);
        }
        logger.debug("Delete MO : successful end for FDN [{}]", moFdn);
    }

    /**
     * Changes the attributes values of the specified Mo according to provided
     * AttributeSpecificationContainer
     *
     * @param fdn
     * @param asContainer
     * @throws DataAccessException
     *             if CmResponse getStatusCode() < 0
     * @return
     */
    public void setManagedObjectAttributes(final String fdn, final AttributeSpecificationContainer asContainer) {
        logger.debug("About to setManagedObjectAttributes with fdn={} , att={}", fdn, asContainer);
        Map<String, Object> attributes = convertAttributeSpecificationToMapForDps(asContainer);

        if (attributes != null && attributes.size() > 0) {
            DataBucket liveBucket = dataPersistenceService.getLiveBucket();
            logger.info("Got dataBucket");

            ManagedObject managedObject = liveBucket.findMoByFdn(fdn);
            if (managedObject == null) {
                logger.error("managedObject is null for fdn [{}]", fdn);
                throw new DataAccessException("Can't update attribute. Invalid MO : " + fdn);
            }
            managedObject.setAttributes(attributes);
            logger.debug("Attributes updated : {}", attributes);
        }
    }

    /**
     * Changes the attributes values of the specified Mo according to provided
     * AttributeSpecificationContainer
     *
     * @param node
     *            NodeReference instance
     * @param asContainer
     *            the AttributeSpecificationContainer
     * @throws DataAccessException
     *             if CmResponse getStatusCode() < 0
     * @return
     */
    public void setManagedObjectAttributes(final NodeReference node, final AttributeSpecificationContainer asContainer) {
        setManagedObjectAttributes(node.getFdn(), asContainer);
    }

    /**
     * Convenience method to create a WriterSpecificationBuilder
     *
     * @return new WriterSpecificationBuilder
     *      WriterSpecificationBuilder
     */
    public WriterSpecificationBuilder withSpecification() {
        return new WriterSpecificationBuilder();
    }

    /**
     * Convenience method to create a WriterSpecificationBuilder
     *
     * @return new WriterSpecificationBuilder
     *      WriterSpecificationBuilder
     */
    public WriterSpecificationBuilder withSpecification(final String type, final String namespace, final String version) {
        return (new WriterSpecificationBuilder().setMoInfo(type, namespace, version));
    }

    /**
     * Convenience method to create a WriterSpecificationBuilder
     *
     * @return new WriterSpecificationBuilder
     *      WriterSpecificationBuilder
     */
    public WriterSpecificationBuilder withSpecification(final String fdn) {
        return (new WriterSpecificationBuilder().setFdn(fdn));
    }

    private CmResponse exceptionIfFail(final CmResponse response) {
        if (response.getStatusCode() == -1) {
            logger.error("Cm-Writer data access fail with status : {}", response.getStatusMessage());
            throw new DataAccessException(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
        } else if (response.getStatusCode() < -1) {
            logger.error("Cm-Writer data access fail with status : {}", response.getStatusMessage());
            throw new DataAccessSystemException(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
        }

        return response;
    }

    private String buildErrorMessages (final String parentMoFdn, final String moType, final String moNamespace, final String moVersion, final String moName) {
        return String.format("createMo: wrong params:%s%s%s%s%s", (parentMoFdn != null ? "" : " null parentMoFdn"), (moType != null ? "" : " null moType"),
                (moNamespace != null ? "" : " null moNamespace"), (moVersion != null ? "" : " null moVersion"), (moName != null ? "" : " null moName"));
    }

    protected CmObjectSpecification buildCmObjectSpecification(final String type, final String namespace, final String version, final String name) {
        final CmObjectSpecification os = new CmObjectSpecification();
        os.setType(type);
        os.setNamespace(namespace);
        os.setName(name);
        os.setNamespaceVersion(version);

        return os;
    }

    private void addAttributeSpecification(final StringifiedAttributeSpecifications attributes, final String name, final Object value) {
        final AttributeSpecification attributeSpecification = new AttributeSpecification();
        attributeSpecification.setName(name);
        attributeSpecification.setValue(value);
        attributes.addAttributeSpecification(attributeSpecification);
    }

    /**
     * Provides a fluent API to create and change MOs.
     * <p>
     * Some examples:
     * </p>
     * <code><pre>
     *     //Creating a new element:
     *     writer.withSpecification(Model.ME_CONTEXT.networkElementSecurity.type(),
     *          Model.ME_CONTEXT.networkElementSecurity.namespace(),
     *          networkElementSecurityVersion)
     *          .setParent(Model.ME_CONTEXT.withNames(node).fdn())
     *          .setAttribute(Model.ME_CONTEXT.networkElementSecurity.NETWORK_ELEMENT_SECURITY_ID, "1")
     *          .setAttribute(Model.ME_CONTEXT.networkElementSecurity.NORMAL_USER_NAME, command.getNormalUserName())
     *          // ... (several additional attributes)
     *          .createMIBRoot();
     *
     *     //Updating an attribute:
     *     writer.withSpecification(Model.ME_CONTEXT.networkElementSecurity.withNames(node).fdn())
     *          .setAttribute(Model.ME_CONTEXT.networkElementSecurity.TARGET_GROUPS, newTargetGroups)
     *          .updateMO();
     * </pre></code>
     */
    public class WriterSpecificationBuilder {
        private String parentFdn;
        private String version;
        private String namespace;
        private String type;
        private String name = "1";
        private String fdn;
        private final Map<String, Object> attributes = new HashMap<>();

        public WriterSpecificationBuilder setParent(final String fdn) {
            this.parentFdn = fdn;
            return this;
        }

        public WriterSpecificationBuilder setFdn(final String fdn) {
            this.fdn = fdn;
            return this;
        }

        public WriterSpecificationBuilder setMoInfo(final String type, final String namespace, final String version, final String name) {
            this.type = type;
            this.namespace = namespace;
            this.version = version;
            this.name = name == null ? "1" : name;
            return this;
        }

        public WriterSpecificationBuilder setMoInfo(final String type, final String namespace, final String version) {
            return setMoInfo(type, namespace, version, null);
        }

        public WriterSpecificationBuilder setVersion(final String version) {
            this.version = version;
            return this;
        }

        public WriterSpecificationBuilder setNamespace(final String namespace) {
            this.namespace = namespace;
            return this;
        }

        public WriterSpecificationBuilder setType(final String type) {
            this.type = type;
            return this;
        }

        public WriterSpecificationBuilder setName(final String name) {
            this.name = name;
            return this;
        }

        public WriterSpecificationBuilder setAttribute(final String attribute, final Object value) {
            this.attributes.put(attribute, value);
            return this;
        }

        /**
         * Sets MO attribute if value is NOT null, void otherwise
         *
         * @param attribute
         * @param value
         * @return WriterSpecificationBuilder builder
         */
        public WriterSpecificationBuilder setNotNullAttribute(final String attribute, final Object value) {
            if (value != null) {
                this.attributes.put(attribute, value);
            }
            return this;
        }

        public WriterSpecificationBuilder addAttributes(final Map<String, Object> newAttributes) {
            this.attributes.putAll(newAttributes);
            return this;
        }

        public CmObjectSpecification toObjectSpecification() {
            final CmObjectSpecification os = buildCmObjectSpecification(type, namespace, version, name);
            final AttributeSpecificationContainer osAttributes = getAttributeSpecifications();

            os.setAttributeSpecificationContainer(osAttributes);
            return os;
        }

        private AttributeSpecificationContainer getAttributeSpecifications() {
            final StringifiedAttributeSpecifications osAttributes = new StringifiedAttributeSpecifications();
            for (final Map.Entry<String, Object> attrib : attributes.entrySet()) {
                addAttributeSpecification(osAttributes, attrib.getKey(), attrib.getValue());
            }
            return osAttributes;
        }

        public ManagedObject createMO() {
            return createMo(parentFdn, toObjectSpecification());
        }

        public ManagedObject createMIBRoot() {
            return createMibRoot(parentFdn, toObjectSpecification());
        }

        public void updateMO() {
            setManagedObjectAttributes(fdn, getAttributeSpecifications());
        }
    }

    private MibRootBuilder createPopulatedMibBuilder(final DataBucket dataBucket, final String namespace, final String type, final String namespaceVersion, final String rootName,
            final Map<String, Object> attributes) {
        return dataBucket.getMibRootBuilder().namespace(namespace).type(type).version(namespaceVersion).name(rootName).addAttributes(attributes);
    }

    private ManagedObjectBuilder createPopulatedMoBuilder(final DataBucket dataBucket, final String type, final String rootName, final Map<String, Object> attributes) {
        return dataBucket.getManagedObjectBuilder().type(type).name(rootName).addAttributes(attributes);
    }

    private Map<String, Object> convertAttributeSpecificationToMapForDps(final AttributeSpecificationContainer attributeSpecifications) {
        final Map<String, Object> result = new HashMap<>(attributeSpecifications.size());
        for (final String name : attributeSpecifications.getAttributeNames()) {
            final AttributeSpecification attributeSpecification = attributeSpecifications.getAttributeSpecification(name);
            result.put(attributeSpecification.getName(), attributeSpecification.getValue());
        }
        return result;
    }

    private NscsServiceException wrapException(final Exception e) {
        return e instanceof NscsServiceException ? (NscsServiceException) e : new DataAccessSystemException(e, NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
    }
}
