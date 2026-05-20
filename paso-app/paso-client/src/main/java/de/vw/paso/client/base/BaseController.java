package de.vw.paso.client.base;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import de.vw.paso.client.exception.ControllerException;
import de.vw.paso.utility.StringCommonTermsUtil;
import de.vw.paso.utility.StringConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * More concrete implementation of the {@link AbstractController} which should be used together
 * with FXML and {@link FXController}. Can also have sub controllers.
 *
 * @param <C>
 *         the type of the UI
 */
public abstract class BaseController<C> extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(BaseController.class);

    private final Collection<AbstractController> subController;

    protected BaseController() {
        subController = new ArrayList<>();
    }

    public abstract C getControl();

    public abstract Parent getStyleableParent();

    protected void registerSubController(AbstractController controller) {
        subController.add(controller);
    }

    protected void unregisterSubController(AbstractController controller) {
        subController.remove(controller);
    }

    @Override
    public void stop() {
        super.stop();

        for (AbstractController abstractController : subController) {
            abstractController.stop();
        }

        subController.clear();
    }

    public static <C extends BaseController<?>> C load(Class<C> controllerClass) {
        FXController controller = controllerClass.getAnnotation(FXController.class);
        String name = controller.name();

        String fxmlFileName = name + StringConstant.DOT + StringCommonTermsUtil.FXML_LOW_CASE;
        URL fxmlUrl = getFxmlUrl(controllerClass, fxmlFileName);

        return getBaseController(fxmlUrl);
    }

    private static <C extends BaseController<?>> C getBaseController(URL fxmlUrl) throws ControllerException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl, I18N.getBundle());
            fxmlLoader.load();

            C controller = fxmlLoader.getController();
            LOG.trace("FXML and controller {} loaded.", controller.getClass().getSimpleName());

            return controller;
        } catch (Exception e) {
            throw new ControllerException("Could not load fxml file", e);
        }
    }

    private static URL getFxmlUrl(Class<?> controllerClass, String fxmlFileName) throws ControllerException {
        try {
            URL fxmlUrl = controllerClass.getResource(fxmlFileName);
            LOG.trace("FXML file for controller {} found: {}", controllerClass.getSimpleName(), fxmlUrl);

            return fxmlUrl;
        } catch (Exception e) {
            throw new ControllerException("Could not get fxml file", e);
        }
    }
}