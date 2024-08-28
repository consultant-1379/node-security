#!/bin/sh

my_version=1.0.0
my_is_verbose=false
my_command=$(basename $0)
my_patches_dir=/netsim/patches
MY_PATCHES_ARRAY=()

usage() {
cat << EOF

Usage: `basename $0` <PATCHES>... [<OPTIONS>]
where:
  <PATCHES>                                   The netsim patches to install.
                                              This field can contain multiple PATCHES (e.g. P05108 P05110)
  <OPTIONS>:
    -h, --help                                This help
    --version                                 The script version
    -v, --verbose                             Set verbose mode on

  The script downloads and installs the specified netsim patches.

EOF
}

version() {
cat << EOF

`basename $0` VERSION: $my_version

EOF
}

error() {
cat << EOF   

Try './${my_command} --help' for more information.

EOF
}

param_index=0

while [ "$1" != "" ] ; do
    case $1 in
        -h | --help )
            usage
            exit 0
            ;;
        --version )
            version
            exit 0
            ;;
        -v | --verbose )
            my_is_verbose=true
            ;;
        -*)
            echo
            echo $1: unknown option
            usage
            exit 1
            ;;
        * )
            case $param_index in
                * )
                    my_patch=$1
                    MY_PATCHES_ARRAY+=($my_patch)
                    param_index=$(($param_index + 1))
                    ;;
            esac
            ;;
    esac
    shift
done

MY_NUM_PATCHES=${#MY_PATCHES_ARRAY[@]}
if [ $MY_NUM_PATCHES -le 0 ]; then
    echo
    echo Missing PATCHES mandatory parameter
    error
    exit 1
fi

mkdir -p $my_patches_dir

echo
echo "Downloading and installing $MY_NUM_PATCHES patches : ${MY_PATCHES_ARRAY[@]} ..."
echo

my_counter=0
while [ $my_counter -lt $MY_NUM_PATCHES ]; do
    my_patch=${MY_PATCHES_ARRAY[$my_counter]}
    echo ".show installation" | /netsim/inst/netsim_shell | grep ^P0 | grep ${my_patch}
    if [ $? != 0 ]; then
        echo ${my_patch} ": patch not yet installed."
        my_patch_zip_name=${my_patch}.zip
        my_patch_zip_url=http://netsim.lmera.ericsson.se/tssweb/patches/${my_patch_zip_name}
        my_patch_zip=${my_patches_dir}/${my_patch_zip_name}
        if [ ! -f $my_patch_zip ]; then
            echo "Downloading ${my_patch} patch from ${my_patch_zip_url} ..."
            wget ${my_patch_zip_url} -P ${my_patches_dir}
        fi
        if [ -f $my_patch_zip ]; then
            echo "Installing ${my_patch} patch ..."
            /netsim/inst/netsim_shell << EOF
.install patch $my_patch_zip
EOF
        else
            echo "${my_patch_zip} : no such file or directory. Skip it."
        fi
    else
        echo ${my_patch} ": patch already installed."
    fi
    my_counter=$(($my_counter + 1))
done

echo
echo Done.
echo
exit 0
