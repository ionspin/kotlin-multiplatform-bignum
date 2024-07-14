if [[ -n "$GITHUB_ACTION" ]]; then
  echo "Running without verification"
  ./gradlew build --dependency-verification lenient -x watchosX64Test -x jsBrowserTest -x wasmJsBrowserTest || exit 1
else
  echo "Running with verification"
./gradlew build -x watchosX64Test -x jsBrowserTest -x wasmJsBrowserTest || exit 1
fi

exit 0
