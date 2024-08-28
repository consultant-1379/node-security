lexer grammar SecCommandLexer;

/*
Defining new command: STEP 1

When defining a new command it is VERY important to respect the following syntax:

    <NscsCommandType enum entry> : ('<expected text in CLI>') -> mode(inside_command);

It is possible to specify multiple options for a command :

    <NscsCommandType enum entry> : ('<expected text in CLI>' | '<alternative text in CLI>') -> mode(inside_command);

Example:

     Entry in NscsCommandType enum
       |
       |               Expected text in the CLI interface
       |                   |                     |
       v                   v                     v
    CPP_GET_SL : ('cpp-get-securitylevel' | 'cpp-get-sl') -> mode(inside_command);
               ^ ^                        ^             ^  ^     ^
               | |                        |             |  |     |
               -----------------------------------------------------> Mandatory syntax elements!
*/

CPP_GET_SL : ('securitylevel get' | 'sl get') -> mode(inside_command);

CPP_SET_SL : ('securitylevel set' | 'sl set') -> mode(inside_command);

//CPP_INSTALL_LAAD : ('laad update') -> mode(inside_command);

CREATE_CREDENTIALS : ('credentials create' | 'creds create') -> mode(inside_command);

UPDATE_CREDENTIALS: ('credentials update' | 'creds update') -> mode(inside_command);

GET_CREDENTIALS : ('credentials get' | 'creds get') -> mode(inside_command);

ADD_TARGET_GROUPS  : ('targetgroup add') -> mode(inside_command);

CPP_IPSEC_STATUS : ('ipsec --status' | 'ipsec -s') -> mode(inside_command);

CPP_IPSEC : ('ipsec') -> mode(inside_command);

CREATE_SSH_KEY : ('sshkey create' | 'skc') -> mode(inside_command);

UPDATE_SSH_KEY : ('sshkey update' | 'sku') -> mode(inside_command);

DELETE_SSH_KEY : ('sshkey delete' | 'skd') -> mode(inside_command);

IMPORT_NODE_SSH_PRIVATE_KEY : ('sshkey import' | 'ski') -> mode(inside_command);

TEST_COMMAND : ('test') -> mode(inside_command);

CERTIFICATE_ISSUE : ('certificate issue' | 'cert issue') -> mode(inside_command);

SNMP_AUTHPRIV : ('snmp authpriv') -> mode(inside_command);

SNMP_AUTHNOPRIV : ('snmp authnopriv') -> mode(inside_command);

GET_SNMP : ('snmp get') -> mode(inside_command);

TRUST_DISTRIBUTE : ('trust distribute' | 'trust distr') -> mode(inside_command);

//SET_ENROLLMENT : ('set enrollment' | 'set enr') -> mode(inside_command);

GET_CERT_ENROLL_STATE : ('certificate get' | 'cert get') -> mode(inside_command);

GET_TRUST_CERT_INSTALL_STATE : ('trust get') -> mode(inside_command);

CERTIFICATE_REISSUE : ('certificate reissue' | 'cert reissue') -> mode(inside_command);

LDAP_CONFIGURATION : ('ldap configure') -> mode(inside_command);

LDAP_RECONFIGURATION : ('ldap reconfigure') -> mode(inside_command);

LDAP_RENEW : ('ldap renew') -> mode(inside_command);

LDAP_PROXY_GET : ('ldap proxy get') -> mode(inside_command);

LDAP_PROXY_SET : ('ldap proxy set') -> mode(inside_command);

LDAP_PROXY_DELETE : ('ldap proxy delete') -> mode(inside_command);

TRUST_REMOVE : ('trust remove' | 'trust rm') -> mode(inside_command);

CRL_CHECK_ENABLE : ('enable crlcheck') -> mode(inside_command);

GET_JOB : ('job get') -> mode(inside_command);

CRL_CHECK_DISABLE : ('disable crlcheck') -> mode(inside_command);

