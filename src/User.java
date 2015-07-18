import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;


public class User {
	
	Long userid;
	HashMap<Long, Double> users_movirating = new HashMap<Long, Double>();
	Double meanVote;
	
	
	User(long userid1, long movieid, double rating){
		userid = userid1;
		users_movirating.put(movieid, rating);
		//meanVote = 0.0;
	}

	public void addEntry(long movieid, double rating) {
		users_movirating.put(movieid, rating);
		
	}

	@Override
	public String toString() {
		return "User [userid=" + userid + ", users_movirating="
				+ users_movirating + "meanvote="+meanVote+"]";
	}

	public void calculateMeanVote() {
		double sum = 0;
		double sizeofmap = users_movirating.size();
		for(Double d : users_movirating.values()){
			sum =sum + d;
		}
		meanVote = new Double((double)sum/(double)sizeofmap);		
	}

	public Set<Long> getMovielist() {
		return users_movirating.keySet();	
	}
	
	public double getMovierating(long MovieId){
		for(Entry<Long, Double> entry : users_movirating.entrySet()){
			if(entry.getKey().compareTo(MovieId) == 0){
				return entry.getValue();
			}
		}
		
		return 0;
		
		
	}

	
	
}
