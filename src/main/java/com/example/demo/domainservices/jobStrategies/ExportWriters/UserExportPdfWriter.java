package com.example.demo.domainservices.jobStrategies.ExportWriters;

import com.example.demo.domain.exceptions.CriticalErrorException;
import com.example.demo.domain.user.UserModelReturn;
import com.lowagie.text.*;
import com.lowagie.text.html.HtmlTags;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfWriter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class UserExportPdfWriter implements UserExportWriterInterface {
    private String html;
    private static final String TEMPLATE_PATH = "/templates/user_export.html";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm");

    public UserExportPdfWriter(String userLogin) {
        var templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("username", userLogin);
        context.setVariable("time", formatter.format(LocalDateTime.now()));

        html = templateEngine.process(TEMPLATE_PATH, context);
    }

    @Override
    public void addData(List<UserModelReturn> modelList) {

    }

    @Override
    public void write(OutputStream outputStream) throws Exception {
        var renderer = new ITextRenderer();
        renderer.getFontResolver().addFont("/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        renderer.getFontResolver().addFont("/fonts/arial_bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);
    }

    @Override
    public void preWrite() {

    }

    @Override
    public void postWrite() {

    }
}
