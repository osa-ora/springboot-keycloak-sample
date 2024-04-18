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
- Run RHBK using the following command 
  ```
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

  
  
