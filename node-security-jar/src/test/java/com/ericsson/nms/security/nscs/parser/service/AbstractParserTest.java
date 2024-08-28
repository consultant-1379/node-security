package com.ericsson.nms.security.nscs.parser.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsCliCommand;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.*;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.parser.NscsCliCommandParser;
import com.ericsson.nms.security.nscs.parser.impl.antlr.AntlrCommandParser;
import com.ericsson.nms.security.nscs.parser.impl.antlr.AntlrNodeNamesFileParser;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * Created by emaynes on 22/05/2014.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractParserTest {

    @Spy
    private final Logger log = LoggerFactory.getLogger(AntlrCommandParser.class);;

    @Mock
    protected BeanManager beanManager;

    @Mock
    protected Bean<?> bean;

    @Mock
    protected CreationalContext creationalContext;

    @Mock
    protected NscsContextService nscsContextService;

    @InjectMocks
    protected NscsCliCommandParser secParserService = new AntlrCommandParser();

    @Before
    public void setup() {
        Set<Bean<?>> beans = new HashSet<Bean<?>>();
        beans.add(bean);
        when(beanManager.getBeans(any(Type.class), any(Annotation.class))).thenReturn(beans);
        when(beanManager.createCreationalContext(bean)).thenReturn(creationalContext);
        when(beanManager.getReference(Mockito.eq(bean), any(Type.class), Mockito.eq(creationalContext))).thenReturn(new AntlrNodeNamesFileParser());
    }

    protected void parseCommandAndAssertSuccess(final String command, final Map<String, Object> expected) {
        parseCommandAndAssertSuccess(command, null, expected);

    }

    protected void parseCommandAndAssertSuccess(final String command, final NscsCommandType commandType, final Map<String, Object> expected) {

        final NscsPropertyCommand parsed = parse(command);

        if (commandType != null) {
            Assert.assertEquals(commandType, parsed.getCommandType());
        }

        Assert.assertEquals(expected, parsed.getProperties());
    }

    protected void parseCommandAndAssertSuccessDebug(final String command, final NscsCommandType commandType, final Map<String, Object> expected) {
        System.out.println("command to parse: " + command);

        System.out.println(" ------- expected properties");
        for (Entry<String, Object> entry : expected.entrySet()) {
            System.out.println("key: " + entry.getKey() + ", value: " + entry.getValue());
        }

        final NscsPropertyCommand parsed = parse(command);

        System.out.println("parsed commandType: " + parsed.getCommandType().toString());

        if (commandType != null) {
            Assert.assertEquals(commandType, parsed.getCommandType());
        }

        System.out.println(" ........... actual properties");
        for (Entry<String, Object> entry : parsed.getProperties().entrySet()) {
            System.out.println("key: " + entry.getKey() + ", value: " + entry.getValue());
        }

        Assert.assertEquals(expected, parsed.getProperties());
    }

    protected void parseCommandAndAssertSuccess(final NscsCliCommand command, final List<String> nodeList) {
        final NscsPropertyCommand parsed = parse(command);

        Assert.assertEquals(nodeList, parsed.getProperties().get(NscsNodeCommand.NODE_LIST_PROPERTY));
    }

    protected void parseCommandAndAssertFail(final String commandString) {
        parseCommandAndAssertFail(new NscsCliCommand(commandString));
    }

    protected void parseCommandAndAssertFail(final NscsCliCommand command) {
        try {
            parse(command);

            Assert.fail(String.format("Parsing command should fail: %s", command));
        } catch (final NscsServiceException se) {
            // ok excpected
        } catch (final Exception e) {
            Assert.fail(String.format("Unexpected error parsing command: %s", command));
        }
    }

    protected NscsPropertyCommand parse(final NscsCliCommand toParse) {
        final NscsPropertyCommand propertyCommand = secParserService.parseCommand(toParse);

        return propertyCommand;
    }

    protected NscsPropertyCommand parse(final String toParse) {
        return parse(new NscsCliCommand(toParse));
    }

    protected void assertValidSLCommand(final String command, final NscsCommandType commandType, final Object nodes, final Integer level) {
        parseCommandAndAssertSuccess(command, commandType, getSLProperties(commandType, nodes, level));
    }

    protected void assertValidCredCommand(final String command, final NscsCommandType commandType, final Object nodes, final String ru,
                                          final String rp, final String su, final String sp, final String nu, final String np, final String nasu,
                                          final String nasp, final String nbsu, final String nbsp) {
        parseCommandAndAssertSuccess(command, commandType,
                getCredCommandProperties(commandType, nodes, ru, rp, su, sp, nu, np, nasu, nasp, nbsu, nbsu));
    }

    protected void assertValidTGCommand(final String command, final NscsCommandType commandType, final Object nodes, final Object targetGroups) {
        parseCommandAndAssertSuccess(command, commandType, getTGCommandProperties(commandType, nodes, targetGroups));
    }

    protected void assertValidSshkey(final String command, final NscsCommandType commandType, final Object nodes, final String algorithmTypeSize) {
        parseCommandAndAssertSuccessDebug(command, commandType, getKeygenCommandProperties(commandType, nodes, algorithmTypeSize));
    }

    protected void assertValidSshkey(final String command, final NscsCommandType commandType, final Object nodes) {
        parseCommandAndAssertSuccessDebug(command, commandType, getKeygenCommandProperties(commandType, nodes));
    }

    protected void assertValidKeygen(final String command, final NscsCommandType commandType, final String nodefile, byte[] stream,
                                     final String algorithmTypeSize) {
        parseCommandAndAssertSuccessDebug(command, commandType, getKeygenCommandPropertiesStream(commandType, nodefile, stream, algorithmTypeSize));
    }

    /**
     * Returns SL (get or set) command properties
     * 
     * @param cmd
     *            type
     * @param nodes
     *            List<String> with the list of nodes OR "*" as String
     * @param level
     *            or null if not specified in the command
     * @return valid command properties
     */
    protected HashMap<String, Object> getSLProperties(final NscsCommandType cmd, final Object nodes, final Integer level) {
        return new HashMap<String, Object>() {
            {
                put(NscsPropertyCommand.COMMAND_TYPE_PROPERTY, cmd.toString());
                put(NscsNodeCommand.NODE_LIST_PROPERTY, nodes);
                if (level != null) {
                    put(CppSecurityLevelCommand.SECURITY_LEVEL_PROPERTY, level.toString());
                }
            }
        };
    }

    /**
     * Returns CRED (create or update) command properties
     * 
     * @param cmd
     *            type
     * @param nodes
     *            List<String> with the list of nodes OR "*" as String
     * @param ru
     *            rootUserName or null if not specified in the command
     * @param rp
     *            rootUserPassword or null if not specified in the command
     * @param su
     *            secureUserName or null if not specified in the command
     * @param sp
     *            secureUserPassword or null if not specified in the command
     * @param nu
     *            normalUserName or null if not specified in the command
     * @param np
     *            normalUserPassword or null if not specified in the command
     * @param nasu
     *            nwieaSecureUserName or null if not specified in the command
     * @param nasp
     *            nwieaSecureUserPassword or null if not specified in the command
     * @param nbsu
     *            nwiebSecureUserName or null if not specified in the command
     * @param nbsp
     *            nwiebSecureUserPassword or null if not specified in the command
     *
     * @return valid command properties
     */
    protected HashMap<String, Object> getCredCommandProperties(final NscsCommandType cmd, final Object nodes, final String ru, final String rp,
                                                               final String su, final String sp, final String nu, final String np, final String nasu,
                                                               final String nasp, final String nbsu, final String nbsp) {
        return new HashMap<String, Object>() {
            {
                put(NscsPropertyCommand.COMMAND_TYPE_PROPERTY, cmd.toString());
                put(NscsNodeCommand.NODE_LIST_PROPERTY, nodes);
                if (ru != null) {
                    put(CredentialsCommand.ROOT_USER_NAME_PROPERTY, ru);
                }
                if (rp != null) {
                    put(CredentialsCommand.ROOT_USER_PASSWORD_PROPERTY, rp);
                }
                if (su != null) {
                    put(CredentialsCommand.SECURE_USER_NAME_PROPERTY, su);
                }
                if (sp != null) {
                    put(CredentialsCommand.SECURE_USER_PASSWORD_PROPERTY, sp);
                }
                if (nu != null) {
                    put(CredentialsCommand.NORMAL_USER_NAME_PROPERTY, nu);
                }
                if (np != null) {
                    put(CredentialsCommand.NORMAL_USER_PASSWORD_PROPERTY, np);
                }
                if (nasu != null) {
                    put(CredentialsCommand.NWIEA_SECURE_USER_NAME_PROPERTY, nasu);
                }
                if (nasp != null) {
                    put(CredentialsCommand.NWIEA_SECURE_PASSWORD_PROPERTY, nasp);
                }
                if (nbsu != null) {
                    put(CredentialsCommand.NWIEB_SECURE_USER_NAME_PROPERTY, nbsu);
                }
                if (nbsp != null) {
                    put(CredentialsCommand.NWIEB_SECURE_PASSWORD_PROPERTY, nbsp);
                }
            }
        };
    }

    protected HashMap<String, Object> getKeygenCommandProperties(final NscsCommandType cmd, final Object nodes, final String algorithmTypeSize) {
        return new HashMap<String, Object>() {
            {
                put(NscsPropertyCommand.COMMAND_TYPE_PROPERTY, cmd.toString());
                put(NscsNodeCommand.NODE_LIST_PROPERTY, nodes);
                if (algorithmTypeSize != null) {
                    put(KeyGeneratorCommand.ALGORITHM_TYPE_SIZE_PROPERTY, algorithmTypeSize);
                }
            }
        };
    }

    protected HashMap<String, Object> getKeygenCommandProperties(final NscsCommandType cmd, final Object nodes) {
        return new HashMap<String, Object>() {
            {
                put(NscsPropertyCommand.COMMAND_TYPE_PROPERTY, cmd.toString());
                if (nodes != null) {
                    put(NscsNodeCommand.NODE_LIST_PROPERTY, nodes);
                }
            }
        };
    }

    protected HashMap<String, Object> getKeygenCommandProperties(final NscsCommandType cmd, final String nodefile, final String algorithmTypeSize) {
        return new HashMap<String, Object>() {
            {
                put(NscsPropertyCommand.COMMAND_TYPE_PROPERTY, cmd.toString());
                put(NscsNodeCommand.NODE_LIST_FILE_PROPERTY, nodefile);
                if (algorithmTypeSize != null) {
                    put(KeyGeneratorCommand.ALGORITHM_TYPE_SIZE_PROPERTY, algorithmTypeSize);
                }
            }
        };
    }

    protected HashMap<String, Object> getKeygenCommandPropertiesStream(final NscsCommandType cmd, final String nodefile, final byte[] stream,
                                                                       final String algorithmTypeSize) {
        return new HashMap<String, Object>() {
            {
                put(NscsPropertyCommand.COMMAND_TYPE_PROPERTY, cmd.toString());
                put(NscsNodeCommand.NODE_LIST_FILE_PROPERTY, nodefile);
                put("file:", stream);
                if (algorithmTypeSize != null) {
                    put(KeyGeneratorCommand.ALGORITHM_TYPE_SIZE_PROPERTY, algorithmTypeSize);
                }
            }
        };
    }

    /**
     * Returns TARGET GROUP command properties
     * 
     * @param cmd
     *            type
     * @param nodes
     *            List<String> with the list of nodes OR "*" as String
     * @param targetGroups
     *            as List<String>
     * 
     * @return valid command properties
     */
    protected HashMap<String, Object> getTGCommandProperties(final NscsCommandType cmd, final Object nodes, final Object targetGroups) {
        return new HashMap<String, Object>() {
            {
                put(NscsPropertyCommand.COMMAND_TYPE_PROPERTY, cmd.toString());
                put(NscsNodeCommand.NODE_LIST_PROPERTY, nodes);
                put(TargetGroupsCommand.TARGET_GROUP_PROPERTY, targetGroups);
            }
        };
    }
}
