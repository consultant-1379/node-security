package com.ericsson.nms.security.nscs.handler.command.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanAttributes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Decorator;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.inject.spi.Producer;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;

import com.ericsson.cds.cdi.support.rule.CdiInjectorRule;
import com.ericsson.cds.cdi.support.rule.ImplementationInstance;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.command.types.TargetGroupsCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModel;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelMock;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.moget.MOGetService;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory.NscsMOGetServiceQualifier;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.gim.EcimUserManager;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.itpf.security.cryptography.CryptographyService;
import com.ericsson.oss.services.cm.cmshared.dto.AttributeSpecificationContainer;
import com.ericsson.oss.services.cm.cmshared.dto.CmObjectSpecification;

/**
 * Tests the CreateCredentialsHandler that Creates a NetworkElementSecurity Mo associated to each of the specified nodes
 *
 * @see CreateCredentialsHandler
 *
 */

public class CommonHandlerFastCommit {

    public static final String NODE_NAME_HEADER = "Node Name        ";
    public static final String NODE_SECURITY_LEVEL_HEADER = "Node Security Level";
    public static final String CHAR_ENCODING = "UTF-8";
    protected static final String PASSWORD2 = "password";
    protected static final String NETWORK_ELEMENT_SECURITY_NS = "networkElementSecurityNamespace";
    protected static final String NETWORK_ELEMENT_SECURITY_VERSION = "networkElementSecurityVersion";
    protected static final String TARGET_GROUP3 = "TargetGroup3";
    protected static final String TARGET_GROUP2 = "TargetGroup2";
    protected static final String TARGET_GROUP1 = "TargetGroup1";
    final Map<String, Object> commandMapAllParams = new HashMap<String, Object>() {

        protected static final long serialVersionUID = 1L;

        {
            {
                put(CredentialsCommand.NORMAL_USER_NAME_PROPERTY, NetworkElementSecurity.NORMAL_USER_NAME);
                put(CredentialsCommand.NORMAL_USER_PASSWORD_PROPERTY, NetworkElementSecurity.NORMAL_USER_PASSWORD);
                put(CredentialsCommand.ROOT_USER_NAME_PROPERTY, NetworkElementSecurity.ROOT_USER_NAME);
                put(CredentialsCommand.ROOT_USER_PASSWORD_PROPERTY, NetworkElementSecurity.ROOT_USER_PASSWORD);
                put(CredentialsCommand.SECURE_USER_NAME_PROPERTY, NetworkElementSecurity.SECURE_USER_NAME);
                put(CredentialsCommand.SECURE_USER_PASSWORD_PROPERTY, NetworkElementSecurity.SECURE_USER_PASSWORD);
                put(CredentialsCommand.NWIEA_SECURE_USER_NAME_PROPERTY, NetworkElementSecurity.NWIEA_SECURE_USER_NAME);
                put(CredentialsCommand.NWIEA_SECURE_PASSWORD_PROPERTY, NetworkElementSecurity.NWIEA_SECURE_USER_PASSWORD);
                put(CredentialsCommand.NWIEB_SECURE_USER_NAME_PROPERTY, NetworkElementSecurity.NWIEB_SECURE_USER_NAME);
                put(CredentialsCommand.NWIEB_SECURE_PASSWORD_PROPERTY, NetworkElementSecurity.NWIEB_SECURE_USER_PASSWORD);
            }
        }
    };
    final Map<String, Object> commandMapNoParams = new HashMap<String, Object>();
    final Map<String, Object> commandMapSecureParams = new HashMap<String, Object>() {

        protected static final long serialVersionUID = 1L;

        {
            {
                put(CredentialsCommand.SECURE_USER_NAME_PROPERTY, NetworkElementSecurity.SECURE_USER_NAME);
                put(CredentialsCommand.SECURE_USER_PASSWORD_PROPERTY, NetworkElementSecurity.SECURE_USER_PASSWORD);
            }
        }
    };
    @Rule
    public CdiInjectorRule cdiInjectorRule = new CdiInjectorRule(this);
    @Inject
    public NscsCapabilityModelMock nscsCapabilityModelMock;
    boolean provideEcimSecurePassword = false;
    @ImplementationInstance()
    EcimUserManager ecimUserManager = new EcimUserManager() {

        @Override
        public Map<String, Boolean> provideEcimSecurePassword() {
            final HashMap<String, Boolean> ret = new HashMap<String, Boolean>();
            ret.put("", true);
            provideEcimSecurePassword = true;
            return ret;
        }
    };
    @ImplementationInstance()
    BeanManager beanManager = new BeanManager() {

        @Override
        public <T> AnnotatedType<T> createAnnotatedType(final Class<T> arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Bean<?> createBean(final BeanAttributes<?> arg0, final Class<?> arg1, final InjectionTarget<?> arg2) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Bean<?> createBean(final BeanAttributes<?> arg0, final Class<?> arg1, final Producer<?> arg2) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T> BeanAttributes<T> createBeanAttributes(final AnnotatedType<T> arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public BeanAttributes<?> createBeanAttributes(final AnnotatedMember<?> arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> CreationalContext<T> createCreationalContext(final Contextual<T> arg0) {
            final CreationalContext<?> creationalContext = new CreationalContext<T>() {

                @Override
                public void push(final T arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void release() {
                    // TODO Auto-generated method stub

                }
            };
            return (CreationalContext<T>) creationalContext;
        }

        @Override
        public InjectionPoint createInjectionPoint(final AnnotatedField<?> arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public InjectionPoint createInjectionPoint(final AnnotatedParameter<?> arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T> InjectionTarget<T> createInjectionTarget(final AnnotatedType<T> arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void fireEvent(final Object arg0, final Annotation... arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public Set<Bean<?>> getBeans(final String arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Set<Bean<?>> getBeans(final Type arg0, final Annotation... arg1) {
            final Set<Bean<?>> beans = new HashSet<Bean<?>>();
            if (NscsCapabilityModel.class.equals(arg0)) {
                beans.add((Bean<?>) nscsCapabilityModelMock);
            } else if (MOGetService.class.equals(arg0)) {
                final String qualifier = ((NscsMOGetServiceQualifier) arg1[0]).moGetServiceType();
                if ("CPP".equals(qualifier)) {

                } else if ("ECIM".equals(qualifier)) {

                } else if ("EOI".equals(qualifier)) {

                }
            }
            return beans;
        }

        @Override
        public Context getContext(final Class<? extends Annotation> arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ELResolver getELResolver() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T extends Extension> T getExtension(final Class<T> arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object getInjectableReference(final InjectionPoint arg0, final CreationalContext<?> arg1) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Set<Annotation> getInterceptorBindingDefinition(final Class<? extends Annotation> arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Bean<?> getPassivationCapableBean(final String arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object getReference(final Bean<?> arg0, final Type arg1, final CreationalContext<?> arg2) {
            return nscsCapabilityModelMock;
        }

        @Override
        public Set<Annotation> getStereotypeDefinition(final Class<? extends Annotation> arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isInterceptorBinding(final Class<? extends Annotation> arg0) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isNormalScope(final Class<? extends Annotation> arg0) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isPassivatingScope(final Class<? extends Annotation> arg0) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isQualifier(final Class<? extends Annotation> arg0) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isScope(final Class<? extends Annotation> arg0) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isStereotype(final Class<? extends Annotation> arg0) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public <X> Bean<? extends X> resolve(final Set<Bean<? extends X>> arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<Decorator<?>> resolveDecorators(final Set<Type> arg0, final Annotation... arg1) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<Interceptor<?>> resolveInterceptors(final InterceptionType arg0, final Annotation... arg1) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T> Set<ObserverMethod<? super T>> resolveObserverMethods(final T arg0, final Annotation... arg1) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void validate(final InjectionPoint arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public ExpressionFactory wrapExpressionFactory(final ExpressionFactory arg0) {
            // TODO Auto-generated method stub
            return null;
        }
    };
    @ImplementationInstance()
    CryptographyService cryptographyService = new CryptographyService() {
        @Override
        public byte[] encrypt(final byte[] toEncrypt) {
            return PASSWORD2.getBytes();
        }

        @Override
        public byte[] decrypt(final byte[] encrypted) {
            return PASSWORD2.getBytes();
        }
    };
    @ImplementationInstance()
    NscsModelServiceImpl nscsModelServiceImpl = new NscsModelServiceImpl() {
        @Override
        public NscsModelInfo getLatestVersionOfNormalizedModel(final String model) {
            final String networkElementSecurityType = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type();
            final String networkElementSecurityNamespace = NETWORK_ELEMENT_SECURITY_NS;
            final String networkElementSecurityVersion = NETWORK_ELEMENT_SECURITY_VERSION;
            final NscsModelInfo nscsModelInfo = new NscsModelInfo(SchemaConstants.DPS_PRIMARYTYPE, networkElementSecurityNamespace,
                    networkElementSecurityType, networkElementSecurityVersion);
            return nscsModelInfo;
        }
    };
    @ImplementationInstance()
    NscsCMWriterService nscsCMWriterService = new NscsCMWriterService() {
        @Override
        public ManagedObject createMibRoot(final String parentFdn, final CmObjectSpecification specification) {
            return null;
        }

        @Override
        public void setManagedObjectAttributes(final String fdn, final AttributeSpecificationContainer asContainer) {
        }
    };

    @ImplementationInstance()
    NscsCMReaderService nscsCMReaderService = new NscsCMReaderService() {
        @Override
        public MoObject getMoObjectByFdn(final String fdn) {
            final MoObject moObj = org.mockito.Mockito.mock(MoObject.class);
            return (moObj);
        }
    };

    List<NodeReference> nodeRefList = new ArrayList<NodeReference>();
    List<NormalizableNodeReference> normNodeRefList = new ArrayList<NormalizableNodeReference>();
    @ImplementationInstance()
    CommandContext commandContext = new CommandContext() {

        @Override
        public List<NodeReference> getAllNodes() {
            return nodeRefList;
        }

        @Override
        public List<NormalizableNodeReference> getValidNodes() {
            return normNodeRefList;
        }

        @Override
        public Set<NodeReference> getInvalidNodes() {
            return new HashSet<NodeReference>();
        }

        @Override
        public List<NodeReference> getNodesNotFound() {
            return null;
        }

        @Override
        public List<NodeReference> toNormalizedRef(final List<? extends NormalizableNodeReference> nodes) {
            return nodeRefList;
        }

        @Override
        public NodeReference getCommandReference(final NodeReference node) {
            return null;
        }

        @Override
        public void setAsInvalidOrFailed(final Collection<NodeReference> nodes, final NscsServiceException error) {
        }

        @Override
        public void setAsInvalidOrFailed(final NodeReference node, final NscsServiceException error) {
        }

        @Override
        public void setAsInvalidOrFailedAndThrow(final Collection<NodeReference> nodes, final NscsServiceException error) {
        }

        @Override
        public void setAsInvalidOrFailedAndThrow(final NodeReference node, final NscsServiceException error) {
        }

        @Override
        public void setAsValid(final NodeReference node) {
        }
    };

    @Before
    public void setupTest() {

        provideEcimSecurePassword = false;
    }

    /**
     *
     */
    protected void setNodeContext(final String nodeName) {
        if ("radioNode".equals(nodeName)) {
            normNodeRefList.add(MockUtils.createNormalizableNodeRef("RadioNode", "radioNode"));
        } else if ("picoNode".equals(nodeName)) {
            normNodeRefList.add(MockUtils.createNormalizableNodeRef("MSRBS_V1", "picoNode"));
        } else {
            normNodeRefList.add(MockUtils.createNormalizableNodeRef("ERBS", "cppNode"));
        }

        for (final NormalizableNodeReference node : normNodeRefList) {
            nodeRefList.add(node.getNormalizedRef());
        }
    }

    /**
     * Tests CreateCredentialsHandler Positive flow
     *
     * @throws Exception
     */

    protected List<String> buildTargetGroupList() {
        final List<String> targetGroups = new ArrayList<String>();
        targetGroups.add(TARGET_GROUP1);
        targetGroups.add(TARGET_GROUP2);
        targetGroups.add(TARGET_GROUP3);
        return targetGroups;
    }

    protected CredentialsCommand buildCredentialsCommand(final NscsCommandType nscsCommandType, final String node,
                                                         final Map<String, Object> commandMap) {
        setNodeContext(node);
        final CredentialsCommand targetGroupsCommand = new CredentialsCommand();
        targetGroupsCommand.setCommandType(nscsCommandType);
        commandMap.put(NscsNodeCommand.NODE_LIST_PROPERTY, Arrays.asList(node));
        commandMap.put(TargetGroupsCommand.TARGET_GROUP_PROPERTY, buildTargetGroupList());

        targetGroupsCommand.setProperties(commandMap);
        return targetGroupsCommand;
    }

    protected CredentialsCommand buildCredentialsCommand(final NscsCommandType nscsCommandType, final String node,
                                                         final Map<String, Object> commandMap, final String ldapUserOption) {
        setNodeContext(node);
        final CredentialsCommand targetGroupsCommand = new CredentialsCommand();
        targetGroupsCommand.setCommandType(nscsCommandType);
        commandMap.put(NscsNodeCommand.NODE_LIST_PROPERTY, Arrays.asList(node));
        commandMap.put(TargetGroupsCommand.TARGET_GROUP_PROPERTY, buildTargetGroupList());
        commandMap.put(CredentialsCommand.LDAP_USER_ENABLE_PROPERTY, ldapUserOption);
        targetGroupsCommand.setProperties(commandMap);
        return targetGroupsCommand;
    }

}
