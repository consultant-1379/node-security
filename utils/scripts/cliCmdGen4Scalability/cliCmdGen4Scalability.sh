#!/bin/bash
my_version=1.0.1
my_command=$(basename $0)
my_cmd=""
my_node_format=""
my_node=""
my_model_info=""
my_ip=""
my_oss_model_id=""
my_mim=""
my_type="ERBS"
my_netsim_file=""
my_node_list=""
my_template="./templates/erbs-node.txt"
my_oam_root_template="./templates/oam_root.xml"
my_ipsec_root_template="./templates/ipsec_root.xml"
my_oam_partial_template="./templates/oam_partial.xml"
my_ipsec_partial_template="./templates/ipsec_partial.xml"
my_sshkey_create_template="./templates/sshkey_create.txt"
my_sshkey_update_template="./templates/sshkey_update.txt"
my_credentials_create_template="./templates/sgsn-node-credentials-create.txt"
my_credentials_update_template="./templates/sgsn-node-credentials-update.txt"
my_trust_oam_template="./templates/trust_oam.txt"
my_trust_ipsec_template="./templates/trust_ipsec.txt"
my_template_config="./templates/erbs-node-scalability.txt"
my_template_delete_config="./templates/erbs-node-delete-scalability.txt"
my_model_info_mapping="./data/modelInfoMapping.txt"
my_out_dir="./output"
my_use_netsim_file=false
my_tmp_file="./tmp_file.txt"
my_tmp_oam_partial_file="./tmp_oam_partial_file.txt"
my_tmp_ipsec_partial_file="./tmp_ipsec_partial_file.txt"
IPOCTMAXVALUE=254
NUMOFNODES="ALL"

param_index=0

usage() {
cat << EOF   

Usage: `basename $0` [CMD] [OPTIONS]
[OPTIONS]:
  -h, --help                 (OPTIONAL);  This help
  -e, --examples             (OPTIONAL);  Examples of usage
  -v, --version              (OPTIONAL);  The script version

  -f, --format=<NODEFORMAT>  (MANDATORY); The node name format, eg LTE03ERBS, 5-digits padding index will be appended at the end
  -m, --model-info=<MI>      (MANDATORY); The node Model Info. MIM (6.1.20) or OSS Model ID (5504-866-139)
  -c, --count=<COUNT>        (OPTIONAL);  The number of nodes to be generated. All available nodes on netsim are generated as default, if the netsim file is provided
  -t, --type=<TYPE>          (OPTIONAL);  The node type. ${my_type} as default, SGSN-MME
  -i, --ip=<IP>              (OPTIONAL);  The node IP address to start from. Use this option to generate custom IP address to be assigned to nodes, instead of reading them from netsim file info. -c, --count option is required.
  -n, --netsim=<FILE>        (OPTIONAL);  The file containing the netsim information about nodes simulations and IP address to use
  -o, --out-dir=<OUTDIR>     (OPTIONAL);  The output directory. ${my_out_dir} as default

[CMD]:
  NODE                       Node create/edit/remove commands

EOF
}

examples() {
cat << EOF   

Examples:

For 13A ERBS node:
./${my_command} NODE -f=LTE06ERBS -c=1000 -t=ERBS -i=192.168.100.1 -m=D.1.44 -o=/media/sf_Ericsson/CliCmd

For 13B ERBS node:
./${my_command} NODE -f=LTE05ERBS -c=500 -t=ERBS -i=192.168.100.161 -m=D.1.189 -o=/media/sf_Ericsson/CliCmd
./${my_command} NODE -f=LTE05ERBS -t=ERBS -n=netsim_file.txt -m=D.1.189 -o=/media/sf_Ericsson/CliCmd

For 14A ERBS node:
./${my_command} NODE -f=LTE04ERBS -c=350 -t=ERBS -i=192.168.101.66 -m=E.1.63 -o=/media/sf_Ericsson/CliCmd

For 14B ERBS node:
./${my_command} NODE -f=LTE03ERBS -c=100 -t=ERBS -i=192.168.101.226 -m=E.1.239 -o=/media/sf_Ericsson/CliCmd

For 15B ERBS node:
./${my_command} NODE -f=LTE01ERBS -c=2500 -t=ERBS -i=192.168.102.131 -m=F.1.101 -o=/media/sf_Ericsson/CliCmd
./${my_command} NODE -f=LTE02ERBS -t=ERBS --netsim=netsim_file.txt -m=F.1.100 -o=/media/sf_Ericsson/CliCmd

For SGSN-MME node:
./${my_command} NODE -f=SGSNMME15BV -c=1000 -t=SGSN-MME -i=192.168.102.5 -m=3231-538-888 -o=/media/sf_Ericsson/CliCmd

EOF
}

