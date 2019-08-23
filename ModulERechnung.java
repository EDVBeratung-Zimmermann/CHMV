/*
 *
 * Das JAVA-Programm Miles-Verlag Verlagsverwaltung stellt alle notwendigen
 * Funktionen für die Verwaltung des Carola Hartman Miles-Verlags bereit.
 *
 * Copyright (C) 2017 EDV-Beratung und Betrieb, Entwicklung von SOftware
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

import static com.neovisionaries.i18n.CountryCode.DE;
import static com.neovisionaries.i18n.CurrencyCode.EUR;
import io.konik.InvoiceTransformer;
import io.konik.PdfHandler;
import io.konik.calculation.InvoiceCalculator;
import io.konik.zugferd.Invoice;
import io.konik.zugferd.entity.Address;
import io.konik.zugferd.entity.DebtorFinancialAccount;
import io.konik.zugferd.entity.FinancialInstitution;
import io.konik.zugferd.entity.GrossPrice;
import io.konik.zugferd.entity.Header;
import io.konik.zugferd.entity.PaymentMeans;
import io.konik.zugferd.entity.PositionDocument;
import io.konik.zugferd.entity.Price;
import io.konik.zugferd.entity.Product;
import io.konik.zugferd.entity.ReferencedDocument;
import io.konik.zugferd.entity.TaxRegistration;
import io.konik.zugferd.entity.TradeParty;
import io.konik.zugferd.entity.trade.Agreement;
import io.konik.zugferd.entity.trade.Delivery;
import io.konik.zugferd.entity.trade.MonetarySummation;
import io.konik.zugferd.entity.trade.Settlement;
import io.konik.zugferd.entity.trade.Trade;
import io.konik.zugferd.entity.trade.item.Item;
import io.konik.zugferd.entity.trade.item.ItemTax;
import io.konik.zugferd.entity.trade.item.SpecifiedAgreement;
import io.konik.zugferd.entity.trade.item.SpecifiedDelivery;
import io.konik.zugferd.entity.trade.item.SpecifiedSettlement;
import static io.konik.zugferd.profile.ConformanceLevel.EXTENDED;
import static io.konik.zugferd.unece.codes.DocumentCode._380;
import static io.konik.zugferd.unece.codes.Reference.VA;
import io.konik.zugferd.unece.codes.TaxCategory;
import io.konik.zugferd.unece.codes.TaxCode;
import static io.konik.zugferd.unece.codes.UnitOfMeasurement.LUMP_SUM;
import static io.konik.zugferd.unece.codes.UnitOfMeasurement.UNIT;
import io.konik.zugferd.unqualified.Amount;
import io.konik.zugferd.unqualified.Quantity;
import io.konik.zugferd.unqualified.ZfDate;
import io.konik.zugferd.unqualified.ZfDateDay;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import static milesVerlagMain.Modulhelferlein.DateString2Date;
import org.apache.commons.lang3.time.DateUtils;

/**
 *
 * @author Thomas Zimmermann nach Internetrecherche
 */
public class ModulERechnung {

