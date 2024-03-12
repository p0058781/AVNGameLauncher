#!/bin/bash

./gradlew --console=plain ktLintCheck && \
./gradlew --console=plain detektMetadataJvmMain && \
./gradlew --console=plain detektJvmMain && \
./gradlew --console=plain detektMetadataMain && \
./gradlew --console=plain detektMetadataCommonMain && \
./gradlew --console=plain detektAndroidRelease && \
./gradlew --console=plain detektAndroidDebug && \
./gradlew --console=plain detektDesktopMain