import java.applet.Applet;
import java.applet.AudioClip;


public class Sound {
    public static final AudioClip CLICK = Applet.newAudioClip(Sound.class.getResource("click.wav"));
    public static final AudioClip BREAK = Applet.newAudioClip(Sound.class.getResource("break.wav"));
    public static final AudioClip BACKGROUND = Applet.newAudioClip(Sound.class.getResource("mazurka.wav"));
    public static final AudioClip GAMEOVER = Applet.newAudioClip(Sound.class.getResource("gameOver.wav"));
    public static final AudioClip LEVELUP = Applet.newAudioClip(Sound.class.getResource("levelUp.wav"));
    public static final AudioClip WIN = Applet.newAudioClip(Sound.class.getResource("win.wav"));
}
