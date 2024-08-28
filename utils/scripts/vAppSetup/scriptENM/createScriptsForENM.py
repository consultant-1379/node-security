import sys
import re
import os
import shutil
import netSimInfo
import xml.etree.cElementTree as ET
from lxml import etree

def main():
    fileNameList       = sys.argv[1]
    fileNameInfo       = sys.argv[2]
    if len(sys.argv) > 3:
        netSimName     = sys.argv[3]
        directory = "target/" + netSimName + "/"
        headerNode = netSimName + "_"
    else: 
         directory = "target/"
         headerNode =""

    if not(os.path.exists(directory)):
         os.makedirs(directory)
    
    # Read simulations from NetSim file  
    simulations = netSimInfo.fillSimulationFromNetSimList(fileNameList)
    netSimInfo.fillNodesFromNetSimInfo(fileNameInfo, simulations)
 
    dirStartedToConfigure=directory + "startedToConfigure"
    if os.path.isdir(dirStartedToConfigure):
        shutil.rmtree(dirStartedToConfigure, ignore_errors=True)
    os.mkdir(dirStartedToConfigure)

    dirStartedToDelete=directory + "startedToDelete"
    if os.path.isdir(dirStartedToDelete):
        shutil.rmtree(dirStartedToDelete, ignore_errors=True)
    os.mkdir(dirStartedToDelete)

    # Create a directory for any simulation 
    for sim in simulations:
        if ( not sim.supported ) :
            continue

        dir = directory + sim.name 
        if os.path.isdir(dir):
    	    shutil.rmtree(dir, ignore_errors=True)
        os.mkdir(dir)

        print "SIM NAME " + sim.name
        
        if netSimInfo.isSimulationERBS(sim.name) :
            createCppSl2XmlFile( dir, sim, headerNode, "MicroRBSOAM_CHAIN_EP", "SCEP" )
            createCppSl2XmlFile( dir, sim, headerNode, "MicroRBSOAM_CHAIN_EP", "CMPv2_INITIAL" )
        #TODO Controllare con che nome vengono creati i nodi sulla macchina fisica
        else : 
            headerNode =""
        
        #create ENM files
        createNodeListFile(dir, sim, headerNode )

        createConfigFile( dir, sim, headerNode )
        createConfigFileStarted( dirStartedToConfigure, sim, headerNode )
#        createCredentialFile( dir, sim, headerNode )
#        createFmAlarmSupervisionFile( dir, sim, headerNode )
        deleteConfigFile( dir, sim, headerNode )
        deleteConfigFileStarted( dirStartedToDelete, sim, headerNode )
      
        #if netSimInfo.isSimulationDG2(sim.name) :
#           comEcimGetCredMObject( dir, sim , headerNode)

        #create NetSim files

def createNodeListFile( dir , sim , headerNode ):
    filename = dir+"/"+sim.name.split("-")[-1]
    filename += "_NodeList.txt"
    nodeList_file  = open( filename, 'w')

    for node in sim.nodes:
        nodeList_file.write(headerNode + node.name+"\n")
    nodeList_file.close()

def getENMNodeType( netSimNodeType ) :
    if ( netSimNodeType == "\"MSRBS-V2\"" ) :
        return "RadioNode" 
    elif  ( netSimNodeType == "\"PRBS\"" ) :  
        return "MSRBS_V1" 
    elif ( netSimNodeType == "\"ERBS\"" ) : 
        return "ERBS" 
    else :
        return "N/A" 

