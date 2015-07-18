# CollabFilter
Collaborative filtering based on User Similarity 

Steps to compile and execute Collabrative filtering algorithm:

Step 1: Download the data set (Netflix dataset) and unzip it . Also, download Src/CF_MainDriver.java and Src/User.java files:
Step 2 : compilation 

 command : javac CF_MainDriver.java 

 Step 3: Execution: java CF_MainDriver <input directoty data set>
 Example command :  java CF_MainDriver /path_to_netflix_unzipfolder/ 


 {cslinux1:~/cfilter/netflixdata} ls
 description.txt  movie_titles.txt  README  TestingRatings.txt  TrainingRatings.txt

 {cslinux1:~/cfilter} java CF_MainDriver netflixdata/