version() {
cat << EOF   

`basename $0` $my_version

EOF
}

incrementIPAddress () {
    ipaddress=${1}
    #echo "Function called with ${ipaddress}" 
    # Split up IP addresses into seperate variables for each octet
    IPLO=(`echo "${ipaddress}" | awk '{split($ipaddress,a,"."); print a[1]" "a[2]" "a[3]" "a[4]}'`)
    #
    # Put array contents into nicely named vars for less confusion
    #
    OCTA=${IPLO[0]}
    OCTB=${IPLO[1]}
    OCTC=${IPLO[2]}
    OCTD=${IPLO[3]}
    #echo ${OCTA} ${OCTB} ${OCTC} ${OCTD} 
    if [ ${OCTD} -eq ${IPOCTMAXVALUE} ] ; then
       #echo "Resetting OCTD, incrementing OCTC"
       OCTD=0
       if [ ${OCTC} -eq ${IPOCTMAXVALUE} ] ; then
           #echo "Resetting OCTC, incrementing OCTB"
           OCTC=0
           if [ ${OCTB} -eq ${IPOCTMAXVALUE} ] ; then
               #echo "Resetting OCTB, incrementing OCTA"
               OCTB=0
               if [ ${OCTA} -eq ${IPOCTMAXVALUE} ] ; then
                   #echo "Invalid IP, out of range"
                   OCTA=0
               else
                   #echo "Incrementing OCTA"
                   OCTA=$(( ${OCTA} + 1 ))
               fi  
           else
               #echo "Incrementing OCTB"
               OCTB=$(( ${OCTB} + 1 ))
           fi
       else
           #echo "Incrementing OCTC"
           OCTC=$(( ${OCTC} + 1 ))
       fi
    else
       #echo "Incrementing OCTD"
       OCTD=$(( ${OCTD} + 1 ))
    fi
    echo "${OCTA}.${OCTB}.${OCTC}.${OCTD}"
}


# Parse parameters

