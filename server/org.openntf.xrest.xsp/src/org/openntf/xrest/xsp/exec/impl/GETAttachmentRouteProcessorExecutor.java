package org.openntf.xrest.xsp.exec.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.datatypes.MimeMapJsonTypeProcessor;
import org.openntf.xrest.xsp.exec.datacontainer.AttachmentDataContainer;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.StreamUtil;
import com.ibm.commons.util.io.json.JsonException;

import lotus.domino.EmbeddedObject;
import lotus.domino.MIMEEntity;
import lotus.domino.MIMEHeader;
import lotus.domino.NotesException;
import lotus.domino.Stream;

public class GETAttachmentRouteProcessorExecutor extends AbstractRouteProcessorExecutor {

	public GETAttachmentRouteProcessorExecutor(final String path) {
		super(path);
	}

	@Override
	protected void submitValues(Context context, RouteProcessor routeProcessor, DataContainer<?> dataContainer) throws IOException, JsonException, ExecutorException {
		HttpServletResponse response = context.getResponse();
		AttachmentDataContainer<?> adc = (AttachmentDataContainer<?>) dataContainer;
		try {
			String mimeType = GuessMimeType(adc);
			response.setContentType(mimeType);
			response.addHeader("Content-Disposition", "attachment;filename=\"" + adc.getFileName() + "\"");
			OutputStream out = response.getOutputStream();
			if (adc.isMime()) {
				Stream outStream = context.getSession().createStream();
				MIMEEntity entity = (MIMEEntity) adc.getData();
				entity.getContentAsBytes(outStream, true);
				outStream.getContents(out);
				outStream.close();
				out.close();
			} else {
				InputStream is = getInputStream(adc);
				StreamUtil.copyStream(is, out);
				out.close();
				is.close();
				
			}
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
	private String GuessMimeType(AttachmentDataContainer<?> adc) {
		if (adc.isMime()) {
			MIMEEntity entity = (MIMEEntity) adc.getData();
			try {
			MIMEHeader mimeHeader = entity.getNthHeader(MimeMapJsonTypeProcessor.CONTENT_TYPE);
			if (mimeHeader != null) {
				String contenType = mimeHeader.getHeaderVal();
				mimeHeader.recycle();
				return contenType;
			}
			} catch(Exception e) {
				e.printStackTrace();
			}
		} 
		String fromUrl = URLConnection.guessContentTypeFromName( adc.getFileName());
		return StringUtil.isEmpty(fromUrl) ? "application/octet-stream": fromUrl;
	}
}
