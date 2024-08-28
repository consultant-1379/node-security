import sys
import re
import os

class Simulation(object):
    def __init__(self, name, supported):
        self.name = name
        self.nodes = []
        self.supported = supported

    def add_node(self, node):
	 self.nodes.append( node )

    def __str__(self):
       str = ""
       for i in self.nodes:
	   str += "\n" + i.toString()
       return "Simulation: " + self.name + str 

class Node(object):
    def __init__(self, name, ip, type, state):
        self.name = name
        self.ip  = ip
	self.type = type
	self.state = state

    def __str__(self):
        return self.toString()
 
    def toString(self):
        return "Node: " + self.name + " IP: " + self.ip + " Type: " + self.type + " State: " + self.state
 
def first(iterable, default=None):
  for item in iterable:
    return item
  return default

def fillSimulationFromNetSimList(fileName):
    in_file = open(fileName,"r")
    simulations = []
    for line in in_file:
        simulation = line.strip("\r\n")
        simulations.append(Simulation(simulation, isSimulationSupported(simulation) ))
    in_file.close()
    return simulations


def fillNodesFromNetSimInfo(fileName, simulations):
    in_file = open(fileName,"r")

    node = None
    simulationSupportedFound = False
    sim = ""

    for line in in_file:
	#remove leading and ending spaces

	line.strip()

	# find Simulation
        foundSimulation = False
        for x in simulations :
          if line.strip("\r\n") == x.name:
            simulationSupportedFound = x.supported
            sim = line.strip("\r\n")
            foundSimulation = True
            break
	
        # Skip lines until I found a simulation 
        if ( foundSimulation or not simulationSupportedFound ):
            continue

        #After the simulation name I have the list of its nodes and parameters
	if not re.match(r'^\s*$', line):
	    if ( not node ):
	        node = line.strip("\r\n")
               # print "Node :" + node
	    else :
                paramNode = line
               # print "params :" + paramNode
		    
                ip = getIPFromParamNode(paramNode)		    
               # print "IP :" + ip

		type = getTypeFromParamNode(paramNode)
		state = getStateFromParamNode(paramNode)
		    
                simulationObject = first(x for x in simulations if x.name == sim)
               # print "SIM :" + sim
	        simulationObject.add_node(Node(node, ip, type, state))      
                node = None

    in_file.close()
    return simulations

def isSimulationSupported( simulation ):
   if ( isSimulationLTENotSupported(simulation)) :
     return False 
   if (isSimulationERBS(simulation) or isSimulationDG2(simulation) or isSimulationPICO(simulation)):
       return True
   return False

def isSimulationERBS( simulation ):
   if simulation.startswith("LTE"):
        if ( not isSimulationDG2( simulation ) and not isSimulationPICO( simulation) ) :
	    return True
   return False

def isSimulationDG2( simulation ):
   if (simulation.find("DG2") != -1) :
	return True
   return False

def isSimulationPICO( simulation ):
   if (simulation.find("PICO") != -1) :
	return True
   return False
	
def isSimulationLTENotSupported( simulation ):
   if (simulation.find("UPGIND") != -1) :
	return True
   return False

def getIPFromParamNode(param):
     return find_between(param,"IP:","MIM").strip()		    

def getTypeFromParamNode(param):
	param2 = find_between(param,"MIM version:","Node Status:").strip();
	return find_between(param2, ",", ",").strip();

def getStateFromParamNode(param):
        return param.split("Node Status:",1)[1].strip(); 

def find_between( s, first, last ):
    try:
        start = s.index( first ) + len( first )
        end = s.index( last, start )
        return s[start:end]
    except ValueError:
        return ""

