package org.openntf.xrest.xsp.exec.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.datacontainer.AttachmentDataContainer;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.StreamUtil;
import com.ibm.commons.util.io.json.JsonException;

import lotus.domino.EmbeddedObject;
import lotus.domino.MIMEEntity;
import lotus.domino.NotesException;

public class GETAttachmentRouteProcessorExecutor extends AbstractRouteProcessorExecutor {

	public GETAttachmentRouteProcessorExecutor(final String path) {
		super(path);
	}

	@Override
	protected void submitValues(Context context, RouteProcessor routeProcessor, DataContainer<?> dataContainer) throws IOException, JsonException, ExecutorException {
		HttpServletResponse response = context.getResponse();
		AttachmentDataContainer<?> adc = (AttachmentDataContainer<?>) dataContainer;
		try {
			String mimeType = URLConnection.guessContentTypeFromName( adc.getFileName());
			InputStream is = getInputStream(adc);
			response.setContentType(StringUtil.isEmpty(mimeType) ?"application/octet-stream": mimeType);
			response.addHeader("Content-Disposition", "attachment;filename=\"" + adc.getFileName() + "\"");
			OutputStream out = response.getOutputStream();
			StreamUtil.copyStream(is, out);
			out.close();
			is.close();
		} catch (Exception e) {
			throw new ExecutorException(500, "Runtime Error: " + e.getMessage(), e, path, "presubmit");
		}
	}

	@Override
	protected void preSubmitValues(Context context, RouteProcessor routeProcessor, DataContainer<?> dataContainer) throws ExecutorException {
	}

	@Override
	protected void executeMethodeSpecific(final Context context, final DataContainer<?> container, RouteProcessor routeProcessor) throws ExecutorException {
	}

	private InputStream getInputStream(final AttachmentDataContainer<?> adc) throws NotesException {
		if (adc.isMime()) {
			return ((MIMEEntity) adc.getData()).getInputStream();
		} else {
			return ((EmbeddedObject) adc.getData()).getInputStream();
		}
	}
}
