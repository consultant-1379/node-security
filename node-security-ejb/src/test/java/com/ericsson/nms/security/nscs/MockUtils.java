package com.ericsson.nms.security.nscs;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.mockito.Matchers;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;

/**
 * Utility class to facilitate creation of unit tests for command handlers.
 * <p>
 * Allows easy configuration of CommandContext and creation of Mock NormalizableNodeReference and NodeReference
 * </p>
 *
 * @author emaynes.
 */
public class MockUtils {

    private static final String CPP_NE_OSS_MODEL_IDENTITY = "397-5538-122";

    public static void setupCommandContext(final CommandContext commandContextMock, final String... validNodes) {
        final List<NormalizableNodeReference> normNodeRefList = createNormalizableNodeRefList(validNodes);
        doReturn(normNodeRefList).when(commandContextMock).getAllNodes();
        doReturn(normNodeRefList).when(commandContextMock).getValidNodes();
        doReturn(new HashSet<NodeReference>()).when(commandContextMock).getInvalidNodes();
        doReturn(new LinkedList<NodeReference>() {
            {
                for (final NormalizableNodeReference normalizableNodeReference : normNodeRefList) {
                    add(normalizableNodeReference.getNormalizedRef());
                }
            }
        }).when(commandContextMock).toNormalizedRef(Matchers.<List<NormalizableNodeReference>> any());
    }

    public static NormalizableNodeReference createNormNodeRefWithMeContext(final String targetName, final String targetType,
            final String targetModelIdentity, final NodeReference nodeRef, final NscsCMReaderService reader) {
        final NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference.class);
        doReturn(targetName).when(normNodeRef).getName();
        doReturn("MeContext=" + targetName).when(normNodeRef).getFdn();
        doReturn(targetType).when(normNodeRef).getNeType();
        doReturn(targetModelIdentity).when(normNodeRef).getOssModelIdentity();
        doReturn(nodeRef).when(normNodeRef).getNormalizedRef();
        doReturn(true).when(normNodeRef).hasNormalizedRef();
        if (reader != null) {
            doReturn(normNodeRef).when(reader).getNormalizedNodeReference(nodeRef);
            doReturn(normNodeRef).when(reader).getNormalizableNodeReference(nodeRef);
        }
        return normNodeRef;
    }

    public static NormalizableNodeReference createNormNodeRef(final String targetName, final String targetType, final String targetModelIdentity,
            final NodeReference nodeRef, final NscsCMReaderService reader) {
        final NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference.class);
        doReturn(targetName).when(normNodeRef).getName();
        doReturn("ManagedElement=" + targetName).when(normNodeRef).getFdn();
        doReturn(targetType).when(normNodeRef).getNeType();
        doReturn(targetModelIdentity).when(normNodeRef).getOssModelIdentity();
        doReturn(nodeRef).when(normNodeRef).getNormalizedRef();
        doReturn(true).when(normNodeRef).hasNormalizedRef();
        if (reader != null) {
            doReturn(normNodeRef).when(reader).getNormalizedNodeReference(nodeRef);
            doReturn(normNodeRef).when(reader).getNormalizableNodeReference(nodeRef);
        }
        return normNodeRef;
    }

    public static List<NormalizableNodeReference> createNormalizableNodeRefList(final String... nodeNames) {
        final List<NormalizableNodeReference> refs = new LinkedList<>();
        for (final String nodeName : nodeNames) {
            refs.add(createNormalizableNodeRef(nodeName));
        }

        return refs;
    }

    public static NormalizableNodeReference createNormalizableNodeRef(final String nodeName) {
        final NormalizableNodeReference reference = mock(NormalizableNodeReference.class);
        doReturn(nodeName).when(reference).getName();
        doReturn(Model.ME_CONTEXT.withNames(nodeName).fdn()).when(reference).getFdn();
        doReturn("ERBS").when(reference).getNeType();
        doReturn(CPP_NE_OSS_MODEL_IDENTITY).when(reference).getOssModelIdentity();

        final NodeReference normRef = createNetworkElementNodeRef(nodeName);

        doReturn(normRef).when(reference).getNormalizedRef();

        return reference;
    }

    public static NodeReference createNetworkElementNodeRef(final String nodeName) {
        final NodeReference normRef = mock(NodeReference.class);
        doReturn(nodeName).when(normRef).getName();
        doReturn(Model.NETWORK_ELEMENT.withNames(nodeName).fdn()).when(normRef).getFdn();
        return normRef;
    }
}
