package de.vw.paso.client.main.ribbonmenu;

import de.vw.paso.client.base.exception.AbstractClientException;
import de.vw.paso.delegate.base.ErrorCode;

public class RibbonMenuException extends AbstractClientException {

	public static final ErrorCode EC_LOAD_RIBBONMENU = new ErrorCode("EC_LOAD_RIBBONMENU", "RibbonMenu could not be loaded");

	public RibbonMenuException(Throwable cause, ErrorCode errorCode) {
		super(cause, errorCode);
	}
}
