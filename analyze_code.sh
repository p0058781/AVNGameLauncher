#!/bin/bash

./gradlew $1 --console=plain ktLintCheck && \
./gradlew $1 --console=plain detektMetadataMain && \
./gradlew $1 --console=plain detektDesktopMain