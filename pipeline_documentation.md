# Jenkins Pipeline Documentation

## Pipeline Overview
This documentation describes the CI/CD pipeline implemented in Jenkins for the Blog Spring application. The pipeline automates the build, test, analysis, packaging, and deployment processes.

## Pipeline Stages

### 1. SCM Checkout

**Purpose:** This stage retrieves the source code from the Git repository.

**Implementation Details:**
- Uses the default Git installation on the Jenkins server
- Connects to the repository at `https://github.com/abou57mehdi/Blog_Spring.git`
- Fetches the latest code from the `master` branch
- Checkouts the specific commit (revision ID visible in logs)

**Execution Example:**
```
Selected Git installation does not exist. Using Default
The recommended git tool is: NONE
No credentials specified
> git.exe rev-parse --resolve-git-dir C:\Users\aboum\.jenkins\workspace\pip\.git
Fetching changes from the remote Git repository
> git.exe config remote.origin.url https://github.com/abou57mehdi/Blog_Spring.git
Fetching upstream changes from https://github.com/abou57mehdi/Blog_Spring.git
> git.exe --version
> git --version # 'git version 2.45.0.windows.1'
> git.exe fetch --tags --force --progress -- https://github.com/abou57mehdi/Blog_Spring.git +refs/heads/*:refs/remotes/origin/*
> git.exe rev-parse "refs/remotes/origin/master^{commit}"
Checking out Revision 17590c93dca14629cf52da6cfacb6d27c7686e91 (refs/remotes/origin/master)
> git.exe config core.sparsecheckout
> git.exe checkout -f 17590c93dca14629cf52da6cfacb6d27c7686e91
Commit message: "changes in Jenkinsfile"
```

**Notes:**
- The pipeline currently does not use specific Git credentials
- The Git version used is 2.45.0 on Windows
- The repository is public, so no authentication is required for fetching code 

### 2. Maven Build

**Purpose:** This stage compiles the Java source code and prepares it for testing and packaging.

**Implementation Details:**
- Uses Maven 3.8.5 configured in Jenkins tools
- Executes `mvn clean compile` command to clean previous builds and compile source files
- Configured to run in a parallel execution block in the pipeline

**Execution Example:**
```
Building with Maven

C:\Users\aboum\.jenkins\workspace\pip>mvn clean compile 
[INFO] Scanning for projects...
[INFO] 
[INFO] --------------------------< com.example:demo >--------------------------
[INFO] Building blog 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- maven-clean-plugin:3.3.2:clean (default-clean) @ demo ---
[INFO] Deleting C:\Users\aboum\.jenkins\workspace\pip\target
[INFO] 
[INFO] --- jacoco-maven-plugin:0.8.11:prepare-agent (default) @ demo ---
[INFO] argLine set to -javaagent:C:\\Users\\aboum\\.m2\\repository\\org\\jacoco\\org.jacoco.agent\\0.8.11\\org.jacoco.agent-0.8.11-runtime.jar=destfile=C:\\Users\\aboum\\.jenkins\\workspace\\pip\\target\\jacoco.exec
[INFO] 
[INFO] --- maven-resources-plugin:3.3.1:resources (default-resources) @ demo ---
[INFO] Copying 1 resource from src\main\resources to target\classes
[INFO] Copying 14 resources from src\main\resources to target\classes
[INFO] 
[INFO] --- maven-compiler-plugin:3.11.0:compile (default-compile) @ demo ---
[INFO] Changes detected - recompiling the module! :source
[INFO] Compiling 18 source files with javac [debug release 17] to target\classes
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.639 s
[INFO] Finished at: 2025-04-22T18:25:47+01:00
[INFO] ------------------------------------------------------------------------
```

**Notes:**
- Project is identified as `com.example:demo` with version `0.0.1-SNAPSHOT`
- JaCoCo plugin is configured for code coverage analysis
- The build compiles 18 Java source files using Java 17
- The build copies resources from src/main/resources to target/classes
- Build executes and completes successfully in 5.639 seconds 

