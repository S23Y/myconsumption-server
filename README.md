# MyConsumption Server + API

[![MyConsumption Server](https://dl.dropboxusercontent.com/u/22987083/banner-myconsumption-server.png)](http://s23y.org)

This repository contains the source code for the S23Y MyConsumption Server and API.

## Server
This is the back end of [MyConsumption](https://github.com/S23Y/myconsumption-android), a mobile application for real-time energy consumption monitoring. The server exposes REST web services to ensure synchronization, distribution and backup of energy consumption data. It is built upon [Spring Boot](http://projects.spring.io/spring-boot/).

## License

* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Building

### With Maven

The easiest way to build is to install [Maven](http://maven.apache.org/download.html)
v3.+ in your development environment. 

Then, the build is pretty simple:

* Run `mvn clean install` from the root directory 

## Deployment
The process to deploy the server is quite straightforward:
* Run the Maven command `mvn package` in the root directory of this project. This will build a self-contained WAR file;
* Rename the WAR file to `ROOT.war`;
* Move it in the folder `/var/lib/tomcat8/webapps/` on a machine where Tomcat 8 and MongoDb are installed;
* Restart Tomcat with the command `/etc/init.d/tomcat8 restart`.

Two other commands may be useful to display the logs:
* `cat /var/log/tomcat8/catalina.out`
* `tail -f /var/log/tomcat8/catalina.201*-**-**.log`

## Acknowledgements

This project uses many other open source libraries such as:

* [Spring](https://github.com/spring-projects/spring-framework)
* [Apache http components](https://github.com/apache/httpclient)
* [Jackson](https://github.com/FasterXML/jackson)

The entire list of dependencies
is listed in the Maven files of the project such as [this one](https://github.com/S23Y/myconsumption-server/blob/master/pom.xml).

## Contributing

Please fork this repository and contribute back using
[pull requests](https://github.com/S23Y/myconsumption-server/pulls).

Any contributions, large or small, major features, bug fixes, language translations, 
unit/integration tests are welcomed and appreciated
but will be reviewed and discussed.
