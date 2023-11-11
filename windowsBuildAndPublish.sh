./gradlew build bignum:publishMingwX64PublicationToSnapshotRepository -x spotlessKotlinCheck -x spotlessKotlinGradleCheck || exit 1
./gradlew bignum-serialization-kotlinx:publishMingwX64PublicationToSnapshotRepository -x spotlessKotlinCheck -x spotlessKotlinGradleCheck || exit 1
./gradlew --stop
exit 0
