# setmincheck-experiment
Performance experiment for [set-minimality-checking](https://github.com/M-Illich/set-minimality-checking.git):
The implementations of the `set-minimality-checking` project are compared in terms of performing minimality checking with a simple implementation that just goes through all the sets and performs a subset test, as well as an approach based on the [UBTree](https://www.researchgate.net/profile/Jana_Koehler2/publication/2294006_A_New_Method_to_Index_and_Query_Sets/links/53fc47820cf2dca8fffefe9a.pdf) developed by Hoffmann and Koehler.


# Prerequisites: 
- [java v.8+](http://java.com) and [maven v.3+](https://maven.apache.org/)
- [docker](https://www.docker.com) for creating and running a docker image
  (The initially available memory of 2GB should be changed to `7GB`, based on the instructions for [Windows](https://docs.docker.com/docker-for-windows/#resources) or [Mac](https://docs.docker.com/docker-for-mac/#resources))
  

# Preparations
1. Clone the repository
   ```
   git clone https://github.com/M-Illich/setmincheck-experiment.git
   ```

2. Go to the root directory of the repository and invoke the following command
	```
    powershell mkdir -force ~/.m2 ; cp settings.xml ~/.m2/settings.xml
    ```
	in order to establish a connection to the external repository that contains the project's dependencies.

3. Install the maven project via
    ```
    mvn clean install
    ```
    which will generate a `jar` file located in the `docker` folder.
	
One possibility of performing the experiment is to directly execute this jar, leading to the creation of a file called `results.csv` that contains a table of the measured times (in nanoseconds) for each test case. However, the recommended way is to build and run a docker image as described below, which furthermore creates plots for each performed test case.


# Execution with docker
1. Move in the repository to the subfolder `docker` and call
    ```
    docker build -t exp .
    ```
    which will build a docker image with the name tag `exp`.
	
2. Run the experiment with
    ```
    docker run --mount type=bind,source=/absolute/path/on/host,target=/home/setmincheck-experiment/plots exp
    ```
    where the `/absolute/path/on/host` must be an accessible path on the host machine and will contain pdf files with the plot for the results of each conducted test case. The plots are created after the experiment is finished, whose progress is displayed in the standard output.
	