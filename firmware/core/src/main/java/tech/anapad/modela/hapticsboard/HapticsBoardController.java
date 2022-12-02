package tech.anapad.modela.hapticsboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;
import tech.anapad.modela.hapticsboard.hapticmotorcontroller.HapticMotorController;
import tech.anapad.modela.hapticsboard.ioportexpander.IOPortExpander;
import tech.anapad.modela.hapticsboard.lra.LRA;
import tech.anapad.modela.hapticsboard.lra.location.Location;
import tech.anapad.modela.hapticsboard.lra.reference.Column;
import tech.anapad.modela.hapticsboard.lra.reference.Reference;
import tech.anapad.modela.hapticsboard.lra.reference.Row;
import tech.anapad.modela.util.i2c.I2CNative;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.List.of;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static tech.anapad.modela.hapticsboard.lra.location.Location.loc;
import static tech.anapad.modela.hapticsboard.lra.reference.Column.A;
import static tech.anapad.modela.hapticsboard.lra.reference.Column.B;
import static tech.anapad.modela.hapticsboard.lra.reference.Column.C;
import static tech.anapad.modela.hapticsboard.lra.reference.Column.D;
import static tech.anapad.modela.hapticsboard.lra.reference.Column.E;
import static tech.anapad.modela.hapticsboard.lra.reference.Column.F;
import static tech.anapad.modela.hapticsboard.lra.reference.Column.G;
import static tech.anapad.modela.hapticsboard.lra.reference.Column.H;
import static tech.anapad.modela.hapticsboard.lra.reference.Column.I;
import static tech.anapad.modela.hapticsboard.lra.reference.Column.J;
import static tech.anapad.modela.hapticsboard.lra.reference.Column.K;
import static tech.anapad.modela.hapticsboard.lra.reference.Column.L;
import static tech.anapad.modela.hapticsboard.lra.reference.Column.M;
import static tech.anapad.modela.hapticsboard.lra.reference.Reference.ref;
import static tech.anapad.modela.hapticsboard.lra.reference.Row._1;
import static tech.anapad.modela.hapticsboard.lra.reference.Row._2;
import static tech.anapad.modela.hapticsboard.lra.reference.Row._3;
import static tech.anapad.modela.hapticsboard.lra.reference.Row._4;
import static tech.anapad.modela.hapticsboard.lra.reference.Row._5;

/**
 * {@link HapticsBoardController} is a controller for the haptics board.
 */
