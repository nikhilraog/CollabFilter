import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.ObjectInputStream.GetField;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

public class CF_MainDriver {



	public static void main(String[] args) throws Exception {

		HashMap<String, Long> movie_hashtable = new HashMap<String, Long>();
		HashMap<String, User> user_hashtable = new HashMap<String, User>();
		HashMap<Long, ArrayList<User>> movie_Usertable = new HashMap<Long, ArrayList<User>>();

		MessageDigest m=MessageDigest.getInstance("MD5");
		String filepath = args[0];
		File trainingfile = new  File(filepath+"/TrainingRatings.txt");
		BufferedReader reader = new BufferedReader(new FileReader(trainingfile));

		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			String[] parts = line.split(",");

			String moviemd = parts[0];
			String usermd = parts[1];

			long movieid = Long.parseLong(parts[0]);
			long userid = Long.parseLong(parts[1]);
			double rating = Double.parseDouble(parts[2]);			 

			m.update(moviemd.getBytes(),0,moviemd.length());
			String md5_movieid = new BigInteger(1,m.digest()).toString(16);
			if(!movie_hashtable.containsKey(md5_movieid)){
				movie_hashtable.put(md5_movieid, movieid);
			}



			m.update(usermd.getBytes(),0,usermd.length());
			String md5_userid = new BigInteger(1,m.digest()).toString(16);			

			if(user_hashtable.containsKey(md5_userid)){				
				User u = user_hashtable.get(md5_userid);
				u.addEntry(movieid,rating);
			}
			else{
				User u = new User(userid, movieid, rating);
				user_hashtable.put(md5_userid, u);
			}


			if(movie_Usertable.containsKey(movieid)){

				ArrayList<User> users = movie_Usertable.get(movieid);
				User u = user_hashtable.get(md5_userid);
				users.add(u);

			}
			else{
				ArrayList<User> list = new ArrayList<User>();
				User u = user_hashtable.get(md5_userid);
				list.add(u);
				movie_Usertable.put(movieid, list);
			}
			//////System.out.println("Movied md5 "+md5_movieid+" User md5 "+md5_userid);	
		}

		reader.close();
/*
		for(Entry<Long, ArrayList<User>> entry : movie_Usertable.entrySet()){
			////System.out.println(entry.getKey() + " / "+ entry.getValue().toString());
		}
		
*/

		for(User u : user_hashtable.values()){

			
			//if(u.userid == 415693){
				u.calculateMeanVote();
				////System.out.println(u.toString());
				////System.out.println(u.meanVote);
				
			//}
		}
		//System.out.println("--done 1st part--");
		String s = new String("415693");
		m.update(s.getBytes(),0,s.length());
		String md5_userid = new BigInteger(1,m.digest()).toString(16);			
		//System.out.println("---");
		//System.out.println(user_hashtable.get(md5_userid).toString());
		
		double diff = 0;
		double squared_diff = 0;
		File testfile = new  File(filepath+"/TestingRatings.txt");
		BufferedReader br = new BufferedReader(new FileReader(testfile));
		int filecount = 0;
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			filecount = filecount+1;
			System.out.println("..."+filecount);
			String[] parts = line.split(",");

			//System.out.println("line is test "+line);
			String moviemd = parts[0];
			String usermd = parts[1];

			long movieid = Long.parseLong(parts[0]);
			long userid = Long.parseLong(parts[1]);
			double actualrating = Double.parseDouble(parts[2]);			 
			double predicted_rating;
			m.update(moviemd.getBytes(),0,moviemd.length());
			String md5_movieid = new BigInteger(1,m.digest()).toString(16);
			movie_hashtable.get(md5_movieid);
			 

			m.update(usermd.getBytes(),0,usermd.length());
			String md5_userid1 = new BigInteger(1,m.digest()).toString(16);						

