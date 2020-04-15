/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package milesVerlagMain;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.* ;
import static milesVerlagMain.Modulhelferlein.Trenner;
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
                  // in n�chste Zeile gehen
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
      lineStrBuff.append(" "); // Leerzeichen zum Trennen des n�chsten Wortes
    }

    retStrBuff.append(lineStrBuff + Trenner);
    return retStrBuff.toString();
  }// end method

  
  public static void main(String[] args) {
  }// end method main

}// end class
