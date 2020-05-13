FROM openjdk:13-slim

WORKDIR /home/setmincheck-experiment
	
COPY . /home/setmincheck-experiment	

RUN apt-get update && apt-get install -y --no-install-recommends \		
		r-base-core \
	&& rm -rf /var/lib/apt/lists/*
	
CMD java -XX:-UseGCOverheadLimit -Xms6g -jar setmincheck-experiment-1.0-SNAPSHOT-jar-with-dependencies.jar && Rscript plot_results.R