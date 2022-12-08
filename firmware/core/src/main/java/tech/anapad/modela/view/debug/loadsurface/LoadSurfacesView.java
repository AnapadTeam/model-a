package tech.anapad.modela.view.debug.loadsurface;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import tech.anapad.modela.loadsurface.adc.ADC;
import tech.anapad.modela.loadsurface.sample.Sample;
import tech.anapad.modela.loadsurface.sample.SampleResult;
import tech.anapad.modela.view.AbstractView;
import tech.anapad.modela.view.ViewController;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static javafx.application.Platform.runLater;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.rgb;
import static javafx.scene.text.TextAlignment.CENTER;
import static tech.anapad.modela.view.ViewController.VIEW_HEIGHT;
import static tech.anapad.modela.view.ViewController.VIEW_WIDTH;

/**
 * {@link LoadSurfacesView} is an {@link AbstractView} for viewing load surface data as a debugging measure.
 */
public class LoadSurfacesView extends AbstractView {

    /**
     * The {@link Color} of pressure for these views.
     */
    public static final Color PRESSURE_COLOR = rgb(54, 124, 224);

    private final ViewController viewController;
    private final Consumer<SampleResult> sampleResultHandler;

    private Rectangle averageRectangle;
    private Label averageLabel;
    private List<LoadSurfaceView> loadSurfaceViews;
    private long lastUpdateMillis;

    /**
     * Instantiates a new {@link LoadSurfacesView}.
     *
     * @param viewController the {@link ViewController}
     */
    public LoadSurfacesView(ViewController viewController) {
        this.viewController = viewController;
        sampleResultHandler = this::handleSampleResult;
        nodeGroup.setClip(new Rectangle(VIEW_WIDTH, VIEW_HEIGHT));
    }

    @Override
    public void start() {
        nodeGroup.getChildren().add(new Rectangle(VIEW_WIDTH, VIEW_HEIGHT, BLACK));

        final double centerX = VIEW_WIDTH / 2.0;
        final double centerY = VIEW_HEIGHT / 2.0;

        averageRectangle = new Rectangle(0, 25);
        averageRectangle.layoutXProperty().bind(averageRectangle.widthProperty().divide(-2));
        averageRectangle.layoutYProperty().bind(averageRectangle.heightProperty().divide(-2));
        averageRectangle.setTranslateX(centerX);
        averageRectangle.setTranslateY(centerY);
        averageRectangle.setFill(PRESSURE_COLOR);
        nodeGroup.getChildren().add(averageRectangle);

        averageLabel = new Label();
        averageLabel.setTextAlignment(CENTER);
        averageLabel.layoutXProperty().bind(averageLabel.widthProperty().divide(-2));
        averageLabel.layoutYProperty().bind(averageLabel.heightProperty().divide(-2));
        averageLabel.setTranslateX(centerX);
        averageLabel.setTranslateY(centerY);
        averageLabel.setTextFill(WHITE);
        nodeGroup.getChildren().add(averageLabel);

        loadSurfaceViews = new ArrayList<>();
        for (ADC adc : viewController.getModelA().getLoadSurfaceController().getADCsOfChannels().values()) {
            final LoadSurfaceView loadSurfaceView = new LoadSurfaceView(adc.getLoadSurfaceLocation());
            loadSurfaceView.start();
            loadSurfaceViews.add(loadSurfaceView);
            nodeGroup.getChildren().add(loadSurfaceView.getNodeGroup());
        }

        viewController.getModelA().getLoadSurfaceController().getSampleResultListeners()
                .add(sampleResult -> runLater(() -> handleSampleResult(sampleResult)));
    }

    @Override
    public void stop() {
        viewController.getModelA().getLoadSurfaceController().getSampleResultListeners().remove(sampleResultHandler);

        loadSurfaceViews.forEach(LoadSurfaceView::stop);
        loadSurfaceViews = null;

        averageLabel = null;

        nodeGroup.getChildren().clear();
    }

    /**
     * Handles a {@link SampleResult}.
     *
     * @param sampleResult the {@link SampleResult}
     */
    private void handleSampleResult(SampleResult sampleResult) {
        long currentMillis = currentTimeMillis();
        if (currentMillis - lastUpdateMillis > 50) {
            lastUpdateMillis = currentMillis;

            averageLabel.setText(format("Average Percent Offset: %,.2f%%",
                    sampleResult.getPercentOffsetSampleAverage() * 100));
            averageRectangle.setWidth(sampleResult.getPercentOffsetSampleAverage() * 4000);

            for (Sample sample : sampleResult.getSamples()) {
                loadSurfaceViews.get(sample.getIndex() - 1).update(sample);
            }
        }
    }
}