### 3. Unit Tests

**Purpose:** This stage executes all unit tests to verify the functionality of the application and ensure code quality.

**Implementation Details:**
- Runs `mvn test` command to execute all unit tests
- Uses JUnit 5 as the testing framework
- Configured with JaCoCo for code coverage reporting
- Test results are published using JUnit plugin in Jenkins

**Execution Example:**
```
Running unit tests

C:\Users\aboum\.jenkins\workspace\pip>mvn test 
[INFO] Scanning for projects...
[INFO] 
[INFO] --------------------------< com.example:demo >--------------------------
[INFO] Building blog 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
...
[INFO] Tests run: 36, Failures: 0, Errors: 0, Skipped: 0
...
[INFO] --- jacoco-maven-plugin:0.8.11:report (report) @ demo ---
[INFO] Loading execution data file C:\Users\aboum\.jenkins\workspace\pip\target\jacoco.exec
[INFO] Analyzed bundle 'blog' with 15 classes
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  32.247 s
[INFO] Finished at: 2025-04-22T18:26:45+01:00
[INFO] ------------------------------------------------------------------------
```

**Test Details:**
| Test Class | Tests | Description |
|------------|-------|-------------|
| AuthControllerTest | 5 | Tests authentication functionality |
| CommentControllerTest | 2 | Tests comment management endpoints |
| CustomErrorControllerTest | 3 | Tests custom error handling |
| HomeControllerTest | 1 | Tests home page functionality |
| PostControllerTest | 4 | Tests blog post CRUD operations |
| DemoApplicationTests | 1 | Tests Spring application context loading |
| CommentServiceTest | 5 | Tests comment service business logic |
| PostServiceTest | 9 | Tests post service business logic |
| UserServiceTest | 6 | Tests user service business logic |

**Notes:**
- All 36 tests executed successfully with no failures
- Code coverage analysis is performed using JaCoCo
- Coverage report generated for 15 classes in the project
- The application uses Spring Boot 3.2.3
- Spring Security is configured with auto-generated passwords for development
- Tests use H2 in-memory database with file persistence at './blogdb' 

### 4. Code Analysis

**Purpose:** This stage performs static code analysis to enforce coding standards, identify potential bugs, and ensure code quality.

#### 4.1. Checkstyle Analysis

**Purpose:** Checks Java source code for adherence to coding standards and style conventions.

**Implementation Details:**
- Executes `mvn checkstyle:checkstyle` command
- Uses Checkstyle 9.3 with the sun_checks.xml ruleset
- HTML reports are published and archived in Jenkins

**Execution Example:**
```
Running Checkstyle analysis

C:\Users\aboum\.jenkins\workspace\pip>mvn checkstyle:checkstyle 
[INFO] Scanning for projects...
[INFO] 
[INFO] --------------------------< com.example:demo >--------------------------
[INFO] Building blog 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- maven-checkstyle-plugin:3.6.0:checkstyle (default-cli) @ demo ---
[WARNING] Unable to find a URL to the parent project. The parent menu will NOT be added.
[INFO] Rendering content with org.apache.maven.skins:maven-fluido-skin:jar:2.0.0-M9 skin
[INFO] There are 342 errors reported by Checkstyle 9.3 with sun_checks.xml ruleset.
[WARNING] Unable to locate Source XRef to link to -- DISABLED
[WARNING] Unable to locate Test Source XRef to link to -- DISABLED
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.533 s
[INFO] Finished at: 2025-04-22T18:27:13+01:00
[INFO] ------------------------------------------------------------------------
```

**Analysis Results:**
- 342 style violations reported
- Reports generated and published using HTML Publisher
- Generated reports are stored at `C:\Users\aboum\.jenkins\jobs\pip\builds\41\htmlreports\Checkstyle_20Report`

