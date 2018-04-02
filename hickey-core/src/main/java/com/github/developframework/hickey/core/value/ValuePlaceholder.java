package com.github.developframework.hickey.core.value;

import com.github.developframework.expression.ExpressionUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 值占位符
 * @author qiuzhenhao
 */
@NoArgsConstructor
@Getter
@Setter
public final class ValuePlaceholder {

    private String prefix = "@{";

    private String suffix = "}";

    public ValuePlaceholder(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public boolean hasPlaceholder(String targetValue) {
        return targetValue.startsWith(prefix) && targetValue.endsWith(suffix);
    }

    public Object replace(Object data, String targetValue) {
        String expressionString = getExpressionString(targetValue);
        return ExpressionUtils.getValue(data, expressionString);
    }

    private String getExpressionString(String targetValue) {
        return targetValue.substring(2, targetValue.length() - 1);
    }
}
