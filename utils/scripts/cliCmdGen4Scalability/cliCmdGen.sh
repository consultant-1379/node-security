#!/bin/bash

my_version=1.0.1
my_is_verbose=false
my_command=$(basename $0)
my_type=""
my_username="netsim"
my_password="netsim"
my_oss_prefix=""
MY_BATCH_SIZE=""
MY_GET_BATCH_SIZE=""
my_is_single_node=true
my_out_dir="./output"
my_nes=""
MY_NES_ARRAY=()
my_ip=""
my_netsim_file=""
my_all_sim_nes="./tmp_all_sim_nes.txt"
my_num_of_nodes=0

# Node type-independent templates
my_node_delete_template="./templates/node_delete.txt"
my_node_unconfigure_template="./templates/node_unconfigure.txt"
my_node_configure_template="./templates/node_configure.txt"
my_cert_cmd_template="./templates/certificate.txt"
my_cert_cmd_nodefile_template="./templates/certificate_with_nodefile.txt"
my_trust_cmd_template="./templates/trust.txt"
my_trust_cmd_nodefile_template="./templates/trust_with_nodefile.txt"
my_trust_remove_template="./templates/trust_remove.txt"
my_trust_remove_nodefile_template="./templates/trust_remove_with_nodefile.txt"
my_oam_root_template="./templates/oam_root.xml"
my_ipsec_root_template="./templates/ipsec_root.xml"
my_oam_cert_issue_template="./templates/oam_cert_issue.txt"
my_ipsec_cert_issue_template="./templates/ipsec_cert_issue.txt"

param_index=0

usage() {
cat << EOF   

Usage: `basename $0` <NES>... [<OPTIONS>]
where:
  <NES>                                       The node simulations
                                              (e.g. LTE07dg2ERBS00089 or LTE01ERBS00099 for a single node
                                                    LTE07dg2ERBS00 or LTE01 for multiple nodes)
                                              This field can contain multiple NES (e.g. LTE07 LTE08 LTE09)
  <OPTIONS>:
    -h, --help                                This help
    --version                                 The script version
    -v, --verbose                             Set verbose mode on
    -e, --examples                            Some examples
    -t, --type=<TYPE>                         The optional node type: if specified, the type-dependent commands are genearted too.
                                              It is MANDATORY for single node.
    -p, --prefix=<PREFIX>                     The optional OSS prefix: ${my_oss_prefix} as default
    -i, --ip=<IP>                             The optional node IP address (for single node only).
    -n, --netsim=<FILE>                       The optional file containing the netsim information about node simulations and IP addresses to use.
    --username=<USERNAME>                     The optional secure (root and normal) username: ${my_username} as default.
    --password=<PASSWORD>                     The optional secure (root and normal) password: ${my_password} as default.
    -b, --batch-size=<BATCH_SIZE>             The optional batch size.  If not specified, all valid specified nodes are added to a single batch.
    -g, --get-batch-size=<GET_BATCH_SIZE>     The optional get batch size.  If not specified, the batch size is used.
                                              The get batch size shall never exceed the batch size.
    -o, --out-dir=<OUTDIR>                    The optional output directory. ${my_out_dir} as default

In case of single node, prepare:
- one file containing CLI commands for:
  - node create/delete/edit/sync and node credentials create/update
  - certificate issue/reissue/get and trust distribute/remove/get
- one XML file for OAM certificate issue and one XML file for IPSEC certificate issue

In case of multiple nodes, prepare batch files containing CLI commands for operations performed on multiple nodes.

EOF
}

error() {
cat << EOF   

Try './${my_command} --help' for more information.

EOF
}