#### 4.2. SpotBugs Analysis

**Purpose:** Scans Java bytecode to detect potential bugs and code vulnerabilities (successor to FindBugs).

**Implementation Details:**
- Executes `mvn spotbugs:spotbugs` command
- Uses SpotBugs Maven Plugin 4.7.3.5
- HTML reports are published and archived in Jenkins

**Execution Example:**
```
Running SpotBugs analysis (FindBugs successor)

C:\Users\aboum\.jenkins\workspace\pip>mvn spotbugs:spotbugs 
[INFO] Scanning for projects...
[INFO] 
[INFO] --------------------------< com.example:demo >--------------------------
[INFO] Building blog 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- spotbugs-maven-plugin:4.7.3.5:spotbugs (default-cli) @ demo ---
[INFO] Fork Value is true
[INFO] Done SpotBugs Analysis....
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  14.043 s
[INFO] Finished at: 2025-04-22T18:27:41+01:00
[INFO] ------------------------------------------------------------------------
```

**Analysis Results:**
- No specific bug counts reported in the logs
- Analysis completed successfully
- Reports generated and published using HTML Publisher
- Generated reports are stored at `C:\Users\aboum\.jenkins\jobs\pip\builds\41\htmlreports\SpotBugs_20Report`

#### 4.3. PMD Analysis

**Purpose:** Analyzes source code to find potential problems like unused variables, empty code blocks, and overcomplicated expressions.

**Implementation Details:**
- Executes `mvn pmd:pmd` command
- Uses PMD 7.7.0
- HTML reports are published and archived in Jenkins

**Execution Example:**
```
Running PMD analysis

C:\Users\aboum\.jenkins\workspace\pip>mvn pmd:pmd 
[INFO] Scanning for projects...
[INFO] 
[INFO] --------------------------< com.example:demo >--------------------------
[INFO] Building blog 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- maven-pmd-plugin:3.26.0:pmd (default-cli) @ demo ---
[WARNING] Unable to locate Source XRef to link to -- DISABLED
[INFO] PMD version: 7.7.0
[WARNING] Unable to find a URL to the parent project. The parent menu will NOT be added.
[INFO] Rendering content with org.apache.maven.skins:maven-fluido-skin:jar:2.0.0-M9 skin
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  8.353 s
[INFO] Finished at: 2025-04-22T18:27:54+01:00
[INFO] ------------------------------------------------------------------------
```

**Analysis Results:**
- No specific violation counts reported in the logs
- Analysis completed successfully
- Reports generated and published using HTML Publisher
- Generated reports are stored at `C:\Users\aboum\.jenkins\jobs\pip\builds\41\htmlreports\PMD_20Report`

**Notes for Code Analysis Stage:**
- All three analysis tools run in parallel as part of the pipeline
- Each tool generates HTML reports that are archived and accessible through the Jenkins UI
- The build continues even with the 342 Checkstyle violations, treating them as warnings rather than errors
- Source cross-referencing is disabled, which would normally allow linking from issue reports to source code
- The Maven Fluido Skin is used for report visualization 

### 5. JavaDoc Generation

**Purpose:** This stage generates API documentation from source code comments, providing comprehensive documentation of the codebase for developers.

**Implementation Details:**
- Executes `mvn javadoc:javadoc` command
- Uses Maven JavaDoc Plugin 3.6.3
- Generates HTML documentation in the target/site/apidocs directory

