package logalyzes.server.templates;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Map;

public abstract  class MailTemplate   {
    protected String tempName;
    protected Map<String, Object> templateData;


    public MailTemplate(String tempName, Map<String, Object> templateData) {
        this.tempName = tempName;
        this.templateData = templateData;
    }


    private String toString(Configuration freemarkerConfig) throws IOException, TemplateException {
        Template template = freemarkerConfig.getTemplate(this.tempName);
        try (StringWriter stringWriter = new StringWriter()) {
            template.process(templateData, stringWriter);
            return stringWriter.toString();
        }
    }
}
