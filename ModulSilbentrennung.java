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


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.* ;
import static milesVerlagMain.ModulHelferlein.Trenner;
import net.davidashen.text.Hyphenator;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;


/*
 Silbentrennung in Java
 ----------------------

 Einfache Klasse zur Silbentrennung.

 Anwendung:
 ----------

String[] spellWord(String)                          Trennen eines Wortes

                    Die Methode liefert ein String-Array 
                    mit den  einzelnen Silben eines Wortes.


String formatLine(String paStr , int paIntWidth)    Formatieren einer Zeile

                    Die Methode bricht eine Zeile 
                    in der verlangten Breite um.


String formatText(String paStr , int paIntWidth)    Formatieren eines Textes

                    Die Methode bricht einen Text 
                    in der verlangten Breite um.




/**
 * Silbentrennung in Java
 */
public class ModulSilbentrennung {

      private static String hyphenate(String word) {
        Hyphenator h = new Hyphenator();
        try {
            h.loadTable(new java.io.BufferedInputStream(new java.io.FileInputStream("hyphen.tex")));
        } catch (FileNotFoundException ex) {
            System.out.println("Silbentrennung: File not Found hyphen.tex" + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Silbentrennung: IO-Exception hyphen.tex" + ex.getMessage());
        }
        return h.hyphenate(word);
    }

  /**
   * erzeugt ein String-Array mit den Silben des Wortes
   * 
     * @param paStrWord
     * @return 
   */
  public static String[] spellWord(String paStrWord) {
      return hyphenate(paStrWord).replace("\u00AD", "-").split("-");
  }// end method

  
  /**
   *
     * @param paStr
     * @param paIntWidth
     * @param cos
     * @param font
     * @param size
     * @return 
   */
  public static String formatText(String paStr , int paIntWidth, PDPageContentStream cos, PDFont font, int size) {
    StringBuffer retStrBuff = new StringBuffer();
    StringTokenizer strTok = new StringTokenizer(paStr , Trenner) ;
    
    while (strTok.hasMoreElements()) {
      //retStrBuff.append(formatLine(strTok.nextToken().trim() , paIntWidth) + Trenner);
      retStrBuff.append(formatLine(strTok.nextToken().trim() , paIntWidth, cos, font, size) + Trenner);
    }

    return retStrBuff.toString();
  }// end method

  /**
   *
     * @param paStr
     * @param paIntWidth
     * @param cos
     * @param font
     * @param size
     * @return 
   */
  public static String formatLine(String paStr , int paIntWidth, PDPageContentStream cos, PDFont font, int size) {
    StringBuffer retStrBuff = new StringBuffer();
    StringBuffer lineStrBuff = new StringBuffer();
    StringTokenizer strTok = new StringTokenizer(paStr , " ") ;

    while (strTok.hasMoreElements()) {
      String[] strArrWord = spellWord(strTok.nextToken().trim()) ;

      for (int i = 0 ; i < strArrWord.length ; i++) {

          try {
              //if (pixelbreite((lineStrBuff.length() + strArrWord[i].length())) > paIntWidth) {
              if (font.getStringWidth(lineStrBuff + strArrWord[i]) / 1000 * size > paIntWidth) {
                  // Zeile ist voll
                  // in nächste Zeile gehen
                  retStrBuff.append(lineStrBuff);
                  if (i > 0) {
                      retStrBuff.append("-");
                  }
                  retStrBuff.append(Trenner);
                  lineStrBuff = new StringBuffer();
              } } catch (IOException ex) {
              System.out.println("Silbentrennung: FormatLine: IOException:"+ex.getMessage());
          }
        lineStrBuff.append(strArrWord[i]);
      }
      lineStrBuff.append(" "); // Leerzeichen zum Trennen des nächsten Wortes
    }

    retStrBuff.append(lineStrBuff + Trenner);
    return retStrBuff.toString();
  }// end method

  
  public static void main(String[] args) {
  }// end method main

}// end class
