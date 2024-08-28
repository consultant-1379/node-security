parser grammar SecCommandBaseParser;

@header {
import java.util.*;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.parser.impl.antlr.ValidProperties;
}

options { tokenVocab=SecCommandLexer; }

@members{

    private static final String EMPTY_STRING = " ".trim();

    private String listToString(List list) {
        StringBuilder str = new StringBuilder();
        for(int i=0; i<list.size(); i++) {
           if(i > 0) str.append(",");
           str.append(list.get(i));
        }
        return str.toString();
    }
    protected Object [] list(Object ... args) {
        Object [] ret = new Object[args.length];
        for ( int i = 0; i < args.length; i++) {
            ret[i] = args[i] instanceof Collection ? args[i] : String.valueOf(args[i]);
        }
        return ret;
    }
    protected Set<String> withAlias(String ... props) {
        Set<String> prop = new LinkedHashSet<>();
        if ( props != null ){
            prop.addAll(Arrays.asList(props));
        }
        return prop;
    }
    protected String removeStartingDashes(String propName){
        return propName.replaceFirst("^[-]+", EMPTY_STRING);
    }
    
    protected void addAttribute(String attName, Object attValue){
    	if($parseCommand::attributes.containsKey(attName)) {
        	throw new CommandSyntaxException();
        }
        
        $parseCommand::attributes.put( attName , attValue );
    	
    }
}

parseCommand
 locals [ Map<String,Object> attributes, List<String> filesAttributesNames]
 @init {
    $attributes = new HashMap<String,Object>();
    $filesAttributesNames = new LinkedList();
 }
 :
    (command) EOF { $attributes.put("command", getTokenNames()[$start.getType()] );}
;

command:

;

propertyList :
    propertyListFromList[null]
;

propertyListFromList [Object[] valids] locals [ValidProperties vProps]
@init{ $vProps = ValidProperties.fromArgSpec($valids); }:
    propertyFromValidProperties[$vProps, null] ( (propertyFromValidProperties[$vProps, null])+ LIST_END? )?
;

propertyListFromListWithoutValue [Object[] valids] locals [ValidProperties vProps]
@init{ $vProps = ValidProperties.fromArgSpec($valids); }:
    propertyFromValidPropertiesWithoutValue[$vProps, null] ( (propertyFromValidPropertiesWithoutValue[$vProps, null])+ LIST_END? )?
;

property :
    propertyFromList[null]
;

propertyFromList [Object[] valids] locals [ValidProperties vProps]
@init{ $vProps = ValidProperties.fromArgSpec($valids); }:
    propertyFromValidProperties[$vProps, null]
;

propertyFromListWithoutValue [Object[] valids] locals [ValidProperties vProps]
@init{ $vProps = ValidProperties.fromArgSpec($valids); }:
    propertyFromValidPropertiesWithoutValue[$vProps, null]
;

propertyValueFromList [Object propName, Object[] valids] locals [ValidProperties vProps]
@init{ $vProps = ValidProperties.fromArgSpec($propName); }:
    propertyFromValidProperties[$vProps, $valids]
;

propertyFromValidProperties[ValidProperties valids, Object[] expectedValues]:

    key=propertyKey value=propertyValue {
        if ($valids != null && !$valids.contains($key.retVal))
            throw new CommandSyntaxException();

        String attName = $key.retVal;
        if ( $valids != null ) {
            attName = $valids.getTargetProperty(attName);
        }

        Object toAdd = $value.retVal;
        String type = $valids == null ? null : $valids.getPropertyType($key.retVal);
        boolean isFile = toAdd.toString().startsWith(getTokenNames()[SecCommandLexer.FILE].replaceAll("'", EMPTY_STRING));
        if ( type != null ) {
            if ( isFile && !"file".equalsIgnoreCase(type) ) {
                throw new CommandSyntaxException();
            }
            if ( "text".equalsIgnoreCase(type) ) {
                if ( toAdd instanceof List) {toAdd = listToString((List)toAdd);}
                else { toAdd = toAdd.toString();}
            }
            else if ("list".equalsIgnoreCase(type) && !(toAdd instanceof List)  ) {
                toAdd = Arrays.asList(toAdd);
            } else if ("file".equalsIgnoreCase(type) && !isFile) {
                throw new CommandSyntaxException();
            }
        }

        if($expectedValues != null){
            List expected = Arrays.asList($expectedValues);
            if ( toAdd instanceof Collection ) {
                Collection col = (Collection) toAdd;
                for (Object item : col){
                    if (! expected.contains(item) ) throw new CommandSyntaxException();
                }
            } else {
                if (! expected.contains(toAdd) ) throw new CommandSyntaxException();
            }
        }

        attName = removeStartingDashes(attName);
        
        addAttribute(attName, toAdd);
        
        if ( isFile ) {
            $parseCommand::filesAttributesNames.add(attName);
        }

    }