**Execution Example:**
```
Generating JavaDoc

C:\Users\aboum\.jenkins\workspace\pip>mvn javadoc:javadoc 
[INFO] Scanning for projects...
[INFO] 
[INFO] --------------------------< com.example:demo >--------------------------
[INFO] Building blog 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] >>> maven-javadoc-plugin:3.6.3:javadoc (default-cli) > generate-sources @ demo >>>
[INFO] 
[INFO] --- jacoco-maven-plugin:0.8.11:prepare-agent (default) @ demo ---
[INFO] argLine set to -javaagent:C:\\Users\\aboum\\.m2\\repository\\org\\jacoco\\org.jacoco.agent\\0.8.11\\org.jacoco.agent-0.8.11-runtime.jar=destfile=C:\\Users\\aboum\\.jenkins\\workspace\\pip\\target\\jacoco.exec
[INFO] 
[INFO] <<< maven-javadoc-plugin:3.6.3:javadoc (default-cli) < generate-sources @ demo <<<
[INFO] 
[INFO] 
[INFO] --- maven-javadoc-plugin:3.6.3:javadoc (default-cli) @ demo ---
[INFO] No previous run data found, generating javadoc.
[WARNING] Javadoc Warnings
[WARNING] C:\Users\aboum\.jenkins\workspace\pip\src\main\java\com\example\demo\controller\AuthController.java:15: warning: no comment
...many warnings omitted...
[WARNING] 100 warnings
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.045 s
[INFO] Finished at: 2025-04-22T18:28:27+01:00
[INFO] ------------------------------------------------------------------------
```

**Analysis Results:**
- 100 Javadoc warnings reported (for missing comments)
- Generated documentation available in the target/site/apidocs directory
- Despite the warnings, the build completes successfully

**Documentation Coverage:**
| Component Type | Total Classes | Classes Missing Comments |
|----------------|---------------|--------------------------|
| Controllers    | 5             | 5 (All controllers)      |
| Models         | 3             | 3 (Post, User, Comment)  |
| Services       | 3             | 3 (All services)         |
| Repositories   | 3             | 3 (All repositories)     |
| Configuration  | 3             | 3 (All config classes)   |
| Application    | 1             | 1 (Main application)     |

**Notes:**
- The project has minimal Javadoc documentation, with comments missing on most classes and methods
- Warnings indicate a clear opportunity for improvement in code documentation
- Common issue across the codebase: missing class, method, and property documentation
- Despite the warnings, the JavaDoc generation succeeds, creating basic structure for API documentation
- This stage provides valuable feedback for developers about documentation quality

### 6. Package Application

**Purpose:** This stage builds the application JAR file that can be deployed to servers or container environments.

**Implementation Details:**
- Executes `mvn package -DskipTests` command to create an executable JAR file
- Uses Spring Boot Maven Plugin 3.2.3 to create a self-contained executable JAR
- Skips running tests again to speed up the packaging process
- Archives the generated JAR file as a Jenkins artifact

**Execution Example:**
```
Packaging application

C:\Users\aboum\.jenkins\workspace\pip>mvn package -DskipTests 
[INFO] Scanning for projects...
[INFO] 
[INFO] --------------------------< com.example:demo >--------------------------
[INFO] Building blog 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- jacoco-maven-plugin:0.8.11:prepare-agent (default) @ demo ---
[INFO] argLine set to -javaagent:C:\\Users\\aboum\\.m2\\repository\\org\\jacoco\\org.jacoco.agent\\0.8.11\\org.jacoco.agent-0.8.11-runtime.jar=destfile=C:\\Users\\aboum\\.jenkins\\workspace\\pip\\target\\jacoco.exec
[INFO] 
[INFO] --- maven-resources-plugin:3.3.1:resources (default-resources) @ demo ---
[INFO] Copying 1 resource from src\main\resources to target\classes
[INFO] Copying 14 resources from src\main\resources to target\classes
[INFO] 
[INFO] --- maven-compiler-plugin:3.11.0:compile (default-compile) @ demo ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-resources-plugin:3.3.1:testResources (default-testResources) @ demo ---
[INFO] Copying 11 resources from src\test\resources to target\test-classes
[INFO] 
[INFO] --- maven-compiler-plugin:3.11.0:testCompile (default-testCompile) @ demo ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-surefire-plugin:3.1.2:test (default-test) @ demo ---
[INFO] Tests are skipped.
[INFO] 
[INFO] --- jacoco-maven-plugin:0.8.11:report (report) @ demo ---
[INFO] Loading execution data file C:\Users\aboum\.jenkins\workspace\pip\target\jacoco.exec
[INFO] Analyzed bundle 'blog' with 15 classes
[INFO] 
[INFO] --- maven-jar-plugin:3.3.0:jar (default-jar) @ demo ---
[INFO] Building jar: C:\Users\aboum\.jenkins\workspace\pip\target\demo-0.0.1-SNAPSHOT.jar
[INFO] 
[INFO] --- spring-boot-maven-plugin:3.2.3:repackage (repackage) @ demo ---
[INFO] Replacing main artifact C:\Users\aboum\.jenkins\workspace\pip\target\demo-0.0.1-SNAPSHOT.jar with repackaged archive, adding nested dependencies in BOOT-INF/.
[INFO] The original artifact has been renamed to C:\Users\aboum\.jenkins\workspace\pip\target\demo-0.0.1-SNAPSHOT.jar.original
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.330 s
[INFO] Finished at: 2025-04-22T18:28:54+01:00
[INFO] ------------------------------------------------------------------------
Archiving artifacts
```

