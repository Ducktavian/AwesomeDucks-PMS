package com.motorph.reporting;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.SaveContributorFactory;

/**
 * Adds a "PDF Document (*.pdf)" option to JasperViewer's Save dialog.
 * @author Ducktavian
 */
public class PdfSaveContributor extends JRSaveContributor {

    private static final String EXTENSION_PDF = ".pdf";

    public PdfSaveContributor(Locale locale, ResourceBundle resourceBundle) {
        super(locale, resourceBundle);
    }

    public PdfSaveContributor(JasperReportsContext jasperReportsContext, Locale locale, ResourceBundle resourceBundle) {
        super(jasperReportsContext, locale, resourceBundle);
    }

    @Override
    public boolean accept(File file) {
        return file.isDirectory() || file.getName().toLowerCase().endsWith(EXTENSION_PDF);
    }

    @Override
    public String getDescription() {
        return "PDF Document (*.pdf)";
    }

    @Override
    public void save(JasperPrint jasperPrint, File file) throws JRException {
        String path = file.getPath();
        if (!path.toLowerCase().endsWith(EXTENSION_PDF)) {
            path += EXTENSION_PDF;
        }
        JasperExportManager.exportReportToPdfFile(jasperPrint, path);
    }

    public static class Factory implements SaveContributorFactory {
        @Override
        public JRSaveContributor create(JasperReportsContext jasperReportsContext, Locale locale, ResourceBundle resourceBundle) {
            return new PdfSaveContributor(jasperReportsContext, locale, resourceBundle);
        }
    }
}