;

propertyFromValidPropertiesWithoutValue[ValidProperties valids, Object[] expectedValues] 
:

    key=propertyKey 
    {
    		
        if ($valids != null && !$valids.contains($key.retVal))
            throw new CommandSyntaxException("Not expected attribute '" + $key.retVal + "'. Expected one of : " + $valids.getValidPropertyOrAliases() );

        String attName = $key.retVal;
        if ( $valids != null ) {
            attName = $valids.getTargetProperty(attName);
        }

		Object toAdd = null;	

        attName = removeStartingDashes(attName);
        
        addAttribute(attName, toAdd);
        
    }
;

propertyKey returns[String retVal]
@init{ $retVal = EMPTY_STRING; } :
    (PROPERTY_PREFIX {$retVal += $PROPERTY_PREFIX.text;})? TEXT {$retVal += $TEXT.text;}
;
propertyValue returns [Object retVal] :
    val1=propertyValueText{$retVal = $val1.txt;}
    | val2=propertyValueList{$retVal=$val2.list;}
    | val3=propertyValueFile{$retVal=$val3.txt;}
;
propertyValueList returns [List list]
@init {$list = new LinkedList();}
:
   SQUARE_BRACKET_OPEN  textOrInt {$list.add($textOrInt.txt);} (SEPARATOR textOrInt {$list.add($textOrInt.txt);})* SQUARE_BRACKET_CLOSE
   |
   textOrInt {$list.add($textOrInt.txt);} (SEPARATOR (textOrInt {$list.add($textOrInt.txt);}))*
;
propertyValueText returns [String txt]:
    textOrInt {$txt=$textOrInt.txt;}
;

propertyValueFile returns [String txt]:
    FILENAME {$txt=$FILENAME.text;}
;


listValue [String name] locals [ List list = new ArrayList();]
@after{
	
	addAttribute($name, $list);
}
:
    textOrInt {$list.add($textOrInt.txt);} ((SEPARATOR (textOrInt {$list.add($textOrInt.txt); }))+ LIST_END)?
;

wildcardValue [String name] :
    ALL {addAttribute( $name, "*" );}
;

fileValue [String name] :
    FILENAME {
        addAttribute( $name, $FILENAME.text );
        $parseCommand::filesAttributesNames.add($name);
    }
;

intValue [String name] : intValueFromList[$name, null];

intValueFromList [String name, Object[] valids] :
    INT {
        if ( $valids != null ){
            List expected = Arrays.asList($valids);
            if (! expected.contains($INT.text) ) throw new CommandSyntaxException( );
        }
        addAttribute( $name, $INT.text );
    }
;


textValue [String name] : textValueFromList[$name, null] ;

textValueFromList [String name, Object[] valids] :
    textOrInt {
        if ( $valids != null ){
            List expected = Arrays.asList($valids);
            if (! expected.contains($textOrInt.txt) ) throw new CommandSyntaxException();
        }
        addAttribute( $name, $textOrInt.txt );
    }
;

textOrInt returns[String txt]:
     (
      TEXT {$txt = ($TEXT.text.endsWith("\"") ? $TEXT.text.substring(0, $TEXT.text.length() - 1) : $TEXT.text ).replaceAll("[\\\\][\"]","\"");}
      | INT{$txt = $INT.text;}
     )
 ;
