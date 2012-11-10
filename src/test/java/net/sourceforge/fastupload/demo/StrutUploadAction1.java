/*
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
package net.sourceforge.fastupload.demo;

import net.sourceforge.fastupload.MultiPartFile;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author <a href="mailto:link.qian@yahoo.com">Link Qian</a>
 * 
 */
public class StrutUploadAction1 extends ActionSupport {

	 

	/**
	 * 
	 */
	private static final long serialVersionUID = -4793885715910217866L;

	private MultiPartFile photo;

	private String description;

	@Override
	public String execute() throws Exception {
		System.out.println(photo);
		System.out.println(description);
		return super.execute();
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public MultiPartFile getPhoto() {
		return photo;
	}

	public void setPhoto(MultiPartFile photo) {
		this.photo = photo;
	}
}
