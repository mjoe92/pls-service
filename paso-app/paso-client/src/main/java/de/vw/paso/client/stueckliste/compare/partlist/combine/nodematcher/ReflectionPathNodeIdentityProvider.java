package de.vw.paso.client.stueckliste.compare.partlist.combine.nodematcher;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import de.vw.paso.client.util.ReflectionUtil;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public class ReflectionPathNodeIdentityProvider extends AbstractPathNodeIdentityProvider {

    private List<Method> methods;

    @Setter
    private boolean combineAll = true;

    public ReflectionPathNodeIdentityProvider(List<Method> methods) {
        this.methods = methods;
    }

    @Override
    protected String getIdOfNode(EfsElementDTO element) {
        if (element == null) {
            return null;
        }
        if (methods.size() == 1) {
            return invoke(methods.get(0), element);
        }

        if (combineAll) {
            StringBuilder sb = new StringBuilder();
            Iterator<Method> iterator = methods.iterator();
            while (iterator.hasNext()) {
                sb.append(invoke(iterator.next(), element));
                if (iterator.hasNext()) {
                    sb.append("-");
                }
            }
            return sb.toString();
        } else {
            for (Method method : methods) {
                String invoke = invoke(method, element);
                if (StringUtils.isNotEmpty(invoke)) {
                    return invoke;
                }
            }
            return "";
        }
    }

    private String invoke(Method method, EfsElementDTO element) {
        Object result = ReflectionUtil.invoke(method, element);
        if (result == null) {
            return "";
        }
        return result.toString();
    }
}
