package tech.anapad.modela.view.views.menu;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import tech.anapad.modela.view.ViewController;
import tech.anapad.modela.view.component.button.Button;

import static tech.anapad.modela.view.ViewController.VIEW_PIXEL_DENSITY;
import static tech.anapad.modela.view.util.geometry.GeometryConstants.RECTANGLE_ARC_SIZE;
import static tech.anapad.modela.view.util.palette.Palette.ACCENT_COLOR_PROPERTY;
import static tech.anapad.modela.view.util.palette.Palette.BACKGROUND_COLOR_PROPERTY;
import static tech.anapad.modela.view.util.palette.Palette.MODE_PROPERTY;

/**
 * {@link MenuButton} is a {@link Button} for the {@link MenuView}.
 */
public class MenuButton extends Button {

    private final ViewController viewController;
    private final Rectangle background;
    private final Image whiteIconImage;
    private final Image blackIconImage;

    /**
     * Instantiates a new {@link MenuButton}.
     *
     * @param viewController the {@link ViewController}
     */
    public MenuButton(ViewController viewController) {
        this.viewController = viewController;

        background = new Rectangle();
        background.setWidth(11.103 * VIEW_PIXEL_DENSITY);
        background.setHeight(6.515 * VIEW_PIXEL_DENSITY);
        background.setArcHeight(RECTANGLE_ARC_SIZE);
        background.setArcWidth(RECTANGLE_ARC_SIZE);
        background.fillProperty().bind(BACKGROUND_COLOR_PROPERTY);

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
        getChildren().add(new StackPane(background, imageView));
    }

    @Override
    public void onPressDown() {
        super.onPressDown();

        background.fillProperty().unbind();
        background.fillProperty().set(ACCENT_COLOR_PROPERTY.get());
    }

    @Override
    public void onPressUp() {
        super.onPressUp();

        background.fillProperty().unbind();
        background.fillProperty().bind(BACKGROUND_COLOR_PROPERTY);

        if (viewController.getActiveView() == viewController.getMenuView()) {
            viewController.setActiveView(viewController.getForceHapticsView());
        } else if (viewController.getActiveView() == viewController.getForceHapticsView()) {
            viewController.setActiveView(viewController.getLoadSurfacesView());
        } else if (viewController.getActiveView() == viewController.getLoadSurfacesView()) {
            viewController.setActiveView(viewController.getTouchesView());
        } else if (viewController.getActiveView() == viewController.getTouchesView()) {
            viewController.setActiveView(viewController.getMenuView());
        }
    }
}
