import java.util.ArrayList;
import java.util.Objects;

public class Fight {

    public Fight() {
    }
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
                System.out.println(Snake.length);
                Snake.snakeList = new ArrayList<>(Snake.snakeList.subList(0, 2 * Snake.length));
                System.out.println(Snake.snakeList);
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
                System.out.println(GamePage.length);
                GamePage.snakeList = new ArrayList<>(GamePage.snakeList.subList(0, 2 * GamePage.length));
                System.out.println(GamePage.snakeList);
                break;
            }
        }
    }
}
