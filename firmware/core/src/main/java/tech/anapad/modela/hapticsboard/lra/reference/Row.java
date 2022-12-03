package tech.anapad.modela.hapticsboard.lra.reference;

/**
 * {@link Row} represents a column reference for an LRA.
 */
public enum Row {

    _1,
    _2,
    _3,
    _4,
    _5;

    @Override
    public String toString() {
        return super.toString().replace("_", "");
    }
}
