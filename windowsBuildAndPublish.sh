./gradlew --no-daemon build bignum:publishMingwX64PublicationToSnapshotStagingRepository -x spotlessKotlinCheck -x spotlessKotlinGradleCheck -x jsBrowserTest -x wasmJsBrowserTest || exit 1
./gradlew --no-daemon bignum-serialization-kotlinx:publishMingwX64PublicationToSnapshotStagingRepository -x spotlessKotlinCheck -x spotlessKotlinGradleCheck -x jsBrowserTest -x wasmJsBrowserTest || exit 1
./gradlew --no-daemon bignum:uploadStagedSnapshots || exit 1
./gradlew --no-daemon bignum-serialization-kotlinx:uploadStagedSnapshots || exit 1
./gradlew --stop
exit 0
