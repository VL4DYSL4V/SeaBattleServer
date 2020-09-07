package enums;

public enum FieldConstraints {

    FIELD_WIDTH(10), FIELD_HEIGHT(10), MAX_X(9), MAX_Y(9), MIN_X(0), MIN_Y(0);

    private final Integer value;

    FieldConstraints(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