**Artifacts Generated:**
- Main executable JAR: `target/demo-0.0.1-SNAPSHOT.jar`
- Original (non-executable) JAR: `target/demo-0.0.1-SNAPSHOT.jar.original`
- Both JARs are fingerprinted and archived in Jenkins

**Key Features of the Spring Boot JAR:**
- Self-contained executable application with embedded application server
- All dependencies packaged within the JAR in the BOOT-INF directory
- Can be run directly with `java -jar demo-0.0.1-SNAPSHOT.jar`
- Optimized for cloud deployment and containerization

**Notes:**
- The packaging process reuses the JaCoCo coverage data from previous stages
- The Spring Boot Maven Plugin converts the standard JAR into an executable JAR
- Tests are skipped to avoid running them twice in the pipeline
- The entire packaging process completes in 5.33 seconds
- The archive step ensures the artifact is stored and accessible through the Jenkins UI

### 7. Deploy to Nexus Repository

**Purpose:** This stage publishes the application artifacts to a Nexus repository for versioning, storage, and sharing with other teams or environments.

**Implementation Details:**
- Executes Maven deploy with a custom settings file for authentication
- Uses alternate deployment repository parameter to specify the Nexus repository URL
- Targets the Nexus snapshots repository at `http://localhost:8081/repository/maven-snapshots/`
- Uses Maven Deploy Plugin 3.1.1
- Configures credentials securely using Jenkins Credentials Provider

**Execution Example:**
```
Deploying to Nexus repository

C:\Users\aboum\.jenkins\workspace\pip>mvn -s C:\Users\aboum\.jenkins\workspace\pip@tmp\config10115068716815493836tmp deploy                         -DaltDeploymentRepository=nexus-snapshots::default::http://localhost:8081/repository/maven-snapshots/                         -DskipTests 
[INFO] Scanning for projects...
[INFO] 
[INFO] --------------------------< com.example:demo >--------------------------
[INFO] Building blog 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
...
[INFO] --- maven-deploy-plugin:3.1.1:deploy (default-deploy) @ demo ---
[INFO] Using alternate deployment repository nexus-snapshots::default::http://localhost:8081/repository/maven-snapshots/
[WARNING] Using legacy syntax for alternative repository. Use "nexus-snapshots::http://localhost:8081/repository/maven-snapshots/" instead.
Downloading from nexus-snapshots: http://localhost:8081/repository/maven-snapshots/com/example/demo/0.0.1-SNAPSHOT/maven-metadata.xml
...
Uploaded to nexus-snapshots: http://localhost:8081/repository/maven-snapshots/com/example/demo/0.0.1-SNAPSHOT/demo-0.0.1-20250422.172920-10.jar (53 MB at 18 MB/s)
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  9.200 s
[INFO] Finished at: 2025-04-22T18:29:29+01:00
[INFO] ------------------------------------------------------------------------
```

