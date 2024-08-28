grammar SecCommandFile;

@header {
import java.util.*;
}

nodeFileList
 locals [
  	 List<String> nodes = new LinkedList();
 ]
:
	(nodeElem {$nodes.add($nodeElem.text); }) ( (SEPARATOR)+ (nodeElem {$nodes.add($nodeElem.text); }) )* (SEPARATOR)* EOF
;

nodeElem :
	(nodeFormatMeContext | nodeFormatNetworkElement | nodeName)
;

nodeFormatMeContext :
	MECONTEXT(nodeName)
;

nodeFormatNetworkElement :
	NETWORKELEMENT(nodeName)
;

nodeName :
	(NODENAME)
;

SEPARATOR : [ \t\r\n;] ;

MECONTEXT : 'MeContext=';

NETWORKELEMENT : 'NetworkElement=';

NODENAME : [a-zA-Z0-9_*-.]+ ;
