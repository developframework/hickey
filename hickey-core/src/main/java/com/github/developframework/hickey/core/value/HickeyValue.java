package com.github.developframework.hickey.core.value;

/**
 * @author qiuzhenhao
 */
public class HickeyValue {

    private ValuePlaceholder valuePlaceholder;

    private String targetValue;

    public HickeyValue(ValuePlaceholder valuePlaceholder, String targetValue) {
        this.valuePlaceholder = valuePlaceholder;
        this.targetValue = targetValue;
    }

    public Object getValue(Object data) {
        if (valuePlaceholder.hasPlaceholder(targetValue)) {
            return valuePlaceholder.replace(data, targetValue);
        }
        return targetValue;
    }
}