			User u_active = user_hashtable.get(md5_userid1);
			double k=0;
			Set<Long> movielist = u_active.getMovielist();// get a list of movies that this active user has voted
			HashSet<User> corrleatedlist = new HashSet<User>();
			for(long movie_ids : movielist){
				ArrayList<User> userslist = movie_Usertable.get(movie_ids);
				corrleatedlist.addAll(userslist);				
			}
			corrleatedlist.remove(u_active);
			////System.out.println("correlated list for user "+ u_active.userid +"  // "+ corrleatedlist);
			double corrlated_sum=0;
			for(User u: corrleatedlist){
				////System.out.println(u.toString());
				double correlated_wt = calculateWt(u_active,u);
				////System.out.println(correlated_wt);
				k=k+correlated_wt;
				if(u.users_movirating.containsKey(movieid)) {
					
					corrlated_sum = corrlated_sum + correlated_wt*(u.users_movirating.get(movieid) - u.meanVote);
				}	
				////System.out.println("K :"+k);
			}
			
			try{
				System.out.println("mean vote "+u_active.meanVote);
				predicted_rating = u_active.meanVote +(1.0/(double)k)*corrlated_sum;
				System.out.println("predicted "+predicted_rating);
				if(!Double.isNaN(Math.abs(predicted_rating-actualrating))){
					diff = diff + Math.abs(predicted_rating-actualrating);
					squared_diff = squared_diff + Math.pow((predicted_rating - actualrating),2);
				}
				else{
					diff = diff + 0;
					squared_diff = squared_diff + 0;
				}
				//System.out.println("diff"+ Math.abs(predicted_rating-actualrating));
				
			}catch(Exception e){
				System.out.println("exception "+e);
			}
			
			
			/*diff = diff + Math.abs(predicted_rating-actualrating);
			System.out.println("diff"+ Math.abs(predicted_rating-actualrating));
			squared_diff = squared_diff + Math.pow((predicted_rating - actualrating),2);*/
			//System.out.println("diff->"+diff);
		}
		br.close();
		
		double MAE = (1.0/(double)filecount)*diff;
		double RMSE = Math.sqrt(((1.0/(double)filecount)*squared_diff));
		System.out.println("MAE :"+MAE + " RMSE: "+RMSE);

	}


	private static double calculateWt(User u_active, User u) {

		Set<Long> activeusers_movielist = u_active.getMovielist();
		Set<Long> currentUsers_movielist  = u.getMovielist();
		Set<Long> commonvotedmovies = new HashSet<Long>(activeusers_movielist);
		commonvotedmovies.retainAll(currentUsers_movielist);
		double numerator=0;
		if(!commonvotedmovies.isEmpty()){
			double user1=0, user2=0;
			for(long mid: commonvotedmovies){// j common items between active user and current user u  
				double rating_active =  u_active.users_movirating.get(mid);//u_active.getMovierating(mid) ;
				double rating_user_i =  u.users_movirating.get(mid);//ui.getMovierating(mid);
				double a = rating_active - u_active.meanVote;
				double b = rating_user_i - u.meanVote;
				if(u.users_movirating.get(mid)==null){
					System.out.println("NULL "+u.userid+ "->"+"mid "+mid);
				}
				if(a == 0){
					//System.out.println("rating by" +u_active.userid + "for movie id "+mid +"->"+rating_active );
					//System.out.println("A is zero "+a);
				}
				if(b==0){
					//b = u.meanVote;
					//System.out.println("rating by" +u.userid + "for movie id "+mid +"->"+rating_user_i +" mean vote "+u.meanVote);
					//System.out.println("B is zero "+b);
				}
				numerator = numerator + (a)*(b);
				
				user1 += Math.pow(rating_active - u_active.meanVote, 2);
				user2 += Math.pow(rating_user_i - u.meanVote, 2);
				//denominator = Math.sqrt(a)
				//double squared_activerating = (a*a)*(b*b);

			}
			double wt = (double)numerator/(double) Math.sqrt((user1 * user2));
			if(!Double.isNaN(wt)){
				return wt;
			}
			else{
				return 0;
			}
		}else{
			return 0;
		}

		
	}

}
