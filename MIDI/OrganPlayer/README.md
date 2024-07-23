# Dancing Pipes - Organ Client 🎹

The organ client connects the pipe organ with the server and the camera and sends the MIDI signals to the organ.

### Prerequisites

Before proceeding with the installation, make sure you have the following software installed on your machine:

1. Java Development Kit (JDK):
   - Make sure you have JDK 8 or later installed.
   - You can download it from Oracle's official website or OpenJDK.

   To check if Java is installed and its version, run:
   ```bash
   java -version
   ```

2. Apache Maven:
   - Maven is used for project management and build automation.
   - You can download it from Maven's official website.

   To check if Maven is installed and its version, run:
   ```bash
   mvn -version
   ```


### Installation

1. **Clone the repository:**
    ```bash
    git clone https://bitbucket.student.fiw.fhws.de:8443/scm/progprojss24/programmierprojekt-ss24---multimodal-interaction-system-for-non-expert-organ-playing.git
    ```
2. **Navigate to project directory**
   ```bash
    cd programmierprojekt-ss24---multimodal-interaction-system-for-non-expert-organ-playing\MIDI\OrganPlayer
    ```
3. **Build project**
    ```bash
    mvn clean generate-sources package
    ```
   
4. **Copy jar file to main directory**
   ```bash
   cp .\target\dancing-pipes-jar-with-dependencies.jar .\
   ```
5. **Start** 🎶
    ```bash
    java -jar dancing-pipes-jar-with-dependencies.jar
    ```
5. **Clean the build** (Optional)
   ```bash
    mvn clean
   ```