examples() {
cat << EOF   

Examples:

For a single DUSGen2 node:
./${my_command} LTE07dg2ERBS00001 -i=192.168.100.1 -o=/media/sf_Ericsson/Dg2CliCmd
./${my_command} LTE07dg2ERBS00002 -p=SubNetwork=RadioNode -i=192.168.100.2 -o=/media/sf_Ericsson/Dg2CliCmd
./${my_command} LTE07dg2ERBS00003 -n=./allsimnes.txt -o=/media/sf_Ericsson/Dg2CliCmd
./${my_command} Radionode_15B_V13_GuinnJteam01 -i=192.168.100.1 -su=guinness -sp=guinness -o=/media/sf_Ericsson/Dg2CliCmd
./${my_command} G2RBS_27 -i=10.45.242.27 -su=labuser -sp=Letmein01 -o=/media/sf_Ericsson/Dg2CliCmd

For a single ERBS node:
./${my_command} LTE01ERBS00001 -t=ERBS -i=192.168.110.1 -o=/media/sf_Ericsson/ErbsCliCmd
./${my_command} LTE01ERBS00002 -t=ERBS -p=SubNetwork=ERBS -i=192.168.110.2 -o=/media/sf_Ericsson/Dg2CliCmd

For multiple nodes:
./${my_command} LTE07dg2ERBS -n=./allsimnes.txt -o=/media/sf_Ericsson/Dg2CliCmd
./${my_command} LTE07 LTE08 -n=./allsimnes.txt -o=/media/sf_Ericsson/Dg2CliCmd
./${my_command} LTE01 LTE02 -t-ERBS -n=./allsimnes.txt -o=/media/sf_Ericsson/Dg2CliCmd

EOF
}

