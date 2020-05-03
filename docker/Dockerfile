FROM openjdk:8u151-jdk

WORKDIR /home/setmincheck-experiment
	
COPY . /home/setmincheck-experiment	

RUN apt-get update && apt-get install -y --no-install-recommends \		
		r-base-core \
	&& rm -rf /var/lib/apt/lists/*
	
CMD java -XX:-UseGCOverheadLimit -Xms4g -Xmx8g -jar setmincheck-experiment-1.0-SNAPSHOT-jar-with-dependencies.jar && Rscript plot_results.R

# mvn package 
# docker build -t exp .
# docker run --mount type=bind,source=C:\Users\Moritz\Documents\setmincheck-experiment,target=/home/setmincheck-experiment/plots exp