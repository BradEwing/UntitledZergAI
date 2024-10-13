package info.time;

public class GameTime {

    private int frame = 0;

    public GameTime(int minutes, int seconds) {
        int totalSeconds = (minutes * 60) + seconds;
        frame = totalSeconds * 24;
    }

    public int frame() {
        return frame;
    }

}
