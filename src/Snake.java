import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

public class Snake {
    int length;
    ArrayList<Integer> snakeList = new ArrayList<>();
    String direction;
    int score = 0;
    int heart = 3; //生命值
    boolean dizzy = false; //是否晕眩

    int dizzyCnt = 0; //晕眩计数器
    int dizzyTime = 0; //晕眩时间

    public void init() {
        snakeList.clear();
        if (!GamePage.gameMode) {                 //100, 100, 75, 100, 50, 100
            Collections.addAll(snakeList, 100, 150, 75, 150, 50, 150);//125, 100, 150, 100, 175, 100
        }
        score = 0;
        length = 3;
        direction = "R";
        dizzy = false;
        dizzyCnt = 0;
        dizzyTime = 0;
        heart = 3;
    }

    public void anotherActionPerformed() {
        snakeAction(length, snakeList, direction);

        if (snakeList.get(0) == GamePage.foodX && snakeList.get(1) == GamePage.foodY) {
            length++;
            snakeList.add(0);
            snakeList.add(0);
//            speedController(speedUp);
            //加分
            score = score + 10;
            //重新生成食物

            int[] foodPositionArray = GamePage.randomMaker();
            GamePage.foodX = foodPositionArray[0];
            GamePage.foodY = foodPositionArray[1];

            GamePage.specialAppleMaker(); //生成金苹果
            //生成毒苹果
            snakeList.set(2 * (length - 1), snakeList.get(2 * (length - 2)));
            snakeList.set(2 * (length - 1) + 1, snakeList.get(2 * (length - 2) + 1));
        }

        //吃到金苹果
        for (int i = 0; i < GamePage.goldenFoodX.size(); i++) {
            if (Objects.equals(snakeList.get(0), GamePage.goldenFoodX.get(i)) &&
                    Objects.equals(snakeList.get(1), GamePage.goldenFoodY.get(i))) {
                int deleteLength = 3;
                if (length > 6) {
                    length = length - deleteLength;
                    for (int j = 0; j < 2 * deleteLength; j++) {
                        snakeList.remove(snakeList.size() - 1);
                    }
//                    speedController(speedUp);
                    score = score + 50;
                } else {
                    score = score + 70;
                }
                //加分
                GamePage.goldenFoodX.set(i, -100);
                GamePage.goldenFoodY.set(i, -100);
            }
        }

        //吃到毒苹果
        for (int i = 0; i < GamePage.poisonFoodX.size(); i++) {
            if (Objects.equals(snakeList.get(0), GamePage.poisonFoodX.get(i)) &&
                    Objects.equals(snakeList.get(1), GamePage.poisonFoodY.get(i))) {
                if (score > 20) {
                    score = score - 20;
                } else {
                    score = 0;
                }
                GamePage.poisonFoodX.set(i, -100);
                GamePage.poisonFoodY.set(i, -100);
                int poisonSeed = new Random().nextInt(0, 3);
//                    poisonSeed = 2;
                if (poisonSeed == 0) {
                    dizzyCnt = dizzyCnt + 1;
                    new Thread(() -> {
                        try {
                            dizzyTime = 1;
                            dizzy = true;
                            int sleepTime = 5000;
                            while (sleepTime > 0) {
                                if (GamePage.isStart) {
                                    Thread.sleep(1000);
                                    sleepTime = sleepTime - 1000;
                                } else {
                                    Thread.sleep(100);
                                }
                            }
//                            Thread.sleep(5000);
                            dizzy = false; //眩晕
                            dizzyTime = 2;
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }).start();
                    new Thread(() -> {
                        try {
                            int sleepTime = 6000;
                            while (sleepTime > 0) {
                                if (GamePage.isStart) {
                                    Thread.sleep(1000);
                                    sleepTime = sleepTime - 1000;
                                } else {
                                    Thread.sleep(100);
                                }
                            }
//                            Thread.sleep(6000);
                            dizzyTime = 3;
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }).start();
                } else if (poisonSeed == 1) {
                    score = score + 70;
                } else {
                    new Thread(() -> {
                        try {
//                                System.out.println(speedUp); // FOR TEST!!!
//                            speedController(true);
                            int sleepTime = 5000;
                            while (sleepTime > 0) {
                                if (GamePage.isStart) {
                                    Thread.sleep(1000);
                                    sleepTime = sleepTime - 1000;
                                } else {
                                    Thread.sleep(100);
                                }
                            }
//                                System.out.println(speedUp); // FOR TEST!!!
//                            speedController(false);
//                                speedUp = false;
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }).start();
                }
            }
        }
        //结束判断，头和身体撞到了
        for (int i = 1; i < length; i++) {
            //如果头和身体碰撞，那就说明游戏失败
            if (Objects.equals(snakeList.get(2 * i), snakeList.get(0))
                    && Objects.equals(snakeList.get(2 * i + 1), snakeList.get(1))) {
                heart = heart - 1;
                if (heart == 0) {
                    GamePage.isFail = true;
                }
                break;
            }
        }
    }

    public static void snakeAction(int length, ArrayList<Integer> snakeList, String direction) {
        for (int i = length - 1; i > 0; i--) {
            snakeList.set(2 * i, snakeList.get(2 * (i - 1)));
            snakeList.set(2 * i + 1, snakeList.get(2 * (i - 1) + 1));
        }
        if (!GamePage.border) {
            switch (direction) {
                case "R" -> {
                    snakeList.set(0, snakeList.get(0) + 25);
                    if (snakeList.get(0) > 850){
                        snakeList.set(0, 25);
                    }
                }
                case "L" -> {
                    snakeList.set(0, snakeList.get(0) - 25);
                    if (snakeList.get(0) < 25){
                        snakeList.set(0, 850);
                    }
                }
                case "U" -> {
                    snakeList.set(1, snakeList.get(1) - 25);
                    if (snakeList.get(1) < 75){
                        snakeList.set(1, 650);
                    }
                }
                case "D" -> {
                    snakeList.set(1, snakeList.get(1) + 25);
                    if (snakeList.get(1) > 650){
                        snakeList.set(1, 75);
                    }
                }
            }
        } else {
            switch (direction) {
                case "R" -> {
                    snakeList.set(0, snakeList.get(0) + 25);
                    if (snakeList.get(0) > 850) GamePage.isFail = true;
                }
                case "L" -> {
                    snakeList.set(0, snakeList.get(0) - 25);
                    if (snakeList.get(0) < 25) GamePage.isFail = true;
                }
                case "U" -> {
                    snakeList.set(1, snakeList.get(1) - 25);
                    if (snakeList.get(1) < 75) GamePage.isFail = true;
                }
                case "D" -> {
                    snakeList.set(1, snakeList.get(1) + 25);
                    if (snakeList.get(1) > 650) GamePage.isFail = true;
                }
            }
        }
    }
        public void secondSnakeDirection ( int keyCode){
            if (!dizzy) {
                if (keyCode == KeyEvent.VK_A && !direction.equals("R")) {
                    direction = "L";
                } else if (keyCode == KeyEvent.VK_D && !direction.equals("L")) {
                    direction = "R";
                } else if (keyCode == KeyEvent.VK_W && !direction.equals("D")) {
                    direction = "U";
                } else if (keyCode == KeyEvent.VK_S && !direction.equals("U")) {
                    direction = "D";
                }
            } else { //dizzy
                if (keyCode == KeyEvent.VK_A && !direction.equals("L")) {
                    direction = "R";
                } else if (keyCode == KeyEvent.VK_D && !direction.equals("R")) {
                    direction = "L";
                } else if (keyCode == KeyEvent.VK_W && !direction.equals("U")) {
                    direction = "D";
                } else if (keyCode == KeyEvent.VK_S && !direction.equals("D")) {
                    direction = "U";
                }
            }
        }
    }