CRL_CHECK_GET_STATUS : ('read crlcheck') -> mode(inside_command);

ON_DEMAND_CRL_DOWNLOAD: ('crl download' | 'crl dl') -> mode(inside_command);

SET_CIPHERS: ('set ciphers') -> mode(inside_command);

GET_CIPHERS: ('get ciphers') -> mode(inside_command);

ENROLLMENT_INFO_FILE : ('generateenrollmentinfo' | 'geninfo') -> mode(inside_command);

RTSEL_ACTIVATE : ('rtsel activate' | 'rtsel actvt') -> mode(inside_command);

RTSEL_DEACTIVATE : ('rtsel deactivate' | 'rtsel dactvt') -> mode(inside_command);

RTSEL_GET : ('rtsel get') -> mode(inside_command);

RTSEL_DELETE : ('rtsel delete' | 'rtsel d') -> mode(inside_command);

HTTPS_ACTIVATE : ('https activate' | 'https act') -> mode(inside_command);

HTTPS_DEACTIVATE : ('https deactivate' | 'https deact') -> mode(inside_command);

HTTPS_GET_STATUS : ('https getstatus' | 'https get') -> mode(inside_command);

FTPES_ACTIVATE : ('ftpes activate' | 'ftpes act') -> mode(inside_command);

FTPES_DEACTIVATE : ('ftpes deactivate' | 'ftpes deact') -> mode(inside_command);

FTPES_GET_STATUS : ('ftpes getstatus' | 'ftpes get') -> mode(inside_command);

GET_NODE_SPECIFIC_PASSWORD : ('ftp get') -> mode(inside_command);

CAPABILITY_GET : ('capability get' | 'cap get') -> mode(inside_command);

LAAD_FILES_DISTRIBUTE : ('laad distribute' | 'laad distr') -> mode(inside_command);

NTP_CONFIGURE : ('ntp configure' | 'ntp conf') -> mode(inside_command);

NTP_REMOVE : ('ntp remove' | 'ntp rem') -> mode(inside_command);

NTP_LIST : ('ntp list' | 'ntp ls') -> mode(inside_command);

SSO_ENABLE : ('sso enable') -> mode(inside_command);

SSO_DISABLE : ('sso disable') -> mode(inside_command);

SSO_GET : ('sso get') -> mode(inside_command);

/* ==== DO NOT WRITE ANYTHING BELOW THIS POINT UNLESS YOU KNOW EXACTLY WHAT YOU ARE DOING ====*/


mode inside_command;

FILE : 'file:' ;

ALL : '*' | '-a' | '--all' ;

INT : [0-9]+;

TEXT : [a-zA-Z0-9_%@>#$<!:/+.?*=-]+;

FILENAME : FILE(TEXT | QUOTED_FILE_NAME_TEXT | INT | [\./])+ ;

PROPERTY_PREFIX : '--'|'-' ;

fragment WS : [ \t\r\n]+;
fragment SPACE : '\t' | ' ' | '\r' | '\u000C' | '\u0009' | '\u000D';
WHITESPACE : WS -> skip;

//Separator in the node list
DOUBLE_QUOTES : '"' -> skip, mode(inside_quotes);
SEPARATOR : ';' | ',';
LIST_END : ';' ;
SQUARE_BRACKET_OPEN : '[' ;
SQUARE_BRACKET_CLOSE : ']' ;

mode inside_quotes;
ESCAPED_QUOTE : '\\"'  -> more;
QUOTED_TEXT : '"' ->  type(TEXT), mode(inside_command);
CHAR : .  -> more;

mode inside_dquotes;
QUOTED_FILE_NAME_TEXT : LQUOTE STRING RQUOTE ;

LQUOTE : '"' -> pushMode(String);
mode String;
fragment ESC: '\\"';
STRING : (TEXT | INT | SPACE | [\./] | ESC)+ ;
RQUOTE : '"' -> popMode;