    public static void createXRechnungDocuments(String InvoiceNumber,
            String InvoiceLanguage,
            String InvoiceDate,
            String InvoiceName,
            String BuyerName,
            String BuyerPLZ,
            String BuyerStrasse,
            String BuyerOrt,
            String BuyerLand,
            String BuyerUID,
            String BuyerBestellzeichen,
            String[] Items, // Items[i]   = Steuer
            // Items[i+1] = Produkt
            // Items[i+2] = Brutto
            // Items[i+3] = Netto
            // Items[i+4] = Anzahl
            // Items[i+5] = 
            Boolean ReverseCharge,
            String setLineTotal, //.setLineTotal(new Amount(100, EUR))
            String setChargeTotal, //.setChargeTotal(new Amount(0, EUR))
            String setAllowanceTotal, //.setAllowanceTotal(new Amount(0, EUR))
            String setTaxBasisTotal, //.setTaxBasisTotal(new Amount(100, EUR))
            String setTaxTotal, //.setTaxTotal(new Amount(19, EUR))
            String setDuePayable, //.setDuePayable(new Amount(119, EUR))
            String setTotalPrepaid, //.setTotalPrepaid(new Amount(0, EUR))
            String setGrandTotal,
            String outputFilename) {     //.setGrandTotal(new Amount(119, EUR))));   

        FileWriter writer;
        File file;

        String XMLFile = outputFilename + ".XRechnung.xml";

        file = new File(XMLFile);

        //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        //String uhrzeit = sdf.format(new Date());
        switch (BuyerLand) {
            case "":
            case "DEUTSCHLAND":
                BuyerLand = "DE";
                break;
            case "NIEDERLANDE":
                BuyerLand = "NL";
                break;
            case "SCHWEIZ":
                BuyerLand = "CH";
                break;
            case "IRLAND":
                BuyerLand = "IE";
                break;
            case "PORTUGAL":
                BuyerLand = "PT";
                break;
            case "ÖSTERREICH":
                BuyerLand = "AT";
                break;
            case "ITALIEN":
                BuyerLand = "IT";
                break;
            case "BELGIEN":
                BuyerLand = "BE";
                break;
            case "FRANKREICH":
                BuyerLand = "FR";
                break;
            case "BULGARIEN":
                BuyerLand = "BG";
                break;
            case "DÄNEMARK":
                BuyerLand = "DK";
                break;
            case "ESTLAND":
                BuyerLand = "EE";
                break;
            case "FINNLAND":
                BuyerLand = "FI";
                break;
            case "GRIECHENLAND":
                BuyerLand = "GR";
                break;
            case "KROATIEN":
                BuyerLand = "HR";
                break;
            case "LETTLAND":
                BuyerLand = "LV";
                break;
            case "LITAUEN":
                BuyerLand = "LT";
                break;
            case "LUXEMBURG":
                BuyerLand = "LU";
                break;
            case "MALTA":
                BuyerLand = "MT";
                break;
            case "POLEN":
                BuyerLand = "PL";
                break;
            case "RUMÄNIEN":
                BuyerLand = "RO";
                break;
            case "SCHWEDEN":
                BuyerLand = "SE";
                break;
            case "SLOWAKEI":
                BuyerLand = "SK";
                break;
            case "SLOWENIEN":
                BuyerLand = "SI";
                break;
            case "SPANIEN":
                BuyerLand = "ES";
                break;
            case "TSCHECHIEN":
                BuyerLand = "CZ";
                break;
            case "UNGARN":
                BuyerLand = "HU";
                break;
            case "ZYPERN":
                BuyerLand = "CY";
                break;
        }

        setLineTotal = setLineTotal.replace(",", ".");
        setTaxBasisTotal = setTaxBasisTotal.replace(",", ".");
        setTaxTotal = setTaxTotal.replace(",", ".");
        setGrandTotal = setGrandTotal.replace(",", ".");
        setDuePayable = setDuePayable.replace(",", ".");

        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
            today = sdf.parse(InvoiceDate);
        } catch (ParseException e) {
            Modulhelferlein.Fehlermeldung("XRechnung erstellen", "ParseException: ", e.getMessage());
        }

        String BillingDate = "";
// create Calendar instance with actual date
        sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(today);
        InvoiceDate = sdf.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        BillingDate = sdf.format(calendar.getTime());