public class HapticsBoardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HapticsBoardController.class);
    private static final int I2C_DEVICE_INDEX = 4;

    private final ModelA modelA;

    private Integer i2cFD;
    private List<IOPortExpander> ioPortExpanders;
    private Map<Integer, IOPortExpander> ioPortExpandersOfIndexes;
    private List<LRA> lraList;
    private Map<Column, Map<Row, LRA>> lraReferenceMap;
    private HapticMotorController hapticMotorController;

    /**
     * Instantiates a new {@link HapticsBoardController}.
     *
     * @param modelA the {@link ModelA} instance
     */
    public HapticsBoardController(ModelA modelA) {
        this.modelA = modelA;
    }

    /**
     * Starts {@link HapticsBoardController}.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void start() throws Exception {
        LOGGER.info("Starting HapticsBoardController...");

        LOGGER.info("Starting I2C-{}...", I2C_DEVICE_INDEX);
        i2cFD = I2CNative.start(I2C_DEVICE_INDEX);
        LOGGER.info("Started I2C-{}...", I2C_DEVICE_INDEX);

        LOGGER.info("Configuring IO port expanders (the TCA9534 chips)...");
        ioPortExpanders = of(
                new IOPortExpander(i2cFD, 1, (short) 0x20),
                new IOPortExpander(i2cFD, 2, (short) 0x21),
                new IOPortExpander(i2cFD, 3, (short) 0x22),
                new IOPortExpander(i2cFD, 4, (short) 0x23),
                new IOPortExpander(i2cFD, 5, (short) 0x24));
        ioPortExpandersOfIndexes = ioPortExpanders.stream()
                .collect(toUnmodifiableMap(IOPortExpander::getIndex, identity()));
        for (IOPortExpander ioPortExpander : ioPortExpanders) {
            ioPortExpander.configure();
            LOGGER.info("Configured IO port expander: {}", ioPortExpander.getIndex());
        }
        LOGGER.info("Configured IO port expanders.");

        // Create LRA array
        lraList = of(
                new LRA(ioPortExpandersOfIndexes.get(1), 0, ref(A, _1), loc(10.873, 69.165)),
                new LRA(ioPortExpandersOfIndexes.get(1), 1, ref(C, _1), loc(58.798, 69.165)),
                new LRA(ioPortExpandersOfIndexes.get(2), 0, ref(E, _1), loc(106.724, 69.165)),
                new LRA(ioPortExpandersOfIndexes.get(3), 0, ref(G, _1), loc(154.65, 69.165)),
                new LRA(ioPortExpandersOfIndexes.get(4), 0, ref(I, _1), loc(202.576, 69.165)),
                new LRA(ioPortExpandersOfIndexes.get(5), 1, ref(K, _1), loc(250.502, 69.165)),
                new LRA(ioPortExpandersOfIndexes.get(5), 0, ref(M, _1), loc(298.427, 69.165)),
                new LRA(ioPortExpandersOfIndexes.get(1), 2, ref(B, _2), loc(34.836, 55.33)),
                new LRA(ioPortExpandersOfIndexes.get(2), 1, ref(D, _2), loc(82.761, 55.33)),
                new LRA(ioPortExpandersOfIndexes.get(3), 1, ref(F, _2), loc(130.687, 55.33)),
                new LRA(ioPortExpandersOfIndexes.get(3), 2, ref(H, _2), loc(178.613, 55.33)),
                new LRA(ioPortExpandersOfIndexes.get(4), 1, ref(J, _2), loc(226.539, 55.33)),
                new LRA(ioPortExpandersOfIndexes.get(5), 2, ref(L, _2), loc(274.464, 55.33)),
                new LRA(ioPortExpandersOfIndexes.get(1), 3, ref(A, _3), loc(10.873, 41.495)),
                new LRA(ioPortExpandersOfIndexes.get(2), 2, ref(C, _3), loc(58.798, 41.495)),
                new LRA(ioPortExpandersOfIndexes.get(2), 3, ref(E, _3), loc(106.724, 41.495)),
                new LRA(ioPortExpandersOfIndexes.get(3), 3, ref(G, _3), loc(154.65, 41.495)),
                new LRA(ioPortExpandersOfIndexes.get(4), 3, ref(I, _3), loc(202.576, 41.495)),
                new LRA(ioPortExpandersOfIndexes.get(4), 2, ref(K, _3), loc(250.502, 41.495)),
                new LRA(ioPortExpandersOfIndexes.get(5), 3, ref(M, _3), loc(298.427, 41.495)),
                new LRA(ioPortExpandersOfIndexes.get(1), 4, ref(B, _4), loc(34.836, 27.66)),
                new LRA(ioPortExpandersOfIndexes.get(2), 4, ref(D, _4), loc(82.761, 27.66)),
                new LRA(ioPortExpandersOfIndexes.get(3), 4, ref(F, _4), loc(130.687, 27.66)),
                new LRA(ioPortExpandersOfIndexes.get(3), 5, ref(H, _4), loc(178.613, 27.66)),
                new LRA(ioPortExpandersOfIndexes.get(4), 4, ref(J, _4), loc(226.539, 27.66)),
                new LRA(ioPortExpandersOfIndexes.get(5), 4, ref(L, _4), loc(274.464, 27.66)),
                new LRA(ioPortExpandersOfIndexes.get(1), 5, ref(A, _5), loc(10.873, 13.825)),
                new LRA(ioPortExpandersOfIndexes.get(1), 6, ref(C, _5), loc(58.798, 13.825)),
                new LRA(ioPortExpandersOfIndexes.get(2), 5, ref(E, _5), loc(106.724, 13.825)),
                new LRA(ioPortExpandersOfIndexes.get(3), 6, ref(G, _5), loc(154.65, 13.825)),
                new LRA(ioPortExpandersOfIndexes.get(4), 5, ref(I, _5), loc(202.576, 13.825)),
                new LRA(ioPortExpandersOfIndexes.get(5), 6, ref(K, _5), loc(250.502, 13.825)),
                new LRA(ioPortExpandersOfIndexes.get(5), 5, ref(M, _5), loc(298.427, 13.825)));
        lraReferenceMap = stream(Column.values()).collect(toUnmodifiableMap(column -> column, column ->
                stream(Row.values()).collect(toUnmodifiableMap(row -> row, row -> lraList.stream()
                        .filter(lra -> lra.getReference().getColumn() == column && lra.getReference().getRow() == row)
                        .findFirst().orElseThrow()))));

        LOGGER.info("Configuring the haptic motor controller (the DRV2605L)...");
        hapticMotorController = new HapticMotorController(i2cFD);
        hapticMotorController.configure();
        LOGGER.info("Configured the haptic motor controller.");

        LOGGER.info("Started HapticsBoardController.");
    }

    /**
     * Stops {@link HapticsBoardController}.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void stop() throws Exception {
        LOGGER.info("Stopping HapticsBoardController...");

        if (hapticMotorController != null) {
            LOGGER.info("Stopping haptic motor controller...");
            hapticMotorController.setRTPValue((byte) 0);
            hapticMotorController.setRTPMode(false);
            LOGGER.info("Stopped haptic motor controller.");
        }

        if (i2cFD != null) {
            LOGGER.info("Stopping I2C-{}...", I2C_DEVICE_INDEX);
            I2CNative.stop(i2cFD);
            LOGGER.info("Stopped I2C-{}...", I2C_DEVICE_INDEX);
        }

        LOGGER.info("Stopped HapticsBoardController.");
    }

    /**
     * Gets the distances of the {@link LRA}s in the {@link #lraList} from the given {@link Location}.
     *
     * @param location the {@link Location} to calculate distances from
     *
     * @return a {@link Map} with the key as the {@link LRA} and the value as the distance
     */
    public Map<LRA, Double> lraDistancesFrom(Location location) {
        final Map<LRA, Double> distancesOfLRAs = new HashMap<>();
        for (LRA lra : lraList) {
            distancesOfLRAs.put(lra, lra.getActiveAreaLocation().distance(location));
        }
        return distancesOfLRAs;
    }

    /**
     * Gets a {@link LRA} for the given {@link Reference}.
     *
     * @param reference the {@link Reference}
     *
     * @return the {@link LRA}
     */
    public LRA getLRA(Reference reference) {
        return lraReferenceMap.get(reference.getColumn()).get(reference.getRow());
    }

    public List<IOPortExpander> getIOPortExpanders() {
        return ioPortExpanders;
    }

    public Map<Integer, IOPortExpander> getIOPortExpandersOfIndexes() {
        return ioPortExpandersOfIndexes;
    }

    public List<LRA> getLRAList() {
        return lraList;
    }

    public Map<Column, Map<Row, LRA>> getLRAReferenceMap() {
        return lraReferenceMap;
    }

    public HapticMotorController getHapticMotorController() {
        return hapticMotorController;
    }
}
