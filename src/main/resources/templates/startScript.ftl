export ${INIT_JSON_FILE_NAME=startScript.jsonFilePath}
java -jar app.jar ${startScript.roleSring} ${} ${startScript.lookupStartDelay} ${startScript.testNum} ${startScript.rpcBuffSize}