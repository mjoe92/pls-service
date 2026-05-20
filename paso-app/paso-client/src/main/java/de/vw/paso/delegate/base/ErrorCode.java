package de.vw.paso.delegate.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.text.StringSubstitutor;

public class ErrorCode implements Serializable {

    private final String key;
    private String message;

    public ErrorCode(String key, String message) {
        this.key = key;
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public String getMessage() {
        return message;
    }

    public void setErrorCodeParameters(ErrorCodeParameter... errorCodeParameters) {
        if (errorCodeParameters == null || errorCodeParameters.length == 0) {
            return;
        }
        replaceHolderInMessage(errorCodeParameters);
    }

    private void replaceHolderInMessage(ErrorCodeParameter... errorCodeParameters) {
        Map<String, String> parameters = new HashMap<String, String>();
        for (int i = 0; i < errorCodeParameters.length; i++) {
            ErrorCodeParameter param = errorCodeParameters[i];
            parameters.put(param.getKey(), param.getValue());
        }
        StringSubstitutor substitutor = new StringSubstitutor(parameters);
        this.message = substitutor.replace(message);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ErrorCode) {
            ErrorCode errObj = (ErrorCode) obj;
            return this.getKey().equals(errObj.getKey()) && this.getMessage().equals(errObj.getMessage());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(key).append(message).toHashCode();
    }
}
