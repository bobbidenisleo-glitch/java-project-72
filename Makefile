.PHONY: build
build:
	cd app && ./gradlew build

.PHONY: run
run:
	cd app && ./gradlew run

.PHONY: test
test:
	cd app && ./gradlew test jacocoTestReport

.PHONY: check
check:
	cd app && ./gradlew check
