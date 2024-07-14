if [[ -v "GITHUB_ACTION" ]]; then
  echo "Running without verification"
  ./gradlew build --dependency-verification lenient || exit 1
else
  echo "Running with verification"
  ./gradlew build || exit 1
fi
exit 0
