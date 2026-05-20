package de.vw.paso.delegate.base;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorCodeUtils {

    public static ErrorCodeParameter[] createParam(String... params) {
        List<ErrorCodeParameter> errorCodeParameters = new ArrayList<>();
        for (int i = 0; i < params.length; i = i + 2) {
            errorCodeParameters.add(new ErrorCodeParameter(params[i], params[i + 1]));
        }
        return errorCodeParameters.toArray(new ErrorCodeParameter[errorCodeParameters.size()]);
    }
}
