# setmincheck-experiment
Performance experiment for [set-minimality-checking](https://github.com/M-Illich/set-minimality-checking.git):
The implementations of the `set-minimality-checking` project are compared in terms of performing minimality checking with a simple implementation that just goes through all the sets and performs a subset test, as well as an approach based on the [UBTree](https://www.researchgate.net/profile/Jana_Koehler2/publication/2294006_A_New_Method_to_Index_and_Query_Sets/links/53fc47820cf2dca8fffefe9a.pdf) developed by Hoffmann and Koehler.

# Prerequisites: 
- [java v.8+](http://java.com) and [maven v.3+](https://maven.apache.org/)
- [docker](https://www.docker.com) for creating and running a docker image

# Preparations
1. Clone the repository
   ```
   git clone https://github.com/M-Illich/setmincheck-experiment.git
   ```

2. Go to the root directory of the repository and invoke the following command
    ```
    mvn clean install
    ```
    which will generate a `jar` file located in the `docker` folder.
	
One possibility of performing the experiment is to directly execute this `jar`, leading to the creation of a file called `results.csv` that contains the measured times (in nanoseconds) for each test case. However, the recommended way is to build and run a docker image that, furthermore, creates plots for each performed test case.

# Execution with docker (recommended)	
1. Move to the subfolder `docker` and call
	```
	docker build -t exp .
	```
	which will build a docker image with the name tag `exp`.
	
2. Run the experiment with
    ```
    docker run --mount type=bind,source=/absolute/path/on/host,target=/home/setmincheck-experiment/plots exp
    ```
	where the `/absolute/path/on/host` must be an accessible path on the host machine and will contain pdf files with the plot for the results of each conducted test case. The plots are created after the experiment is finished, whose progress is indicated in the standard output.

