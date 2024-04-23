# Spring Boot 3 and Red Hat Build of Keycloak

## Purpose
A sample java SpringBoot code to demonstrate a Spring Boot 3 integration with Keycloak 22. It utilize Keycloak login page, and fetch a user's attribute based on Keycloak user profile. 
This code is modified from the original code: https://github.com/edwin/spring-3-keycloak

## Version
- Spring Boot 3.0.4
- Keycloak 22
- Red Hat OpenJDK 17
- Maven

## Installation Steps:
- Make sure you are using Java 17 or later version
  ```
  java --version
  openjdk 17.0.10 2024-01-16
  ```
- Download Red Hat build of KeyCloak server (this app is tested against version 22)
  https://access.redhat.com/jbossnetwork/restricted/listSoftware.html?product=rhbk&downloadType=distributions

  <img width="949" alt="Screenshot 2024-04-18 at 5 25 43 PM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/4c1d0927-8a64-4017-937d-7114a063f038">

  You'll neeed redhat.com user to be able to donwload it.
- Add entry to your hosts file mapping mysso to 127.0.0.1 so we can use a dedicated url for the SSO
  ```
  sudo vim /etc/hosts
  //or any correspinding command in your environment 
  ```
  <img width="677" alt="Screenshot 2024-04-18 at 5 22 43 PM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/e8c5a52c-c6bb-4b60-bb08-044e81af7fd0">

- Extract RHBK 
- Copy the my-theme folder (in our repository) to themes folder under RHBK parent folder
- Run RHBK using the following command (we will use start-dev to start it in the Dev mode, while start only will start it for production use where you need hostname and tls configurations)
  
  ```
  //switch to the bin folder
  ./kc.sh start-dev --hostname=mysso --http-port=8080
  ```
- Set up an admin user and password and check the admin console is working fine.

  <img width="1599" alt="Screenshot 2024-04-18 at 5 21 34 PM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/a6b43eff-6d72-4366-aeb1-b03c7d72bc99">

- Stop the application (Ctl+C) and run the import statement for the realm as following:
  ```
  curl https://raw.githubusercontent.com/osa-ora/springboot-keycloak-sample/main/realm/realm-export.json > realm-export.json
  ./kc.sh import --file ~/Downloads/realm-export.json
  ./kc.sh --spi-login-protocol-openid-connect-legacy-logout-redirect-uri=true start-dev --metrics-enabled=true --hostname=mysso --http-port=8080 --health-enabled=true
  ```
- Switch to "External" realm
<img width="461" alt="Screenshot 2024-04-18 at 5 34 24 PM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/31e0aaeb-8278-4cf2-a513-eaf95afb66b6">

- Create 2 users (Admin and User) in the "external" realm and set up their password, then assign the following roles to them:
  - Admin with role: BIG_USER
  - User1 with role: NORMAL_USER

<img width="1719" alt="Screenshot 2024-04-18 at 5 12 26 PM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/ac6e94f8-fd11-4387-9f7f-e265f22871aa">
<img width="1714" alt="Screenshot 2024-04-18 at 5 13 00 PM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/908985f8-3bd1-486a-b58b-09078dc68abf">

Note: these roles are external-client roles, so you need to select "Filter by Client"
<img width="571" alt="Screenshot 2024-04-19 at 11 30 47 AM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/977e331a-24ff-4117-8204-6421acf88b45">

Navigate to Clients --> External-Client --> Client Scope and select "external-client-dedicated" 
Check existing mapping "client roles", you can see we have included the roles in the token in "resource_access" element, so the client app can validate it.
<img width="493" alt="Screenshot 2024-04-19 at 11 35 46 AM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/7cbb6e97-99b6-4c31-a420-f582842c8010">


- Create client secret token for our external-client (client that will be used for our SpringBoot application)

<img width="1539" alt="Screenshot 2024-04-18 at 5 15 34 PM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/a41c34e6-815e-4df6-805e-2c5c4ecc9ab3">
-   Add the client token to our src/main/resources/application.properties corresponding to "spring.security.oauth2.client.registration.external.client-secret=" property
<img width="887" alt="Screenshot 2024-04-18 at 5 18 25 PM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/50053d0d-9761-48f6-a01a-2f0f0240f032">

- Build and run our SpringBoot application
  ```
  mvn clean package
  java -jar ./target/spring-3-keycloak-1.0-SNAPSHOT.jar
  ```
- Access the application using the following URLs and see the different results:
```
  http://localhost:8081/public
  http://localhost:8081/
  http://localhost:8081/admin
```
First page is a public page and doesn't need any authentication or authorization, the 2nd one need authentication and 3rd page needs authentication and authorization for BIG_USER role only (the admin user in our case, so try to access it by both admin and user1).
Notice, how the look and feel of the different pages:

The Login page theme when you accesss  http://localhost:8081/ or http://localhost:8081/login page.

<img width="1688" alt="Screenshot 2024-04-18 at 5 30 43 PM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/5cc404bf-e2a4-408e-9572-62df6df41503">

