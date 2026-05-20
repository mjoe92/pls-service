package de.vw.paso.client.control.textfield;

import javafx.scene.input.KeyCode;

public class PasoCustomTextFieldClearable extends PasoCustomTextField<String> {

	public PasoCustomTextFieldClearable(){
		createClearableTextField();
		setClearable(true);
		initKeyEvents();
	}

	private void initKeyEvents() {
		setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ESCAPE)) {
				clear();
			}
		});
	}

}
