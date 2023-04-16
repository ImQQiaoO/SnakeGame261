import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

//游戏的面板
public class GamePage extends JPanel implements KeyListener, ActionListener {
    //定义蛇的数据结构
    static int length; //蛇的长度

    //将蛇的位置信息存储到集合中，此集合中所有的偶数位置存储蛇的x坐标，所有的奇数位置存储蛇的y坐标
    static ArrayList<Integer> snakeList = new ArrayList<>();
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
    static boolean border;  //是否有边界
    static boolean fight; //是否是对战模式
    boolean isWin = false; //是否胜利;
    JFrame gameFrame;
    boolean dizzy = false; //是否晕眩
    int dizzyCnt = 0; //晕眩计数器
    int dizzyTime = 0; //晕眩时间
    boolean speedUp = false; //是否加速
    static boolean gameMode; //set game mode
    Snake anotherSnake; //双人模式下的另一条蛇
    static ArrayList<Integer> obstacleX = new ArrayList<>(); //障碍物的数组
    static ArrayList<Integer> obstacleY = new ArrayList<>();

    public GamePage(JFrame gameFrame, boolean gameMode) {
        GamePage.gameMode = gameMode;       //set game mode, true: single mode, false: double mode.
        if (!gameMode) {
            anotherSnake = new Snake();
        } else anotherSnake = null;
        init();//初始化
        this.setFocusable(true); //获取焦点事件
        this.addKeyListener(this); //键盘监听事件
        timer.start();
        this.gameFrame = gameFrame;
    }

