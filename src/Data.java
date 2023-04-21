import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@SuppressWarnings("all")
public class Data {

    private static Clip clip0;
    private static Clip clip1;

    static { //static block, for loading audio file
        try {
            clip0 = AudioSystem.getClip();
            clip1 = AudioSystem.getClip();
            clip0.open(AudioSystem.getAudioInputStream(Data.class.getResourceAsStream("statics/homage_to_BOTW0.wav")));
            clip1.open(AudioSystem.getAudioInputStream(Data.class.getResourceAsStream("statics/homage_to_BOTW1.wav")));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

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
    //paling
    public static URL palingUrl = Data.class.getResource("/statics/paling.png");
    public static ImageIcon paling = new ImageIcon(palingUrl);
    //packet
    public static URL packetUrl = Data.class.getResource("/statics/packet.png");
    public static ImageIcon packet = new ImageIcon(packetUrl);
    //packet0
    public static URL packet0Url = Data.class.getResource("/statics/packet0.png");
    public static ImageIcon packet0 = new ImageIcon(packet0Url);
    public static File sound0 = new File("src/statics/homage_to_BOTW0.wav");
    public static File sound1 = new File("src/statics/homage_to_BOTW1.wav");

    public static void playBGM1() {
        AudioInputStream audioIn = null;
        try {
            audioIn = AudioSystem.getAudioInputStream(Data.sound1);
        } catch (UnsupportedAudioFileException | IOException ex) {
            throw new RuntimeException(ex);
        }
        Clip clip;
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException ex) {
            throw new RuntimeException(ex);
        }
        try {
            clip.open(audioIn);
        } catch (LineUnavailableException | IOException ex) {
            throw new RuntimeException(ex);
        }
        clip.start();
    }

    public static void playBGM0() {
        AudioInputStream audioIn = null;
        try {
            audioIn = AudioSystem.getAudioInputStream(Data.sound0);
        } catch (UnsupportedAudioFileException | IOException ex) {
            throw new RuntimeException(ex);
        }
        Clip clip;
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException ex) {
            throw new RuntimeException(ex);
        }
        try {
            clip.open(audioIn);
        } catch (LineUnavailableException | IOException ex) {
            throw new RuntimeException(ex);
        }
        clip.start();
    }

}