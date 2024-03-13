#!/bin/bash

git --no-pager log --pretty=format:"%s" "$1"..."$2"