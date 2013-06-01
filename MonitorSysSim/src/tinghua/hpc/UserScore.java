package tinghua.hpc;
public class UserScore {
	public UserScore(int userId, int newScore) {
		this.id = userId;
		this.score = newScore;
	}

	public int score;
	public int id;
	
	public int getScore(){
		return score;
	}
	
	public int getUserId(){
		return id;
	}

}