When login successfully, you will get redirected to the application home page:

<img width="632" alt="Screenshot 2024-04-18 at 6 10 55 PM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/54e2d005-1e8a-4a53-8904-f40fad187e2d">

You can also click on the account details link to see and manage your account detauls.

<img width="1716" alt="Screenshot 2024-04-18 at 5 32 19 PM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/fa4719c1-7900-4222-afe0-b09dcae8800f">

When you try access the admin page using the user1:

<img width="548" alt="Screenshot 2024-04-18 at 6 12 24 PM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/cbbf46f5-c44c-40fe-9a88-7ed916eaac86">

## Metrics & Health Information

As we enabled the metrics and health information using the command line parameter: --metrics-enabled=true --health-enabled=true
You can access the metics using the url: http://mysso:8080/metrics, Prometheus text format which can be integrated with OpenShift Metrics or Cryostat.

<img width="842" alt="Screenshot 2024-04-18 at 6 20 37 PM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/0eef37d6-60b3-4d30-97a9-823d2dda2f98">

You can access the health information using the url: http://mysso:8080/health (or http://mysso:8080/health/live or http://mysso:8080/health/ready)

<img width="541" alt="Screenshot 2024-04-18 at 6 21 18 PM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/15d458cf-08a8-411c-bae8-8399bff4f47c">

## Configurations
As this release is built using Quarkus and built for both rraditional deployment or container based deployment, the configurations can have many options:
1- Using the command line (as we did)
2- Using Environment variables (just prefix the parameters with KC_parameter-name so it doesn't overlap with any other environment variables
3- Using config file.
To know the available configurations, check:
```
./kc.sh show-config
```
We can use the environment variable in our example, so we will set environment variable for the hostname, make sure the KC command picked it using show-config, then remove the hostname from the command line parameters:

```
export KC_HOSTNAME=mysso
echo $KC_HOSTNAME
./kc.sh show-config
./kc.sh --spi-login-protocol-openid-connect-legacy-logout-redirect-uri=true start-dev --metrics-enabled=true --http-port=8080 --health-enabled=true 
```
Now if you override the command with another hostname such as localhost, the mysso hostname will not be configured as the order of priority is command line > environment variables > config files.

## Database Configurations
Note that as we didn't specify any DB, the RHBK will use h2, but you can easily configure any supported DB by parameters, but to get the best options use the "build" phase to optomize the configurations then run the start command afterwards.

```
   ./kc.sh start \
        --db postgres \
        --db-url-host my-postgres-db \
        --db-username db-user \
        --db-password db-password
```

## Using REST APIs with RHBK

You can perform any action using the Keycloak REST APIs, for example:
```
curl \
  -d "client_id=admin-cli" \
  -d "username=admin" \
  -d "password=admin" \
  -d "grant_type=password" \
  "http://mysso:8080/realms/master/protocol/openid-connect/token"

//this will output access token:
{"access_token":"eyJhb… }

//then take the ourput token to call the subsequent calls

curl -H “Authorization: bearer eyJhb…” \ 
"http://mysso:8080/admin/realms/master"
```

## Theme Customization
You can do a full customization for the theme used in different pages:
Account - Account management
Admin - Admin Console
Email - Emails
Login - Login forms
Welcome - Welcome page

Keycloak uses Apache Freemarker templates to generate HTML and render pages. All you need is to create a folder with your theme name and either put your created files or extend one of the existing themes and customize it.
For example for the Account page, we just modified the icon, so we created a folder called my-theme/account and added a file called theme.properties:
```
parent=keycloak.v2
import=common/keycloak
logo=/img/my-logo.png
```
So we extended the parent theme and just changed the logo, in the my-theme/account/resources/img we placed the logo icon "my-logo.png"

For the login page, we changed the background image using the css, so in the theme.properties:
```
parent=keycloak
import=common/keycloak
styles=web_modules/@patternfly/react-core/dist/styles/base.css web_modules/@patternfly/react-core/dist/styles/app.css node_modules/patternfly/dist/css/patternfly.min.css node_modules/patternfly/dist/css/patternfly-additions.min.css css/login.css css/mycss.css
```
So we kept everything but added one additional mycss.css file that has the following content:
```
.login-pf body {
    background: url("../img/my-logo.png") repeat center center fixed;
    background-size: repeat;
    height: 60%;
    margin: 0;
}
```
And we also placed the image in the my-theme/login/resources/img folder.

<img width="1087" alt="Screenshot 2024-04-18 at 6 52 20 PM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/d8756211-e0c7-4541-93a0-25993e890c99">

## OpenShift Deployment
You can deploy RHBK to OCP using the operator, it will simplify the required configurations and HA setup, you need to prepare the DB configurations, hostname and tls configurations for a production use instance.

<img width="594" alt="Screenshot 2024-04-18 at 6 54 06 PM" src="https://github.com/osa-ora/springboot-keycloak-sample/assets/18471537/431d9336-7191-414d-9324-e32fdcead8a1">


