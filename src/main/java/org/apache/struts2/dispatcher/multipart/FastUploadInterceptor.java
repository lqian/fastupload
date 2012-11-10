/*
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.dispatcher.multipart;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.fastupload.MultiPartFile;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public class FastUploadInterceptor extends AbstractInterceptor {

	protected static final Logger LOG = LoggerFactory
			.getLogger(FastUploadInterceptor.class);

	protected boolean useActionMessageBundle;

	// private String parseType;

	private static final String DEFAULT_MESSAGE = "no.message.found";

	/**
	 * 
	 */
	private static final long serialVersionUID = 8587234903836481551L;

	// @Inject("fastupload.parse.type")
	// public void setParseType(String parseType) {
	// this.parseType = parseType;
	// }

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		ActionContext ac = invocation.getInvocationContext();

		HttpServletRequest request = (HttpServletRequest) ac
				.get(ServletActionContext.HTTP_REQUEST);

		if (!(request instanceof MultiPartRequestWrapper)) {
			if (LOG.isDebugEnabled()) {
				ActionProxy proxy = invocation.getProxy();
				LOG.debug(getTextMessage(
						"struts.messages.bypass.request",
						new Object[] { proxy.getNamespace(),
								proxy.getActionName() }, ac.getLocale()));
			}

			return invocation.invoke();
		}

		ValidationAware validation = null;

		Object action = invocation.getAction();

		if (action instanceof ValidationAware) {
			validation = (ValidationAware) action;
		}

		FastUploadMultiPartRequestWrapper multiWrapper = (FastUploadMultiPartRequestWrapper) request;

		if (multiWrapper.getErrors() != null
				&& multiWrapper.getErrors().size() > 0) {
			for (Object header : multiWrapper.getErrors()) {
				if (validation != null) {
					validation.addActionError(header.toString());
				}

				if (LOG.isWarnEnabled()) {
					LOG.warn(header.toString());
				}
			}
		}

		// process all parameter against accepted MultiPartData object
		Enumeration<String> paramNames = multiWrapper.getParameterNames();
		Map<String, Object> requestParams = ac.getParameters();
		if (paramNames != null) {
			while (paramNames.hasMoreElements()) {
				String fieldName = paramNames.nextElement();

				MultiPartFile[] fieldValue = multiWrapper
						.getMultiPartFile(fieldName);
				if (multiWrapper.isFile(fieldName)) {
					if (fieldValue != null) {
						requestParams.put(fieldName, fieldValue);
						requestParams.put(fieldName + "ContentType",
								multiWrapper.getContentTypes(fieldName));
						requestParams.put(fieldName + "FileName",
								multiWrapper.getFileNames(fieldName));
					}
				} else {
					requestParams.put(fieldName,
							multiWrapper.getParameterValues(fieldName));
				}
			}
		}

		// invoke action
		return invocation.invoke();
	}

	private String getTextMessage(String messageKey, Object[] args,
			Locale locale) {
		return getTextMessage(null, messageKey, args, locale);
	}

	private String getTextMessage(Object action, String messageKey,
			Object[] args, Locale locale) {
		if (args == null || args.length == 0) {
			if (action != null && useActionMessageBundle) {
				return LocalizedTextUtil.findText(action.getClass(),
						messageKey, locale);
			}
			return LocalizedTextUtil.findText(this.getClass(), messageKey,
					locale);
		} else {
			if (action != null && useActionMessageBundle) {
				return LocalizedTextUtil.findText(action.getClass(),
						messageKey, locale, DEFAULT_MESSAGE, args);
			}
			return LocalizedTextUtil.findText(this.getClass(), messageKey,
					locale, DEFAULT_MESSAGE, args);
		}
	}

}
