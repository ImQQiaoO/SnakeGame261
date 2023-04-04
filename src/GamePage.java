import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

//游戏的面板
public class GamePage extends JPanel implements KeyListener, ActionListener {
    //定义蛇的数据结构
    static int length; //蛇的长度

    //将蛇的位置信息存储到集合中，此集合中所有的偶数位置存储蛇的x坐标，所有的奇数位置存储蛇的y坐标
    static ArrayList<Integer> snakeList = new ArrayList<>();
    //    int[] snakeLastPosition = new int[2]; //记录上一次运动的蛇尾的位置  TODO
    String direction = "R"; //蛇的方向 ： R:右  L:左  U:上  D:下
    static boolean isStart = false; //游戏是否开始
    Timer timer = new Timer(200, this);
    //食物/
    static int foodX; //Normal food
    static int foodY;
    static ArrayList<Integer> goldenFoodX = new ArrayList<>(); //Golden food
    static ArrayList<Integer> goldenFoodY = new ArrayList<>(); //goldenFoodX.size() is always equals to goldenFoodY.size()
    static ArrayList<Integer> poisonFoodX = new ArrayList<>();//Poison food
    static ArrayList<Integer> poisonFoodY = new ArrayList<>();
    static Random random = new Random();
    static boolean isFail = false; //游戏是否结束
    static int score; //游戏分数！
    static int heart = 3; //生命值
    static boolean border = false;  //是否有边界
    boolean isWin = false; //是否胜利;
    JFrame gameFrame;
    boolean dizzy = false; //是否晕眩
    int dizzyCnt = 0; //晕眩计数器
    int dizzyTime = 0; //晕眩时间
    boolean speedUp = false; //是否加速
    static boolean gameMode; //set game mode
    Snake anotherSnake; //双人模式下的另一条蛇

    public GamePage(JFrame gameFrame, boolean gameMode) {
        GamePage.gameMode = gameMode;       //set game mode, true: single mode, false: double mode.
        if (!gameMode) {
            anotherSnake = new Snake();
            System.out.println(anotherSnake);
        } else anotherSnake = null;
        init();//初始化
        this.setFocusable(true); //获取焦点事件
        this.addKeyListener(this); //键盘监听事件
        timer.start();
        this.gameFrame = gameFrame;
        System.out.println(gameMode);
    }

    //初始化方法
    public void init() {
        isFail = false;
        isWin = false;
        length = 3; //init length = 3
        timer.stop();
        timer = new Timer(200, this);
        timer.start();
        snakeList.clear();
        Collections.addAll(snakeList, 100, 100, 75, 100, 50, 100);//init snakeList
        int[] foodPositionArray = randomMaker();
        foodX = foodPositionArray[0];
        foodY = foodPositionArray[1];
        goldenFoodX.clear();
        goldenFoodY.clear();
        poisonFoodX.clear(); //init poisonFood
        poisonFoodY.clear();
        score = 0;
        heart = 3;
        dizzy = false;
        dizzyCnt = 0;
        dizzyTime = 0;
        speedUp = false;
        if (!gameMode) {        //双人模式：初始化另一条蛇
            anotherSnake.init();
        }
        //
        //添加JButton
        JButton pauseGameButton = new JButton("Pause Game");
        this.add(pauseGameButton);
        pauseGameButton.setBackground(new Color(27, 80, 104));
        pauseGameButton.setForeground(Color.WHITE);
        pauseGameButton.setBounds(25, 20, 150, 25);
        pauseGameButton.addActionListener(e -> {    //PAUSE GAME
            if (isStart) {
                timer.stop();
            } else {
                timer.start();
            }
            isStart = !isStart;
            requestFocus();
            repaint();
        });
        JButton backButton = new JButton("Back to Menu");
        this.add(backButton);
        backButton.setBackground(new Color(27, 80, 104));
        backButton.setForeground(Color.WHITE);
        backButton.setBounds(25, 50, 150, 25);
        backButton.addActionListener(e -> {    //BACK TO MENU
            timer.stop();
            new Menu();
            gameFrame.dispose();
            requestFocus();
        });
        setLayout(null);
    }

