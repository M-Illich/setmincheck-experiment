# read results
results <- read.csv("results.csv")

# number of tested set representation objects
objectNum <- ncol(results) - 1

# number of tested files
fileNum <- nrow(results)
# names of tested files
fileNames <- results[, 1]

# show results in relation to first entry
#for (i in 1:fileNum) {
#  results[i, 2:ncol(results)] <-
#    results[i, 2:ncol(results)] / results[i, 2]
#}

# plot results
for (i in 1:fileNum) {
  # create pdf for plot
  pdf(paste0("plots/plot_", fileNames[i], ".pdf"), width = 10.5)
  y <- floor(unlist(results[i, 2:(objectNum + 1)]) / 1000)
  x <- barplot(
    y,
    ylim = c(0,max(y)*1.1),
    xlab = "test objects",
    ylab = "execution time [ms]",
    main = fileNames[i],
    col = grey.colors(objectNum),
    axes = TRUE,
    axis.lty = 1,
    las = 1
  )
  text(x,(y + max(y)/20),labels=as.character(floor(y * 100)/100))
}

dev.off()
