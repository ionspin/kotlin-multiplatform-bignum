if [[ -n "$GITHUB_ACTION" ]]; then
  echo "Running without verification"
  ./gradlew build --dependency-verification lenient -x spotlessKotlinCheck -x spotlessKotlinGradleCheck -x jsBrowserTest -x wasmJsBrowserTest || exit 1
else
  echo "Running with verification"
  ./gradlew build -x spotlessKotlinCheck -x spotlessKotlinGradleCheck -x jsBrowserTest -x wasmJsBrowserTest || exit 1
fi

./gradlew --stop
exit 0
