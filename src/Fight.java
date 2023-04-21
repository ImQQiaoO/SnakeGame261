import java.util.ArrayList;
import java.util.Objects;


public class Fight {
    static ArrayList<Integer> appleList = new ArrayList<>();
    static int fightCountDown;
    static boolean timeUp = false;
    public Fight() {
    }

    @SuppressWarnings("Duplicates")
    public static void GPTouchSnake() {
        for (int i = 0; i < 4; i++) {
            if (Objects.equals(GamePage.snakeList.get(0), Snake.snakeList.get(2 * i)) &&
                    Objects.equals(GamePage.snakeList.get(1), Snake.snakeList.get(2 * i + 1))) {
                GamePage.heart = GamePage.heart - 1;
                if (GamePage.heart == 0) {
                    GamePage.isFail = true;
                }
                break;
            }
        }
        //如果碰到对方第四节以后的身体，那么对方长度会变成碰撞的那一节
        for (int i = 4; i < Snake.snakeList.size() / 2; i++) {
            if (Objects.equals(GamePage.snakeList.get(0), Snake.snakeList.get(2 * i)) &&
                    Objects.equals(GamePage.snakeList.get(1), Snake.snakeList.get(2 * i + 1))) {
//                Snake.length = i + 1;
                Snake.length = i;
                ArrayList<Integer> tempList = Snake.snakeList;
                Snake.snakeList = new ArrayList<>(tempList.subList(0, 2 * Snake.length));
                appleList.addAll(new ArrayList<>(tempList.subList(2 * (Snake.length + 1), tempList.size())));
                GamePage.length = GamePage.length + 1;
                GamePage.snakeList.add(0);
                GamePage.snakeList.add(0);
                GamePage.snakeList.set(2 * (GamePage.length - 1), GamePage.snakeList.get(2 * (GamePage.length - 2)));
                GamePage.snakeList.set(2 * (GamePage.length - 1) + 1, GamePage.snakeList.get(2 * (GamePage.length - 2) + 1));
//                System.out.println(appleList);
                break;
            }
        }
    }

    public static void SnakeTouchGP() {
        for (int i = 0; i < 4; i++) {
            if (Objects.equals(Snake.snakeList.get(0), GamePage.snakeList.get(2 * i)) &&
                    Objects.equals(Snake.snakeList.get(1), GamePage.snakeList.get(2 * i + 1))) {
                Snake.heart = Snake.heart - 1;
                if (Snake.heart == 0) {
                    Snake.isFail = true;
                }
                break;
            }
        }
        for (int i = 4; i < GamePage.snakeList.size() / 2; i++) {
            if (Objects.equals(Snake.snakeList.get(0), GamePage.snakeList.get(2 * i)) &&
                    Objects.equals(Snake.snakeList.get(1), GamePage.snakeList.get(2 * i + 1))) {
//                GamePage.length = i + 1;
                GamePage.length = i;
                ArrayList<Integer> tempList = GamePage.snakeList;
                GamePage.snakeList = new ArrayList<>(tempList.subList(0, 2 * GamePage.length));
                appleList.addAll(new ArrayList<>(tempList.subList(2 * (GamePage.length + 1), tempList.size())));
                Snake.length = Snake.length + 1;
                Snake.snakeList.add(0);
                Snake.snakeList.add(0);
                Snake.snakeList.set(2 * (Snake.length - 1), Snake.snakeList.get(2 * (Snake.length - 2)));
                Snake.snakeList.set(2 * (Snake.length - 1) + 1, Snake.snakeList.get(2 * (Snake.length - 2) + 1));
//                System.out.println(appleList);
                break;
            }
        }
    }

}