**Artifacts Deployed:**
- POM file: `demo-0.0.1-SNAPSHOT.pom` (5.6 KB)
- Executable JAR: `demo-0.0.1-SNAPSHOT.jar` (53 MB)
- Maven metadata files (for version tracking)

**Versioning Details:**
- The artifacts are versioned with timestamp-based SNAPSHOT versioning
- Current uploaded version: `0.0.1-20250422.172920-10`
- This is the 10th snapshot version of 0.0.1 deployed to the repository
- The timestamp format is `yyyyMMdd.HHmmss`

**Notes:**
- The stage uses the Jenkins Config File Provider plugin to securely manage Maven settings
- Maven settings contain Nexus authentication credentials (not visible in logs)
- The JAR file is large (53 MB) due to all embedded dependencies
- Upload speed was approximately 18 MB/s to the local Nexus instance
- Legacy repository syntax warning doesn't affect functionality but could be improved in future
- The SNAPSHOT versioning allows for continuous deployment without manual version changes

### 8. Docker Preconditions

**Purpose:** This stage verifies that all prerequisites for Docker operations are met before attempting to build or push Docker images.

**Implementation Details:**
- Checks if Docker daemon is running
- Verifies the existence of a Dockerfile in the workspace
- Validates Dockerfile syntax using hadolint
- Attempts to clean up old Docker images
- Checks available disk space
- Creates directory for Docker artifacts

**Execution Example:**
```
Checking Docker preconditions

C:\Users\aboum\.jenkins\workspace\pip>docker info 
Client:
 Version:    27.2.0
 Context:    desktop-linux
 Debug Mode: false
...
Server:
 Containers: 3
  Running: 1
  Paused: 0
  Stopped: 2
 Images: 2
 Server Version: 27.2.0
...
WARNING: No blkio throttle.read_bps_device support
WARNING: No blkio throttle.write_bps_device support
WARNING: No blkio throttle.read_iops_device support
WARNING: No blkio throttle.write_iops_device support
WARNING: daemon is not using the default seccomp profile
✅ Docker daemon is running
✅ Dockerfile exists

C:\Users\aboum\.jenkins\workspace\pip>docker run --rm -i hadolint/hadolint   0<Dockerfile  || true
Unable to find image 'hadolint/hadolint:latest' locally
latest: Pulling from hadolint/hadolint
db4123164570: Pulling fs layer
db4123164570: Download complete
Digest: sha256:fff226bdf9ebcc08db47fb90ee144dd770120b35c2b1cbbb46e932a650cfe232
Status: Downloaded newer image for hadolint/hadolint:latest
✅ Dockerfile validation completed

C:\Users\aboum\.jenkins\workspace\pip>docker images "aboum22/blog-spring" -q   | findstr .   && docker rmi $(docker images "aboum22/blog-spring" -q)   || echo "No images to clean" 
835e51d9ae4b
unknown shorthand flag: 'q' in -q)
See 'docker rmi --help'.
"No images to clean"
⚠️ Old image cleanup failed but continuing: script returned exit code 125

C:\Users\aboum\.jenkins\workspace\pip>wmic logicaldisk get deviceid,freespace,size 
D e v i c e I D     F r e e S p a c e         S i z e                     
C :                 2 3 4 3 2 9 9 4 8 1 6     5 1 0 8 7 2 5 1 4 5 6 0     

C:\Users\aboum\.jenkins\workspace\pip>mkdir -p target\docker-image 
```

**Docker Environment:**
- Docker version: 27.2.0
- Running on Docker Desktop with WSL2 backend
- Containers: 3 (1 running, 2 stopped)
- Images: 2
- Storage Driver: overlayfs