    public static int[] randomMaker() {
        int[] foodPositionArray = new int[2];
        foodPositionArray[0] = 25 + 25 * random.nextInt(34);
        foodPositionArray[1] = 75 + 25 * random.nextInt(24);
        for (int i = 0; i < length; i++) {
            if (snakeList.get(2 * i) == foodPositionArray[0] &&
                    snakeList.get(2 * i + 1) == foodPositionArray[1]) {
                foodPositionArray[0] = 25 + 25 * random.nextInt(34);
                foodPositionArray[1] = 75 + 25 * random.nextInt(24);
                i = 0;
            }
        }
        return foodPositionArray;
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);//清屏
        this.setBackground(new Color(40, 97, 81)); //设置面板的背景色
        g.fillRect(25, 75, 850, 600); //绘制游戏区域
        Data.head.paintIcon(this, g, snakeList.get(0), snakeList.get(1));
        for (int i = 1; i < length; i++) {
            //蛇的身体长度根据length来控制
            Data.body.paintIcon(this, g, snakeList.get(2 * i), snakeList.get(2 * i + 1));
        }
        if (!gameMode) {
            Data.head1.paintIcon(this, g, anotherSnake.snakeList.get(0), anotherSnake.snakeList.get(1));
            for (int i = 1; i < anotherSnake.length; i++) {
                //蛇的身体长度根据length来控制
                Data.body1.paintIcon(this, g, anotherSnake.snakeList.get(2 * i),
                        anotherSnake.snakeList.get(2 * i + 1));
            }
        }
        //画食物
        Data.food.paintIcon(this, g, foodX, foodY); //Draw the normal apple
        for (int i = 0; i < goldenFoodX.size(); i++) { //Draw the golden apple
            Data.goldenApple.paintIcon(this, g, goldenFoodX.get(i), goldenFoodY.get(i));
        }
        for (int i = 0; i < poisonFoodX.size(); i++) {  //Draw the poison apple
            Data.poisonApple.paintIcon(this, g, poisonFoodX.get(i), poisonFoodY.get(i));
        }
        g.setColor(Color.white);
        g.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 18));
        if (gameMode) { //单人模式计分信息显示
            g.drawString("Length " + length, 750, 40);
            g.drawString("Score " + score, 750, 60);
        } else { //双人模式计分信息显示
            g.setColor(new Color(255, 0, 0));
            g.drawString("Player A", 200, 25);
            g.setColor(Color.white);
            g.drawString("Length " + length, 200, 40);
            g.drawString("Score " + score, 200, 55);

            g.setColor(new Color(255, 156, 0));
            g.drawString("Player B", 550, 25);
            g.setColor(Color.white);
            g.drawString("Length " + anotherSnake.length, 550, 40);
            g.drawString("Score " + anotherSnake.score, 550, 55);
//            Data.head1.paintIcon(this, g, 550, 50);
        }
        //游戏提示
        if (!isStart) {
            g.setColor(Color.white);
            g.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 40));
            g.drawString("Press SPACE to start!", 300, 300);
        }
        //失败判断
        if (isFail) {
            g.setColor(Color.RED);
            g.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 40));
            g.drawString("You Dead, Press SPACE to restart!", 200, 300);
            direction = "R";
            //读取最高分
            if (gameMode) { //单人模式才会写入最高分
                getNewHighestScore();
            }
        }
        if (isWin) {
            g.setColor(new Color(181, 163, 20));
            g.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 40));
            g.drawString("You win! Press SPACE to restart.", 150, 300);
        }
        if (dizzy && dizzyTime != 3) {
            g.setColor(new Color(123, 37, 153));
            g.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 40));
            g.drawString("You're poisoned!", 300, 250);
        }
        if (dizzyCnt != 0 && !dizzy && dizzyTime != 3) {
            g.setColor(new Color(49, 37, 153));
            g.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 40));
            g.drawString("Poisoning has been removed.", 300, 350);
        }
        //draw the rest heart
        int gap = 0;
        int maxHeart = 3;
        if (gameMode) {
            for (int i = 0; i < maxHeart; i++) {
                gap++;
                if (i < heart) {
                    Data.heart.paintIcon(this, g, 625 + i * 30 + gap * 7, 30);
                } else {
                    Data.emptyHeart.paintIcon(this, g, 625 + i * 30 + gap * 7, 30);
                }
            }
        } else { //Double Mode
            for (int i = 0; i < maxHeart; i++) {
                gap++;
                if (i < heart) {
                    Data.heart.paintIcon(this, g, 300 + i * 30 + gap * 7, 20);
                } else {
                    Data.emptyHeart.paintIcon(this, g, 300 + i * 30 + gap * 7, 20);
                }
            }

            gap = 0;
            for (int i = 0; i < maxHeart; i++) {
                gap++;
                if (i < anotherSnake.heart) {
                    Data.heart1.paintIcon(this, g, 650 + i * 30 + gap * 7, 20);
                } else {
                    Data.emptyHeart.paintIcon(this, g, 650 + i * 30 + gap * 7, 20);
                }
            }
        }
    }

    public void getNewHighestScore() {
        try {
            FileReader fd = new FileReader("./src/Score.txt");
            BufferedReader br = new BufferedReader(fd);
            int score = Integer.parseInt(br.readLine());
            if (GamePage.score > score) {
                FileWriter fw = new FileWriter("./src/Score.txt");
                fw.write(String.valueOf(GamePage.score));
                fw.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    //键盘监听事件
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_SPACE) {
            if (isFail) {
                isFail = false;
                init();
            } else {
                isStart = !isStart;
                if (isStart) {
                    timer.start();
                } else {
                    timer.stop();
                }
            }
            if (isWin) { //If won, restart the game.
                isWin = false;
                init();
            }
            repaint();
//            System.out.println(snakeList);//todo: print out the snakeList
        }
        //键盘控制走向
        if (!dizzy) {
            if (keyCode == KeyEvent.VK_LEFT && !direction.equals("R")) {
                direction = "L";
            } else if (keyCode == KeyEvent.VK_RIGHT && !direction.equals("L")) {
                direction = "R";
            } else if (keyCode == KeyEvent.VK_UP && !direction.equals("D")) {
                direction = "U";
            } else if (keyCode == KeyEvent.VK_DOWN && !direction.equals("U")) {
                direction = "D";
            }
        } else { //dizzy
            if (keyCode == KeyEvent.VK_LEFT && !direction.equals("L")) {
                direction = "R";
            } else if (keyCode == KeyEvent.VK_RIGHT && !direction.equals("R")) {
                direction = "L";
            } else if (keyCode == KeyEvent.VK_UP && !direction.equals("U")) {
                direction = "D";
            } else if (keyCode == KeyEvent.VK_DOWN && !direction.equals("D")) {
                direction = "U";
            }
        }
        //The direction of motion of the other snake
        if (!gameMode) {
            anotherSnake.secondSnakeDirection(keyCode);
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    //定时执行的操作
    @Override
    public void actionPerformed(ActionEvent e) {

        if (isStart && !isFail && !isWin) {

            if (!gameMode) {
                anotherSnake.anotherActionPerformed();
            }
//            snakeAction(this.length, this.snakeList, this.direction, score);
            Snake.snakeAction(length, snakeList, direction);

            if (snakeList.get(0) == foodX && snakeList.get(1) == foodY) {
                length++;
                if (length == 20 && border) { // if the length of the snake is 20, you win
                    timer.stop();
                    isWin = true;
                }
                snakeList.add(0);
                snakeList.add(0);
                speedController(speedUp);
                //加分
                score = score + 10;
                //重新生成食物

                int[] foodPositionArray = randomMaker();
                foodX = foodPositionArray[0];
                foodY = foodPositionArray[1];

                specialAppleMaker(); //生成特殊苹果
                snakeList.set(2 * (length - 1), snakeList.get(2 * (length - 2)));
                snakeList.set(2 * (length - 1) + 1, snakeList.get(2 * (length - 2) + 1));
            }

            //吃到金苹果
            for (int i = 0; i < goldenFoodX.size(); i++) {
                if (Objects.equals(snakeList.get(0), goldenFoodX.get(i)) &&
                        Objects.equals(snakeList.get(1), goldenFoodY.get(i))) {
                    int deleteLength = 3;
                    if (length > 6) {
                        length = length - deleteLength;
                        for (int j = 0; j < 2 * deleteLength; j++) {
                            snakeList.remove(snakeList.size() - 1);
                        }
                        speedController(speedUp);
                        score = score + 50;
                    } else {
                        score = score + 70;
                    }
                    //加分
//                    goldenFoodX = -100;
//                    goldenFoodY = -100;
                    goldenFoodX.set(i, -100);
                    goldenFoodY.set(i, -100);
                }
            }

            //吃到毒苹果
            for (int i = 0; i < poisonFoodX.size(); i++) {
                if (Objects.equals(snakeList.get(0), poisonFoodX.get(i)) &&
                        Objects.equals(snakeList.get(1), poisonFoodY.get(i))) {
                    if (score > 20) {
                        score = score - 20;
                    } else {
                        score = 0;
                    }
                    poisonFoodX.set(i, -100);
                    poisonFoodY.set(i, -100);
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
                                    if (isStart) {
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
                                    if (isStart) {
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
                                speedController(true);
                                int sleepTime = 5000;
                                while (sleepTime > 0) {
                                    if (isStart) {
                                        Thread.sleep(1000);
                                        sleepTime = sleepTime - 1000;
                                    } else {
                                        Thread.sleep(100);
                                    }
                                }
//                                System.out.println(speedUp); // FOR TEST!!!
                                speedController(false);
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
                        isFail = true;
                    }
                    break;
                }
            }
            repaint(); //需要不断地更新页面实现动画
//            System.out.println(snakeList);//todo: print out the snakeList
        }
        timer.start();//让时间动起来!
    }


    public static void specialAppleMaker() {
        int appleSeed = new Random().nextInt(0, 10);
//        appleSeed = 2; // FOR TEST!!!
        if (appleSeed == 1) {
            boolean goldenApple = true;
            specialAppleThreadMaker(goldenFoodX, goldenFoodY, goldenApple);
        } else if (appleSeed == 2) {
            boolean goldenApple = false;
            specialAppleThreadMaker(poisonFoodX, poisonFoodY, goldenApple);
        }
    }

    public static void specialAppleThreadMaker(ArrayList<Integer> specialFoodX,
                                               ArrayList<Integer> specialFoodY, boolean goldenApple) {
        int[] specialFoodPositionArray = randomMaker();
        specialFoodX.add(specialFoodPositionArray[0]);
        specialFoodY.add(specialFoodPositionArray[1]);
        //计时器，随机时间后特殊苹果消失
        new Thread(() -> {
            try {
                int sleepTime;
                if (goldenApple) { //当游戏暂停时，此线程休眠不会暂停。
//                    Thread.sleep(new Random().nextInt(5000, 10000));
                    sleepTime = new Random().nextInt(5000, 10000);
                } else {
                    sleepTime = 15000;
                }
                while (sleepTime > 0) {
                    if (isStart) {
                        Thread.sleep(1000);
                        sleepTime -= 1000;
                    } else {
                        Thread.sleep(100);
                    }
                }
                if (specialFoodX.size() == 0) {
                    return;
                }
                specialFoodX.remove(specialFoodX.size() - 1);
                specialFoodY.remove(specialFoodY.size() - 1);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }).start();
    }

    /**
     * 控制贪吃蛇的速度，随着长度的增加，速度也会随之增加
     */
    public void speedController(boolean speedUp) {
        if (!gameMode) {
            if (length + anotherSnake.length >= 6 && length + anotherSnake.length < 20) {
                timer.stop();
                timer = new Timer(200, this);
                timer.start();
            } else if (length + anotherSnake.length >= 20 && length + anotherSnake.length < 30) {
                timer.stop();
                timer = new Timer(175, this);
                timer.start();
            } else if (length + anotherSnake.length >= 30 && length + anotherSnake.length < 40) {
                timer.stop();
                timer = new Timer(150, this);
                timer.start();
            } else if (length + anotherSnake.length >= 40 && length + anotherSnake.length < 50) {
                timer.stop();
                timer = new Timer(125, this);
                timer.start();
            } else if (length + anotherSnake.length >= 50) {
                timer.stop();
                timer = new Timer(100, this);
                timer.start();
            }
            return;
        }
        if (speedUp) {
            timer.stop();
            timer = new Timer(120, this);
            timer.start();
            return;
        }
        if (length >= 3 && length < 10) {
            timer.stop();
            timer = new Timer(200, this);
            timer.start();
        } else if (length >= 10 && length < 15) {
            timer.stop();
            timer = new Timer(175, this);
            timer.start();
        } else if (length >= 15 && length < 20) {
            timer.stop();
            timer = new Timer(150, this);
            timer.start();
        } else if (length >= 20 && length < 25) {
            timer.stop();
            timer = new Timer(125, this);
            timer.start();
        } else if (length >= 25) {
            timer.stop();
            timer = new Timer(100, this);
            timer.start();
        }
    }
}