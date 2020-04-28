# read results
results <- read.csv("results.csv")
# number of tested set representation objects
objectNum <- ncol(results) - 1
# number of tested files
fileNum <- nrow(results)
# names of tested files
fileNames <- results[, 1]

# plot results
for (i in 1:fileNum) {
  # create pdf for plot
  pdf(paste0("plots/plot_", fileNames[i], ".pdf"))
  barplot(
    unlist(results[i, 2:(objectNum + 1)]),
    xlab = "test objects",
    ylab = "execution time [ns]",
    main = fileNames[i],
    col = grey.colors(objectNum),
    axes = TRUE,
    axis.lty = 1
  )
}

dev.off()