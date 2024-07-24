# Dancing Pipes - Server 🌐

### Prerequisites

Before proceeding with the installation, make sure you have the following software installed on your machine:

1. Java Development Kit (JDK):
    - Make sure you have JDK 17 installed.
    - You can download it from Oracle's official website.

   To check if Java is installed and its version, run:
   ```bash
   java -version
   ```

2. Gradle:

You can download Gradle from the official website. Follow the installing instructions there.

To check if Gradle is installed and its version, run:

   ```bash
   gradle -v
   ```

### Running the server on the local machine

#### Initial setup

1. **Clone the repository:**
    ```bash
    git clone https://bitbucket.student.fiw.fhws.de:8443/scm/progprojss24/programmierprojekt-ss24---multimodal-interaction-system-for-non-expert-organ-playing.git
    ```
2. **Navigate to the server directory**
   ```bash
    cd programmierprojekt-ss24---multimodal-interaction-system-for-non-expert-organ-playing\server\organServer
    ```

#### Building the project

```bash
gradle build
```

#### Starting the server

   ```bash
   java -jar build/libs/organServer-0.0.1-SNAPSHOT.jar
   ```

#### Cleaning the project (optional)
To clean your project (remove build artifacts), run the following command:
   ```bash
   gradle clean
   ```

### Running the server on the virtual machine in the THWS network

The server application can also be started on a virtual server machine hosted within the university network. The server is located at the address `10.10.35.129`.

To start the server, navigate to the `C:\Users\Public\Documents\Server` folder. Inside this folder, you will find a server JAR file. Open a terminal window and run the following command:

   ```bash
   java -jar organServer-0.0.1-SNAPSHOT.jar
   ```

If you need access to the virtual machine, please contact [Matthias Lode](https://fiw.thws.de/fakultaet/personen/person/matthias-lode/).