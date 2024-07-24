# Frontend 👁️

Default credentials to login to the frontend are:

- **Admin**
  - **Login:** admin
  - **Password:** password1
- **Guest**
  - **Login:** guest
  - **Password:** guest1

If you want to change them or add new ones, you can modify the following file:
`server/organServer/src/main/resources/credentials.txt`

## Prerequisites

### Before starting the Angular project:

1. **Install Node.js:**
  - Download and install Node.js from [nodejs.org](https://nodejs.org). This will also install npm, which is Node's package manager.

2. **Install Angular CLI:**
  - Install the Angular CLI globally using:
    ```bash
    npm install -g @angular/cli
    ```

## Initial Setup

1. Clone the project from the repository.
2. Navigate to the project directory: `frontend/OrganUI`
3. Install dependencies:
   ```bash
   npm install
   ```

## Development Build and Local Server Configuration
 1. **Build the Application**
  - Run the build command using:
    ```bash
    ng build
    ```
2. **Serve Using a Simple HTTP Server**
  - Install http-server globally using:
    ```bash
    npm install -g http-server
    ```
  - Navigate to the dist/ directory using:
    ```bash
    cd dist/organ-ui
    ```
  - Start the server using:
    ```bash
    http-server -p 8080
    ```
## Production Build for Virtual Machine

#### Running version of the frontend
A running version of the frontend is available on a virtual machine hosted on our university network. You can access the frontend at the following address: `http://10.10.35.129/organ-ui/`.

To access it, you must be connected to the university network.
The frontend won't work properly unless you also start the server.
Instructions for that can be found [here](../../server/organServer/README.md).

However, if you want to deploy an updated version on the virtual machine do the following steps:

- To prepare application for deployment on a production server, such as a virtual machine, use:
  ```bash
  ng build --configuration production
  ```
  This command creates an optimized build suitable for production environments. 
- Copy the contents of the `dist/` directory to the virtual server machine. 
- Configure the application using the Internet Information Services (IIS) on server.

## Configuring Production Environment in Angular

- **Locate Environment Files:**
  - Angular projects created with the Angular CLI come with predefined environment files located in the `src/environments` folder. For production, the specific file is `environment.prod.ts`.

- **Modify the Production Environment File:**
  - Open `src/environments/environment.prod.ts` in your code editor.
  - Set the `apiUrl` or similar variable to the URL of your virtual machine. Here’s an example of what the file might look like before and after the change:

## Note on Angular Default Port and CORS Configuration

- **Angular Default Port and Host Configuration:**
  Ensure Angular application starts at the default port (4200) and default host (localhost). This is important because the server is configured to accept requests from this default port due to established CORS policies.

- **Modifying CORS Policy on the Backend:**
  If for any reason the Angular application needs to run on a port other than the default (4200) or another host, you must update the CORS configuration on backend server to allow requests from the new port. For instance, if Angular application is running on port 4300, you need to add this to server’s CORS allowed origins.

  Path: `server/organServer/src/main/java/com/example/organServer/OrganServerApplication.java`

  **Default Allowed Origins:**
  Requests can be accepted from:
  - `http://localhost:4200`
  - `http://localhost:8081`
  - `http://127.0.0.1:8081`
