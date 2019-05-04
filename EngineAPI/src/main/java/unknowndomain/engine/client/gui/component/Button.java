package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.MutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import unknowndomain.engine.client.gui.event.MouseEvent;
import unknowndomain.engine.client.gui.misc.Background;
import unknowndomain.engine.client.gui.misc.Insets;
import unknowndomain.engine.client.gui.misc.Pos;
import unknowndomain.engine.client.gui.rendering.ButtonRenderer;
import unknowndomain.engine.client.gui.rendering.ComponentRenderer;
import unknowndomain.engine.client.gui.text.Font;
import unknowndomain.engine.client.gui.text.Text;
import unknowndomain.engine.util.Color;

import java.util.function.Consumer;

public class Button extends Control {

    private final MutableValue<String> text = new SimpleMutableObjectValue<>();
    private final MutableValue<Font> font = new SimpleMutableObjectValue<>(Font.getDefaultFont());
    private final MutableValue<Color> textColor = new SimpleMutableObjectValue<>(Color.WHITE);
    private final MutableValue<Pos> textAlignment = new SimpleMutableObjectValue<>(Pos.CENTER);

    private final MutableFloatValue buttonWidth = new SimpleMutableFloatValue();
    private final MutableFloatValue buttonHeight = new SimpleMutableFloatValue();

    private final MutableValue<Background> background = new SimpleMutableObjectValue<>(Background.fromColor(Color.BLACK));
    private final MutableValue<Background> hoveredBg = new SimpleMutableObjectValue<>(Background.fromColor(Color.BLUE));
    private final MutableValue<Background> pressedBg = new SimpleMutableObjectValue<>(Background.fromColor(Color.fromRGB(0x507fff)));
    private final MutableValue<Background> disableBg = new SimpleMutableObjectValue<>(Background.fromColor(Color.fromRGB(0x7f7f7f)));

    private Text cachedText;

    public Button() {
        text.addChangeListener((ob, o, n) -> {
            rebuildText();
            requestParentLayout();
        });
        font.addChangeListener((ob, o, n) -> {
            rebuildText();
            requestParentLayout();
        });
        textColor.addChangeListener((ob, o, n) -> {
            rebuildText();
            requestParentLayout();
        });
        textAlignment.addChangeListener((observable, oldValue, newValue) -> {
            rebuildText();
            requestParentLayout();
        });
        buttonWidth.addChangeListener((ob, o, n) -> {
            rebuildText();
            requestParentLayout();
        });
        buttonHeight.addChangeListener((ob, o, n) -> {
            rebuildText();
            requestParentLayout();
        });
        pressed.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        disabled.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        hover.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        background.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        pressedBg.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        hoveredBg.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        disableBg.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        padding().setValue(new Insets(0, 5, 5, 5));
    }

    public Button(String text) {
        this();
        this.text.setValue(text);
    }

    private void rebuildText() {
        if (cachedText == null) {
            cachedText = new Text();
        }
        cachedText.text().setValue(text.getValue());
        cachedText.font().setValue(font.getValue());
        cachedText.color().setValue(textColor.getValue());
        cachedText.textAlignment().setValue(textAlignment.getValue());
        cachedText.relocate(padding().getValue().getLeft(), padding().getValue().getTop());
        cachedText.resize(this.prefWidth() - padding().getValue().getLeft() - padding().getValue().getRight(),
                this.prefHeight() - padding().getValue().getTop() - padding().getValue().getBottom());
    }

    @Override
    public float prefWidth() {
        return buttonwidth().get() != 0 ? buttonwidth().get() : cachedText.prefWidth() + padding().getValue().getLeft() + padding().getValue().getRight();
    }

    @Override
    public float prefHeight() {
        return buttonheight().get() != 0 ? buttonheight().get() : cachedText.prefHeight() + padding().getValue().getTop() + padding().getValue().getBottom();
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return ButtonRenderer.INSTANCE;
    }

    public MutableValue<Color> textcolor() {
        return textColor;
    }

    public MutableValue<String> text() {
        return text;
    }

    public MutableValue<Font> font() {
        return font;
    }

    public MutableValue<Pos> textAlignment() {
        return textAlignment;
    }

    public MutableValue<Background> hoverbackground() {
        return hoveredBg;
    }

    public MutableValue<Background> pressbackground() {
        return pressedBg;
    }

    public MutableValue<Background> disabledbackground() {
        return disableBg;
    }

    private Consumer<MouseEvent.MouseClickEvent> onClick;

    @Override
    public void onClick(MouseEvent.MouseClickEvent event) {
        if (onClick != null)
            onClick.accept(event);
    }

    public void setOnClick(Consumer<MouseEvent.MouseClickEvent> onClick) {
        this.onClick = onClick;
    }

    public Text getCachedText() {
        return cachedText;
    }

    public MutableFloatValue buttonwidth() {
        return buttonWidth;
    }

    public MutableFloatValue buttonheight() {
        return buttonHeight;
    }

    public MutableValue<Background> buttonbackground() {
        return background;
    }

    private void handleBackground() {
        if (disabled().get()) {
            super.background().setValue(disabledbackground().getValue());
        } else if (pressed().get()) {
            super.background().setValue(pressbackground().getValue());
        } else if (hover().get()) {
            super.background().setValue(hoverbackground().getValue());
        } else {
            super.background().setValue(buttonbackground().getValue());
        }
    }
}
