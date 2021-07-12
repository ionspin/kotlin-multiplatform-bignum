./gradlew build bignum:publishMingwX64PublicationToSnapshotRepository bignum:publishMingwX86PublicationToSnapshotRepository || exit 1
./gradlew bignum-serialization-kotlinx:publishMingwX64PublicationToSnapshotRepository  || exit 1
exit 0
