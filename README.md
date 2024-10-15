# device-canon-capture
The Printix Canon embedded client for capture

1. How to build local and run JUnit test:
- Build local:
Open Command Prompt at the project location >> input command: gradlew clean assemble 
>> output is JAR file "device-canon-capture-2025.1.0.0.0_CCHN_unsigned.jar, device-canon-capture-2025.1.0.0.0_GLOBAL_unsigned.jar" at $proj\build\dist

- Run JUnit test:
Open Command Prompt at the project location >> input command line: gradlew clean test

2. Config using Postman:
POST: https://{Device_IP}:8443/printixcapture
For example:
{
  "tenantId": "c8c1a8ea-f917-446c-9435-ead28d3b1061",
  "printerId": "e46375c1-48af-4103-9705-03fa076f962f",
  "monitoringWorkstationId": "89ddfa9c-2e60-4151-9b03-d5400f43de95",
  "networkId": "2718d489-d923-4623-9f1c-c5fd6f9584ec",
  "ipOrHostname": "192.168.38.130",
  "vendor": "CANON",
  "username": "admin",
  "password": "MTIzNDU2Nw==",
  "snmpConfigurationId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
}

3. Check app config using Postman:
GET: https://{Device_IP}:8443/printixcapture 

4. How to see Console Logger:
Find Console.jar in MEAP SDK: {MEAP_SDK}\MEAPPart\MEAP_TOOLS\Console
Open command line at Console.jar:
>> Input cmd: javaw -jar Console.jar -host {Device_IP}
>> For example: javaw -jar Console.jar -host 172.26.3.130

5. Enable debug log:
    - method: POST
    - Endpoint:https://{Device_IP}:8443/printixcapture/deviceLogEnable
    - Body: raw - JSON
      {
      "deviceLogEnable":true,
      "logLevel":"DEBUG"
      "username":"Admin",
      "password":"Printix"
      }
    - logLevel is one of the following values: DEBUG/INFO/WARNING/ERROR/CRITICAL
    - Using username/password from Admin account.
      The admin credentials are default "Admin/Printix" (not configured) or configured by sign-in profile. 
