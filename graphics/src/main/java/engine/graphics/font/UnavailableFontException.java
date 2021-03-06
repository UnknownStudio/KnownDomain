package engine.graphics.font;

public class UnavailableFontException extends RuntimeException {

    private final Font font;

    public UnavailableFontException(Font font) {
        super("Unavailable font. Font name: " + font.getName());
        this.font = font;
    }

    public Font getFont() {
        return font;
    }
}
