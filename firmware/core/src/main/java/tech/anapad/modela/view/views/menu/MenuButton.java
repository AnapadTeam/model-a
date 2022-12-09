package tech.anapad.modela.view.views.menu;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tech.anapad.modela.view.ViewController;
import tech.anapad.modela.view.component.button.ContentButton;

import static tech.anapad.modela.view.util.palette.Palette.MODE_PROPERTY;

/**
 * {@link MenuButton} is a {@link ContentButton} for the {@link MenuView}.
 */
public class MenuButton extends ContentButton {

    private final ViewController viewController;
    private final Image whiteIconImage;
    private final Image blackIconImage;

    /**
     * Instantiates a new {@link MenuButton}.
     *
     * @param viewController the {@link ViewController}
     */
    public MenuButton(ViewController viewController) {
        super(11.103, 6.515, 4.0);
        this.viewController = viewController;

        whiteIconImage = new Image("image/icon/white_small.png");
        blackIconImage = new Image("image/icon/black_small.png");
        final ImageView imageView = new ImageView();
        imageView.imageProperty().bind(MODE_PROPERTY.map(mode -> {
            switch (mode) {
                case LIGHT -> {
                    return blackIconImage;
                }
                case DARK -> {
                    return whiteIconImage;
                }
                default -> throw new UnsupportedOperationException();
            }
        }));
        setContent(imageView);
    }

    @Override
    public void onPressDown() {
        super.onPressDown();
    }

    @Override
    public void onPressUp() {
        super.onPressUp();
        viewController.getMenuView().toggle();
    }
}
