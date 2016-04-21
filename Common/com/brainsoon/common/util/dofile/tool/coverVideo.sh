#!/bin/bash
cd $1
PARAMS="$2 -y -i $3 -vf \"movie=$4 [wn] ; [in] [wn] overlay=$5 [out]\" $6"
CMD="$PARAMS"
eval $CMD


