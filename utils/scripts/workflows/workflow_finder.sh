#!/bin/bash
my_version=1.0.1
my_is_verbose=false
my_command=$(basename $0)
my_out_dir="/tmp/workflow_finder/output"
my_in_dir="."
MY_PATTERNS_ARRAY=()
MY_TASKS_ARRAY=()
my_num_of_tasks=0

param_index=0

usage() {
cat << EOF   

Usage: `basename $0` <PATTERNS>... [<OPTIONS>]
where:
  <PATTERNS>                                  The workflow task patterns. Multiple task patterns can be specified.
  <OPTIONS>:
    -h, --help                                This help
    --version                                 The script version
    -v, --verbose                             Set verbose mode on
    -e, --examples                            Some examples
    -i, --in-dir=<INDIR>                      The optional input directory. ${my_in_dir} as default.
    -o, --out-dir=<OUTDIR>                    The optional output directory. ${my_out_dir} as default

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

./${my_command} CheckCertAlreadyInstalledTask -i=~/workspace/ -o=/media/sf_Ericsson/wffTest
./${my_command} AlreadyInstalled -i=~/workspace/ -o=/media/sf_Ericsson/wffTest

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
        -i=* | --in-dir=* )
            my_in_dir=$1
            my_in_dir=${my_in_dir#--in-dir=}
            my_in_dir=${my_in_dir#-i=}
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
                    my_pattern=$1 
                    MY_PATTERNS_ARRAY+=($my_pattern)
                    param_index=$(($param_index + 1))
                    ;;
            esac
            ;;
    esac
    shift
done

MY_NUM_PATTERNS=${#MY_PATTERNS_ARRAY[@]}
if [ $MY_NUM_PATTERNS -le 0 ]; then
    echo
    echo Missing PATTERNS mandatory parameter
    error
    exit 1
fi

my_node_security_dir=${my_in_dir}/node-security
my_security_workflows_dir=${my_in_dir}/security-workflows
my_security_workflows_java_dir=${my_security_workflows_dir}/security-workflows-war/src/main/java/
my_security_workflows_resources_dir=${my_security_workflows_dir}/security-workflows-war/src/main/resources/

if [ ! -d $my_node_security_dir ]; then
    echo
    echo $my_node_security_dir : No such file or directory
    error
    exit 1
fi

if [ ! -d $my_security_workflows_dir ]; then
    echo
    echo $my_security_workflows_dir : No such file or directory
    error
    exit 1
fi

if [ $my_is_verbose == true ]; then
    echo
    echo node-security=$my_node_security_dir
    echo security-workflows=$my_security_workflows_dir
fi

my_workflows_out_dir=$my_out_dir
mkdir -p $my_workflows_out_dir

my_workflows_xml=$my_workflows_out_dir/workflows.xml
rm -f $my_workflows_xml
touch $my_workflows_xml

my_workflows_out_tmp_dir=$my_workflows_out_dir/tmp
mkdir -p $my_workflows_out_tmp_dir

my_tmp_tasks_file=${my_workflows_out_tmp_dir}/tmp_tasks.txt
rm -f $my_tmp_tasks_file
touch $my_tmp_tasks_file

my_tmp_builders_file=${my_workflows_out_tmp_dir}/tmp_builders_file.txt
rm -f $my_tmp_builders_file
touch $my_tmp_builders_file

my_tmp_task_builder=${my_workflows_out_tmp_dir}/tmp_task_builder.txt
rm -f $my_tmp_task_builder
touch $my_tmp_task_builder

my_tmp_workflows_file=${my_workflows_out_tmp_dir}/tmp_workflows.txt
rm -f $my_tmp_workflows_file
touch $my_tmp_workflows_file

my_tmp_calling_workflows_file=${my_workflows_out_tmp_dir}/tmp_calling_workflows.txt
rm -f $my_tmp_calling_workflows_file
touch $my_tmp_calling_workflows_file

echo '<?xml version="1.0" encoding="UTF-8"?>' >> $my_workflows_xml
echo "<patterns>" >> $my_workflows_xml

my_counter=0
while [ $my_counter -lt $MY_NUM_PATTERNS ]; do
    my_pattern=${MY_PATTERNS_ARRAY[$my_counter]}
    echo "    <pattern name=\"${my_pattern}\" id=\"$my_counter\">" >> $my_workflows_xml
    find $my_node_security_dir -name "*${my_pattern}*" -print | grep -e "Task.java" > $my_tmp_tasks_file
    my_num_of_tasks=`cat $my_tmp_tasks_file | wc -l`
    echo "        <tasks>" >> $my_workflows_xml
    if [ $my_num_of_tasks -le 0 ]; then
        echo "            <!-- No task found for pattern $my_pattern. Skipping it! -->" >> $my_workflows_xml
        echo "        </tasks>" >> $my_workflows_xml
        echo "    </pattern>" >> $my_workflows_xml
        my_counter=$(($my_counter + 1))
        continue
    fi

    # Loop over the tasks
    task_count=1
    while [ ${task_count} -le ${my_num_of_tasks} ]
    do
        # This code read the {task_count}-nth line in file
        # sed '10q;d' file : read the 10th line in file
        my_task_file=`sed $task_count'q;d' $my_tmp_tasks_file | awk '{print $1}'`
        my_task_class=`basename ${my_task_file} .java`
        if [ $my_is_verbose == true ]; then
            echo Task file: ${my_task_file}
            echo Task class: ${my_task_class}
        fi
        echo "            <task name=\"${my_task_class}\" id=\"$task_count\">" >> $my_workflows_xml
        my_task_handler_file=`find $my_node_security_dir -name "${my_task_class}Handler.java" -print`
        my_task_handler_class=`basename ${my_task_handler_file} .java`
        if [ $my_is_verbose == true ]; then
            echo Task handler file: ${my_task_handler_file}
            echo Task handler class: ${my_task_handler_class}
        fi
        echo "                <handler name=\"${my_task_handler_class}\"/>" >> $my_workflows_xml
        echo "                <delegate-expressions>" >> $my_workflows_xml

        # Find builders for current task
        grep -l -r $my_task_class ${my_security_workflows_java_dir} > $my_tmp_builders_file
        my_num_of_builders=`cat $my_tmp_builders_file | wc -l`
        if [ $my_num_of_builders -le 0 ]; then
            echo "                    <!-- No delegate-expression found for task $my_task_class. Skipping it! -->" >> $my_workflows_xml
            echo "                </delegate-expressions>" >> $my_workflows_xml
            echo "            </task>" >> $my_workflows_xml
            task_count=$(( $task_count + 1 ))
            continue
        fi

        # Loop over the builders
        builder_count=1

        while [ ${builder_count} -le ${my_num_of_builders} ]
        do
            # This code read the {builder_count}-nth line in file
            # sed '10q;d' file : read the 10th line in file
            my_builder_file=`sed $builder_count'q;d' $my_tmp_builders_file | awk '{print $1}'`
            my_builder_class=`basename ${my_builder_file} .java`
            my_annotation_line=`grep -e "@Named(\"" ${my_builder_file}`
            my_builder_annotation_regexp="@Named\(\"([a-zA-Z]*)\"\)"
            my_builder_qualifier=""
            if [[ $my_annotation_line =~ $my_builder_annotation_regexp ]]; then
                my_builder_qualifier=${BASH_REMATCH[1]}
            fi
            if [ $my_is_verbose == true ]; then
                echo my_annotation_line=$my_annotation_line
                echo my_builder_qualifier=$my_builder_qualifier
            fi

            grep -B 1 -h -r $my_task_class ${my_builder_file} > $my_tmp_task_builder
            my_builder_method_regexp="[ ]*public[ ]*AbstractServiceTask[ ]*get([A-Z][a-zA-Z]*)[ ]*\(\)"
            my_delegate_expression=""
            while read line
            do
                if [[ $line =~ $my_builder_method_regexp ]]; then
                    my_uppercase_delegate_expression=${BASH_REMATCH[1]}
                    my_delegate_expression=${my_uppercase_delegate_expression,}
                    # To uppercase first letter ${my_uppercase_delegate_expression^}
                    if [ $my_is_verbose == true ]; then
                        echo uppercase_delegate_method=$my_uppercase_delegate_expression
                        echo delegate_expression=$my_delegate_expression
                    fi
                    break
                fi
            done < $my_tmp_task_builder

            if [ "$my_delegate_expression" != "" ]; then
                if [ "$my_delegate_expression" != "" ]; then
                    my_delegate_expression="$my_builder_qualifier.$my_delegate_expression"
                fi
                if [ $my_is_verbose == true ]; then
                    echo my_delegate_expression=$my_delegate_expression
                fi
                echo "                    <delegate-expression name=\"${my_delegate_expression}\" id=\"$builder_count\">" >> $my_workflows_xml
                echo "                        <builder name=\"${my_builder_class}\"/>" >> $my_workflows_xml
                #grep -h $my_delegate_expression ${my_security_workflows_resources_dir}/*.bpmn
                grep -l $my_delegate_expression ${my_security_workflows_resources_dir}/*.bpmn > $my_tmp_workflows_file
                my_num_of_workflows=`cat $my_tmp_workflows_file | wc -l`
                echo "                        <workflows>" >> $my_workflows_xml
                if [ $my_num_of_workflows -le 0 ]; then
                    echo "                            <!-- No workflow found for delegate-expression $my_delegate_expression. Skipping it! -->" >> $my_workflows_xml
                    echo "                        </workflows>" >> $my_workflows_xml
                    echo "                    </delegate-expression>" >> $my_workflows_xml
                    builder_count=$(( $builder_count + 1 ))
                    continue
                fi

                # Loop over the workflows
                workflow_count=1
                while [ ${workflow_count} -le ${my_num_of_workflows} ]
                do
                    # This code read the {workflow_count}-nth line in file
                    # sed '10q;d' file : read the 10th line in file
                    my_workflow_file=`sed $workflow_count'q;d' $my_tmp_workflows_file | awk '{print $1}'`
                    my_workflow_name=`basename ${my_workflow_file} .bpmn`
                    echo "                            <workflow name=\"${my_workflow_name}\" id=\"$workflow_count\">" >> $my_workflows_xml
                    grep -l -e "calledElement=\"${my_workflow_name}\"" ${my_security_workflows_resources_dir}/*.bpmn > $my_tmp_calling_workflows_file
                    my_num_of_calling_workflows=`cat $my_tmp_calling_workflows_file | wc -l`
                    echo "                                <calling-workflows>" >> $my_workflows_xml
                    if [ $my_num_of_calling_workflows -le 0 ]; then
                        echo "                                    <!-- No calling workflow found for workflow $my_workflow_name. It is a top workflow -->" >> $my_workflows_xml
                        echo "                                </calling-workflows>" >> $my_workflows_xml
                    else
                        # Loop over the calling workflows
                        calling_workflow_count=1
                        while [ ${calling_workflow_count} -le ${my_num_of_calling_workflows} ]
                        do
                            # This code read the {calling_workflow_count}-nth line in file
                            # sed '10q;d' file : read the 10th line in file
                            my_calling_workflow_file=`sed $calling_workflow_count'q;d' $my_tmp_calling_workflows_file | awk '{print $1}'`
                            my_calling_workflow_name=`basename ${my_calling_workflow_file} .bpmn`
                            echo "                                    <calling_workflow name=\"${my_calling_workflow_name}\" id=\"$calling_workflow_count\"/>" >> $my_workflows_xml
                            calling_workflow_count=$(( $calling_workflow_count + 1 ))
                        done
                        echo "                                </calling-workflows>" >> $my_workflows_xml
                    fi
                    echo "                            </workflow>" >> $my_workflows_xml
                    workflow_count=$(( $workflow_count + 1 ))
                done

                echo "                        </workflows>" >> $my_workflows_xml
            else
                echo "                    <!-- No valid delegate-expression found for task $my_task_class. Skipping it! -->" >> $my_workflows_xml
                builder_count=$(( $builder_count + 1 ))
                continue
            fi

            echo "                    </delegate-expression>" >> $my_workflows_xml
            builder_count=$(( $builder_count + 1 ))
        done

        echo "                </delegate-expressions>" >> $my_workflows_xml
        echo "            </task>" >> $my_workflows_xml
        task_count=$(( $task_count + 1 ))
    done

    echo "        </tasks>" >> $my_workflows_xml
    echo "    </pattern>" >> $my_workflows_xml
    my_counter=$(($my_counter + 1))
done
echo "</patterns>" >> $my_workflows_xml

if [ -d $my_workflows_out_tmp_dir ]; then
    if [ $my_is_verbose == true ]; then
        echo
        echo Removing tmp directory $my_workflows_out_tmp_dir
    fi
    rm -rf $my_workflows_out_tmp_dir
fi

echo
echo "Output file available in $my_workflows_xml".
echo "Use for example 'firefox $my_workflows_xml' to view it."
echo
echo Done.
echo
exit 0

