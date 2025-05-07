package Game;

public class GameContext {
  private double score;
    
  public void setFinalScore(int score) {
    this.score = score;
  }

  public double getScore() {
    return score;
  }

  public void addInFinalScore(double currScore){
    score += currScore;
  }
}
