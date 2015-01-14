package de.tlongo.roscoe.core;

import com.github.mustachejava.Code;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by tomas on 31.12.14.
 */
public class MustacheViewHandler implements ViewHandler {
    enum FileType {
        VIEW,
        TEMPLATE
    }

    Logger logger = LoggerFactory.getLogger(MustacheViewHandler.class);

    String fileRoot;

    String viewFolder = "/views";
    String templateFolder = "/templates";

    public MustacheViewHandler() {
        logger.debug("Mustache file root: {}", System.getProperty("roscoe.root"));
        fileRoot = System.getProperty("roscoe.root");
    }

    private MustacheFactory createMustacheFactory() {
        if (fileRoot.isEmpty()) {
            // Mustache files are in the classpath
            return new DefaultMustacheFactory();
        } else {
            // User system path to locate the mustache files
            return new DefaultMustacheFactory(new File(fileRoot));
        }
    }

    @Override
    public String loadView(String viewName) {
        return loadView(viewName, new ViewData());
    }

    @Override
    public String loadView(String viewName, ViewData data) {
        Mustache mustache = createMustacheFactory().compile(resolvePathForFile(viewName, FileType.VIEW));

        /**
         * Check if templates have been specified inside the mustache file.
         * If so, add the snippets to the file.
         */
        for (Code code : mustache.getCodes()) {
            if (isTemplateCode(code)) {
                String[] codeTokens = code.getName().split("-");

                String templateFile = codeTokens[1] + ".template";
                logger.debug("Found template {} in view {}. Loading '{}' into it.", codeTokens[1], viewName, templateFile);

                String resolvedTemplate = loadTemplate(templateFile, data);
                data.add(code.getName(), resolvedTemplate);
            }
        }

        StringWriter writer = new StringWriter();

        Map<String, Object> map = data.asMap();
        mustache.execute(writer, map);
        writer.flush();
        return writer.toString();
    }

    private boolean isTemplateCode(Code code) {
        if (code.getName() == null) {
            return false;
        }

        return code.getName().startsWith("template");
    }

    private String loadTemplate(String templateName, ViewData data) {
        Mustache mustache = createMustacheFactory().compile(resolvePathForFile(templateName, FileType.TEMPLATE));

        StringWriter writer = new StringWriter();
        return mustache.execute(writer, data.asMap()).toString();
    }

    public void viewFolder(String folder) {
        viewFolder = folder;
    }

    public void templateFolder(String folder) {
        templateFolder = folder;
    }

    private String resolvePathForFile(String fileName, FileType type) {
        switch (type) {
            case VIEW:
                if (viewFolder.isEmpty()) {
                    return fileName;
                } else {
                    return viewFolder + "/" + fileName;
                }

            case TEMPLATE:
                if (templateFolder.isEmpty()) {
                    return fileName;
                } else {
                    return templateFolder + "/" + fileName;
                }

            default:
                String message = String.format("Error resolving path for file %s. Unknown filetype (%s)", fileName, type);
                logger.error(message);
                throw new RuntimeException(message);
        }
    }
}
