package Game.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Random;
import java.util.function.Supplier;
import java.lang.NoSuchMethodException;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;

import org.reflections.Reflections;

import Game.gamecomponents.Obstacle;
import javafx.geometry.Point2D;

public class ObstacleManager {
  private Random random = new Random();
  private Map<String, Supplier<Obstacle>> constructorsObstacle = new HashMap<>();
  private Map<String, Queue<Obstacle>> poolsQueue = new HashMap<>();

  public ObstacleManager() {
    registerObstacles();
  }

  private void registerObstacles() { 
    Reflections reflection = new Reflections("Game.gamecomponents.obstacles");
    Set<Class<? extends Obstacle>> obstacleClasses = reflection.getSubTypesOf(Obstacle.class);

    for (Class<? extends Obstacle> o : obstacleClasses){
      if (!o.getSimpleName().endsWith("Obstacle"))
        continue;
      String obstacleName = o.getSimpleName()
        .replace("Obstacle", "")
        .toLowerCase();
      Supplier<Obstacle> constructor = () -> {
        try {
          return o.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException(new ManagerException());   // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }
      };          
      constructorsObstacle.put(obstacleName, constructor);
      poolsQueue.put(obstacleName, new LinkedList<>());
    }
  }
    
  public Obstacle createRandObstacle(double[] linesCenterY, List<Point2D> obstacleCoords, double startX, double endX) {
    int randLineNumber = random.nextInt(linesCenterY.length); //  выбрали дорогу [0, 2]
    Obstacle newObstacle = getObstacleFromPool();
    double posX = random.nextDouble(endX - startX) + startX - newObstacle.getHeight(); 
    double posY = linesCenterY[randLineNumber];
    if ((posX < 1000) || (endX - posX) < 130)  return null;

    for (Point2D coords : obstacleCoords) {
      double x = coords.getX();
      double y = coords.getY();
      if (y == posY && Math.abs(x - posX) < 140)
        return null;    
    }
    
    newObstacle.setPosition(posX, posY);
    obstacleCoords.add(new Point2D(posX, posY));
    return newObstacle;
  }
  
  private Obstacle getObstacleFromPool(){
    List<String> obstacleTypes = new ArrayList<>(constructorsObstacle.keySet());
    String randObstacleType = obstacleTypes.get(random.nextInt(obstacleTypes.size()));
    
    Queue<Obstacle> obstaclePool = poolsQueue.get(randObstacleType);

    Obstacle newObstacle;
    if (!obstaclePool.isEmpty()) {
      newObstacle = obstaclePool.poll();
      newObstacle.reset();
    } else {
      newObstacle = constructorsObstacle.get(randObstacleType).get();
    }

    return newObstacle;
  }

  public void returnObstacleToPool(Obstacle obstacle) {
    String obstacleType = obstacle.getClass().getSimpleName()
      .replace("Obstacle", "")
      .toLowerCase();
      
    Queue<Obstacle> pool = poolsQueue.get(obstacleType);
    if (pool != null){
      pool.add(obstacle);
    }
  }
}