def createNodeConfig( headerNode, nodeList_file, node) : 

    nodeName = headerNode + node.name

    nodeType=getENMNodeType(node.type)
    headerString = "cmedit create NetworkElement=" + nodeName 
    neIdString =" networkElementId=" + nodeName + ", "
    neTypeString ="neType=" + nodeType + ", "
    ossPrefixString="ossPrefix=\"MeContext=" + nodeName + "\""
    ipString="ipAddress=\"" + node.ip 
    credentialString="secadm credentials create --secureusername netsim --secureuserpassword netsim"
    nodeList_file.write(headerString + neIdString + neTypeString + ossPrefixString + " -ns=OSS_NE_DEF -version=2.0.0"+"\n")
    if (nodeType == "RadioNode" or nodeType == "MSRBS_V1" ) :
        nodeList_file.write(headerString + ",ComConnectivityInformation=1 ComConnectivityInformationId=1, port=6513, transportProtocol=TLS," +
                                           " snmpAgentPort=1161, " + ipString + "\" -ns=COM_MED -version=1.1.0"+"\n")
    elif (nodeType == "ERBS" ) :
        nodeList_file.write(headerString + ",CppConnectivityInformation=1 CppConnectivityInformationId=1, " + ipString +
                                           "\", port=80 -ns=CPP_MED -version=1.0.0"+"\n")
        credentialString += " --rootusername u1 --rootuserpassword pw1 --normalusername u3 --normaluserpassword pw3"
    else : # other
        print "nodeType = " + node.type + " not supported"

    nodeList_file.write(credentialString + " -n " + nodeName + "\n")
    nodeList_file.write("cmedit set NetworkElement=" + nodeName + ",CmNodeHeartbeatSupervision=1 active=true"+"\n")
    nodeList_file.write("fmedit set NetworkElement=" + nodeName + ",FmAlarmSupervision=1 alarmSupervisionState=true"+"\n\n")


def createConfigFile( dir, sim , headerNode):
    filename = dir+"/"+sim.name.split("-")[-1]
    filename += "_CreateConfig.txt"
    nodeList_file  = open( filename, 'w')

    for node in sim.nodes:
        createNodeConfig(headerNode, nodeList_file, node)
    nodeList_file.close()


def createConfigFileStarted( dir, sim , headerNode):
    filename = dir+"/"+sim.name.split("-")[-1]
    if netSimInfo.isSimulationERBS(sim.name) : filename += "_ERBS"
    if netSimInfo.isSimulationDG2(sim.name) : filename += "_DG2"
    if netSimInfo.isSimulationPICO(sim.name) : filename += "_PICO"

    filename += "_CreateConfigStarted.txt"
    nodeList_file  = open( filename, 'w')

    for node in sim.nodes:
        if (node.state == "started") :
            createNodeConfig(headerNode, nodeList_file, node)
    nodeList_file.close()


def deleteConfigNode(headerNode, nodeList_file, node):
    nodeName = headerNode + node.name
    nodeList_file.write("cmedit set NetworkElement="+nodeName+",CmNodeHeartbeatSupervision=1 active=false"+"\n")
    nodeList_file.write("cmedit set NetworkElement="+nodeName+",InventorySupervision=1 active=false"+"\n")
    nodeList_file.write("cmedit set NetworkElement="+nodeName+",FmAlarmSupervision=1 active=false"+"\n")
    nodeList_file.write("fmedit set NetworkElement="+nodeName+",FmAlarmSupervision=1 alarmSupervisionState=false"+"\n")
    nodeList_file.write("cmedit action NetworkElement="+nodeName+",CmFunction=1 deleteNrmDataFromEnm"+"\n")
    nodeList_file.write("cmedit delete NetworkElement="+nodeName+" -ALL"+"\n\n")


def deleteConfigFile( dir, sim , headerNode):
    filename = dir+"/"+sim.name.split("-")[-1]
    filename += "_DeleteConfig.txt"
    nodeList_file  = open( filename, 'w')

    for node in sim.nodes:
        deleteConfigNode(headerNode, nodeList_file, node)
    nodeList_file.close()


def deleteConfigFileStarted( dir, sim , headerNode):
    filename = dir+"/"+sim.name.split("-")[-1]
    if netSimInfo.isSimulationERBS(sim.name) : filename += "_ERBS"
    if netSimInfo.isSimulationDG2(sim.name) : filename += "_DG2"
    if netSimInfo.isSimulationPICO(sim.name) : filename += "_PICO"
    
    filename += "_DeleteConfig.txt"
    nodeList_file  = open( filename, 'w')

    for node in sim.nodes:
        if (node.state == "started") :
            deleteConfigNode(headerNode, nodeList_file, node)
    nodeList_file.close()


