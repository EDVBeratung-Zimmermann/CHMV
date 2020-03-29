/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package milesVerlagMain;


import java.util.* ;


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


Grunds�tzliche Vorgehensweise

 Alle beginnenden Konsonanten der zu untersuchenden Silbe werden �bersprungen.
 Dann wird �ber alle Vokale gelaufen. Am Ende der Silbe werden alle Konsonanten
 bis auf den letzten mit einbezogen. Der letzte Konsonant bleibt f�r die n�chste
 Silbe.

 Dann werden folgende Regeln angewendet:

 st wird nicht getrennt
 ft am Ende der Silbe bleibt zusammen
 ng wird nicht getrennt
 sch wird nicht getrennt
 str wird nicht getrennt

 ck wird in kk umgewandelt

 Problem nicht anwendbarer Regeln
 --------------------------------

 Das Wort Seitengestaltung wird in Sei-ten-ge-stal-tung getrennt. Dabei wird eine
 ng-Verbindung, die entsprechend obiger Regeln nicht getrennt werden d�rfte,
 getrennt. Daf�r habe ich vorgesehen, dass f�r solche W�rter Trennungen in einer
 HashMap hinterlegt werden.

 Methode addWord( String , String
 Methode addIgnoreCasexxx


 Alternativ w�re es m�glich, Teilworte mit den dazugeh�rigen Trennungen zu
 hinterlegen. Dies f�hrt aber zu einer kombinatorischen Explosion mit
 entsprechend schlechten Laufzeiten.

 Weiter besteht das Problem, dass Teilworte erkannt werden k�nnten:

 Problem: Auto-rennen <--> Autoren-nennungen

 Es ist sehr problematisch, ob nun die l�ngeren oder k�rzeren Worte zum Vergleich
 herangezogen werden sollten.

 Eine weitere denkbare M�glichkeit ist das definieren von Worten und
 Wortkombinationen:

 Wort A: Teilzeit
 Wort B: Mitarbeiter
 Wort C: A+B Teilzeitmitarbeiter

 Dies wird in dieser Klasse wegen der oben genannten Probleme nicht gemacht.

 Problem: ck zu kk oder � zu ss umgewandelt. Bei nicht erfolgter Trennung muss
 der originale String in den endg�ltigen Text eingef�gt werden.

 Formatieren eines Textes mit Silbentrennung:

 Es ist g�nstig f�r die Trennung ein Spezialzeichen zu verwenden, damit eine
 Trennung jederzeit wieder r�ckg�ngig gemacht werden kann. Ein weiteres Problem
 ist das R�ckg�ngigmachen von �nderungen, die durch das Trennen entstanden sind (
 ck -> kk , � -> ss )


 */


/**
 * Silbentrennung in Java
 */
public class spell {

  public static void main(String[] args) {
      /**
    test("Lattenrost"         , "Lat-ten-rost") ;
    test("Genossenschaft"     , "Ge-nos-sen-schaft") ;
    test("K�cker"             , "K�k-ker") ;
    test("Mellouk"            , "Mel-louk") ;
    test("Hut"                , "Hut") ;
    test("Sack"               , "Sack") ;
    test("Tante"              , "Tan-te") ;
    test("Supermann"          , "Su-per-mann") ;
    test("Filzlaus"           , "Filz-laus") ;
    test("Karton"             , "Kar-ton") ;
    test("Projektmitarbeiter" , "Pro-jekt-mit-ar-bei-ter") ;
    test("Ecke"               , "Ek-ke") ;
    test("Anwendung"          , "An-wen-dung") ;
    test("Anwendungen"        , "An-wen-dungen") ;
    test("Errungenschaften"   , "Er-rungen-schaf-ten") ;
    test("Baumstruktur"       , "Baum-struk-tur") ;
    test("Datenbankverbindungen"       , "Da-ten-bank-ver-bin-dungen") ;
    test("resultierenden"       , "re-sul-tie-ren-den") ;
    test("Seitengestaltung"       , "Sei-ten-ge-stal-tung") ;
    test("Autorennen",           "Au-to-ren-nen");
    test("Autorengemeinschaft" , "Au-to-ren-ge-mein-schaft") ;
    test("Musik" , "Mu-sik") ;
    test("Aufgabe" , "Auf-ga-be") ;
    test("Aufgaben" , "Auf-ga-ben") ;
    test("Schaftr�nke" , "Schaf-tr�n-ke") ;
    test("Gesamtautorengemeinschaft" , "Ge-samt-au-to-ren-ge-mein-schaft") ;
    test("Betreten" , "Be-tre-ten") ;
    test("Beschreibung" , "Be-schrei-bung") ;
    test("Beschreibungen" , "Be-schrei-bungen") ;
    test("Entwurf" , "Ent-wurf") ;
    test("Ablaufbeschreibung", "Ab-lauf-be-schrei-bung") ;
    test("Ablaufbeschreibungen", "Ab-lauf-be-schrei-bungen") ;
    test("Artikelbezeichnung" , "Ar-ti-kel-be-zeich-nung") ;
    test("Artikelbezeichnungen" , "Ar-ti-kel-be-zeich-nungen") ;
    test("Begr�ndung" , "Be-gr�n-dung") ;
    test("Lieferantenname" , "Lie-fe-ran-ten-na-me") ; // leider falsch
    test("Lieferantennummer" , "Lie-fe-ran-ten-num-mer") ; // leider falsch
    test("Anforderungstext" , "An-for-de-rungs-text") ; // leider falsch
    test("Listungsinformation" , "Lis-tungs-in-for-ma-tion") ; // leider falsch
    test("Sortiment" , "Sor-ti-ment") ;
    test("Aktion" , "Ak-tion") ;
    test("Bezugsweg" , "Be-zugs-weg") ;
    test("Preis" , "Preis") ;
    test("Auflistung" , "Auf-lis-tung") ;
    test("Benutzer" , "Be-nut-zer") ;
    test("Kontakt" , "Kon-takt") ;
    test("Zeilen" , "Zei-len") ;
    test("Zeichen" , "Zei-chen") ;
    test("�nderungstext" , "�n-de-rungs-text") ;// leider falsch
    test("Ablehnungstext" , "Ab-leh-nungs-text") ;
    test("Bearbeitung" , "Be-ar-bei-tung") ;
    test("Zur�cksetzen" , "Zu-r�ck-set-zen") ;
    test("Freigeben","Frei-ge-ben") ;
    test("Pr�fen" , "Pr�-fen") ;
    test("L�schen" , "L�-schen") ;
    test("Datum" , "Da-tum") ;
    test("Vorbelegung","Vor-be-le-gung") ;
    test("folgende","fol-gen-de");
    test("Kenntnis","Kennt-nis");
    test("extern","ex-tern");
    test("externe","ex-ter-ne");
    test("Herr","Herr");
    test("Herren","Her-ren");
    test("Gesch�ftsbereichsleitung","Ge-sch�fts-be-reichs-lei-tung");
    test("Bereich","Be-reich");
    test("freundlich","freund-lich");
    test("freundlichen","freund-li-chen");
    test("Anwendungsentwicklung","An-wen-dungs-ent-wick-lung");
    test("Beauftragung","Be-auf-tra-gung");
    test("Geheimhaltung","Ge-heim-hal-tung");
    test("zugeteilten","zu-ge-teil-ten");
    test("Informationen","In-for-ma-tio-nen");
    test("Empfang","Emp-fang");
    test("Beratung","Be-ra-tung");
    // test( "","");
    // test( "","");
    // test( "","");

    String strText = // ...
        "Sehr geehrte Damen und Herren,\n" // ...
        + "anbei erhalten Sie die Information �ber meinen Antrag bez�glich meines Anliegens vom benannten Termin entsprechend unserer Absprache.\n" // ...
        + "Mit freundlichen Gr�ssen\n" ;

    System.out.println(formatText(strText , 25)) ;
  */
  }// end method main

  /**
   * Testmethode f�r die Entwicklng
   */
  //public static void test(String paStr, String paStrExpected) {
  //  System.out.println("=====================================================================");
  //
  //  System.out.println(paStr);
  //  String strResult = arr2Str(spellWord(paStr) , "-") ;
  //
  //  System.out.println(" -> " + strResult) ;
  //  if (!strResult.equals(paStrExpected)) {
  //    throw new RuntimeException("expected was " + paStrExpected) ;
  //  }
  //}// end method

  /**
   *
     * @param paStr
     * @param paIntWidth
     * @return 
   */
  public static String formatText(String paStr , int paIntWidth) {
    StringBuffer retStrBuff = new StringBuffer();
    StringTokenizer strTok = new StringTokenizer(paStr , "\n") ;

    while (strTok.hasMoreElements()) {
      //retStrBuff.append(formatLine(strTok.nextToken().trim() , paIntWidth) + "\n");
      retStrBuff.append(formatLine(strTok.nextToken().trim() , paIntWidth) + "~#!#~");
    }

    return retStrBuff.toString();
  }// end method

  /**
   *
     * @param paStr
     * @param paIntWidth
     * @return 
   */
  public static String formatLine(String paStr , int paIntWidth) {
    StringBuffer retStrBuff = new StringBuffer();
    StringBuffer lineStrBuff = new StringBuffer();
    StringTokenizer strTok = new StringTokenizer(paStr , " ") ;

    while (strTok.hasMoreElements()) {
      String[] strArrWord = spellWord(strTok.nextToken().trim()) ;

      for (int i = 0 ; i < strArrWord.length ; i++) {

        if ((lineStrBuff.length() + strArrWord[i].length()) > paIntWidth) {
          // Zeile ist voll
          // in n�chste Zeile gehen
          retStrBuff.append(lineStrBuff);
          if (i > 0) {
            retStrBuff.append("-");
          }
          retStrBuff.append("\n");
          lineStrBuff = new StringBuffer();
        }
        lineStrBuff.append(strArrWord[i]);
      }
      lineStrBuff.append(" "); // Leerzeichen zum Trennen des n�chsten Wortes
    }

    retStrBuff.append(lineStrBuff + "\n");
    return retStrBuff.toString();
  }// end method

  /**
   * Liste mit bekannten Worten als Ausnahmen f�r die ng-Trennregel Worten
   * Achtung, alles klein schreiben
   */
  private static HashSet knownWordHashSet = newKnownWordHashSet() ;

  /**
   * Initialisierungsmethode f�r knownWordHashSet
   */
  private static HashSet newKnownWordHashSet() {
    HashSet newHashSet = new HashSet();

    newHashSet.add("seiten") ;
    newHashSet.add("autoren") ;
    newHashSet.add("gesamt") ;
    return   newHashSet;
  }// end method

  /**
   * HashMap mit bereits getrennten Worten
   */
  private static HashMap wordHashMap = new HashMap();

  /**
   * HashSet mit bekannten Silben
   */
  private static HashSet syllHashSet = newSyllHashSet();

  /**
   * Initialisierungsmethode f�r syllHashSet
   */
  private static HashSet newSyllHashSet() {
    HashSet newHashSet = new HashSet();

    newHashSet.add("an") ;
    newHashSet.add("trag") ;
    newHashSet.add("sch�fts") ;
    newHashSet.add("chen") ;
    newHashSet.add("gen") ;

    newHashSet.add("dung") ;
    newHashSet.add("nung") ;
    newHashSet.add("rung") ;
    newHashSet.add("tung") ;

    newHashSet.add("dungs") ;
    newHashSet.add("nungs") ;
    newHashSet.add("rungs") ;
    newHashSet.add("tungs") ;

    newHashSet.add("ent") ;
    newHashSet.add("auf") ;
    newHashSet.add("trag") ;

    newHashSet.add("lis") ;
    newHashSet.add("in") ;
    newHashSet.add("tion") ;
    newHashSet.add("grund") ;
    newHashSet.add("gr�nd") ;
    newHashSet.add("�nderung") ;
    newHashSet.add("be") ;
    newHashSet.add("ver") ;
    newHashSet.add("lauf") ;
    newHashSet.add("schreib") ;
    newHashSet.add("ngen") ;
    newHashSet.add("ten") ;
    newHashSet.add("treten") ;
    newHashSet.add("mit") ;
    newHashSet.add("pro") ;
    newHashSet.add("jekt") ;
    newHashSet.add("re") ;
    newHashSet.add("agieren") ;
    newHashSet.add("arbeit") ;
    newHashSet.add("schaft") ;
    newHashSet.add("schaf") ;
    newHashSet.add("tr�nke") ;
    newHashSet.add("gesamt") ;
    newHashSet.add("samt") ;
    newHashSet.add("autor") ;
    newHashSet.add("rungs") ;
    newHashSet.add("text") ;
    return   newHashSet;

  }// end method

  /**
   * erzeugt ein String-Array mit den Silben des Wortes
     * @param paStrWord
     * @return 
   */
  public static String[] spellWord(String paStrWord) {
    String[] retStrArr = null ;

    retStrArr = (String[]) wordHashMap.get(paStrWord);
    if (retStrArr != null) {
      return retStrArr;
    }
    ArrayList arrList = new ArrayList();

    paStrWord = paStrWord.trim();

    String strPreSyllable = "" ;
    String strPreSyllables = "" ;

    while (paStrWord.length() > 0) {
      System.out.println("-----------------------------------------------------");
      String syllable = parseSyllable(paStrWord,strPreSyllable,strPreSyllables) ;

      // System.out.println( "silbe:"+silbe);

      paStrWord = paStrWord.substring(syllable.length()) ;
      arrList.add(syllable) ;
      strPreSyllable = syllable ;
      strPreSyllables += syllable ;
    }// while

    retStrArr = (String[]) arrList.toArray(new String[0]) ;

    wordHashMap.put(paStrWord , retStrArr) ;

    return retStrArr;
  }// end method

  /**
   * Zur�ckliefern der ersten Silbe aus dem �bergebenen Wort
   */
  private static String parseSyllable(String paStr, String paStrPreSyllable, String paStrPreSyllables) {
    int i = 0 ;
    boolean bCkToKK = false ;

    boolean bCont = true ;

    while (bCont) {
      bCont = false;

      boolean bMatch = false;

      // �ber beginnende Konsonanten laufen
      for (;!bMatch && i < paStr.length() && isConsonant(paStr.charAt(i)) ; i++) {
        bMatch = checkKnownSyllable(paStr.substring(0 , Math.min(i , paStr.length())) ,
            paStr.substring(Math.min(i , paStr.length()))) ;
      }
      // �ber Vocale laufen
      for (;!bMatch && i < paStr.length() && isVocal(paStr.charAt(i)) ; i++) {
        bMatch = checkKnownSyllable(paStr.substring(0 , Math.min(i , paStr.length())) ,
            paStr.substring(Math.min(i , paStr.length())));
      }

      // �ber endende Konsonanten laufen
      for (;!bMatch && (i < (paStr.length() - 1)) && isConsonant(paStr.charAt(i + 1)) ; i++) {
        bMatch = checkKnownSyllable(paStr.substring(0 , Math.min(i , paStr.length())) ,
            paStr.substring(Math.min(i , paStr.length())));
      }

      System.out.println("vor den Regeln: " + paStr.substring(0 , Math.min(i , paStr.length())) + "|"
          + paStr.substring(Math.min(i , paStr.length()))) ;

      // Starter der else-if-Kaskade,  damit else if beliebig verschoben werden kann ohne Risiko Verwechslung if <--> else if
      if (bMatch) {
        i--;
      }

      // // bekanntes Wort wurde getrennt
      // else if (contentsKnownWord(paStrPreSyllables.toLowerCase() , // + // "?" +
      // // paStr.substring(0,Math.min(i , paStr.length())).toLowerCase())
      // paStr.substring(0,Math.min(i , paStr.length())).toLowerCase())
      // // paStr.toLowerCase() )
      // ) {
      // System.out.println("bekanntes Wort wurde getrennt " + paStr.substring(Math.min(i , paStr.length()))) ;
      // // ???
      // }

      /*
       // Spezialregel Silbe mit
       // Mi-tarbeiter wird zu Mit-arbeiter
       else if ((i > 1 && (i < paStr.length() - 2) && paStr.substring(i - 2).toLowerCase().startsWith("mi")) // ...
       && (i < paStr.length() - 1) && paStr.substring(i).startsWith("t")) {
       System.out.println("Spezialregel Silbe mit " + paStr.substring(i)) ;
       i++;
       }
       */

      /*
       // Spezialregel Silbe samt
       // Gesam-twerk wird zu Gesamt-werk
       else if ((i > 2 && (i < paStr.length() - 3) && paStr.substring(i - 3).toLowerCase().startsWith("sam")) // ...
       && (i < paStr.length() - 1) && paStr.substring(i).startsWith("t")) {
       System.out.println("Spezialregel Silbe samt " + paStr.substring(i)) ;
       i++;
       }
       */

      // Trennung zweier bekannter Silben aufgetreten
      else if (checkKnownSyllable(paStr.substring(0 , Math.min(i , paStr.length())) ,
          paStr.substring(Math.min(i , paStr.length())))) {
        System.out.println("Trennung zweier bekannter Silben aufgetreten " + paStr.substring(i)) ;
      }

      // Trennung zweier bekannter Silben an Folgeposition 1 aufgetreten
      else if (checkKnownSyllable(paStr.substring(0 , Math.min(i + 1 , paStr.length())) ,
          paStr.substring(Math.min(i + 1, paStr.length())))) {
        System.out.println("Trennung zweier bekannter Silben an Folgeposition 1 aufgetreten " + paStr.substring(i)) ;
        i += 1 ;
      }

      // Trennung zweier bekannter Silben an Folgeposition 2 aufgetreten
      else if (checkKnownSyllable(paStr.substring(0 , Math.min(i + 2 , paStr.length())) ,
          paStr.substring(Math.min(i + 2, paStr.length())))) {
        System.out.println("Trennung zweier bekannter Silben an Folgeposition 2 aufgetreten " + paStr.substring(i)) ;
        i += 2 ;
      }

      /*
       // Trennung zweier bekannter Silben an Folgeposition 3 aufgetreten
       else if (checkKnownSyllable(paStr.substring(0 , Math.min(i + 3 , paStr.length())) ,
       paStr.substring(Math.min(i + 3, paStr.length())))) {
       System.out.println("Trennung zweier bekannter Silben an Folgeposition 3 aufgetreten " + paStr.substring(i)) ;
       i += 3 ;
       }
       */

      // st wurde getrennt
      else if (i > 1 && (i < paStr.length() - 1) && paStr.substring(i - 1).startsWith("st")) {
        System.out.println("st wurde getrennt " + paStr.substring(i)) ;
        i--;
      }

      // st am Ende der Silbe
      else if ((i < paStr.length() - 1) && paStr.substring(i).startsWith("st")) {
        System.out.println("st am Ende der Silbe " + paStr.substring(i)) ;
        i += 2;
      }

      // ft am Ende der Silbe
      else if ((i < paStr.length() - 1) && paStr.substring(i).startsWith("ft")) {
        System.out.println("ft am Ende der Silbe " + paStr.substring(i)) ;
        i += 2;
      }

      // sch wurde getrennt
      else if (i > 2 && (i < paStr.length() - 1) && paStr.substring(i - 2).startsWith("sch")) {
        System.out.println("sch wurde getrennt " + paStr.substring(i)) ;
        i -= 2;
      }

      // ch wurde getrennt
      else if (i > 1 && (i < paStr.length() - 1) && paStr.substring(i - 1).startsWith("ch")) {
        System.out.println("ch wurde getrennt " + paStr.substring(i)) ;
        i--;
      }

      // str am Anfang der n�chsten Silbe
      else if (i > 2 && (i < paStr.length() - 1) && paStr.substring(i - 2).startsWith("str")) {
        System.out.println("str am Anfang der n�chsten Silbe " + paStr.substring(i)) ;
        i -= 2;
      }

      // ck: c am Ende der Silbe und k am Anfang der n�chsten Silbe
      // ck wird in kk umgewandelt
      else if ((i > 1 && (i < paStr.length() - 1) && paStr.substring(i - 1).startsWith("c")) // ...
          && (i < paStr.length() - 1) && paStr.substring(i).startsWith("k")) {
        System.out.println("ck wird in kk umgewandelt " + paStr.substring(i)) ;
        bCkToKK = true ;
      }

      // Spezialregel Silbe ng
      // ng wird nicht getrennt
      else if ((i > 1 && (i < paStr.length() - 1) && paStr.substring(i - 1).toLowerCase().startsWith("n")) // ...
          && (i < paStr.length() - 1) && paStr.substring(i).startsWith("g")// ...
          // && ( ! ( paStrPreSyllable.toLowerCase() + paStr.toLowerCase() ).startsWith( "seiten" ) ) // seiten-gestaltung als Ausnahme
          // && (!startsWithKnownWord(paStrPreSyllables.toLowerCase() + paStr.toLowerCase())) // Suchen nach Ausnahmen f�r ng-Trennung (seitengestaltung, autorengemeinschaft)
          // && (!startsWithKnownWord(paStrPreSyllables.toLowerCase() + paStr.substring(0,i).toLowerCase())) // Suchen nach Ausnahmen f�r ng-Trennung (seitengestaltung, autorengemeinschaft)
          // && (!endsWithKnownWord(paStrPreSyllables.toLowerCase() )) // Suchen nach Ausnahmen f�r ng-Trennung (seitengestaltung, autorengemeinschaft)
          // && (!endsWithKnownWord(paStrPreSyllables.toLowerCase() + paStr.toLowerCase())) // Suchen nach Ausnahmen f�r ng-Trennung (seitengestaltung, autorengemeinschaft)
          && (!endsWithKnownWord(paStrPreSyllables.toLowerCase() + paStr.substring(0,i).toLowerCase())) // Suchen nach Ausnahmen f�r ng-Trennung (seitengestaltung, autorengemeinschaft)
          ) {
        System.out.println("ng wird nicht getrennt " + paStr.substring(i)) ;
        i++;
        bCont = true;
      }

      // ein Vocal folgt sofort nach dem endenden Konsonant
      // der Konsonant soll zur n�chsten Silbe geh�ren
      else if (i > 0 && (i < paStr.length() - 1) && isVocal(paStr.charAt(i + 1))) {
        System.out.println("ein Vocal folgt sofort nach dem endenden Konsonant " + paStr.substring(i)) ;
      }

      // Ende der Silbe mit Konsonant
      else if (i < paStr.length() && isConsonant(paStr.charAt(i))) {
        System.out.println("Ende der Silbe mit Konsonant " + paStr.substring(i)) ;
        i++;
      }

      // es folgen nur noch Konsonanten bis zum Ende des Wortes
      else if (isAllConsonantes(paStr.substring(i))) {
        System.out.println("es folgen nur noch Konsonanten bis zum Ende des Wortes " + paStr.substring(i)) ;
        i = paStr.length();
      }

      // keine Regel hat gezogen
      else {
        System.out.println("keine Regel hat gezogen") ;
      }

    }// while

    String retStr = paStr.substring(0 , i) ;

    if (bCkToKK) {
      retStr = retStr.substring(0 , retStr.length() - 1) + "k" ;
    }

    return retStr ;
  }// end method

  /**
   * stimmlos
     * @param paCh
     * @return 
   */
  public static boolean isConsonant(char paCh) {
    return !isVocal(paCh) ;
  }// end method

  /**
   * Pr�fung, ob alle Zeichen des Wortes Konsonanten sind
     * @param paStr
     * @return 
   */
  public static boolean isAllConsonantes(String paStr) {
    for (int i = 0 ; i < paStr.length() ; i++) {
      if (isVocal(paStr.charAt(i))) {
        return false ;
      }
    }
    return true ;

  }// end method

  /**
   * stimmhaft
     * @param paCh
     * @return 
   */
  public static boolean isVocal(char paCh) {
    return (
        paCh == 'e' || // ...
        paCh == 'a' || // ...
        paCh == 'o' || // ...
        paCh == 'u' || // ...
        paCh == 'i' || // ...
        paCh == '�' || // ...
        paCh == '�' || // ...
        paCh == '�' || // ...

        paCh == 'E' || // ...
        paCh == 'A' || // ...
        paCh == 'O' || // ...
        paCh == 'U' || // ...
        paCh == 'I' || // ...
        paCh == '�' || // ...
        paCh == '�' || // ...
        paCh == '�') ;
  }// end method

  private static boolean startsWithKnownWord(String paStr) {
    System.out.println("startsWithKnownWord:" + paStr) ;
    paStr = paStr.toLowerCase() ;
    Iterator iter = knownWordHashSet.iterator();

    while (iter.hasNext()) {
      if (paStr.startsWith((String) iter.next())) {
        return true;
      }
    }
    return false;
  }// end method

  private static boolean endsWithKnownWord(String paStr) {
    System.out.println("endsWithKnownWord:" + paStr) ;
    paStr = paStr.toLowerCase() ;
    Iterator iter = knownWordHashSet.iterator();

    while (iter.hasNext()) {
      if (paStr.endsWith((String) iter.next())) {
        return true;
      }
    }
    return false;
  }// end method

  private static boolean contentsKnownWord(String paStr) {
    System.out.println("contentsKnownWord:" + paStr) ;
    paStr = paStr.toLowerCase() ;
    Iterator iter = knownWordHashSet.iterator();

    while (iter.hasNext()) {
      if (paStr.contains((String) iter.next())) {
        return true;
      }
    }
    return false;
  }// end method

  private static boolean contentsKnownWord(String paStrFirst,String paStrNext) {
    System.out.println("contentsKnownWord:" + paStrFirst + "?" + paStrNext) ;
    String paStr = paStrFirst + paStrNext ;

    Iterator iter = knownWordHashSet.iterator();

    while (iter.hasNext()) {
      String strKnownWord = (String) iter.next() ;

      if ((!paStrFirst.endsWith(strKnownWord)) && paStr.contains(strKnownWord)) {
        return true;
      }
    }
    return false;
  }// end method

  /**
   * Silben-String-Array zusammenf�gen
   */
  private static String arr2Str(String[] paStrArr , String paDelim) {
    StringBuffer sb = new StringBuffer();

    for (int i = 0 ; i < paStrArr.length ; i++) {
      sb.append(paStrArr[ i ]) ;
      if (i < (paStrArr.length - 1)) {
        sb.append(paDelim) ;
      }
    }
    return sb.toString();
  }// end method

  private static boolean checkKnownSyllable(String paStrLast , String paStrNext) {
    System.out.println(paStrLast + "|" + paStrNext) ;
    if (endsWithKnownSyllable(paStrLast.toLowerCase()) && startsWithKnownSyllable(paStrNext)) {
      System.out.println("!!!");
      return true;
    }
    return false;

  }// end method

  private static boolean endsWithKnownSyllable(String paStr) {
    Iterator iter = syllHashSet.iterator();

    while (iter.hasNext()) {
      if (paStr.endsWith((String) iter.next())) {
        return true;
      }

    }
    return false;

  }// end method

  private static boolean startsWithKnownSyllable(String paStr) {
    Iterator iter = syllHashSet.iterator();

    while (iter.hasNext()) {
      if (paStr.startsWith((String) iter.next())) {
        return true;
      }
    }
    return false;

  }// end method

}// end class
