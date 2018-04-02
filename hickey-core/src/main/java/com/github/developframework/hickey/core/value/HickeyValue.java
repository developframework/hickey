package com.github.developframework.hickey.core.value;

import com.github.developframework.expression.ExpressionUtils;
import com.github.developframework.hickey.core.exception.HickeyValueException;
import com.github.developframework.toolkit.base.Sugar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 可替换值对象
 * @author qiuzhenhao
 */
public class HickeyValue {

    private String targetValue;

    public HickeyValue(String targetValue) {
        this.targetValue = targetValue;
    }

    public String getValue(Object data) {
        Pattern pattern = Pattern.compile("@\\{.+?\\}");
        Matcher matcher = pattern.matcher(targetValue);
        String result = targetValue;
        while (matcher.find()) {
            String matchString = matcher.group();
            String matchValue = getExpressionString(matchString);
            Object value = ExpressionUtils.getValue(data, matchValue);
            result = result.replace(matchString, Sugar.use(value, new HickeyValueException(matchValue)).toString());
        }
        return result;
    }

    private String getExpressionString(String targetValue) {
        return targetValue.substring(2, targetValue.length() - 1);
    }
}
