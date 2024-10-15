# device-canon-meap-print
The Printix Canon embedded client for printing

1. How to build local and run JUnit test:
- Build local:
Open Command Prompt at the project location >> input command: gradlew clean assemble 
>> output is JAR file "device-canon-meap-print-dist.jar" at $proj\build\libs folder

- Run JUnit test:
Open Command Prompt at the project location >> input command line: gradlew clean test

2. Config using Postman:
POST: https://{Device_IP}:8443/printixprint
Body as raw data:
{
    "tenantId":{tenantId},
    "printerId":{printerId},
    "deviceCanonHost":{deviceCanonHost}, 
    "printerSecret":{printerSecret}
}
For example:
{
    "tenantId":"8fd32ef6-4f00-4751-b5e3-4ead8268ebb6",
    "printerId":"686c863e-0d69-4f96-a13d-d3879bef4ae4",
    "deviceCanonHost":"https://on-device-api.dev02.printix.dev:443", 
    "printerSecret":"GK63gfqiwejfGr8o1UmUhYgdBi1FkMvNwdQ5kDW87uX4cz8uhbHplKVC"
}

3. Check app config using Postman:
GET: https://{Device_IP}:8443/printixprint 

4. How to see Console Logger:
Find Console.jar in MEAP SDK: {MEAP_SDK}\MEAPPart\MEAP_TOOLS\Console
Open command line at Console.jar:
>> Input cmd: javaw -jar Console.jar -host {Device_IP}
>> For example: javaw -jar Console.jar -host 172.26.3.130

5. Enable debug log:
    - method: POST
    - Endpoint:https://{Device_IP}:8443/printixprint/deviceLogEnable
    - Body: raw - JSON
      {
      "deviceLogEnable":true,
      "logLevel":"DEBUG"
      "username":"Admin",
      "password":"12345678"
      }
    - logLevel is one of the following values: DEBUG/INFO/WARNING/ERROR/CRITICAL
    - Using username/password from Admin account.
      The admin credentials are default "Admin/12345678" (not configured) or configured by sign-in profile. 