version() {
cat << EOF

`basename $0` $my_version

EOF
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
        --version )
            version
            exit 0
            ;;
        -v | --verbose )
            my_is_verbose=true
            ;;
        -t=* | --type=* )
            my_type=$1
            my_type=${my_type#--type=}
            my_type=${my_type#-t=}
            ;;
        -su=* | --secure-username=* )
            my_username=$1
            my_username=${my_username#--secure-username=}
            my_username=${my_username#-su=}
            ;;
        -sp=* | --secure-password=* )
            my_password=$1
            my_password=${my_password#--secure-password=}
            my_password=${my_password#-sp=}
            ;;
        -p=* | --prefix=* )
            my_oss_prefix=$1
            my_oss_prefix=${my_oss_prefix#--prefix=}
            my_oss_prefix=${my_oss_prefix#-p=}
            ;;
        -n=* | --netsim=* )
            my_netsim_file=$1
            my_netsim_file=${my_netsim_file#--netsim=}
            my_netsim_file=${my_netsim_file#-n=}
            ;;
        -i=* | --ip=* )
            my_ip=$1
            my_ip=${my_ip#--ip=}
            my_ip=${my_ip#-i=}
            ;;
        -b=* | --batch-size=* )
            MY_BATCH_SIZE=$1
            MY_BATCH_SIZE=${MY_BATCH_SIZE#--batch-size=}
            MY_BATCH_SIZE=${MY_BATCH_SIZE#-b=}
            ;;
        -g=* | --get-batch-size=* )
            MY_GET_BATCH_SIZE=$1
            MY_GET_BATCH_SIZE=${MY_GET_BATCH_SIZE#--get-batch-size=}
            MY_GET_BATCH_SIZE=${MY_GET_BATCH_SIZE#-g=}
            ;;
        -o=* | --out-dir=* )
            my_out_dir=$1
            my_out_dir=${my_out_dir#--out-dir=}
            my_out_dir=${my_out_dir#-o=}
            ;;
        -* )
            echo
            echo $1: unknown option
            error
            exit 1
            ;;
        * )
            case $param_index in
                * )
                    my_nes=$1
                    MY_NES_ARRAY+=($my_nes)
                    param_index=$(($param_index + 1))
                    ;;
            esac
            ;;
    esac
    shift
done

MY_NUM_NES=${#MY_NES_ARRAY[@]}
if [ $MY_NUM_NES -le 0 ]; then
    echo
    echo Missing NES mandatory parameter
    error
    exit 1
fi

my_ipv4_regexp="((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])"
my_ipv6_regexp="(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))"

rm -f $my_all_sim_nes
touch $my_all_sim_nes

if [ "$my_netsim_file" != "" ]; then
    if [ ! -f "$my_netsim_file" ]; then
        echo
        echo $my_netsim_file : No such file or directory
        error
        exit 1
    fi

    # Grep in the netsim file the occurrences for all items in MY_NES_ARRAY
    my_counter=0
    while [ $my_counter -lt $MY_NUM_NES ]; do
        my_nes=${MY_NES_ARRAY[$my_counter]}
        if [ $my_is_verbose == true ]; then
            echo
            echo All data matching $my_nes in netsim file $my_netsim_file will be managed
        fi
        grep $my_nes $my_netsim_file >> $my_all_sim_nes
        my_counter=$(($my_counter + 1))
    done

    my_num_of_nodes=`cat $my_all_sim_nes | wc -l`
    if [ $my_num_of_nodes -le 0 ]; then
        echo
        echo No match found for requested NES in file $my_netsim_file
        error
        exit 1
    fi
else
    if [ "$my_ip" != "" ]; then
        if [[ ! $my_ip =~ $my_ipv4_regexp ]] && [[ ! $my_ip =~ $my_ipv6_regexp ]]; then
            echo
            echo $my_ip: wrong format for node IP Address
            error
            exit 1
        fi
        if [ $MY_NUM_NES -gt 1 ]; then
            echo
            echo $my_ip: too many NES specified for a single IP Address
            error
            exit 1
        fi
        my_ne=${MY_NES_ARRAY[0]}
        echo $my_ne $my_ip > $my_all_sim_nes
        my_num_of_nodes=1
    else
        echo
        echo Missing both IP address and netsim file to read simulation info!
        error 
        exit 1
    fi
fi

if [ "$my_type" == "" ]; then
    if [ $my_num_of_nodes -eq 1 ]; then
        echo
        echo Missing node type. It is mandatory for single node.
        error 
        exit 1
    fi

    echo
    echo Node type not specified. Batch files for type-independent commands will be generated only.
    my_oam_partial_template="./templates/oam_partial.xml"
    my_ipsec_partial_template="./templates/ipsec_partial.xml"
else
    my_node_template="./templates/node.txt"

    if [ "$my_type" == "ERBS" ] || [ "$my_type" == "MGW" ]; then
        my_node_create_template="./templates/CPP/node_create.txt"
        my_oam_partial_template="./templates/CPP/oam_partial.xml"
        my_ipsec_partial_template="./templates/CPP/ipsec_partial.xml"
    elif [ "$my_type" == "RadioNode" ]; then
        my_node_create_template="./templates/COMECIM/node_create.txt"
        my_oam_partial_template="./templates/COMECIM/oam_partial.xml"
        my_ipsec_partial_template="./templates/COMECIM/ipsec_partial.xml"
    elif [ "$my_type" == "MSRBS_V1" ]; then
        my_node_create_template="./templates/COMECIM/node_create.txt"
        my_oam_partial_template="./templates/COMECIM/MSRBS_V1/oam_partial.xml"
        my_ipsec_partial_template="./templates/COMECIM/MSRBS_V1/ipsec_partial.xml"
    else
        echo
        echo $my_type: unsupported node type
        error
        exit 1
    fi
fi

if [ $my_num_of_nodes -eq 1 ]; then
    echo
    echo Single node : no batch files will be generated.
    MY_BATCH_SIZE=1
    MY_GET_BATCH_SIZE=1
    my_is_single_node=true
else
    if [ "$MY_BATCH_SIZE" == "" ] || [ $MY_BATCH_SIZE -gt $my_num_of_nodes ]; then
        MY_BATCH_SIZE=$my_num_of_nodes
    fi
    if [ "$MY_GET_BATCH_SIZE" == "" ]; then
        MY_GET_BATCH_SIZE=$MY_BATCH_SIZE
    fi
    if [ $MY_GET_BATCH_SIZE -gt $MY_BATCH_SIZE ]; then
        echo
        echo Get batch size $MY_GET_BATCH_SIZE cannot exceed batch size $MY_BATCH_SIZE
        error
        exit 1
    fi
    my_is_single_node=false

    echo
    echo Number of nodes to be managed is $my_num_of_nodes
    echo Number of nodes in a batch is $MY_BATCH_SIZE
    echo Number of nodes in a get batch is $MY_GET_BATCH_SIZE
fi

my_nes_dir=
my_counter=0
while [ $my_counter -lt $MY_NUM_NES ]; do
  my_nes=${MY_NES_ARRAY[$my_counter]}
  if [ "$my_nes_dir" == "" ]; then
      my_nes_dir=$my_nes
  else
      my_nes_dir=$my_nes_dir-$my_nes
  fi
  my_counter=$(($my_counter + 1))
done

my_cli_cmd_out_dir=$my_out_dir/$my_nes_dir
rm -rf $my_cli_cmd_out_dir
mkdir -p $my_cli_cmd_out_dir

if [ $my_is_verbose == true ]; then
    echo
    echo my_nes_dir=$my_nes_dir
    echo my_cli_cmd_out_dir=$my_cli_cmd_out_dir
fi

my_tmp_node_create_file="./tmp_node_create_file.txt"
rm -f $my_tmp_node_create_file
touch $my_tmp_node_create_file
my_tmp_node_delete_file="./tmp_node_delete_file.txt"
rm -f $my_tmp_node_delete_file
touch $my_tmp_node_delete_file
my_tmp_oam_partial_file="./tmp_oam_partial_file.txt"
rm -f $my_tmp_oam_partial_file
touch $my_tmp_oam_partial_file
my_tmp_ipsec_partial_file="./tmp_ipsec_partial_file.txt"
rm -f $my_tmp_ipsec_partial_file
touch $my_tmp_ipsec_partial_file

#Loop over the nodes
count=1
batch_index=1

while [ ${count} -le ${my_num_of_nodes} ]
do
    batch_count=1
    get_batch_index=1

    if [ $my_is_single_node == false ]; then
        batch_prefix=BATCH-$batch_index
        my_nodelist=""

        my_batch_dir=$my_cli_cmd_out_dir/${batch_prefix}
        mkdir -p $my_batch_dir

        # Batch nodelist
        my_batch_nodelist=${my_batch_dir}/${batch_prefix}-nodelist.txt
        touch $my_batch_nodelist

        # Batch nodefile
        my_batch_nodefile=${my_batch_dir}/${batch_prefix}-nodefile.txt
        touch $my_batch_nodefile

        # Batch node unconfigure
        my_batch_node_unconfigure=${my_batch_dir}/${batch_prefix}-node-unconfigure.txt
        cp -f $my_node_unconfigure_template $my_batch_node_unconfigure

        if [ "$my_type" != "" ]; then
            # Batch node configure
            my_batch_node_configure=${my_batch_dir}/${batch_prefix}-node-configure.txt
            cp -f $my_node_configure_template $my_batch_node_configure
        fi

        # Batch OAM certificate issue
        my_batch_OAM_cert_issue_xml=${my_batch_dir}/${batch_prefix}-OAM-issue.xml
        my_batch_OAM_cert_issue=${my_batch_dir}/${batch_prefix}-OAM-cert-issue.txt
        cp -f $my_oam_root_template $my_batch_OAM_cert_issue_xml
        touch $my_batch_OAM_cert_issue

        # Batch IPSEC certificate issue
        my_batch_IPSEC_cert_issue_xml=${my_batch_dir}/${batch_prefix}-IPSEC-issue.xml
        my_batch_IPSEC_cert_issue=${my_batch_dir}/${batch_prefix}-IPSEC-cert-issue.txt
        cp -f $my_ipsec_root_template $my_batch_IPSEC_cert_issue_xml
        touch $my_batch_IPSEC_cert_issue

        # Batch OAM certificate reissue
        my_batch_OAM_cert_reissue=${my_batch_dir}/${batch_prefix}-OAM-cert-reissue.txt
        my_batch_OAM_cert_reissue_nodefile=${my_batch_dir}/${batch_prefix}-OAM-cert-reissue-nodefile.txt
        touch $my_batch_OAM_cert_reissue
        touch $my_batch_OAM_cert_reissue_nodefile

        # Batch IPSEC certificate reissue
        my_batch_IPSEC_cert_reissue=${my_batch_dir}/${batch_prefix}-IPSEC-cert-reissue.txt
        my_batch_IPSEC_cert_reissue_nodefile=${my_batch_dir}/${batch_prefix}-IPSEC-cert-reissue-nodefile.txt
        touch $my_batch_IPSEC_cert_reissue
        touch $my_batch_IPSEC_cert_reissue_nodefile

        # Batch OAM certificate get
        my_batch_OAM_cert_get=${my_batch_dir}/${batch_prefix}-OAM-cert-get.txt
        touch $my_batch_OAM_cert_get

        # Batch IPSEC certificate get
        my_batch_IPSEC_cert_get=${my_batch_dir}/${batch_prefix}-IPSEC-cert-get.txt
        touch $my_batch_IPSEC_cert_get

        # Batch OAM trust distribute
        my_batch_OAM_trust_distribute=${my_batch_dir}/${batch_prefix}-OAM-trust-distribute.txt
        my_batch_OAM_trust_distribute_nodefile=${my_batch_dir}/${batch_prefix}-OAM-trust-distribute-nodefile.txt
        touch $my_batch_OAM_trust_distribute
        touch $my_batch_OAM_trust_distribute_nodefile

        # Batch IPSEC trust distribute
        my_batch_IPSEC_trust_distribute=${my_batch_dir}/${batch_prefix}-IPSEC-trust-distribute.txt
        my_batch_IPSEC_trust_distribute_nodefile=${my_batch_dir}/${batch_prefix}-IPSEC-trust-distribute-nodefile.txt
        touch $my_batch_IPSEC_trust_distribute
        touch $my_batch_IPSEC_trust_distribute_nodefile

        # Batch OAM trust remove
        my_batch_OAM_trust_remove=${my_batch_dir}/${batch_prefix}-OAM-trust-remove.txt
        my_batch_OAM_trust_remove_nodefile=${my_batch_dir}/${batch_prefix}-OAM-trust-remove-nodefile.txt
        touch $my_batch_OAM_trust_remove
        touch $my_batch_OAM_trust_remove_nodefile

        # Batch IPSEC trust remove
        my_batch_IPSEC_trust_remove=${my_batch_dir}/${batch_prefix}-IPSEC-trust-remove.txt
        my_batch_IPSEC_trust_remove_nodefile=${my_batch_dir}/${batch_prefix}-IPSEC-trust-remove-nodefile.txt
        touch $my_batch_IPSEC_trust_remove
        touch $my_batch_IPSEC_trust_remove_nodefile

        # Batch OAM trust get
        my_batch_OAM_trust_get=${my_batch_dir}/${batch_prefix}-OAM-trust-get.txt
        touch $my_batch_OAM_trust_get

        # Batch IPSEC trust get
        my_batch_IPSEC_trust_get=${my_batch_dir}/${batch_prefix}-IPSEC-trust-get.txt
        touch $my_batch_IPSEC_trust_get
    else
        my_node_dir=$my_cli_cmd_out_dir
    fi

    while [ ${batch_count} -le ${MY_BATCH_SIZE} ] && [ ${count} -le ${my_num_of_nodes} ]
    do
        get_batch_count=1

        if [ $my_is_single_node == false ]; then
            get_batch_prefix=GET-$get_batch_index
            my_get_nodelist=""

            my_get_batch_dir=${my_batch_dir}/GET
            mkdir -p ${my_get_batch_dir}

            # Batch Get nodelist
            my_batch_get_nodelist=${my_get_batch_dir}/${batch_prefix}-${get_batch_prefix}-nodelist.txt
            touch $my_batch_get_nodelist

            # Batch Get nodefile
            my_batch_get_nodefile=${my_get_batch_dir}/${batch_prefix}-${get_batch_prefix}-nodefile.txt
            touch $my_batch_get_nodefile

            # Batch Get OAM certificate get
            my_batch_get_OAM_cert_get=${my_get_batch_dir}/${batch_prefix}-${get_batch_prefix}-OAM-cert-get.txt
            my_batch_get_OAM_cert_get_nodefile=${my_get_batch_dir}/${batch_prefix}-${get_batch_prefix}-OAM-cert-get-nodefile.txt
            touch $my_batch_get_OAM_cert_get
            touch $my_batch_get_OAM_cert_get_nodefile

            # Batch Get IPSEC certificate get
            my_batch_get_IPSEC_cert_get=${my_get_batch_dir}/${batch_prefix}-${get_batch_prefix}-IPSEC-cert-get.txt
            my_batch_get_IPSEC_cert_get_nodefile=${my_get_batch_dir}/${batch_prefix}-${get_batch_prefix}-IPSEC-cert-get-nodefile.txt
            touch $my_batch_get_IPSEC_cert_get
            touch $my_batch_get_IPSEC_cert_get_nodefile

            # Batch Get OAM trust get
            my_batch_get_OAM_trust_get=${my_get_batch_dir}/${batch_prefix}-${get_batch_prefix}-OAM-trust-get.txt
            my_batch_get_OAM_trust_get_nodefile=${my_get_batch_dir}/${batch_prefix}-${get_batch_prefix}-OAM-trust-get-nodefile.txt
            touch $my_batch_get_OAM_trust_get
            touch $my_batch_get_OAM_trust_get_nodefile

            # Batch Get IPSEC trust get
            my_batch_get_IPSEC_trust_get=${my_get_batch_dir}/${batch_prefix}-${get_batch_prefix}-IPSEC-trust-get.txt
            my_batch_get_IPSEC_trust_get_nodefile=${my_get_batch_dir}/${batch_prefix}-${get_batch_prefix}-IPSEC-trust-get-nodefile.txt
            touch $my_batch_get_IPSEC_trust_get
            touch $my_batch_get_IPSEC_trust_get_nodefile
        fi

        while [ ${get_batch_count} -le ${MY_GET_BATCH_SIZE} ] && [ ${batch_count} -le ${MY_BATCH_SIZE} ] && [ ${count} -le ${my_num_of_nodes} ]
        do
            my_skip_node=false
            #This code read the {count}-nth line in file
            # sed '10q;d' file : read the 10th line in file
            my_node=`sed $count'q;d' $my_all_sim_nes | awk '{print $1}'`
            my_ip=`sed $count'q;d' $my_all_sim_nes | awk '{print $2}'`
            #echo
            #echo Data read from netsim file ${my_node}, ${my_ip}
            if [ "$my_ip" != "" ]; then
                if [[ ! $my_ip =~ $my_ipv4_regexp ]] && [[ ! $my_ip =~ $my_ipv6_regexp ]]; then
                    echo $my_ip: wrong format for node IP Address
                    echo Skip $my_node node!
                    my_skip_node=true
                fi
            else
                #echo Empty IP address
                #echo Skip $my_node node!
                my_skip_node=true
            fi

            if [ $my_skip_node == false ]; then
                if [ $my_is_verbose == true ]; then
                    echo "Node name is ${my_node}, IP address ${my_ip}"
                fi

                if [ "$my_type" != "" ]; then
                    # Replace data for node create file
                    sed "s/XXNODENAMEXX/$my_node/g;s/XXNETYPEXX/$my_type/g;s/XXOSSPREFIXXX/$my_oss_prefix/g;s/XXUSERNAMEXX/$my_username/g;s/XXPASSWORDXX/$my_password/g;s/XXIPADDRESSXX/$my_ip/g" $my_node_create_template >> $my_tmp_node_create_file
                fi

                # Replace data for node delete file
                sed "s/XXNODENAMEXX/$my_node/g" $my_node_delete_template >> $my_tmp_node_delete_file
                # Replace data for partial OAM xml file, common to all nodes into a tmp partial file
                sed "s/XXNODENAMEXX/$my_node/g" $my_oam_partial_template >> $my_tmp_oam_partial_file
                # Replace data for partial IPSEC xml file, common to all nodes into a tmp partial file
                sed "s/XXNODENAMEXX/$my_node/g;s/XXIPADDRESSXX/$my_ip/g" $my_ipsec_partial_template >> $my_tmp_ipsec_partial_file

                if [ $my_is_single_node == false ]; then
                    my_nodelist="${my_nodelist},$my_node"
                    my_get_nodelist="${my_get_nodelist},$my_node"
                    echo $my_node >> $my_batch_nodefile
                    echo $my_node >> $my_batch_get_nodefile
                else
                    # Node file
                    my_node_file=$my_node_dir/$my_node.txt
                    cp -f $my_node_template $my_node_file

                    # OAM issue file
                    my_oam_file=$my_node_dir/$my_node-OAM.xml
                    cp -f $my_oam_root_template $my_oam_file

                    # IPSEC issue file
                    my_ipsec_file=$my_node_dir/$my_node-IPSEC.xml
                    cp -f $my_ipsec_root_template $my_ipsec_file

                    # Replace data in node file single node for node create section
                    sed -e "/XXNODECREATEXX/ {" -e "r ${my_tmp_node_create_file}" -e "d" -e "}" -i $my_node_file
                    # Replace data in node file single node for node delete section
                    sed -e "/XXNODEDELETEXX/ {" -e "r ${my_tmp_node_delete_file}" -e "d" -e "}" -i $my_node_file
                    # Replace node name in node file single node
                    sed -i "s/XXNODENAMEXX/$my_node/g" $my_node_file

                    #Replace data for OAM xml file for single node
                    sed -e "/XXOAMDATAXX/ {" -e "r ${my_tmp_oam_partial_file}" -e "d" -e "}" -i $my_oam_file
                    #Replace data for IPSEC xml file for single node
                    sed -e "/XXIPSECDATAXX/ {" -e "r ${my_tmp_ipsec_partial_file}" -e "d" -e "}" -i $my_ipsec_file
                fi

                if [ $my_is_verbose == true ]; then
                    echo "Commands generated for node $my_node."
                fi
            fi
            get_batch_count=$(( $get_batch_count + 1 ))
            batch_count=$(( $batch_count + 1 ))
            count=$(( $count + 1 ))
        done

        if [ $my_is_single_node == false ]; then
            #Replace data related to get batch nodefile option in commands
            #echo Batch $batch_index Get $get_batch_index
            my_get_nodelist=${my_get_nodelist#","}
            echo $my_get_nodelist > $my_batch_get_nodelist

            # Branch Get OAM certificate get command
            sed "s/XXCERTCOMMANDXX/get/g;s/XXCERTTYPEXX/OAM/g;s/XXNODENAMEXX/${my_get_nodelist}/g" $my_cert_cmd_template >> $my_batch_get_OAM_cert_get
            cat $my_batch_get_OAM_cert_get >> $my_batch_OAM_cert_get
            sed "s/XXCERTCOMMANDXX/get/g;s/XXCERTTYPEXX/OAM/g;s/XXNODEFILEXX/${batch_prefix}-${get_batch_prefix}/g" $my_cert_cmd_nodefile_template >> $my_batch_get_OAM_cert_get_nodefile

            # Branch Get IPSEC certificate get command
            sed "s/XXCERTCOMMANDXX/get/g;s/XXCERTTYPEXX/IPSEC/g;s/XXNODENAMEXX/${my_get_nodelist}/g" $my_cert_cmd_template >> $my_batch_get_IPSEC_cert_get
            cat $my_batch_get_IPSEC_cert_get >> $my_batch_IPSEC_cert_get
            sed "s/XXCERTCOMMANDXX/get/g;s/XXCERTTYPEXX/IPSEC/g;s/XXNODEFILEXX/${batch_prefix}-${get_batch_prefix}/g" $my_cert_cmd_nodefile_template >> $my_batch_get_IPSEC_cert_get_nodefile

            # Branch Get OAM trust get command
            sed "s/XXTRUSTCOMMANDXX/get/g;s/XXCERTTYPEXX/OAM/g;s/XXNODENAMEXX/${my_get_nodelist}/g" $my_trust_cmd_template >> $my_batch_get_OAM_trust_get
            cat $my_batch_get_OAM_trust_get >> $my_batch_OAM_trust_get
            sed "s/XXTRUSTCOMMANDXX/get/g;s/XXCERTTYPEXX/OAM/g;s/XXNODEFILEXX/${batch_prefix}-${get_batch_prefix}/g" $my_trust_cmd_nodefile_template >> $my_batch_get_OAM_trust_get_nodefile

            # Branch Get IPSEC trust get command
            sed "s/XXTRUSTCOMMANDXX/get/g;s/XXCERTTYPEXX/IPSEC/g;s/XXNODENAMEXX/${my_get_nodelist}/g" $my_trust_cmd_template >> $my_batch_get_IPSEC_trust_get
            cat $my_batch_get_IPSEC_trust_get >> $my_batch_IPSEC_trust_get
            sed "s/XXTRUSTCOMMANDXX/get/g;s/XXCERTTYPEXX/IPSEC/g;s/XXNODEFILEXX/${batch_prefix}-${get_batch_prefix}/g" $my_trust_cmd_nodefile_template >> $my_batch_get_IPSEC_trust_get_nodefile
        fi

        get_batch_index=$(( $get_batch_index + 1 ))
    done

    if [ $my_is_single_node == false ]; then
        #Replace data related to nodelist option in commands
        #Strip 1st char in list that is ','
        my_nodelist=${my_nodelist#","}
        echo $my_nodelist > $my_batch_nodelist

        #echo
        #echo Batch $batch_index: nodelist=$my_nodelist

        if [ "$my_type" != "" ]; then
            # Batch node configure
            sed -e "/XXNODECREATEDATAXX/ {" -e "r ${my_tmp_node_create_file}" -e "d" -e "}" -i $my_batch_node_configure
        fi

        # Batch node unconfigure
        sed -e "/XXNODEDELETEDATAXX/ {" -e "r ${my_tmp_node_delete_file}" -e "d" -e "}" -i $my_batch_node_unconfigure

        # Batch OAM certificate reissue command
        sed "s/XXCERTCOMMANDXX/reissue/g;s/XXCERTTYPEXX/OAM/g;s/XXNODENAMEXX/${my_nodelist}/g" $my_cert_cmd_template >> $my_batch_OAM_cert_reissue
        sed "s/XXCERTCOMMANDXX/reissue/g;s/XXCERTTYPEXX/OAM/g;s/XXNODEFILEXX/${batch_prefix}/g" $my_cert_cmd_nodefile_template >> $my_batch_OAM_cert_reissue_nodefile

        # Batch IPSEC certificate reissue command
        sed "s/XXCERTCOMMANDXX/reissue/g;s/XXCERTTYPEXX/IPSEC/g;s/XXNODENAMEXX/${my_nodelist}/g" $my_cert_cmd_template >> $my_batch_IPSEC_cert_reissue
        sed "s/XXCERTCOMMANDXX/reissue/g;s/XXCERTTYPEXX/IPSEC/g;s/XXNODEFILEXX/${batch_prefix}/g" $my_cert_cmd_nodefile_template >> $my_batch_IPSEC_cert_reissue_nodefile

        # Batch OAM trust distribute command 
        sed "s/XXTRUSTCOMMANDXX/distribute/g;s/XXCERTTYPEXX/OAM/g;s/XXNODENAMEXX/${my_nodelist}/g" $my_trust_cmd_template >> $my_batch_OAM_trust_distribute
        sed "s/XXTRUSTCOMMANDXX/distribute/g;s/XXCERTTYPEXX/OAM/g;s/XXNODEFILEXX/${batch_prefix}/g" $my_trust_cmd_nodefile_template >> $my_batch_OAM_trust_distribute_nodefile

        # Batch IPSEC trust distribute command 
        sed "s/XXTRUSTCOMMANDXX/distribute/g;s/XXCERTTYPEXX/IPSEC/g;s/XXNODENAMEXX/${my_nodelist}/g" $my_trust_cmd_template >> $my_batch_IPSEC_trust_distribute
        sed "s/XXTRUSTCOMMANDXX/distribute/g;s/XXCERTTYPEXX/IPSEC/g;s/XXNODEFILEXX/${batch_prefix}/g" $my_trust_cmd_nodefile_template >> $my_batch_IPSEC_trust_distribute_nodefile

        # Batch OAM trust remove command 
        sed "s/XXCERTTYPEXX/OAM/g;s/XXNODENAMEXX/${my_nodelist}/g" $my_trust_remove_template >> $my_batch_OAM_trust_remove
        sed "s/XXCERTTYPEXX/OAM/g;s/XXNODEFILEXX/${batch_prefix}/g" $my_trust_remove_nodefile_template >> $my_batch_OAM_trust_remove_nodefile
        # Batch IPSEC trust remove command 
        sed "s/XXCERTTYPEXX/IPSEC/g;s/XXNODENAMEXX/${my_nodelist}/g" $my_trust_remove_template >> $my_batch_IPSEC_trust_remove
        sed "s/XXCERTTYPEXX/IPSEC/g;s/XXNODEFILEXX/${batch_prefix}/g" $my_trust_remove_nodefile_template >> $my_batch_IPSEC_trust_remove_nodefile

        # Batch OAM certificate issue command
        #We need to put the content of my_tmp_oam_partial_file into my_batch_OAM_cert_issue_xml
        sed -e "/XXOAMDATAXX/ {" -e "r ${my_tmp_oam_partial_file}" -e "d" -e "}" -i $my_batch_OAM_cert_issue_xml

        # Batch IPSEC certificate issue command
        #We need to put the content of my_tmp_ipsec_partial_file into my_batch_IPSEC_cert_issue_xml
        sed -e "/XXIPSECDATAXX/ {" -e "r ${my_tmp_ipsec_partial_file}" -e "d" -e "}" -i $my_batch_IPSEC_cert_issue_xml

        # Batch OAM certificate issue command
        sed "s/XXISSUEFILENAMEXX/$batch_prefix/g" $my_oam_cert_issue_template >> $my_batch_OAM_cert_issue

        # Batch IPSEC certificate issue command
        sed "s/XXISSUEFILENAMEXX/$batch_prefix/g" $my_ipsec_cert_issue_template >> $my_batch_IPSEC_cert_issue

        echo
        echo Batch $batch_index files generated.
    fi

    batch_index=$(( $batch_index + 1 ))
done

rm -f $my_all_sim_nes
rm -f $my_tmp_node_create_file
rm -f $my_tmp_node_delete_file
rm -f $my_tmp_oam_partial_file
rm -f $my_tmp_ipsec_partial_file

echo
if [ $my_is_single_node == false ]; then
    echo "Batch commands files available in $my_cli_cmd_out_dir/ directory"
else
    echo "Commands files available in $my_cli_cmd_out_dir/ directory"
fi
echo
echo Done.
echo
exit 0

