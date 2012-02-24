package org.openlegacy.designtime.terminal.generators;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openlegacy.designtime.mains.OverrideConfirmer;
import org.openlegacy.exceptions.GenerationException;
import org.openlegacy.layout.PageDefinition;
import org.openlegacy.terminal.definitions.ScreenEntityDefinition;
import org.openlegacy.terminal.layout.support.DefaultScreenPageBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;

public class ScreenEntityMvcGenerator implements ScreenEntityWebGenerator {

	private static final String VIEWS_DIR = "src/main/webapp/WEB-INF/views/";

	private static final String TILES_VIEWS_FILE = VIEWS_DIR + "/views.xml";
	private static final String TILES_VIEW_PLACEHOLDER_START = "<!-- Marker for code generation start";
	private static final String TILES_VIEW_PLACEHOLDER_END = "Marker for code generation end -->";

	private static final String VIEW_TOKEN = "VIEW-NAME";

	private static final CharSequence TILES_VIEW_PLACEHOLDER = "<!-- Place holder for code generation -->";

	private final static Log logger = LogFactory.getLog(ScreenEntityMvcGenerator.class);

	public void generatePage(PageDefinition pageDefinition, OutputStream output) {

		GenerateUtil.generate(pageDefinition, output, "ScreenEntityMvcPage.jspx.template");
	}

	public void generateController(PageDefinition pageDefinition, OutputStream output) {

		GenerateUtil.generate(pageDefinition, output, "ScreenEntityMvcController.java.template");
	}

	public void generateControllerAspect(PageDefinition pageDefinition, OutputStream output) {
		GenerateUtil.generate(pageDefinition, output, "ScreenEntityMvcController.aj.template");
	}

	public void generateAll(File projectDir, ScreenEntityDefinition screenEntityDefinition, File sourceDirectory,
			String packageDirectoryName, OverrideConfirmer overrideConfirmer) throws GenerationException {
		FileOutputStream fos = null;
		try {

			File packageDir = new File(sourceDirectory, packageDirectoryName);
			String entityClassName = screenEntityDefinition.getEntityClassName();
			File contollerFile = new File(packageDir, entityClassName + "Controller.java");
			boolean generateController = true;
			if (contollerFile.exists()) {
				boolean override = overrideConfirmer.isOverride(contollerFile);
				if (!override) {
					generateController = false;
				}
			}
			PageDefinition pageDefinition = new DefaultScreenPageBuilder().build(screenEntityDefinition);
			if (generateController){
				contollerFile.getParentFile().mkdirs();
				fos = new FileOutputStream(contollerFile);
				generateController(pageDefinition, fos);
				fos.close();
				logger.info(MessageFormat.format("Generated controller : {0}", contollerFile.getAbsoluteFile()));
			}

			File contollerAspectFile = new File(packageDir, entityClassName + "Controller_Aspect.aj");
			fos = new FileOutputStream(contollerAspectFile);
			generateControllerAspect(pageDefinition, fos);
			fos.close();
			logger.info(MessageFormat.format("Generated controller aspect: {0}", contollerAspectFile.getAbsoluteFile()));

			File webPageFile = new File(projectDir, VIEWS_DIR + entityClassName + ".jspx");
			if (webPageFile.exists()) {
				boolean override = overrideConfirmer.isOverride(webPageFile);
				if (!override) {
					return;
				}
			}
			fos = new FileOutputStream(webPageFile);
			generatePage(pageDefinition, fos);
			fos.close();
			logger.info(MessageFormat.format("Generated jspx file: {0}", webPageFile.getAbsoluteFile()));

			updateViewsFile(projectDir, entityClassName);
		} catch (Exception e) {
			throw (new GenerationException(e));
		} finally {
			IOUtils.closeQuietly(fos);
		}

	}

	private static void updateViewsFile(File projectDir, String viewName) throws IOException {
		File viewsFile = new File(projectDir, TILES_VIEWS_FILE);
		if (!viewsFile.exists()) {
			logger.warn(MessageFormat.format("Views file {0} not found in project directory:{1}", TILES_VIEWS_FILE, projectDir));
		}

		FileOutputStream fos = null;
		try {
			String viewsFileContent = FileUtils.readFileToString(viewsFile);
			int templateMarkerStart = viewsFileContent.indexOf(TILES_VIEW_PLACEHOLDER_START)
					+ TILES_VIEW_PLACEHOLDER_START.length();
			int templateMarkerEnd = viewsFileContent.indexOf(TILES_VIEW_PLACEHOLDER_END) - 1;
			if (templateMarkerStart < 0 || templateMarkerEnd < 0) {
				logger.warn(MessageFormat.format("Could not find template markers within views file: {0}",
						viewsFile.getAbsolutePath()));
				return;
			}

			String definitionTemplate = viewsFileContent.substring(templateMarkerStart, templateMarkerEnd);
			String newViewDefinition = definitionTemplate.replaceAll(VIEW_TOKEN, viewName);
			viewsFileContent = viewsFileContent.replace(TILES_VIEW_PLACEHOLDER, TILES_VIEW_PLACEHOLDER + newViewDefinition);
			fos = new FileOutputStream(viewsFile);
			IOUtils.write(viewsFileContent, fos);

			logger.info(MessageFormat.format("Added view {0} to {1}", viewName, viewsFile.getAbsoluteFile()));

		} finally {
			IOUtils.closeQuietly(fos);
		}
	}
}