        try {
            // new FileWriter(file ,true) - falls die Datei bereits existiert
            // werden die Bytes an das Ende der Datei geschrieben

            // new FileWriter(file) - falls die Datei bereits existiert
            // wird diese überschrieben
            writer = new FileWriter(file);

// Text wird in den Stream geschrieben
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.write(System.getProperty("line.separator"));
            writer.write("<rsm:CrossIndustryInvoice xmlns:rsm=\"urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100\"\n"
                    + "                          xmlns:ram=\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100\"\n"
                    + "                          xmlns:udt=\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100\"\n"
                    + "                          xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                    + "                          xsi:schemaLocation=\"urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100 ../../../schemas/UN_CEFACT/CrossIndustryInvoice_100pD16B.xsd\">");
            writer.write(System.getProperty("line.separator"));

            writer.write("    <rsm:ExchangedDocumentContext>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        <ram:GuidelineSpecifiedDocumentContextParameter>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            <ram:ID>urn:ce.eu:en16931:2017:xoev-de:kosit:standard:xrechnung_1.1</ram:ID>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        </ram:GuidelineSpecifiedDocumentContextParameter>");
            writer.write(System.getProperty("line.separator"));
            writer.write("    </rsm:ExchangedDocumentContext>");
            writer.write(System.getProperty("line.separator"));

            writer.write("    <rsm:ExchangedDocument>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        <ram:ID>" + InvoiceNumber + "</ram:ID>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        <ram:TypeCode>380</ram:TypeCode>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        <ram:IssueDateTime>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            <udt:DateTimeString format=\"102\">" + InvoiceDate + "</udt:DateTimeString>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        </ram:IssueDateTime>");
            writer.write(System.getProperty("line.separator"));
            writer.write("    </rsm:ExchangedDocument>");
            writer.write(System.getProperty("line.separator"));

            writer.write("    <rsm:SupplyChainTradeTransaction>");
            writer.write(System.getProperty("line.separator"));

// Schleife durch die Item - jedes Produkt ergibt ein IncludedSupplyChainTradeLineItem
            for (int j = 0; j < Items.length / 6; j = j + 1) {
                int i = j * 6;
                // Items[i]   = Steuer
                // Items[i+1] = Produkt
                // Items[i+2] = Brutto
                // Items[i+3] = Netto
                // Items[i+4] = Anzahl
                // Items[i+5] = ISBN
                Items[i + 2] = Items[i + 2].replace(",", ".");
                Items[i + 3] = Items[i + 3].replace(",", ".");

                writer.write("<        <ram:IncludedSupplyChainTradeLineItem>");
                writer.write(System.getProperty("line.separator"));
                writer.write("            <ram:AssociatedDocumentLineDocument>");
                writer.write(System.getProperty("line.separator"));
                writer.write("                <ram:LineID>" + Integer.toString(j) + "</ram:LineID>");
                writer.write(System.getProperty("line.separator"));
                writer.write("            </ram:AssociatedDocumentLineDocument>");
                writer.write(System.getProperty("line.separator"));
                writer.write("            <ram:SpecifiedTradeProduct>");
                writer.write(System.getProperty("line.separator"));
                writer.write("                <ram:SellerAssignedID>" + Items[i + 5] + "</ram:SellerAssignedID>");
                writer.write(System.getProperty("line.separator"));
                writer.write("                <ram:Name>" + Items[i + 1] + "</ram:Name>");
                writer.write(System.getProperty("line.separator"));
                writer.write("            </ram:SpecifiedTradeProduct>");
                writer.write(System.getProperty("line.separator"));
                writer.write("            <ram:SpecifiedLineTradeAgreement>");
                writer.write(System.getProperty("line.separator"));
                writer.write("                <ram:NetPriceProductTradePrice>");
                writer.write(System.getProperty("line.separator"));
                writer.write("                    <ram:ChargeAmount>" + Float.toString(Float.parseFloat(Items[i + 3]) / Integer.parseInt(Items[i + 4])) + "</ram:ChargeAmount>");
                writer.write(System.getProperty("line.separator"));
                writer.write("                </ram:NetPriceProductTradePrice>");
                writer.write(System.getProperty("line.separator"));
                writer.write("            </ram:SpecifiedLineTradeAgreement>");
                writer.write(System.getProperty("line.separator"));
                writer.write("            <ram:SpecifiedLineTradeDelivery>");
                writer.write(System.getProperty("line.separator"));
                writer.write("                <ram:BilledQuantity unitCode=\"PP\">" + Items[i + 4] + "</ram:BilledQuantity>");
                writer.write(System.getProperty("line.separator"));
                writer.write("            </ram:SpecifiedLineTradeDelivery>");
                writer.write(System.getProperty("line.separator"));
                writer.write("            <ram:SpecifiedLineTradeSettlement>");
                writer.write(System.getProperty("line.separator"));
                writer.write("                <ram:ApplicableTradeTax>");
                writer.write(System.getProperty("line.separator"));
                writer.write("                    <ram:TypeCode>VAT</ram:TypeCode>");
                writer.write(System.getProperty("line.separator"));
                writer.write("                    <ram:RateApplicablePercent>" + Items[i] + "</ram:RateApplicablePercent>");
                writer.write(System.getProperty("line.separator"));
                if (ReverseCharge) {
                    writer.write("                    <ram:CategoryCode>AE</ram:CategoryCode>");
                    writer.write(System.getProperty("line.separator"));
                } else {
                    writer.write("                    <ram:CategoryCode>S</ram:CategoryCode>");
                    writer.write(System.getProperty("line.separator"));
                }
                writer.write("                </ram:ApplicableTradeTax>");
                writer.write(System.getProperty("line.separator"));
                writer.write("                <ram:SpecifiedTradeSettlementLineMonetarySummation>");
                writer.write(System.getProperty("line.separator"));
                writer.write("                    <ram:LineTotalAmount>" + Float.toString(Float.parseFloat(Items[i + 3]) * Integer.parseInt(Items[i + 4])) + "</ram:LineTotalAmount>");
                writer.write(System.getProperty("line.separator"));
                writer.write("                </ram:SpecifiedTradeSettlementLineMonetarySummation>");
                writer.write(System.getProperty("line.separator"));
                writer.write("            </ram:SpecifiedLineTradeSettlement>");
                writer.write(System.getProperty("line.separator"));
                writer.write("        </ram:IncludedSupplyChainTradeLineItem>");
                writer.write(System.getProperty("line.separator"));
            } // for Produkte

            writer.write("        <ram:ApplicableHeaderTradeAgreement>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            <ram:BuyerReference>" + BuyerBestellzeichen + "</ram:BuyerReference>");
            writer.write(System.getProperty("line.separator"));

            writer.write("            <ram:SellerTradeParty>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <ram:Name>Carola Hartmann Miles Verlag</ram:Name>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <ram:Description>Geschäftsführung: Carola Hartmann</ram:Description>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <ram:DefinedTradeContact>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ram:PersonName>Carola Hartmann</ram:PersonName>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ram:TelephoneUniversalCommunication>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                        <ram:CompleteNumber>+49 (0)30-36288677</ram:CompleteNumber>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    </ram:TelephoneUniversalCommunication>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ram:EmailURIUniversalCommunication>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                        <ram:URIID>miles-verlag@t-online.de</ram:URIID>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    </ram:EmailURIUniversalCommunication>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                </ram:DefinedTradeContact>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <ram:PostalTradeAddress>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ram:PostcodeCode>14089</ram:PostcodeCode>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ram:LineOne>Alt Kladow 16d</ram:LineOne>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ram:LineTwo></ram:LineTwo>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ram:CityName>Berlin</ram:CityName>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ram:CountryID>DE</ram:CountryID>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                </ram:PostalTradeAddress>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <ram:SpecifiedTaxRegistration>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ram:ID schemeID=\"VA\">DE269369280</ram:ID>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                </ram:SpecifiedTaxRegistration>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            </ram:SellerTradeParty>");
            writer.write(System.getProperty("line.separator"));

            writer.write("            <ram:BuyerTradeParty>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <ram:Name>" + BuyerName + "</ram:Name>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <ram:PostalTradeAddress>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ram:PostcodeCode>" + BuyerPLZ + "</ram:PostcodeCode>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ram:LineOne>" + BuyerStrasse + "</ram:LineOne>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ram:LineTwo></ram:LineTwo>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ram:CityName>" + BuyerOrt + "</ram:CityName>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ram:CountryID>" + BuyerLand + "</ram:CountryID>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                </ram:PostalTradeAddress>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            </ram:BuyerTradeParty>");
            writer.write(System.getProperty("line.separator"));

            writer.write("        </ram:ApplicableHeaderTradeAgreement>");
            writer.write(System.getProperty("line.separator"));

            writer.write("        <ram:ApplicableHeaderTradeDelivery/>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        <ram:ApplicableHeaderTradeSettlement>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            <ram:TaxCurrencyCode>EUR</ram:TaxCurrencyCode>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            <ram:InvoiceCurrencyCode>EUR</ram:InvoiceCurrencyCode>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            <ram:SpecifiedTradeSettlementPaymentMeans>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <ram:SpecifiedTradeSettlementPaymentMeans>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ram:TypeCode>30</ram:TypeCode>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <ram:PayeePartyCreditorFinancialAccount>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ram:IBANID>DE12345678912345678913</ram:IBANID>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                </ram:PayeePartyCreditorFinancialAccount>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            </ram:SpecifiedTradeSettlementPaymentMeans>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            <ram:SpecifiedTradePaymentTerms>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <ram:DueDateDateTime>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <udt:DateTimeString format=\"102\">" + BillingDate + "</udt:DateTimeString>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                </ram:DueDateDateTime>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            </ram:SpecifiedTradePaymentTerms>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                </ram:SpecifiedTradePaymentTerms>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            </ram:SpecifiedTradeSettlementPaymentMeans>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            <ram:SpecifiedTradeSettlementHeaderMonetarySummation>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <ram:LineTotalAmount>" + setLineTotal + "</ram:LineTotalAmount>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <ram:TaxBasisTotalAmount>" + setTaxBasisTotal + "</ram:TaxBasisTotalAmount>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <ram:TaxTotalAmount currencyID=\"EUR\">" + setTaxTotal + "</ram:TaxTotalAmount>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <ram:GrandTotalAmount>" + setGrandTotal + "</ram:GrandTotalAmount>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <ram:DuePayableAmount>" + setDuePayable + "</ram:DuePayableAmount>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            <ram:SpecifiedTradeSettlementHeaderMonetarySummation>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        </ram:ApplicableHeaderTradeSettlement>");
            writer.write(System.getProperty("line.separator"));

            writer.write("    </rsm:SupplyChainTradeTransaction>");
            writer.write(System.getProperty("line.separator"));

            writer.write("</rsm:CrossIndustryInvoice>");
            writer.write(System.getProperty("line.separator"));

//Text in Datei schreiben            
            writer.flush();

// Schließt den Stream
            writer.close();
        } catch (IOException e) {
            Modulhelferlein.Infomeldung("IO-Exception: ", e.getMessage());
        }

