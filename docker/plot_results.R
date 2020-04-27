
# read results
results <- read.csv("../results.csv")
# number of tested set representation objects
objectNum <- ncol(results) - 1
# number of tested files
fileNum <- nrow(results)
# names of tested files
fileNames <- results[,1]

# plot results
pdf("results_plot.pdf")
par(mfcol = c(fileNum,1))
for (i in 1:fileNum) {
  barplot(unlist(results[i,2:(objectNum+1)]),xlab="test objects",ylab="execution time [ns]",main=fileNames[i], col = grey.colors(objectNum), axes = TRUE, axis.lty = 1)
}

dev.off()