**Precondition Checks:**
- ✅ Docker daemon is running: Confirmed
- ✅ Dockerfile exists: Verified
- ✅ Dockerfile syntax validation: Passed using hadolint
- ⚠️ Old image cleanup: Failed due to command syntax issue
- ✅ Disk space check: Approximately 234GB free on C: drive
- ✅ Docker artifact directory: Created at target\docker-image

**Notes:**
- Some Docker warnings about missing features are displayed but don't affect functionality
- The image cleanup attempt failed due to Windows command syntax issues with the $(docker images...) notation
- Despite the cleanup failure, the pipeline continues since this is a non-critical issue

### 9. Deploy to Docker

**Purpose:** This stage builds a Docker image from the application and pushes it to Docker Hub for deployment.

**Implementation Details:**
- Builds a Docker image using the JAR file from the Package stage
- Tags the image with the repository name and latest tag
- Pushes the image to Docker Hub with authentication
- Archives the Docker image as a backup artifact
- Includes retry mechanism for handling push failures

**Execution Example:**
```
Building and pushing Docker image

C:\Users\aboum\.jenkins\workspace\pip>docker build -t aboum22/blog-spring:latest . 
#0 building with "desktop-linux" instance using docker driver

#1 [internal] load build definition from Dockerfile
#1 transferring dockerfile: 391B 0.0s done
#1 DONE 0.1s

#2 [internal] load metadata for docker.io/library/openjdk:17-jdk-slim
...
#8 [3/3] COPY target/*.jar app.jar
#8 DONE 0.2s

#9 exporting to image
#9 exporting layers
#9 exporting layers 1.9s done
...
#9 naming to docker.io/aboum22/blog-spring:latest done
#9 unpacking to docker.io/aboum22/blog-spring:latest
#9 unpacking to docker.io/aboum22/blog-spring:latest 0.3s done
#9 DONE 2.5s

C:\Users\aboum\.jenkins\workspace\pip>docker logout 
Removing login credentials for https://index.docker.io/v1/

C:\Users\aboum\.jenkins\workspace\pip>docker login -u "aboum22 " -p "****" 
WARNING! Using --password via the CLI is insecure. Use --password-stdin.
Login Succeeded

C:\Users\aboum\.jenkins\workspace\pip>docker push aboum22/blog-spring:latest 
...
4bb3ba0ab10c: Layer already exists
d4fe87555ae0: Already exists
4d8a55268579: Pushed
latest: digest: sha256:81101b05455f7c43145dde917d7cf2abf4e0fa141400d1e5cc930fa7d98aaf28 size: 856

C:\Users\aboum\.jenkins\workspace\pip>docker save aboum22/blog-spring:latest -o target/docker-image/blog-spring.tar 
Archiving artifacts
```

**Docker Image Details:**
- Repository: `aboum22/blog-spring`
- Tag: `latest`
- Base Image: `openjdk:17-jdk-slim`
- Image Digest: `sha256:81101b05455f7c43145dde917d7cf2abf4e0fa141400d1e5cc930fa7d98aaf28`
- Image Size: Multiple layers, exact size not specified in logs

**Dockerfile Overview:**
- Based on OpenJDK 17 slim image
- Sets working directory to /app
- Copies the Spring Boot JAR as app.jar
- Likely exposes HTTP port (not visible in logs)
- Likely sets an ENTRYPOINT to run the JAR (not visible in logs)

**Security Features:**
- Docker Hub credentials are managed securely through Jenkins credentials
- Password is masked in the logs (shown as ****)
- Session is properly logged out before login

**Artifacts:**
- Docker image archive: `target/docker-image/blog-spring.tar`
- Archived in Jenkins for backup and debugging purposes

**Notes:**
- The initial push attempt failed and required a retry
- Most image layers were cached from previous builds (shown as "Layer already exists")
- Only one layer (4d8a55268579) needed to be pushed, indicating efficient layering
- Push operation succeeded on the second attempt
- The pipeline configuration includes retry logic for handling transient Docker Hub issues