        Modulhelferlein.Infomeldung(XMLFile + " wurde gespeichert!");

    }

    public static void createZUGFeRDInvoiceDocuments(Integer ERechnung,
            String InvoiceNumber,
            String InvoiceLanguage,
            String InvoiceDate,
            String InvoiceName,
            String BuyerName,
            String BuyerPLZ,
            String BuyerStrasse,
            String BuyerOrt,
            String BuyerLand,
            String BuyerUID,
            String BuyerBestellzeichen,
            String[] Items, // Items[i]   = Steuer
            // Items[i+1] = Produkt
            // Items[i+2] = Brutto
            // Items[i+3] = Netto
            // Items[i+4] = Anzahl
            // Items[i+5] = 
            Boolean ReverseCharge,
            String setLineTotal, //.setLineTotal(new Amount(100, EUR))
            String setChargeTotal, //.setChargeTotal(new Amount(0, EUR))
            String setAllowanceTotal, //.setAllowanceTotal(new Amount(0, EUR))
            String setTaxBasisTotal, //.setTaxBasisTotal(new Amount(100, EUR))
            String setTaxTotal, //.setTaxTotal(new Amount(19, EUR))
            String setDuePayable, //.setDuePayable(new Amount(119, EUR))
            String setTotalPrepaid, //.setTotalPrepaid(new Amount(0, EUR))
            String setGrandTotal,
            String outputFilename) {

//        setLineTotal = setLineTotal.replace(",", ".");
//        setTaxBasisTotal = setTaxBasisTotal.replace(",", ".");
//        setTaxTotal = setTaxTotal.replace(",", ".");
//        setGrandTotal = setGrandTotal.replace(",", ".");
//        setDuePayable = setDuePayable.replace(",", ".");
        System.out.println("Erzeuge ZUGFeRD-Dokumente");
        try {
            Invoice invoice = createZUGFeRDInvoice(InvoiceNumber,
                    InvoiceLanguage,
                    InvoiceDate,
                    InvoiceName,
                    BuyerName,
                    BuyerPLZ,
                    BuyerStrasse,
                    BuyerOrt,
                    BuyerLand,
                    BuyerUID,
                    BuyerBestellzeichen,
                    Items, // Items[i]   = Steuer
                    // Items[i+1] = Produkt
                    // Items[i+2] = Brutto
                    // Items[i+3] = Netto
                    // Items[i+4] = Anzahl
                    // Items[i+5] =
                    ReverseCharge,
                    setLineTotal, //.setLineTotal(new Amount(100, EUR))
                    setChargeTotal, //.setChargeTotal(new Amount(0, EUR))
                    setAllowanceTotal, //.setAllowanceTotal(new Amount(0, EUR))
                    setTaxBasisTotal, //.setTaxBasisTotal(new Amount(100, EUR))
                    setTaxTotal, //.setTaxTotal(new Amount(19, EUR))
                    setDuePayable, //.setDuePayable(new Amount(119, EUR))
                    setTotalPrepaid, //.setTotalPrepaid(new Amount(0, EUR))
                    setGrandTotal);
            transformInvoiceToXml(invoice, outputFilename + ".ZUGFeRD.xml");
            Modulhelferlein.Infomeldung("ZUGFeRD-XML-Rechnung erstellt ");

            if (ERechnung == 2) {
                appendInvoiceToPdf(invoice, outputFilename, outputFilename + ".ZUGFeRD.pdf");
                Modulhelferlein.Infomeldung("ZUGFerd-PDF erstellt ");
            }
        } catch (IOException ex) {
            Modulhelferlein.Fehlermeldung("Brief Rechnung", "ZUGFeRD-Rechnung: IO-Exception: ", ex.getMessage());
        }
    }

    /*
    
     */
    public static Invoice createZUGFeRDInvoice(String InvoiceNumber,
            String InvoiceLanguage,
            String InvoiceDate,
            String InvoiceName,
            String BuyerName,
            String BuyerPLZ,
            String BuyerStrasse,
            String BuyerOrt,
            String BuyerLand,
            String BuyerUID,
            String BuyerBestellzeichen,
            String[] Items, // Items[i]   = Steuer
            // Items[i+1] = Produkt
            // Items[i+2] = Brutto
            // Items[i+3] = Netto
            // Items[i+4] = Anzahl
            // Items[i+5] = 
            Boolean ReverseCharge,
            String setLineTotal, //.setLineTotal(new Amount(100, EUR))
            String setChargeTotal, //.setChargeTotal(new Amount(0, EUR))
            String setAllowanceTotal, //.setAllowanceTotal(new Amount(0, EUR))
            String setTaxBasisTotal, //.setTaxBasisTotal(new Amount(100, EUR))
            String setTaxTotal, //.setTaxTotal(new Amount(19, EUR))
            String setDuePayable, //.setDuePayable(new Amount(119, EUR))
            String setTotalPrepaid, //.setTotalPrepaid(new Amount(0, EUR))
            String setGrandTotal) {     //.setGrandTotal(new Amount(119, EUR))));   

//helferlein.Infomeldung(setDuePayable);   1.194,00
        System.out.println("Erstelle ZUGFeRD Invoice");

        setLineTotal = setLineTotal.replace(".", "");
        setLineTotal = setLineTotal.replace(",", ".");

        setChargeTotal = setChargeTotal.replace(".", "");
        setChargeTotal = setChargeTotal.replace(",", ".");

        setAllowanceTotal = setAllowanceTotal.replace(".", "");
        setAllowanceTotal = setAllowanceTotal.replace(",", ".");

        setTaxBasisTotal = setTaxBasisTotal.replace(".", "");
        setTaxBasisTotal = setTaxBasisTotal.replace(",", ".");

        setTaxTotal = setTaxTotal.replace(".", "");
        setTaxTotal = setTaxTotal.replace(",", ".");

        setDuePayable = setDuePayable.replace(".", "");
        setDuePayable = setDuePayable.replace(",", ".");

        setTotalPrepaid = setTotalPrepaid.replace(".", "");
        setTotalPrepaid = setTotalPrepaid.replace(",", ".");

        setGrandTotal = setGrandTotal.replace(".", "");
        setGrandTotal = setGrandTotal.replace(",", ".");

        System.out.println("Zahlenstrings angepasst");
        com.neovisionaries.i18n.CountryCode BuyerCountryCode = com.neovisionaries.i18n.CountryCode.DE;
        switch (BuyerLand) {
            case "DEUTSCHLAND":
                BuyerLand = "DE";
                BuyerCountryCode = com.neovisionaries.i18n.CountryCode.DE;
                break;
            case "NIEDERLANDE":
                BuyerLand = "NL";
                BuyerCountryCode = com.neovisionaries.i18n.CountryCode.NL;
                break;
            case "SCHWEIZ":
                BuyerLand = "CH";
                BuyerCountryCode = com.neovisionaries.i18n.CountryCode.CH;
                break;
            case "IRLAND":
                BuyerLand = "IE";
                BuyerCountryCode = com.neovisionaries.i18n.CountryCode.IE;
                break;
            case "PORTUGAL":
                BuyerLand = "PT";
                BuyerCountryCode = com.neovisionaries.i18n.CountryCode.PT;
                break;
            case "ÖSTERREICH":
                BuyerLand = "AT";
                BuyerCountryCode = com.neovisionaries.i18n.CountryCode.AT;
                break;
        }
        ZfDate today = new ZfDateDay(DateString2Date(InvoiceDate));
        // today = (ZfDate) SQLDateString2Date(InvoiceDate);
        //ZfDate nextMonth = new ZfDateMonth(addMonths(today, 1));
        ZfDate nextMonth = new ZfDateDay(DateUtils.addDays(today, 14));

        Invoice invoice = new Invoice(EXTENDED);
        invoice.setHeader(new Header()
                .setInvoiceNumber(InvoiceNumber)
                .setCode(_380)
                .setIssued(today)
                .setName(InvoiceName));

        System.out.println("Invoice-Modell erzeugt");

        Trade trade = new Trade();
        trade.setAgreement(new Agreement()
                .setSeller(new TradeParty()
                        .setName("Carola Hartmann Miles Verlag")
                        .setAddress(new Address("14089", "George Calay Straße 48", "Berlin", DE))
                        .addTaxRegistrations(new TaxRegistration("DE269369280", VA)))
                .setBuyer(new TradeParty()
                        .setName(BuyerName)
                        .setAddress(new Address(BuyerPLZ, BuyerStrasse, BuyerOrt, BuyerCountryCode))
                        .addTaxRegistrations(new TaxRegistration(BuyerUID, VA)))
                .setBuyerOrder(new ReferencedDocument(BuyerBestellzeichen)));

        System.out.println("Invoide: Buyer/Seller geschrieben");

        trade.setDelivery(new Delivery(nextMonth));
        System.out.println("Invoice: Lieferdatum geschrieben");

        for (int j = 0; j < Items.length / 6; j = j + 1) {
            int i = j * 6;

            System.out.println("Invoice: Erzeuge Item Nr. " + Integer.toString(j));
            Items[i + 2] = Items[i + 2].replace(".", "");
            Items[i + 2] = Items[i + 2].replace(",", ".");
            Items[i + 3] = Items[i + 3].replace(".", "");
            Items[i + 3] = Items[i + 3].replace(",", ".");

            ItemTax itemTax = new ItemTax();
            itemTax.setPercentage(BigDecimal.valueOf(Integer.parseInt(Items[i])));
            itemTax.setType(TaxCode.VAT);
            System.out.println("- VAT   : " + Items[i]);

            Item itemTrade = new Item();
            // ein TradeItem besteht aus
            // - Product
            // - SpecifiedAgreement
            // - PositionDocument
            // - SpecifiedSettlement
            // - SpecifiedDelivery

            Product itemTradeProduct = new Product();
            itemTradeProduct.setName(Items[i + 1]);
            System.out.println("- Name  : " + Items[i + 1]);
            itemTradeProduct.setSellerAssignedId(Items[i + 5]);
            System.out.println("- ID    : " + Items[i + 5]);

            SpecifiedAgreement itemTradeSpecifiedAgreement = new SpecifiedAgreement();

            PositionDocument itemTradePositionDocument = new PositionDocument(j);

            if (ReverseCharge) {
                System.out.println("- Reverse Charge");
                itemTax.setCategory(TaxCategory.AE);
                System.out.println("  => Category AE");
                itemTax.setPercentage(BigDecimal.valueOf(Integer.parseInt("0")));
                System.out.println("  => 0%");
                itemTradeSpecifiedAgreement.setGrossPrice(new GrossPrice(new Amount(new BigDecimal(Items[i + 3]), EUR))).setNetPrice(new Price(new Amount(new BigDecimal(Items[i + 3]), EUR)));
                System.out.println("  => 0% => Brutto:" + Items[i + 3] + " => Netto: " + Items[i + 3]);
            } else {
                System.out.println("- Normal Charge");
                itemTax.setCategory(TaxCategory.S);
                System.out.println("  => Category S");
                itemTax.setPercentage(BigDecimal.valueOf(Integer.parseInt(Items[i])));
                System.out.println("  => " + Items[i] + "%");
                itemTradeSpecifiedAgreement.setGrossPrice(new GrossPrice(new Amount(new BigDecimal(Items[i + 2]), EUR))).setNetPrice(new Price(new Amount(new BigDecimal(Items[i + 3]), EUR)));
                System.out.println("  => Brutto:" + Items[i + 2] + " => Netto: " + Items[i + 3]);
            }

            SpecifiedSettlement itemTradeSpecifiedSettlement = new SpecifiedSettlement();
            itemTradeSpecifiedSettlement.addTradeTax(itemTax);

            SpecifiedDelivery itemTradeSpecifiedDelivery = new SpecifiedDelivery();
            if (Items[i + 1].equals("Versandkosten") || Items[i + 1].equals("Shipping costs")) {
                itemTradeSpecifiedDelivery = new SpecifiedDelivery(new Quantity(Integer.parseInt(Items[i + 4]), LUMP_SUM));
            } else {
                itemTradeSpecifiedDelivery = new SpecifiedDelivery(new Quantity(Integer.parseInt(Items[i + 4]), UNIT));
            }

            itemTrade.setProduct(itemTradeProduct);
            itemTrade.setAgreement(itemTradeSpecifiedAgreement);
            itemTrade.setSettlement(itemTradeSpecifiedSettlement);
            itemTrade.setDelivery(itemTradeSpecifiedDelivery);
            itemTrade.setPosition(itemTradePositionDocument);

            trade.addItem(itemTrade);
            System.out.println("Invoice: Item geschrieben");
            //trade.addItem(new Item()
            //        .setProduct(new Product().setName(Items[i + 1]))
            //        .setAgreement(new SpecifiedAgreement().setGrossPrice(new GrossPrice(new Amount(Items[i + 2], EUR))).setNetPrice(new Price(new Amount(Items[i + 3], EUR))))
            //        .setSettlement(new SpecifiedSettlement().addTradeTax(itemTax))
            //        .setDelivery(new SpecifiedDelivery(new Quantity(Integer.parseInt(Items[i + 4]), UNIT))));
        }

//helferlein.Infomeldung("items iteriert");
        System.out.println("Invoice: done items/products");
        PaymentMeans itemPaymentMeans = new PaymentMeans();
        itemPaymentMeans.setPayerAccount(new DebtorFinancialAccount("DE61100900002233832017"));
        itemPaymentMeans.setPayerInstitution(new FinancialInstitution("Volksbank Berlin"));

//helferlein.Infomeldung("done paymentmeans");
        System.out.println("Invoice: done payment means");
        MonetarySummation itemMonetarySummation = new MonetarySummation();
        if (ReverseCharge) {
            itemMonetarySummation.setLineTotal(new Amount(new BigDecimal(setLineTotal), EUR));
            itemMonetarySummation.setChargeTotal(new Amount(new BigDecimal(setChargeTotal), EUR));
            itemMonetarySummation.setAllowanceTotal(new Amount(new BigDecimal(setAllowanceTotal), EUR));
            itemMonetarySummation.setTaxBasisTotal(new Amount(new BigDecimal(setLineTotal), EUR));
            itemMonetarySummation.setTaxTotal(new Amount(new BigDecimal("0"), EUR));
            itemMonetarySummation.setDuePayable(new Amount(new BigDecimal(setLineTotal), EUR));
            itemMonetarySummation.setTotalPrepaid(new Amount(new BigDecimal(setTotalPrepaid), EUR));
            itemMonetarySummation.setGrandTotal(new Amount(new BigDecimal(setLineTotal), EUR));
        } else {
            itemMonetarySummation.setLineTotal(new Amount(new BigDecimal(setLineTotal), EUR));
            itemMonetarySummation.setChargeTotal(new Amount(new BigDecimal(setChargeTotal), EUR));
            itemMonetarySummation.setAllowanceTotal(new Amount(new BigDecimal(setAllowanceTotal), EUR));
            itemMonetarySummation.setTaxBasisTotal(new Amount(new BigDecimal(setTaxBasisTotal), EUR));
            itemMonetarySummation.setTaxTotal(new Amount(new BigDecimal(setTaxTotal), EUR));
            itemMonetarySummation.setDuePayable(new Amount(new BigDecimal(setDuePayable), EUR));
            itemMonetarySummation.setTotalPrepaid(new Amount(new BigDecimal(setTotalPrepaid), EUR));
            itemMonetarySummation.setGrandTotal(new Amount(new BigDecimal(setGrandTotal), EUR));
        }

//helferlein.Infomeldung("done MonetarySummation");
        System.out.println("Invoice: done Monetary Summation");
        Settlement itemSettlement = new Settlement();
        itemSettlement.setPaymentReference(InvoiceNumber);
        itemSettlement.setCurrency(EUR);
        itemSettlement.addPaymentMeans(itemPaymentMeans);
        itemSettlement.setMonetarySummation(itemMonetarySummation);

//helferlein.Infomeldung("done Settlement");
        System.out.println("Invoice: done settlement");
        trade.setSettlement(itemSettlement);

        /*             
        trade.setSettlement(new Settlement()
                .setPaymentReference(InvoiceNumber)
                .setCurrency(EUR)
                .addPaymentMeans(new PaymentMeans()
                        .setPayerAccount(new DebtorFinancialAccount("DE61100900002233832017"))
                        .setPayerInstitution(new FinancialInstitution("Volksbank Berlin")))
                .setMonetarySummation(new MonetarySummation()
                        .setLineTotal(new Amount(new BigDecimal(setLineTotal), EUR))
                        .setChargeTotal(new Amount(new BigDecimal(setChargeTotal), EUR))
                        .setAllowanceTotal(new Amount(new BigDecimal(setAllowanceTotal), EUR))
                        .setTaxBasisTotal(new Amount(new BigDecimal(setTaxBasisTotal), EUR))
                        .setTaxTotal(new Amount(new BigDecimal(setTaxTotal), EUR))
                        .setDuePayable(new Amount(new BigDecimal(setDuePayable), EUR))
                        .setTotalPrepaid(new Amount(new BigDecimal(setTotalPrepaid), EUR))
                        .setGrandTotal(new Amount(new BigDecimal(setGrandTotal), EUR))));
         */
        invoice.setTrade(trade);
        System.out.println("Invoice: done trade");
        return invoice;

        //Invoice completedInvoice = new InvoiceCalculator(invoice).complete(); 
        //return completedInvoice;
    }

    /*
    
     */
    public static void transformInvoiceToXml(Invoice invoice, String outputFile) throws IOException {
        System.out.println("Erzeuge ZUGFeRD-XML");
        try {
            InvoiceTransformer transformer = new InvoiceTransformer();
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            transformer.fromModel(invoice, outputStream);
        } catch (IOException ex) {
            Modulhelferlein.Fehlermeldung("ZUGFerd XML erstellen", "IO-Exception: ", ex.getMessage());
        }
    }

    /**
     *
     * @param invoice
     * @param inputFile
     * @param outputFile
     * @throws IOException
     */
    /*
        public void appendInvoiceToPdf() throws IOException {
            Invoice invoice = createInvoice();
            PdfHandler handler = new PdfHandler();   
            InputStream inputPdf = getClass().getResourceAsStream("/acme_invoice-42.pdf");
            OutputStream resultingPdf = new FileOutputStream("target/acme_invoice-42_ZUGFeRD.pdf");
            handler.appendInvoice(invoice, inputPdf, resultingPdf);   
        }
     */
    public static void appendInvoiceToPdf(Invoice invoice, String inputFile, String outputFile) throws IOException {
        System.out.println("Erzeue ZUGFeRD-PDF");
        try {
            PdfHandler handler = new PdfHandler();
            InputStream inputPdf = null;

            inputPdf = new FileInputStream(inputFile);

            OutputStream resultingPdf = new FileOutputStream(outputFile);
            handler.appendInvoice(invoice, inputPdf, resultingPdf);
        } catch (IOException ex) {
            Modulhelferlein.Fehlermeldung("ZUGFerd PDF erstellen", "IO-Exception: ", ex.getMessage());
        }
    }

}