    //初始化方法
    public void init() {
        isFail = false;
        isWin = false;
        if (!fight) {
            length = 3; //init length = 3
        } else {
            length = 12;
        }
        timer.stop();
        timer = new Timer(200, this);
        timer.start();
        snakeList.clear();
        if (!fight) {
            Collections.addAll(snakeList, 100, 125, 75, 125, 50, 125);//init snakeList
        } else {
            for (int i = 0; i < 12; i++) {
                snakeList.add(400 - 25 * i);
                snakeList.add(125);
            }
        }
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
        obstacleX.clear(); //init obstacle
        obstacleY.clear();
        if (border && gameMode) {
            for (int i = 0; i < 16; i++) {
                obstacleX.add(25 * (i + 10));
                obstacleY.add(9 * 25);
                obstacleX.add(25 * (i + 10));
                obstacleY.add(22 * 25);
            }
            for (int i = 0; i < 2; i++) {
                obstacleX.add(25 * 10);
                obstacleY.add((10 + i) * 25);
                obstacleX.add(25 * 25);
                obstacleY.add((10 + i) * 25);
                obstacleX.add(25 * 10);
                obstacleY.add((20 + i) * 25);
                obstacleX.add(25 * 25);
                obstacleY.add((20 + i) * 25);
            }
        }
        int[] foodPositionArray = randomMaker();
        foodX = foodPositionArray[0];
        foodY = foodPositionArray[1];
        if (fight) {    //在fight模式下，游戏面板中不会出现苹果
            foodX = -500;
            foodY = -500;
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
        foodPositionArray[1] = 100 + 25 * random.nextInt(24);
        ArrayList<Integer> buildingList = new ArrayList<>();
        int sumLength;
        if (gameMode) { //单人模式
            buildingList.addAll(GamePage.snakeList);
            sumLength = GamePage.length;
            if (border) { //避免苹果出现在障碍物上
                for (int i = 0; i < obstacleX.size(); i++) {
                    buildingList.add(obstacleX.get(i));
                    buildingList.add(obstacleY.get(i));
                }
                sumLength = sumLength + obstacleX.size();
            }

        } else {
            buildingList.addAll(GamePage.snakeList);
            buildingList.addAll(Snake.snakeList);
            sumLength = GamePage.length + Snake.length;
        }
        for (int i = 0; i < sumLength; i++) {
            if (buildingList.get(2 * i) == foodPositionArray[0] &&
                    buildingList.get(2 * i + 1) == foodPositionArray[1]) {
                foodPositionArray[0] = 25 + 25 * random.nextInt(34);
                foodPositionArray[1] = 100 + 25 * random.nextInt(24);
                i = 0;
            }
        }
        return foodPositionArray;
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);//清屏
        this.setBackground(new Color(40, 97, 81)); //设置面板的背景色
        g.fillRect(25, 100, 850, 600); //绘制游戏区域
        //边界:
        if (gameMode && border) { // 1 模式下边界显示
            for (int i = 0; i < 35; i++) {
                Data.paling.paintIcon(this, g, 25 + i * 25, 75);
                Data.paling.paintIcon(this, g, 25 + i * 25, 700);
            }
            for (int i = 0; i < 26; i++) {
                Data.paling.paintIcon(this, g, 0, 75 + 25 * i);
                Data.paling.paintIcon(this, g, 875, 75 + 25 * i);
            }
            //地图内部的障碍物
            for (int i = 0; i < obstacleX.size(); i++) {
                Data.paling.paintIcon(this, g, obstacleX.get(i), obstacleY.get(i));
            }
        }

        Data.head.paintIcon(this, g, snakeList.get(0), snakeList.get(1));
        if (!fight) {
            for (int i = 1; i < length; i++) {
                //蛇的身体长度根据length来控制
                Data.body.paintIcon(this, g, snakeList.get(2 * i), snakeList.get(2 * i + 1));
            }
            if (!gameMode) {
                Data.head1.paintIcon(this, g, Snake.snakeList.get(0), Snake.snakeList.get(1));
                for (int i = 1; i < Snake.length; i++) {
                    //蛇的身体长度根据length来控制
                    Data.body1.paintIcon(this, g, Snake.snakeList.get(2 * i),
                            Snake.snakeList.get(2 * i + 1));
                }
            }
        } else {
            for (int i = 1; i < length; i++) {
                //蛇的身体长度根据length来控制
                if (i < 4) {
                    Data.body.paintIcon(this, g, snakeList.get(2 * i), snakeList.get(2 * i + 1));
                }
                if (i >= 4) {
                    Data.packet.paintIcon(this, g, snakeList.get(2 * i), snakeList.get(2 * i + 1));
                }
            }
            Data.head1.paintIcon(this, g, Snake.snakeList.get(0), Snake.snakeList.get(1));
            for (int i = 1; i < Snake.length; i++) {
                if (i < 4) {
                    Data.body1.paintIcon(this, g, Snake.snakeList.get(2 * i), Snake.snakeList.get(2 * i + 1));
                }
                if (i >= 4) {
                    Data.packet.paintIcon(this, g, Snake.snakeList.get(2 * i), Snake.snakeList.get(2 * i + 1));
                }
            }
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
                if (i < Snake.heart) {
                    Data.heart1.paintIcon(this, g, 650 + i * 30 + gap * 7, 20);
                } else {
                    Data.emptyHeart.paintIcon(this, g, 650 + i * 30 + gap * 7, 20);
                }
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
        if (fight) {
            for (int i = 0; i < Fight.appleList.size() / 2; i++) {
                Data.packet.paintIcon(this, g, Fight.appleList.get(2 * i), Fight.appleList.get(2 * i + 1));
            }
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
            if (!fight) {
                g.drawString("Score " + score, 200, 55);
            }

            g.setColor(new Color(255, 156, 0));
            g.drawString("Player B", 550, 25);
            g.setColor(Color.white);
            g.drawString("Length " + Snake.length, 550, 40);
            if (!fight) {
                g.drawString("Score " + anotherSnake.score, 550, 55);
            }
        }
        //游戏提示
        if (!isStart) {
            g.setColor(Color.white);
            g.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 40));
            g.drawString("Press SPACE to start!", 270, 300);
        }
        //失败判断
        if (isFail && gameMode) {
            g.setColor(Color.RED);
            g.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 40));
            g.drawString("You Dead, Press SPACE to restart!", 145, 300);
            direction = "R";
        }
        //双人模式失败判断
        if (isFail && !gameMode) {
            g.setColor(Color.RED);
            g.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 40));
            g.drawString("Player A Dead!", 290, 250);
            winnerPrinter(g);
        } else if (Snake.isFail && !gameMode) {
            g.setColor(new Color(255, 156, 0));
            g.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 40));
            g.drawString("Player B Dead!", 290, 250);
            winnerPrinter(g);
        }
        if (isWin) {
            g.setColor(new Color(181, 163, 20));
            g.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 40));
            g.drawString("You win! Press SPACE to restart.", 150, 300);
        }
        if (gameMode) {
            if (dizzy && dizzyTime != 3) {
                g.setColor(new Color(123, 37, 153));
                g.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 40));
                g.drawString("You're poisoned!", 300, 250);
            }
            if (dizzyCnt != 0 && !dizzy && dizzyTime != 3) {
                g.setColor(new Color(49, 37, 153));
                g.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 40));
                g.drawString("Poisoning has been removed.", 250, 350);
            }
        } else { //双人模式下中毒效果显示
            if (dizzy && dizzyTime != 3) {
                g.setColor(new Color(0, 225, 30));
                g.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 40));
                g.drawString("Player 1 is poisoned!", 300, 250);
            }
            if (dizzyCnt != 0 && !dizzy && dizzyTime != 3) {
                g.setColor(new Color(225, 113, 0));
                g.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 40));
                g.drawString("Player 1 poisoning has been removed.", 150, 350);
            }
            if (Snake.dizzy && Snake.dizzyTime != 3) {
                g.setColor(new Color(0, 174, 225));
                g.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 40));
                g.drawString("Player 2 is poisoned!", 300, 250);
            }
            if (Snake.dizzyCnt != 0 && !Snake.dizzy && Snake.dizzyTime != 3) {
                g.setColor(new Color(203, 0, 225));
                g.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 40));
                g.drawString("Player 2 poisoning has been removed.", 150, 350);
            }
        }
    }

    public void winnerPrinter(Graphics g) {
        int scoreA = score + heart * 25;
        int scoreB = anotherSnake.score + Snake.heart * 25;
        g.setColor(new Color(255, 0, 0));
        g.drawString("Player A's Final Score: " + scoreA, 180, 300);
        g.setColor(new Color(255, 156, 0));
        g.drawString("Player B's Final Score: " + scoreB, 180, 350);
        g.setColor(new Color(4, 122, 189));
        if (scoreA > scoreB) {
            g.drawString("Player A Wins!", 290, 400);
        } else if (scoreA < scoreB) {
            g.drawString("Player B Wins!", 290, 400);
        } else {
            g.drawString("Draw!", 290, 400);
        }
        g.setColor(new Color(255, 255, 255));
        Data.heart.paintIcon(this, g, 310, 420);
        g.drawString("= 25 Marks", 350, 450);
        g.drawString("Press SPACE to restart!", 200, 500);
        direction = "R";
    }

    public void getNewHighestScore() {
        // 写入当前时间和得分，追加至文件末尾    写入游戏得分
        Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(day);
        String fileName = "./src/Score.txt";
        Path path = Paths.get(fileName);
        //读取文件中所有的内容，将其存入一个TreeMap中
        TreeMap<String, Integer> highestScoreMap = new TreeMap<>();
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] arr = line.split("=");
                highestScoreMap.put(arr[0], Integer.valueOf(arr[1]));
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        highestScoreMap.put(date, GamePage.score);
        //将TreeMap中的内容依据value值进行排序
        List<Map.Entry<String, Integer>> list = new ArrayList<>(highestScoreMap.entrySet());
        list.sort((o1, o2) -> o2.getValue() - o1.getValue());
        //将排序后的内容写入文件中,只保留前十条
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
            for (int i = 0; i < list.size() && i < 10; i++) {
                bufferedWriter.write(list.get(i).getKey() + "=" + list.get(i).getValue() + "\n");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
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
            if (isFail || Snake.isFail) {
                isFail = false;
                Snake.isFail = false;
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
//            System.out.println(snakeList);// print out the snakeList
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

        if (isStart && !isFail && !isWin && !Snake.isFail) {

            speedController(speedUp);
            if (!gameMode) {
                anotherSnake.anotherActionPerformed();
            }
            //如果头部与地图内部的障碍物重合，那么游戏失败
            if (border) {
                for (int i = 0; i < GamePage.obstacleX.size(); i++) {
                    if (Objects.equals(snakeList.get(0), GamePage.obstacleX.get(i)) &&
                            Objects.equals(snakeList.get(1), GamePage.obstacleY.get(i))) {
                        GamePage.heart = 0;
                        GamePage.isFail = true;
                        repaint();
                        return;
                    }
                }
            }
            Snake.snakeAction(length, snakeList, direction);

            if (snakeList.get(0) == foodX && snakeList.get(1) == foodY) {
                length++;
                if (length == 20 && border) { // if the length of the snake is 20, you win
                    timer.stop();
                    isWin = true;
                }
                snakeList.add(0);
                snakeList.add(0);
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
            if (fight) {
                for (int i = 0; i < Fight.appleList.size() / 2; i++) {
                    if (Objects.equals(GamePage.snakeList.get(0), Fight.appleList.get(2 * i)) &&
                            Objects.equals(GamePage.snakeList.get(1), Fight.appleList.get(2 * i + 1))) {
                        Fight.appleList.set(2 * i, -100);
                        Fight.appleList.set(2 * i + 1, -100);
                        GamePage.length++;
                        GamePage.snakeList.add(0);
                        GamePage.snakeList.add(0);
                        GamePage.snakeList.set(2 * (GamePage.length - 1), GamePage.snakeList.get(2 * (GamePage.length - 2)));
                        GamePage.snakeList.set(2 * (GamePage.length - 1) + 1, GamePage.snakeList.get(2 * (GamePage.length - 2) + 1));
                    }
                }
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
//                    poisonSeed = 2; //TEST!!!
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
                                dizzyTime = 3;
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }).start();
                    } else if (poisonSeed == 1) {
                        score = score + 70;
                    } else if (poisonSeed == 2) {
                        if (gameMode) {
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
                        } else {
                            if (heart > 0 && heart < 3) {
                                heart = heart + 1;
                            }
                        }
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
                        //读取最高分
                        if (gameMode && !border) { //单人模式无边界才会记录最高分
                            getNewHighestScore();
                        }
                    }
                    break;
                }
            }
            if (fight) {
                //战斗模式，如果碰到对方的前四节身体，那么自己会掉一点血
                Fight.GPTouchSnake();
                Fight.SnakeTouchGP();
            }
            repaint(); //需要不断地更新页面实现动画
