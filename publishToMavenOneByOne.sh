#!/bin/bash

./gradlew publishJsPublicationToMavenRepository || exit 1
./gradlew publishJvmPublicationToMavenRepository || exit 1
./gradlew publishKotlinMultiplatformPublicationToMavenRepository || exit 1
./gradlew publishLinuxArm32HfpPublicationToMavenRepository || exit 1
./gradlew publishLinuxArm64PublicationToMavenRepository || exit 1
./gradlew publishLinuxPublicationToMavenRepository || exit 1
./gradlew publishMetadataPublicationToMavenRepository || exit 1
