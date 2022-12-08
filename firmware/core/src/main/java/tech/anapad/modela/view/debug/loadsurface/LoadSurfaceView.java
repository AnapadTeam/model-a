package tech.anapad.modela.view.debug.loadsurface;

import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import tech.anapad.modela.loadsurface.sample.Sample;
import tech.anapad.modela.util.location.Location;
import tech.anapad.modela.view.AbstractView;

import static java.lang.String.format;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.text.TextAlignment.CENTER;
import static tech.anapad.modela.view.debug.loadsurface.LoadSurfacesView.PRESSURE_COLOR;

/**
 * {@link LoadSurfaceView} represents {@link LoadSurfacesView} data.
 */
public class LoadSurfaceView extends AbstractView {

    private Label dataLabel;
    private Circle pressureCircle;

    /**
     * Instantiates a new {@link LoadSurfaceView}.
     *
     * @param center the center {@link Location}
     */
    public LoadSurfaceView(Location center) {
        nodeGroup.setTranslateX(center.getX());
        nodeGroup.setTranslateY(center.getY());
    }

    /**
     * Updates this {@link LoadSurfaceView}.
     *
     * @param sample the {@link Sample}
     */
    @SuppressWarnings("StringBufferReplaceableByString")
    public void update(Sample sample) {
        final StringBuilder textDataBuilder = new StringBuilder();
        textDataBuilder.append("Index: ").append(sample.getIndex()).append("\n");
        textDataBuilder.append("X: ").append(sample.getLocation().getX()).append(" ");
        textDataBuilder.append("Y: ").append(sample.getLocation().getY()).append("\n");
        textDataBuilder.append("Raw: ").append(format("%,d", sample.getRawSample())).append("\n");
        textDataBuilder.append("Filtered: ").append(format("%,.2f", sample.getFilteredSample())).append("\n");
        textDataBuilder.append("Baseline Filtered: ")
                .append(format("%,.2f", sample.getFilteredBaselineSample())).append("\n");
        textDataBuilder.append("Percent Offset: ")
                .append(format("%,.2f%%", sample.getPercentOffsetSample() * 100));
        dataLabel.setText(textDataBuilder.toString());

        pressureCircle.setRadius(sample.getPercentOffsetSample() * 2000);
    }

    @Override
    public void start() {
        dataLabel = new Label();
        dataLabel.setTextFill(WHITE);
        dataLabel.setTextAlignment(CENTER);
        dataLabel.layoutXProperty().bind(dataLabel.widthProperty().divide(-2));
        dataLabel.layoutYProperty().bind(dataLabel.heightProperty().divide(-2));

        pressureCircle = new Circle(0, 0, 0, PRESSURE_COLOR);

        nodeGroup.getChildren().addAll(pressureCircle, dataLabel);
    }

    @Override
    public void stop() {
        nodeGroup.getChildren().clear();

        dataLabel = null;
        pressureCircle = null;
    }
}
