./gradlew build \
bignum:publishJvmPublicationToSnapshotRepository \
bignum:publishJsPublicationToSnapshotRepository \
bignum:publishKotlinMultiplatformPublicationToSnapshotRepository \
bignum:publishLinuxX64PublicationToSnapshotRepository \
bignum:publishLinuxArm64PublicationToSnapshotRepository \
bignum:publishAndroidNativeX64PublicationToSnapshotRepository \
bignum:publishAndroidNativeX86PublicationToSnapshotRepository \
bignum:publishAndroidNativeArm32PublicationToSnapshotRepository \
bignum:publishAndroidNativeArm64PublicationToSnapshotRepository \
bignum:publishWasmJsPublicationToSnapshotRepository \
bignum:publishWasmWasiPublicationToSnapshotRepository || exit 1

./gradlew \
bignum-serialization-kotlinx:publishJsPublicationToSnapshotRepository \
bignum-serialization-kotlinx:publishJvmPublicationToSnapshotRepository \
bignum-serialization-kotlinx:publishJsPublicationToSnapshotRepository \
bignum-serialization-kotlinx:publishKotlinMultiplatformPublicationToSnapshotRepository \
bignum-serialization-kotlinx:publishLinuxX64PublicationToSnapshotRepository \
bignum-serialization-kotlinx:publishLinuxArm64PublicationToSnapshotRepository \
bignum-serialization-kotlinx:publishAndroidNativeX64PublicationToSnapshotRepository \
bignum-serialization-kotlinx:publishAndroidNativeX86PublicationToSnapshotRepository \
bignum-serialization-kotlinx:publishAndroidNativeArm32PublicationToSnapshotRepository \
bignum-serialization-kotlinx:publishAndroidNativeArm64PublicationToSnapshotRepository \
bignum-serialization-kotlinx:publishWasmJsPublicationToSnapshotRepository \
bignum-serialization-kotlinx:publishWasmWasiPublicationToSnapshotRepository || exit 1
exit 0
