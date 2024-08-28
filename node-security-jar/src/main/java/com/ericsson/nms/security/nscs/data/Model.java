package com.ericsson.nms.security.nscs.data;

import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ManagementSystem;
import com.ericsson.nms.security.nscs.data.ModelDefinition.MeContext;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkFunctionVirtualizationOrchestrator;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NormalizedRootMO;
import com.ericsson.nms.security.nscs.data.ModelDefinition.VirtualInfrastructureManager;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CloudInfrastructureManager;
import com.ericsson.nms.security.nscs.data.ModelDefinition.VirtualNetworkFunctionManager;

/**
 * Convenience class to access Model's constants structure.
 *
 * @see com.ericsson.nms.security.nscs.data.ModelDefinition
 *
 * @author emaynes on 02/05/2014.
 */
public abstract class Model {

    public static final MeContext ME_CONTEXT = new MeContext();

    public static final NetworkElement NETWORK_ELEMENT = new NetworkElement();

    public static final VirtualNetworkFunctionManager VNFM = new VirtualNetworkFunctionManager();

    public static final NetworkFunctionVirtualizationOrchestrator NFVO = new NetworkFunctionVirtualizationOrchestrator();

    public static final ManagementSystem MS = new ManagementSystem();

    public static final VirtualInfrastructureManager VIM = new VirtualInfrastructureManager();

    public static final CloudInfrastructureManager CIM = new CloudInfrastructureManager();

    /**
     * Returns the NormalizedRootMO internal class (not the actual MO!) representing the root of normalized model of a node of given normalized FDN.
     * 
     * The method SHALL NOT be invoked for CBP-OI Yang based node types!
     *
     * @param fdn
     *            the normalized FDN of the involved node.
     * @return the internal class NormalizedRootMO representing the normalized root MO or null if not found.
     */
    public static final NormalizedRootMO getNomalizedRootMO(final String fdn) {
        if (fdn.contains(NETWORK_ELEMENT.type())) {
            return NETWORK_ELEMENT;
        } else if (fdn.contains(VNFM.type())) {
            return VNFM;
        } else if (fdn.contains(NFVO.type())) {
            return NFVO;
        } else if (fdn.contains(MS.type())) {
            return MS;
        } else if (fdn.contains(VIM.type())) {
            return VIM;
        } else if (fdn.contains(CIM.type())) {
            return CIM;
        }
        return null;
    }

    public static final ComEcimManagedElement SGSN_MME_MANAGED_ELEMENT = new ComEcimManagedElement(ModelDefinition.SGSN_MME_TOP_NS);

    public static final ComEcimManagedElement COM_MANAGED_ELEMENT = new ComEcimManagedElement(ModelDefinition.COM_TOP_NS);

    /**
     * Returns the Mo internal class (not the actual MO!) representing the root of mirror model of a node of given normalizable FDN. The namespace of
     * actual root MO is provided too.
     * 
     * The method SHALL NOT be invoked for CBP-OI Yang based node types!
     *
     * @param fdn
     *            the normalizable FDN of the involved node.
     * @param namespace
     *            the namespace of the actual root MO
     * @return the internal class Mo representing the mirror root MO or null if not found.
     */
    public static final Mo getMirrorRoot(final String fdn, final String namespace) {
        if (ME_CONTEXT.isPresent(fdn)) {
            // the normalizable FDN contains MeContext=
            if (ME_CONTEXT.managedElement.namespace().equals(namespace)) {
                return ME_CONTEXT.managedElement;
            } else if (ME_CONTEXT.mgwManagedElement.namespace().equals(namespace)) {
                return ME_CONTEXT.mgwManagedElement;
            } else if (ME_CONTEXT.mrsManagedElement.namespace().equals(namespace)) {
                return ME_CONTEXT.mrsManagedElement;
            } else if (ME_CONTEXT.sgsnMmeManagedElement.namespace().equals(namespace)) {
                return ME_CONTEXT.sgsnMmeManagedElement;
            } else if (ME_CONTEXT.comManagedElement.namespace().equals(namespace)) {
                return ME_CONTEXT.comManagedElement;
            } else if (ME_CONTEXT.rncManagedElement.namespace().equals(namespace)) {
                return ME_CONTEXT.rncManagedElement;
            } else if (ME_CONTEXT.rbsManagedElement.namespace().equals(namespace)) {
                return ME_CONTEXT.rbsManagedElement;
            } else if (ME_CONTEXT.namespace().equals(namespace)) {
                /**
                 * This is a special case for node types (ESC and SCU) which are of ERS-SN platformType, supporting both ECIM and YANG MOs. For these
                 * node types the rootMoType defined in ENM model is "//OSS_TOP/MeContext/", so the MeContext MO is mandatory and contains 2 children:
                 * the ManagedElement MO (with namespace SCU_Top) as top MO of ECIM MOs hierarchy and the SDN MO (with namespace
                 * "http://www.ericsson.com/sdn") as top MO of YANG MOs hierarchy.
                 * 
                 * Even if the namespace of ManagedElement should be SCU_Top, the implementation follows this behavior: when the namespace of
                 * rootMoType (returned by Model Service) matches the namespace of MeContext (OSS_TOP) then the mirror root Mo is returned as
                 * ME_CONTEXT.comManagedElement.
                 * 
                 * This is an instance of class Mo (internal class of node-security containing essentially the name and namespace of a MO) of name
                 * ManagedObject and namespace ComTop.This does not cause issues since node-security does not use, in current implementation, the
                 * namespace of such Mo.
                 **/
                return ME_CONTEXT.comManagedElement;
            } else {
                return null;
            }
        } else {
            // the normalizable FDN does not contain MeContext=
            if (SGSN_MME_MANAGED_ELEMENT.namespace().equals(namespace) && SGSN_MME_MANAGED_ELEMENT.isPresent(fdn)) {
                return SGSN_MME_MANAGED_ELEMENT;
            } else if (COM_MANAGED_ELEMENT.namespace().equals(namespace) && COM_MANAGED_ELEMENT.isPresent(fdn)) {
                return COM_MANAGED_ELEMENT;
            } else {
                return null;
            }
        }
    }

}
