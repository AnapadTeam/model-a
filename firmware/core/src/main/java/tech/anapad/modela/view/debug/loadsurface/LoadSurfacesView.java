package tech.anapad.modela.view.debug.loadsurface;

import javafx.scene.control.Label;
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
import static javafx.scene.text.TextAlignment.CENTER;
import static tech.anapad.modela.view.ViewController.VIEW_HEIGHT;
import static tech.anapad.modela.view.ViewController.VIEW_WIDTH;

/**
 * {@link LoadSurfacesView} is an {@link AbstractView} for viewing load surface data as a debugging measure.
 */
public class LoadSurfacesView extends AbstractView {

    private final ViewController viewController;
    private final Consumer<SampleResult> sampleResultConsumer;

    private Label percentOffsetSampleAverageLabel;
    private List<LoadSurfaceView> loadSurfaceViews;
    private long lastUpdateMillis;

    /**
     * Instantiates a new {@link LoadSurfacesView}.
     *
     * @param viewController the {@link ViewController}
     */
    public LoadSurfacesView(ViewController viewController) {
        this.viewController = viewController;
        sampleResultConsumer = this::handleSampleResult;
        nodeGroup.setClip(new Rectangle(VIEW_WIDTH, VIEW_HEIGHT));
    }

    @Override
    public void start() {
        nodeGroup.getChildren().add(new Rectangle(VIEW_WIDTH, VIEW_HEIGHT, BLACK));

        percentOffsetSampleAverageLabel = new Label();
        percentOffsetSampleAverageLabel.setTextFill(WHITE);
        percentOffsetSampleAverageLabel.setTextAlignment(CENTER);
        percentOffsetSampleAverageLabel.layoutXProperty().bind(
                percentOffsetSampleAverageLabel.widthProperty().divide(-2));
        percentOffsetSampleAverageLabel.layoutYProperty().bind(
                percentOffsetSampleAverageLabel.heightProperty().divide(-2));
        percentOffsetSampleAverageLabel.setTranslateX(VIEW_WIDTH / 2.0);
        percentOffsetSampleAverageLabel.setTranslateY(VIEW_HEIGHT / 2.0);
        nodeGroup.getChildren().add(percentOffsetSampleAverageLabel);

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
        viewController.getModelA().getLoadSurfaceController().getSampleResultListeners().remove(sampleResultConsumer);

        loadSurfaceViews.forEach(LoadSurfaceView::stop);
        loadSurfaceViews = null;

        percentOffsetSampleAverageLabel = null;

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

            percentOffsetSampleAverageLabel.setText(format("Average Percent Offset: %,.2f%%",
                    sampleResult.getPercentOffsetSampleAverage() * 100));
            for (Sample sample : sampleResult.getSamples()) {
                loadSurfaceViews.get(sample.getIndex() - 1).update(sample);
            }
        }
    }
}