def createFmAlarmSupervisionFile( dir, sim , headerNode):
    filename = dir+"/"+sim.name.split("-")[-1]
    filename += "_FmAlarmSupervision.txt"
    nodeList_file  = open( filename, 'w')

    for node in sim.nodes:
        nodeName = headerNode + node.name
        nodeList_file.write("fmedit set NetworkElement="+nodeName+",FmAlarmSupervision=1 alarmSupervisionState=true"+"\n\n")

    nodeList_file.close()


def createCredentialFile( dir, sim , headerNode):
    nodeList = []
    nodeComEcimList = []
    for node in sim.nodes:
        nodeName = headerNode + node.name
        if ( node.type != "\"MSRBS-V2\"" ) :
            nodeList.append(nodeName)
	else :
            nodeComEcimList.append(nodeName)
    if ( len(nodeList) > 0 ) :
        filename = dir+"/" + sim.name.split("-")[-1]
        filename += "_CreateCredentials.txt"
        nodeList_file  = open( filename, 'w')

        nodeListStr = ",".join(nodeList) 
        nodeList_file.write("secadm credentials create --rootusername u1 --rootuserpassword pw1 --secureusername netsim --secureuserpassword netsim --normalusername u3 --normaluserpassword pw3 -n "+nodeListStr +"\n")
        nodeList_file.close()
    if ( len(nodeComEcimList) > 0 ) :
        fileComEcimname = dir+"/" + sim.name.split("-")[-1]
        fileComEcimname += "__CreateComEcimCredentials.txt"
        nodeComEcimList_file  = open( fileComEcimname, 'w')

        nodeComEcimListStr = ",".join(nodeComEcimList) 
        nodeComEcimList_file.write("secadm credentials create --secureusername netsim --secureuserpassword netsim -n "+nodeComEcimListStr +"\n")
        nodeComEcimList_file.close()


def updateCredentialFile( dir, sim , headerNode):
    filename = dir+"/" + sim.name.split("-")[-1]
    filename += "_updateCredentials.txt"
    nodeList_file  = open( filename, 'w')
    nodeList = []
    for node in sim.nodes:
        nodeName = headerNode + node.name
        nodeList.append(nodeName)
    
    nodeListStr = ",".join(nodeList) 
    nodeList_file.write("secadm credentials update --rootusername u1 --rootuserpassword pw1 --secureusername netsim --secureuserpassword netsim --normalusername u3 --normaluserpassword pw3 -n "+nodeListStr +"\n")
    nodeList_file.close()

def createCppSl2XmlFile( dir, sim, headerNode, entityProfileName, enrollmentMode ):
    filename = dir+"/" + sim.name.split("-")[-1]
    filename += "_SL2_" + entityProfileName + "_" + enrollmentMode + ".xml"

    nodesTag = etree.Element("Nodes")
    
    for node in sim.nodes:
        nodeTag = etree.Element("Node")

        etree.SubElement(nodeTag, "NodeFdn").text = headerNode + node.name
        etree.SubElement(nodeTag, "EntityProfileName").text = entityProfileName 
        etree.SubElement(nodeTag, "EnrollmentMode").text = enrollmentMode
        etree.SubElement(nodeTag, "KeySize").text = "RSA_2048"
        nodesTag.append (nodeTag)
 
    xmlFile  = open( filename, 'w')
    xmlFile.write( etree.tostring(nodesTag, pretty_print=True, encoding='utf-8', xml_declaration=True))
    xmlFile.close()


def comEcimGetCredMObject( dir, sim , headerNode):
    filename = dir+"/" + sim.name.split("-")[-1] + "_comEcimGetCredMObject.txt"
    nodeList_file  = open( filename, 'w')
    for node in sim.nodes:
        nodeName = headerNode + node.name
	
#        if ( node.type != "\"MSRBS-V2\"" ) :
        nodeList_file.write("cmedit get SubNetwork=NETSimW,ManagedElement="+nodeName+",SystemFunctions=1,SecM=1 CertM.*"+"\n")


    nodeList_file.close()

if __name__ == "__main__":
    main()
