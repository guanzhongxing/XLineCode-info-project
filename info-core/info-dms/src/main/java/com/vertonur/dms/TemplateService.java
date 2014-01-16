package com.vertonur.dms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplateService {

	private static final String TEMPLATE_ROOT_PATH = "/com/vertonur/freemarker-template";
	private static final Object OBJ_LOCK = new Object();
	private static TemplateService service;
	private Configuration cfg;

	private TemplateService() {
	}

	public static TemplateService getService() throws IOException {
		if (service == null)
			synchronized (OBJ_LOCK) {
				Configuration cfg = new Configuration();
				ClassTemplateLoader ctl = new ClassTemplateLoader(
						TemplateService.class, TEMPLATE_ROOT_PATH);
				cfg.setTemplateLoader(ctl);
				cfg.setObjectWrapper(new DefaultObjectWrapper());

				service = new TemplateService();
				service.setCfg(cfg);
			}

		return service;
	}

	private void setCfg(Configuration cfg) {
		this.cfg = cfg;
	}

	@SuppressWarnings("rawtypes")
	public String getProcessedTxt(String templateName, Map data)
			throws IOException, TemplateException {
		Template template = cfg.getTemplate(templateName);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(os);
		template.process(data, writer);

		return os.toString();
	}
}
