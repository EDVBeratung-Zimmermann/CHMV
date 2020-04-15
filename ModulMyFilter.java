/*
 *
 * Das JAVA-Programm Miles-Verlag Verlagsverwaltung stellt alle notwendigen
 * Funktionen für die Verwaltung des Carola Hartman Miles-Verlags bereit.
 *
 * Copyright (C) 2017 EDV-Beratung und Betrieb, Entwicklung von Software
 *                    Dipl.Inform Thomas Zimmermann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package milesVerlagMain;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * FileFilter stellt die Dateifilter für die Dateiauswahldialoge bereit genutzt
 * wird die Klasse in den den Modulen - dlgVerkaufBoD - dlgVerkaufGesamt -
 * dlgVerkaufStatistiken
 *
 * @author Thomas Zimmermann
 */
public class ModulMyFilter extends FileFilter {

    private final String endung;

    /**
     *
     * @param endung Dateiendung für die Auswahl der anzuzeigenden Dateien
     */
    public ModulMyFilter(String endung) {
        this.endung = endung;
    }

    @Override

    /**
     *
     * @param f Datei
     */
    public boolean accept(File f) {
        if (f == null) {
            return false;
        }

        // Ordner anzeigen
        if (f.isDirectory()) {
            return true;
        }

        // true, wenn File gewuenschte Endung besitzt
        return f.getName().toLowerCase().endsWith(endung);
    }

    @Override

    /**
     *
     */
    public String getDescription() {
        return endung + " only";
    }
}
