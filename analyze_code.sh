#!/bin/bash

./gradlew $1 --console=plain ktLintCheck && \
./gradlew $1 --console=plain detektMetadataMain && \
./gradlew $1 --console=plain detektMetadataCommonMain && \
./gradlew $1 --console=plain detektAndroidRelease && \
./gradlew $1 --console=plain detektAndroidDebug && \
./gradlew $1 --console=plain detektDesktopMain