
import javax.swing.*;
import java.net.URL;

public class Data {
    //头部图片
    //head
    public static URL headUrl = Data.class.getResource("/statics/head.png");
    public static ImageIcon head = new ImageIcon(headUrl);
    public static URL headUrl1 = Data.class.getResource("/statics/head1.png");
    public static ImageIcon head1 = new ImageIcon(headUrl1);
    //身体
    public static URL bodyUrl = Data.class.getResource("/statics/dot.png");
    public static ImageIcon body = new ImageIcon(bodyUrl);
    public static URL bodyUrl1 = Data.class.getResource("/statics/dot1.png");
    public static ImageIcon body1 = new ImageIcon(bodyUrl1);
    //NormalApple
    public static URL foodUrl = Data.class.getResource("/statics/apple.png");
    public static ImageIcon food = new ImageIcon(foodUrl);
    //GoldenApple
    public static URL goldenAppleUrl = Data.class.getResource("/statics/goldenApple.png");
    public static ImageIcon goldenApple = new ImageIcon(goldenAppleUrl);
    //PoisonApple
    public static URL poisonAppleUrl = Data.class.getResource("/statics/poisonApple.png");
    public static ImageIcon poisonApple = new ImageIcon(poisonAppleUrl);

    public static URL labelUrl = Data.class.getResource("/statics/label.png");
    public static ImageIcon label = new ImageIcon(labelUrl);
    //heart
    public static URL heartUrl = Data.class.getResource("/statics/fullHeart.png");
    public static ImageIcon heart = new ImageIcon(heartUrl);
    //emptyHeart
    public static URL emptyHeartUrl = Data.class.getResource("/statics/emptyHeart.png");
    public static ImageIcon emptyHeart = new ImageIcon(emptyHeartUrl);
    //heart1
    public static URL heartUrl1 = Data.class.getResource("/statics/fullHeart1.png");
    public static ImageIcon heart1 = new ImageIcon(heartUrl1);

}