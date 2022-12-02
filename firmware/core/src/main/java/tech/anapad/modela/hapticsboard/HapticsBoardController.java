package tech.anapad.modela.hapticsboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;
import tech.anapad.modela.hapticsboard.ioportexpander.IOPortExpander;
import tech.anapad.modela.hapticsboard.lra.LRA;
import tech.anapad.modela.hapticsboard.lra.LRAReferenceColumn;
import tech.anapad.modela.hapticsboard.lra.LRAReferenceRow;
import tech.anapad.modela.util.i2c.I2CNative;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.List.of;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceColumn.A;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceColumn.B;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceColumn.C;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceColumn.D;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceColumn.E;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceColumn.F;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceColumn.G;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceColumn.H;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceColumn.I;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceColumn.J;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceColumn.K;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceColumn.L;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceColumn.M;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceRow._1;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceRow._2;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceRow._3;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceRow._4;
import static tech.anapad.modela.hapticsboard.lra.LRAReferenceRow._5;
import static tech.anapad.modela.util.i2c.I2CNative.writeRegisterByte;
import static tech.anapad.modela.util.i2c.I2CUtil.resetRegisterBit;
import static tech.anapad.modela.util.i2c.I2CUtil.setRegisterBit;

/**
 * {@link HapticsBoardController} is a controller for the haptics board.
 */
public class HapticsBoardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HapticsBoardController.class);
    private static final int I2C_DEVICE_INDEX = 4;
    private static final short DRV2605L_I2C_ADDRESS = 0x5A;
    private static final List<IOPortExpander> IO_PORT_EXPANDERS;
    private static final Map<Integer, IOPortExpander> IO_PORT_EXPANDERS_OF_INDEXES;
    private static final List<LRA> LRA_LIST;
    private static final Map<LRAReferenceColumn, Map<LRAReferenceRow, LRA>> LRA_REFERENCE_MAP;

    static {
        IO_PORT_EXPANDERS = of(
                new IOPortExpander(1, (short) 0x20),
                new IOPortExpander(2, (short) 0x21),
                new IOPortExpander(3, (short) 0x22),
                new IOPortExpander(4, (short) 0x23),
                new IOPortExpander(5, (short) 0x24));
        IO_PORT_EXPANDERS_OF_INDEXES = IO_PORT_EXPANDERS.stream()
                .collect(toUnmodifiableMap(IOPortExpander::getIndex, identity()));

        LRA_LIST = of(
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(1), 0, A, _1, 10.873, 69.165),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(1), 1, C, _1, 58.798, 69.165),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(2), 0, E, _1, 106.724, 69.165),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(3), 0, G, _1, 154.65, 69.165),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(4), 0, I, _1, 202.576, 69.165),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(5), 1, K, _1, 250.502, 69.165),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(5), 0, M, _1, 298.427, 69.165),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(1), 2, B, _2, 34.836, 55.33),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(2), 1, D, _2, 82.761, 55.33),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(3), 1, F, _2, 130.687, 55.33),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(3), 2, H, _2, 178.613, 55.33),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(4), 1, J, _2, 226.539, 55.33),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(5), 2, L, _2, 274.464, 55.33),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(1), 3, A, _3, 10.873, 41.495),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(2), 2, C, _3, 58.798, 41.495),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(2), 3, E, _3, 106.724, 41.495),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(3), 3, G, _3, 154.65, 41.495),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(4), 3, I, _3, 202.576, 41.495),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(4), 2, K, _3, 250.502, 41.495),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(5), 3, M, _3, 298.427, 41.495),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(1), 4, B, _4, 34.836, 27.66),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(2), 4, D, _4, 82.761, 27.66),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(3), 4, F, _4, 130.687, 27.66),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(3), 5, H, _4, 178.613, 27.66),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(4), 4, J, _4, 226.539, 27.66),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(5), 4, L, _4, 274.464, 27.66),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(1), 5, A, _5, 10.873, 13.825),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(1), 6, C, _5, 58.798, 13.825),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(2), 5, E, _5, 106.724, 13.825),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(3), 6, G, _5, 154.65, 13.825),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(4), 5, I, _5, 202.576, 13.825),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(5), 6, K, _5, 250.502, 13.825),
                new LRA(IO_PORT_EXPANDERS_OF_INDEXES.get(5), 5, M, _5, 298.427, 13.825));
        LRA_REFERENCE_MAP = stream(LRAReferenceColumn.values()).collect(toUnmodifiableMap(column -> column, column ->
                stream(LRAReferenceRow.values()).collect(toUnmodifiableMap(row -> row, row -> {
                    // Brute force search is fine here
                    for (LRA lra : LRA_LIST) {
                        if (lra.getReferenceColumn() == column && lra.getReferenceRow() == row) {
                            return lra;
                        }
                    }
                    throw new IllegalStateException();
                }))));
    }

    private final ModelA modelA;

    private Integer i2cFD;

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
        for (IOPortExpander ioPortExpander : IO_PORT_EXPANDERS) {
            ioPortExpander.configure(i2cFD);
            LOGGER.info("Configured IO port expander: {}", ioPortExpander.getIndex());
        }
        LOGGER.info("Configured IO port expanders.");

        LOGGER.info("Configuring the haptic motor controller (the DRV2605L)...");
        writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x01, (byte) 0x05, true);
        LOGGER.info("Set RTP mode.");
        writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x02, (byte) 0x00, true);
        LOGGER.info("Set RTP register to zero.");
        writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x17, (byte) 0xFF, true);
        LOGGER.info("Set overdrive voltage-clamp to max value.");
        setRegisterBit(i2cFD, DRV2605L_I2C_ADDRESS, (short) 0x1A, true, 7);
        LOGGER.info("Set LRA mode.");
        resetRegisterBit(i2cFD, DRV2605L_I2C_ADDRESS, (short) 0x1D, true, 0);
        LOGGER.info("Set open-loop LRA mode.");
        // No need to set DRV2605 resonant frequency as it defaults to 205Hz which is what the resonant frequency
        // of the LRAs on the haptics board are.
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

        if (i2cFD != null) {
            writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x02, (byte) 0x00, true);
            LOGGER.info("Set RTP register to zero.");

            LOGGER.info("Stopping I2C-{}...", I2C_DEVICE_INDEX);
            I2CNative.stop(i2cFD);
            LOGGER.info("Stopped I2C-{}...", I2C_DEVICE_INDEX);
        }

        LOGGER.info("Stopped HapticsBoardController.");
    }
}
