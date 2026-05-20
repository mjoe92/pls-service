package de.vw.paso.client.main.statusbar;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public record MainStatusBarEvent(Service<?> service, Task<?> progressTask) { }