//            System.out.println(snakeList);// print out the snakeList
        }
        timer.start();//让时间动起来!
    }


    public static void specialAppleMaker() {
        int appleSeed = new Random().nextInt(0, 15); // 概率为1/15
//        appleSeed = 2; //   FOR TEST!!!
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
            int currIndex = specialFoodX.size() - 1;
            try {
                int sleepTime;
                if (goldenApple) { //当游戏暂停时，此线程休眠不会暂停。
                    sleepTime = new Random().nextInt(5000, 10000);
                } else {    //紫色苹果
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

                specialFoodX.set(currIndex, -500);
                specialFoodY.set(currIndex, -500);

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
            if (length + Snake.length >= 6 && length + Snake.length < 20) {
                timer.stop();
                timer = new Timer(200, this);
                timer.start();
            } else if (length + Snake.length >= 20 && length + Snake.length < 30) {
                timer.stop();
                timer = new Timer(175, this);
                timer.start();
            } else if (length + Snake.length >= 30 && length + Snake.length < 40) {
                timer.stop();
                timer = new Timer(150, this);
                timer.start();
            } else if (length + Snake.length >= 40 && length + Snake.length < 50) {
                timer.stop();
                timer = new Timer(125, this);
                timer.start();
            } else if (length + Snake.length >= 50) {
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