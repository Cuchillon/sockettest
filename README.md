#### Requirements
1. Java 14+
2. Gradle 6.5.1+
3. Intellij IDEA 2020.1.3+

#### Running tests
To run tests you need to clone the project and execute the next command in the root of the project:
```
./gradlew clean test -Ptags=binance // to run only binance tests
./gradlew clean test // to run all tests
```

To change web application URL you should append the next parameter to the aforementioned command:
```
-Pcommon.baseUrl=<your URL>
```

To generate allure report you should execute the next command in the root of project after running tests:
```
allure generate build/allure-results
``` 
After that you can find generated report in the 'allure-report' directory