while [ "$1" != "" ] ; do
    case $1 in
        -h | --help )
            usage
            exit 0
            ;;
        -e | --examples )
            examples
            exit 0
            ;;
        -v | --version )
            version
            exit 0
            ;;
        -f=* | --format=* )
            my_node_format=$1
            my_node_format=${my_node_format#--format=}
            my_node_format=${my_node_format#-f=}
            ;;
        -t=* | --type=* )
            my_type=$1
            my_type=${my_type#--type=}
            my_type=${my_type#-t=}
            ;;
        -m=* | --mim=* )
            my_model_info=$1
            my_model_info=${my_model_info#--mim=}
            my_model_info=${my_model_info#-m=}
            ;;
        -n=* | --netsim=* )
            my_netsim_file=$1
            my_netsim_file=${my_netsim_file#--netsim=}
            my_netsim_file=${my_netsim_file#-n=}
            ;;
        -c=* | --count=* )
            NUMOFNODES=$1
            NUMOFNODES=${NUMOFNODES#--count=}
            NUMOFNODES=${NUMOFNODES#-c=}
            ;;
        -i=* | --ip=* )
            my_ip=$1
            my_ip=${my_ip#--ip=}
            my_ip=${my_ip#-i=}
            ;;
        -o=* | --out-dir=* )
            my_out_dir=$1
            my_out_dir=${my_out_dir#--out-dir=}
            my_out_dir=${my_out_dir#-o=}
            ;;
        -* )
            usage
            echo $1: unknown option
            exit 1
            ;;
        * )
            case $param_index in
                0 )
                    my_cmd=$1 
                    param_index=$(($param_index + 1))
                    ;;
                1 )
                   echo $1: unknown parameter
                   usage
                   exit 1
                   ;;
            esac
            ;;
    esac
    shift
done

########################## MANDATORY PARAMETERS CHECK #################################################
if [ "$my_cmd" == "" ]; then
    echo Missing CMD mandatory parameter
    usage
    exit 1
fi

if [ "$my_node_format" == "" ]; then
    echo Missing node name format mandatory parameter
    usage
    exit 1
fi

if [ "$my_model_info" == "" ]; then
    echo Missing node Model Info mandatory parameter
    usage
    exit 1
fi

if [ "$my_ip" != "" ]; then
    echo Checking IP address...
    my_ip_regexp="[0-9]+\.[0-9]+\.[0-9]+\.[0-9]"
    if [[ ! $my_ip =~ $my_ip_regexp ]]; then
        echo $my_ip: wrong format for node IP Address
        usage
        exit 1
    fi
    if [ "$NUMOFNODES" == "ALL" ]; then
        echo Default number of node is 1
        NUMOFNODES=1
    elif [[ ! $NUMOFNODES =~ ^[0-9]+$ ]]; then
        echo $NUMOFNODES : Invalid format, must be a integer number
        usage
        exit 1
    fi
    my_use_netsim_file=false
else
    echo Checking netsim file...
    if [ "$my_netsim_file" == "" ]; then 
        echo Missing netsim file to read simulation info!
        usage 
        exit 1
    elif [ ! -f "$my_netsim_file" ]; then
        echo The provided netsim file is not found
        usage
        exit 1
    else
        #Grep in the netsim file the num of occurrences for my_node_format
        # if NUMOFNODES==ALL, set the NUMOFNODES==num of occurrences
        # if NUMOFNODES!=ALL, and NUMOFNODES -gt num of occurrences,  set the NUMOFNODES==num of occurrences with some warning
        echo All data matching $my_node_format in netsim file $my_netsim_file will be managed
        grep $my_node_format $my_netsim_file > $my_tmp_file
        actualNumOfNodesFoundInFile=`cat $my_tmp_file | wc -l`
        if [ $actualNumOfNodesFoundInFile -le 0 ]; then
            echo No match found for $my_node_format in file
            usage
            exit 1
        fi
        if [ "$NUMOFNODES" == "ALL" ]; then
            NUMOFNODES=$actualNumOfNodesFoundInFile
        elif [[ ! $NUMOFNODES =~ ^[0-9]+$ ]]; then
            echo $NUMOFNODES : Invalid format, must be a integer number
            usage
            exit 1
        elif [ $NUMOFNODES -gt $actualNumOfNodesFoundInFile ]; then    
            echo Resetting num of nodes from $NUMOFNODES to $actualNumOfNodesFoundInFile 
            NUMOFNODES=$actualNumOfNodesFoundInFile
        fi
        echo Number of nodes to be managed is $NUMOFNODES
        
    fi
    my_use_netsim_file=true
fi
#echo my_use_netsim_file is ${my_use_netsim_file}


my_mim_regexp="[0-9,A-Z]+\.[0-9]+\.[0-9]"
my_oss_model_id_regexp="[0-9]+\-[0-9]+\-[0-9]"

my_is_oss_model_id=
if [[ $my_model_info =~ $my_mim_regexp ]]; then
    my_is_oss_model_id=false
elif [[ $my_model_info =~ $my_oss_model_id_regexp ]]; then
    my_is_oss_model_id=true
else
    echo $my_model_info: wrong format for node Model Info
    usage
    exit 1
fi
########################## MANDATORY PARAMETERS CHECK END #################################################

while read line
do
    my_curr_type=`echo $line | awk '{print $1}'`
    my_curr_oss_model_id=`echo $line | awk '{print $2}'`
    my_curr_mim_1=`echo $line | awk '{print $3}'`
    my_curr_mim_2=`echo $line | awk '{print $4}'`
    if [[ $my_curr_type == $my_type ]]; then
        if [[ "$my_is_oss_model_id" == "false" ]]; then
            if [[ $my_curr_mim_1 == $my_model_info ]] || [[ $my_curr_mim_2 == $my_model_info ]]; then
                my_mim=$my_model_info
                my_oss_model_id=$my_curr_oss_model_id
                break
            fi
        elif [[ "$my_is_oss_model_id" == "true" ]]; then
            if [[ $my_curr_oss_model_id == $my_model_info ]]; then
                my_mim=$my_curr_mim_1
                my_oss_model_id=$my_curr_oss_model_id
                break
            fi
        fi
    fi
done < $my_model_info_mapping

if [ "$my_oss_model_id" == "" ] || [ "$my_mim" == "" ]; then
    echo $my_model_info: unknown Model Info
    usage
    exit 1
fi

case $my_cmd in
    NODE )
        if [ "$my_type" == "ERBS" ]; then
            my_template="./templates/erbs-node.txt"
            my_template_config="./templates/erbs-node-scalability.txt"
            my_template_delete_config="./templates/erbs-node-delete-scalability.txt"
        elif [ "$my_type" == "SGSN-MME" ] || [ "$my_type" == "SGSN" ]; then
            my_template="./templates/sgsn-mme-node.txt"
            my_template_config="./templates/sgsn-mme-node-scalability.txt"
            my_template_delete_config="./templates/sgsn-mme-node-delete-scalability.txt"
        else
            echo $my_type: unknown node type
            usage
            exit 1
        fi

        formatDir=$my_out_dir/$my_node_format
        mkdir -p $formatDir
        # Will generate some file as batch command lists for
        # - Node configuration
        my_out_file_config_batch=${formatDir}/${my_node_format}-config-batch.txt
        # - Node delete
        my_out_file_delete_batch=${formatDir}/${my_node_format}-delete-batch.txt
        # - Secadm key ssh create generation
        my_out_file_sshkey_create_batch=${formatDir}/${my_node_format}-sshkey-create-batch.txt
        # - Secadm key ssh update generation
        my_out_file_sshkey_update_batch=${formatDir}/${my_node_format}-sshkey-update-batch.txt
        # - Secadm OAM issue certificate
        my_out_file_OAM_issue_xml=${formatDir}/${my_node_format}-OAM-issue.xml
        # - Secadm IPSEC issue certificate
        my_out_file_IPSEC_issue_xml=${formatDir}/${my_node_format}-IPSEC-issue.xml
        # - Secadm OAM Trust distribution 
        my_out_file_OAM_trust_batch=${formatDir}/${my_node_format}-OAM-trust-batch.txt
        # - Secadm IPSEC Trust distribution
        my_out_file_IPSEC_trust_batch=${formatDir}/${my_node_format}-IPSEC-trust-batch.txt
        # - Secadm credentials create
        my_out_file_credentials_create_batch=${formatDir}/${my_node_format}-credentials-create-batch.txt
        # - Secadm credentials update
        my_out_file_credentials_update_batch=${formatDir}/${my_node_format}-credentials-update-batch.txt
        touch $my_out_file_config_batch
        touch $my_out_file_delete_batch
        touch $my_out_file_sshkey_create_batch
        touch $my_out_file_sshkey_update_batch
        cp -f $my_oam_root_template $my_out_file_OAM_issue_xml
        cp -f $my_ipsec_root_template $my_out_file_IPSEC_issue_xml
        touch $my_out_file_OAM_trust_batch
        touch $my_out_file_IPSEC_trust_batch
        touch $my_out_file_credentials_create_batch
        touch $my_out_file_credentials_update_batch
        
        #Loop over the number of nodes
        count=1
        while [ ${count} -le ${NUMOFNODES} ]
        do  
            #Pad the string name with 5 zero
            my_node_padding=`printf %05d ${count}`
            my_node=`echo ${my_node_format}${my_node_padding}`
            # echo count is ${count}
            if [ ${my_use_netsim_file} == true ]; then
                #This code read the {count}-nth line in file
                # sed '10q;d' file : read the 10th line in file
                my_node=`sed $count'q;d' $my_tmp_file | awk '{print $1}'`
                my_ip=`sed $count'q;d' $my_tmp_file | awk '{print $2}'`
                #echo Data read from netsim file ${my_node}, ${my_ip}
            fi
            #echo "Node name is ${my_node}, IP address ${my_ip}"
            mkdir -p $formatDir/$my_node
            my_out_file=$formatDir/$my_node/$my_node.txt
            my_oam_file=$formatDir/$my_node/$my_node-OAM.xml
            my_ipsec_file=$formatDir/$my_node/$my_node-IPSEC.xml
            cp -f $my_template $my_out_file
            cp -f $my_oam_root_template $my_oam_file
            cp -f $my_ipsec_root_template $my_ipsec_file

            # Replace data in single file for each node
            sed -i "s/XXNODENAMEXX/$my_node/g" $my_out_file
            sed -i "s/XXOSSMODELIDENTITYXX/$my_oss_model_id/g" $my_out_file
            sed -i "s/XXMIMXX/$my_mim/g" $my_out_file
            sed -i "s/XXIPADDRESSXX/$my_ip/g" $my_out_file

            # Replace data for configuration batch file, common to all nodes
            sed "s/XXNODENAMEXX/$my_node/g;s/XXOSSMODELIDENTITYXX/$my_oss_model_id/g;s/XXMIMXX/$my_mim/g;s/XXIPADDRESSXX/$my_ip/g" $my_template_config >> $my_out_file_config_batch

            # Replace data for delete batch file, common to all nodes
            sed "s/XXNODENAMEXX/$my_node/g;" $my_template_delete_config >> $my_out_file_delete_batch

            #Replace data for OAM xml file for single node
            sed -e "/XXOAMDATAXX/ {" -e "r ${my_oam_partial_template}" -e "d" -e "}" -i $my_oam_file
            #Replace data for IPSEC xml file for single node
            sed -e "/XXIPSECDATAXX/ {" -e "r ${my_ipsec_partial_template}" -e "d" -e "}" -i $my_ipsec_file
            sed -i "s/XXNODENAMEXX/$my_node/g" $my_oam_file
            sed -i "s/XXNODENAMEXX/$my_node/g" $my_ipsec_file
            sed -i "s/XXIPADDRESSXX/$my_ip/g" $my_ipsec_file
            
            # Replace data for partial OAM xml file, common to all nodes into a tmp partial file
            sed "s/XXNODENAMEXX/$my_node/g" $my_oam_partial_template >> $my_tmp_oam_partial_file
            # Replace data for partial IPSEC xml file, common to all nodes into a tmp partial file
            sed "s/XXNODENAMEXX/$my_node/g;s/XXIPADDRESSXX/$my_ip/g" $my_ipsec_partial_template >> $my_tmp_ipsec_partial_file

            my_node_list="${my_node_list},$my_node"
            if [ ${my_use_netsim_file} == false ]; then
                my_ip=$(incrementIPAddress $my_ip)
            fi
            echo "Commands generated for node $my_node..."
            count=$(( $count + 1 ))
        done
        #Replace data related to nodelist option in commands
        #Strip 1st char in list that is ','
        my_node_list=${my_node_list#","}
        
        # secadm SSH Key create command 
        sed "s/XXNODENAMEXX/$my_node_list/g" $my_sshkey_create_template >> $my_out_file_sshkey_create_batch
        # secadm SSH Key update command 
        sed "s/XXNODENAMEXX/$my_node_list/g" $my_sshkey_update_template >> $my_out_file_sshkey_update_batch

        # secadm credentials create command
        sed "s/XXNODENAMEXX/$my_node_list/g" $my_credentials_create_template >> $my_out_file_credentials_create_batch
        # secadm credentials update command
        sed "s/XXNODENAMEXX/$my_node_list/g" $my_credentials_update_template >> $my_out_file_credentials_update_batch

        # secadm OAM Trust distribute command 
        sed "s/XXNODENAMEXX/$my_node_list/g" $my_trust_oam_template >> $my_out_file_OAM_trust_batch
        # secadm IPSEC Trust distribute command 
        sed "s/XXNODENAMEXX/$my_node_list/g" $my_trust_ipsec_template >> $my_out_file_IPSEC_trust_batch
        
        #We need to put the content of my_tmp_oam_partial_file into my_out_file_OAM_issue_xml
        sed -e "/XXOAMDATAXX/ {" -e "r ${my_tmp_oam_partial_file}" -e "d" -e "}" -i $my_out_file_OAM_issue_xml
        #We need to put the content of my_tmp_ipsec_partial_file into my_out_file_IPSEC_issue_xml
        sed -e "/XXIPSECDATAXX/ {" -e "r ${my_tmp_ipsec_partial_file}" -e "d" -e "}" -i $my_out_file_IPSEC_issue_xml
        ;;
    * )
        echo $my_cmd: unknown CMD
        usage
        exit 1
        ;;
esac

rm -f $my_tmp_file
rm -f $my_tmp_oam_partial_file
rm -f $my_tmp_ipsec_partial_file
echo "Batch commands files available in $formatDir/ directory"
echo Done.
exit 0
