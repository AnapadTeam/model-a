package tech.anapad.modela.hapticsboard.lra.reference;

/**
 * {@link Reference} contains a {@link Column} and a {@link Row}.
 */
public class Reference {

    /**
     * Creates a new {@link Reference} from the given {@link Column} and {@link Row}.
     *
     * @param column the {@link Column}
     * @param row    the {@link Row}
     *
     * @return the {@link Reference}
     */
    public static Reference ref(Column column, Row row) {
        return new Reference(column, row);
    }

    private final Column column;
    private final Row row;

    /**
     * Instantiates a new {@link Reference}.
     *
     * @param column the {@link Column}
     * @param row    the {@link Row}
     */
    private Reference(Column column, Row row) {
        this.column = column;
        this.row = row;
    }

    public Column getColumn() {
        return column;
    }

    public Row getRow() {
        return row;
    }
}
