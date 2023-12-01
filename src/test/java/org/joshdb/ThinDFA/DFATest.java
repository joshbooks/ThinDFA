package org.joshdb.ThinDFA;

import org.testng.annotations.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.fail;

/**
 * Created by josh.hight on 11/29/16.
 */
public class DFATest
{
    private static final String ssnDFA = new SSNDFA().toJson();
    private static final String ipDFA = new IPDFA().toJson();
    private static final String emailDFA = new EmailDFA().toJson();
    private static final String einDFA = new EINDFA().toJson();
    private static final String ccDFA = new LuhnCCDFA(16).toJson(); // generic
    private static final String amexCCDFA = new LuhnCCDFA(15, new ArrayList<>(Arrays.asList("34", "37"))).toJson(); // AMEX
    private static final String discCCDFA = new LuhnCCDFA(16, new ArrayList<>(Arrays.asList("6011", "622126", "622127","622286", "644"))).toJson(); // Discover 6011, 622126-622925
    private static final String visaCCDFA = new LuhnCCDFA(16, new ArrayList<>(Arrays.asList("4"))).toJson(); // VISA
    private static final String maestroCCDFA = new LuhnCCDFA(12, 18, new ArrayList<>(Arrays.asList("50", "56", "57", "58", "60", "67")), new ArrayList<>(Arrays.asList("6011", "622126", "622127","622286", "644"))).toJson(); // fuckin maestro
    private static final String mcCCDFA = new LuhnCCDFA(16, new ArrayList<>(Arrays.asList("51", "52", "53"))).toJson(); // Mastercard
    private static final String dcCCDFA = new LuhnCCDFA(14, new ArrayList<>(Arrays.asList("300"))).toJson(); // lol Diners Club Int'l
    private static final String dccopyCCDFA = new LuhnCCDFA(14, new ArrayList<>(Arrays.asList("300"))).toJson();
    private static final String usCurrencyDFA = new USCurrencyDFA().toJson();
    private static final String hpidDFA = new HPIDDFA().toJson();
    private static final String abaDFA = new ABADFA().toJson();
    private static final String mbidDFA = new MBIDDFA().toJson();
    private static final String deaDFA = new DEANumberDFA().toJson();
    private static final String zipDFA = new FiveFourZipDFA().toJson();
    private static final String dateDFA = new DateDFA().toJson();
    private static final String prnDFA = new PRNDFA().toJson();

    @Test
    public void printJsonDfas()
    {
        Path ssnFile = Paths.get("SSN.json");
        Path ipFile = Paths.get("IP.json");
        Path emailFile = Paths.get("EMAIL.json");
        Path einFile = Paths.get("EIN.json");
        Path ccFile = Paths.get("CC.json");
        Path usCurrencyFile = Paths.get("USCURRENCY.json");
        Path hpidFile = Paths.get("HPID.json");
        Path abaFile = Paths.get("ABA.json");
        Path mbidFile = Paths.get("MBID.json");
        Path deaFile = Paths.get("DEA.json");
        Path zipFile = Paths.get("ZIP.json");
        Path dateFile = Paths.get("DATE.json");
        Path prnFile = Paths.get("PRN.json");


        for (Path outFile : new Path[]{ssnFile, ipFile, emailFile, einFile, ccFile, usCurrencyFile, hpidFile, abaFile, deaFile, zipFile, dateFile, prnFile})
        {
            if (Files.exists(outFile))
            {
                try
                {
                    Files.delete(outFile);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    fail();
                }
            }
        }

        try(OutputStream ssnWriter = Files.newOutputStream(ssnFile);
            OutputStream ipWriter = Files.newOutputStream(ipFile);
            OutputStream emailWriter = Files.newOutputStream(emailFile);
            OutputStream einWriter = Files.newOutputStream(einFile);
            OutputStream ccWriter = Files.newOutputStream(ccFile);
            OutputStream usCurrencyWriter = Files.newOutputStream(usCurrencyFile);
            OutputStream hpidWriter = Files.newOutputStream(hpidFile);
            OutputStream abaWriter = Files.newOutputStream(abaFile);
            OutputStream mbidWriter = Files.newOutputStream(mbidFile);
            OutputStream deaWriter = Files.newOutputStream(deaFile);
            OutputStream zipWriter = Files.newOutputStream(zipFile);
            OutputStream dateWriter = Files.newOutputStream(dateFile);
            OutputStream prnWriter = Files.newOutputStream(prnFile);
            )
        {
            ssnWriter.write(ssnDFA.getBytes(StandardCharsets.UTF_8));
            ipWriter.write(ipDFA.getBytes(StandardCharsets.UTF_8));
            emailWriter.write(emailDFA.getBytes(StandardCharsets.UTF_8));
            einWriter.write(einDFA.getBytes(StandardCharsets.UTF_8));
            ccWriter.write(ccDFA.getBytes(StandardCharsets.UTF_8));
            usCurrencyWriter.write(usCurrencyDFA.getBytes(StandardCharsets.UTF_8));
            hpidWriter.write(hpidDFA.getBytes(StandardCharsets.UTF_8));
            abaWriter.write(abaDFA.getBytes(StandardCharsets.UTF_8));
            mbidWriter.write(mbidDFA.getBytes(StandardCharsets.UTF_8));
            deaWriter.write(deaDFA.getBytes(StandardCharsets.UTF_8));
            zipWriter.write(zipDFA.getBytes(StandardCharsets.UTF_8));
            dateWriter.write(dateDFA.getBytes(StandardCharsets.UTF_8));
            prnWriter.write(prnDFA.getBytes(StandardCharsets.UTF_8));

        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void printDotDfas()
    {
        Path ssnFile = Paths.get("SSN.gv");
        Path ipFile = Paths.get("IP.gv");
        Path emailFile = Paths.get("EMAIL.gv");
        Path einFile = Paths.get("EIN.gv");
        Path ccFile = Paths.get("CC.gv");
        Path usCurrencyFile = Paths.get("USCURRENCY.gv");
        Path hpidFile = Paths.get("HPID.gv");
        Path abaFile = Paths.get("ABA.gv");
        Path mbidFile = Paths.get("MBID.gv");
        Path deaFile = Paths.get("DEA.gv");
        Path zipFile = Paths.get("ZIP.gv");
        Path dateFile = Paths.get("DATE.gv");
        Path prnFile = Paths.get("PRN.gv");


        for (Path outFile : new Path[]{ssnFile, ipFile, emailFile, einFile, ccFile, usCurrencyFile, hpidFile, abaFile, mbidFile, deaFile, zipFile, dateFile, prnFile})
        {
            if (Files.exists(outFile))
            {
                try
                {
                    Files.delete(outFile);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    fail();
                }
            }
        }

        try(OutputStream ssnWriter = Files.newOutputStream(ssnFile);
            OutputStream ipWriter = Files.newOutputStream(ipFile);
            OutputStream emailWriter = Files.newOutputStream(emailFile);
            OutputStream einWriter = Files.newOutputStream(einFile);
            OutputStream ccWriter = Files.newOutputStream(ccFile);
            OutputStream usCurrencyWriter = Files.newOutputStream(usCurrencyFile);
            OutputStream hpidWriter = Files.newOutputStream(hpidFile);
            OutputStream abaWriter = Files.newOutputStream(abaFile);
            OutputStream mbidWriter = Files.newOutputStream(mbidFile);
            OutputStream deaWriter = Files.newOutputStream(deaFile);
            OutputStream zipWriter = Files.newOutputStream(zipFile);
            OutputStream dateWriter = Files.newOutputStream(dateFile);
            OutputStream prnWriter = Files.newOutputStream(prnFile);
        )
        {
            ssnWriter.write(ThinDFA.fromJson(ssnDFA).toDot().getBytes(StandardCharsets.UTF_8));
            ipWriter.write(ThinDFA.fromJson(ipDFA).toDot().getBytes(StandardCharsets.UTF_8));
            emailWriter.write(ThinDFA.fromJson(emailDFA).toDot().getBytes(StandardCharsets.UTF_8));
            einWriter.write(ThinDFA.fromJson(einDFA).toDot().getBytes(StandardCharsets.UTF_8));
            ccWriter.write(ThinDFA.fromJson(ccDFA).toDot().getBytes(StandardCharsets.UTF_8));
            usCurrencyWriter.write(ThinDFA.fromJson(usCurrencyDFA).toDot().getBytes(StandardCharsets.UTF_8));
            hpidWriter.write(ThinDFA.fromJson(hpidDFA).toDot().getBytes(StandardCharsets.UTF_8));
            abaWriter.write(ThinDFA.fromJson(abaDFA).toDot().getBytes(StandardCharsets.UTF_8));
            mbidWriter.write(ThinDFA.fromJson(mbidDFA).toDot().getBytes(StandardCharsets.UTF_8));
            deaWriter.write(ThinDFA.fromJson(deaDFA).toDot().getBytes(StandardCharsets.UTF_8));
            zipWriter.write(ThinDFA.fromJson(zipDFA).toDot().getBytes(StandardCharsets.UTF_8));
            dateWriter.write(ThinDFA.fromJson(dateDFA).toDot().getBytes(StandardCharsets.UTF_8));
            prnWriter.write(ThinDFA.fromJson(prnDFA).toDot().getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void testPrintDFA()
    {
        System.out.println(ccDFA);
        ThinDFA bassAckwards = ThinDFA.fromJson(ccDFA);
        String wtf = ThinDFA.toJson(bassAckwards);
        System.out.println(wtf);
        assert wtf.length() == ccDFA.length();
        assert bassAckwards.jsonEquals(ccDFA);
        ThinDFA bwarAssackds = ThinDFA.fromJson(wtf);
        assert(bwarAssackds.equals(bassAckwards));
    }

    @Test
    public void testUnionDfa()
    {
        try
        {
            ThinDFA unionDfa = new UnionDFA(new String[]{"hello", "hella"});

            assert unionDfa.update("hella") == 1;
            assert unionDfa.update("hello") == 1;

            String[] confederacy = new String[] {"hello", "hella", "helsinki"};
            ThinDFA confederateDfa = new UnionDFA(confederacy);

            for (String i : confederacy)
            {
                System.out.println(i);
                assert confederateDfa.update(i) == 1;
            }

        }
        catch (UnionDFA.IncompatibleRegexException e)
        {
            fail("Got an incompatible regex exception, that's not good");
        }

        Throwable expectedError = null;

        try
        {
            ThinDFA unionDfa = new UnionDFA(new String[]{"arkansas", "ark"});
        } catch (Throwable t)
        {
            expectedError = t;
        }

        assert expectedError != null;
        assert expectedError instanceof UnionDFA.IncompatibleRegexException;

        expectedError = null;

        try
        {
            ThinDFA unionDfa = new UnionDFA(new String[]{"ark", "arkansas"});
        } catch (Throwable t)
        {
            expectedError = t;
        }
        assert expectedError != null;
        assert expectedError instanceof UnionDFA.IncompatibleRegexException;
    }

    @Test
    public void testPRNDFA()
    {
        ThinDFA prn = ThinDFA.fromJson(prnDFA);

        assert prn.update("DP278077 ") == 1;
    }

    @Test
    public void testCCDFA()
    {
        ThinDFA cc = ThinDFA.fromJson(ccDFA);
        //bogus, then real
        assert cc.update("1111-1111-1111-1111 ") == 0;
        assert cc.update("4111111111111111 ") == 1;

        ThinDFA amexDFA = ThinDFA.fromJson(amexCCDFA);
        assert amexDFA.update("371238839571772 ") == 1;
        assert amexDFA.update("341238839571779 ") == 1;
        assert amexDFA.update("371238839571773 ") == 0;
        assert amexDFA.update("3712388395717723") == 0;
        assert amexDFA.update("3812388395717706") == 0;

        ThinDFA discDFA = ThinDFA.fromJson(discCCDFA);
        assert discDFA.update("6011111111111117 ") == 1;
        assert discDFA.update("60111111111111170") == 0;
        assert discDFA.update("6011111111111118 ") == 0;
        assert discDFA.update("6011-1111-1111-1117 ") == 1;
        assert discDFA.update("6013111111111115 ") == 0;
        assert discDFA.update("6221267264888368 ") == 1;
        assert discDFA.update("6221267264888368 ") == 1;
        assert discDFA.update("62212672648883684") == 0;
        assert discDFA.update("622126726488837 ") == 0;
        assert discDFA.update("6321267264888367 ") == 0;

        ThinDFA visaDFA = ThinDFA.fromJson(visaCCDFA);
        assert visaDFA.update("4111-1111-1111-1111 ") == 1;
        assert visaDFA.update("5111111111111111 ") == 0;
        assert visaDFA.update("4111111111111112 ") == 0;
        assert visaDFA.update("411111111111116") == 0;
        assert visaDFA.update("41111111111111113") == 0;

        ThinDFA dcDFA = ThinDFA.fromJson(dcCCDFA);
        assert dcDFA.update("30000000000004 ") == 1;
        assert dcDFA.update("30000000000005 ") == 0;
        assert dcDFA.update("31000000000003 ") == 0;
        assert dcDFA.update("3000000000007 " ) == 0;
        assert dcDFA.update("300000000000049") == 0;

        ThinDFA dccopyDFA = ThinDFA.fromJson(dccopyCCDFA);
        assert dccopyDFA.update("30000000000004 ") == 1;
        assert dccopyDFA.update("30000000000005 ") == 0;
        assert dccopyDFA.update("31000000000003 ") == 0;
        assert dccopyDFA.update("3000000000007 " ) == 0;
        assert dccopyDFA.update("300000000000049") == 0;

        ThinDFA evenMaestroDFA = ThinDFA.fromJson(maestroCCDFA);
        assert evenMaestroDFA.update("50339619890917 ") == 1;
        assert evenMaestroDFA.update("586824160825533338 ") == 1;
        assert evenMaestroDFA.update("6759411100000008 ") == 1;
        assert evenMaestroDFA.update("6799990100000000019 ") == 0;
        assert evenMaestroDFA.update("6011111111111117 ") == 0;

        ThinDFA mcDFA = ThinDFA.fromJson(mcCCDFA);
        assert mcDFA.update("5105105105105100 ") == 1;
        assert mcDFA.update("5105105105105101 ") == 0;
        assert mcDFA.update("510510510510515 ") == 0;
        assert mcDFA.update("51051051051051005") == 0;
        assert mcDFA.update("5805105105105103 ") == 0;

    }

    @Test
    public void testEINDFA()
    {
        ThinDFA ein = ThinDFA.fromJson(einDFA);
        assert ein.update("10-8888888 ") == 1;
        assert ein.update("Acme Co's EIN is 26-1234567 ") == 1;
        assert ein.update("18-8888888 ") == 0;
        assert ein.update("70-1231238 ") == 0;
    }

    @Test
    public void testDate()
    {
        ThinDFA date = ThinDFA.fromJson(dateDFA);

        assert date.update("12-02-92 ") == 1;
        assert date.update("12-02-1992 ") == 1;

        assert date.update("12/02/92 ") == 1;
        assert date.update("12/02/1992 ") == 1;

        assert date.update("1999-12-01 ") == 1;
    }

    @Test
    public void testEssentialMedications()
    {
        try
        {
            ThinDFA essentials = new EssentialMedicationsDFA();

            assert essentials.update("testosterone ") == 1;

            System.out.println();
        }
        catch (UnionDFA.IncompatibleRegexException e)
        {
            fail("The union of all essential medications is impossible to perfectly represent as a DFA", e);
        }
    }

    @Test
    public void testCommonIcd10()
    {
        try
        {
            ThinDFA commonIcd10 = new CommonICD10DFA();

            assert commonIcd10.update("Z82.49 ") == 1;
        }
        catch (UnionDFA.IncompatibleRegexException e)
        {
            fail("The union of all commonly used ICD-10 codes is impossible to perfectly represent as a DFA", e);
        }
    }

    @Test
    public void testCommonIcd9()
    {
        try
        {
            ThinDFA commonIcd9 = new CommonICD9DFA();

            assert commonIcd9.update("278.01") == 1;
        }
        catch (UnionDFA.IncompatibleRegexException e)
        {
            fail("The union of all commonly used ICD-9 codes is impossible to perfectly represent as a DFA", e);
        }

    }

    @Test
    public void testZip()
    {
        ThinDFA zip = ThinDFA.fromJson(zipDFA);
        assert zip.update("12345-6789 ") == 1;
    }

    @Test
    public void testDeaDFA()
    {
        ThinDFA dea = ThinDFA.fromJson(deaDFA);

        assert dea.update("AP5836727 ") == 1;
    }

    @Test
    public void testIPDFA()
    {
        ThinDFA ip = ThinDFA.fromJson(ipDFA);
        assert ip.update("192.168.1.1 ") == 1;
        assert ip.update("8.8.8.8 ") == 1;
        assert ip.update("256.256.256.256 ") == 0;
        assert ip.update("1.1.1.256 ") == 0;
        assert ip.update("8.8.256.8 ") == 0;
        assert ip.update("8.256.8.8 ") == 0;
        assert ip.update("256.8.8.8 ") == 0;
        assert ip.update("008.8.8.8 ") == 1;
        assert ip.update("8.008.8.8 ") == 1;
        assert ip.update("8.8.008.8 ") == 1;
        assert ip.update("8.8.8.008 ") == 1;
        assert ip.update("000255.000255.000255.000255 ") == 0;
        assert ip.update("1.1.1.00256 ") == 0;
        assert ip.update("8.008.256.8 ") == 0;
        assert ip.update("127.000.000.001 ") == 1;
        assert ip.update("0.0.0.0 ") == 1;
        assert ip.update("000.000.000.000 ") == 1;
    }

    @Test
    public void testEmailDFA()
    {
        ThinDFA email = ThinDFA.fromJson(emailDFA);
        long count;

        count = email.update("josh.hight@test.com ");
        System.out.println("got a match_count of " + count + " on the string 'josh.hight@test.com'");
        assert count == 1;

        count = email.update("josh.hight@[8.8.8.8] ");
        System.out.println("got a match_count of " + count + " on the string 'josh.hight@[8.8.8.8]'");
        assert count == 1;

        count = email.update("josh.hight@[256.8.8.8] ");
        System.out.println("got a match_count of " + count + " on the string 'josh.hight@[256.8.8.8]'");
        assert count == 0;

        count = email.update("josh.hight@[8.256.8.8] ");
        System.out.println("got a match_count of " + count + " on the string 'josh.hight@[8.256.8.8]'");
        assert count == 0;

        count = email.update("josh.hight@[8.8.256.8] ");
        System.out.println("got a match_count of " + count + " on the string 'josh.hight@[8.8.256.8]'");
        assert count == 0;

        count = email.update("josh.hight@[8.8.8.256] ");
        System.out.println("got a match_count of " + count + " on the string 'josh.hight@[8.8.8.256]'");
        assert count == 0;

    }

    @Test
    public void testSSNDFA()
    {
        ThinDFA ssn = ThinDFA.fromJson(ssnDFA);
        assert ssn.update("111-11-1111 ") == 1;
    }

    @Test
    public void testMBIDDFA()
    {
        ThinDFA mbid = ThinDFA.fromJson(mbidDFA);

        String root = "111-11-1111";
        String space = " ";
        String [] suffixes = new String[] {"A", "B1", "B2", "B3", "B4", "B5", "B6", "BY", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "D", "D1", "D2", "D3", "D4", "D5", "D6", "E", "E1", "E2", "E3", "E4", "E5", "F", "F1", "F2", "F3", "F4", "F5", "F6", "HA", "HB", "HC", "M", "M1", "T", "TA", "TB", "W", "W1", "W2", "W3", "W4", "W5", "W6", "WA"};

        for (String suffix : suffixes)
        {
            String thisId = root+suffix+space;
            System.out.println("Now testing: "+thisId);

            assert mbid.update(thisId) == 1;
        }
    }

    @Test
    public void testUSCurrencyDFA()
    {
        ThinDFA cash = ThinDFA.fromJson(usCurrencyDFA);

        assert cash.update("I spent $5.50 on this meal.") == 1;
        assert cash.update("$1 ") == 1;
        assert cash.update("$ 1 ") == 1;
        assert cash.update("USD 1 ") == 1;
        assert cash.update("USD1 ") == 1;
        assert cash.update("$12 ") == 1;
        assert cash.update("$123 ") == 1;
        assert cash.update("$1,234 ") == 1;
        assert cash.update("$1234 ") == 1;
        assert cash.update("$1,234,567 ") == 1;
        assert cash.update("$12,345.00 ") == 1;
        assert cash.update("$1.00 ") == 1;
        assert cash.update("$12345.67 ") == 1;

        assert cash.update("USDA ") == 0;
        assert cash.update("$ A ") == 0;
        assert cash.update("USD A ") == 0;
        assert cash.update("$A ") == 0;
        assert cash.update("$01 ") == 0;
        assert cash.update("$12,2 ") == 0;
        assert cash.update("$12.0 ") == 0;
        assert cash.update("$1.0000 ") == 0;
    }

    @Test
    public void testHPIDDFA()
    {
        ThinDFA doc = ThinDFA.fromJson(hpidDFA);

        assert doc.update("1588667638") == 1;
        assert doc.update("1497758544") == 1;
        assert doc.update("1306849450") == 1;
        assert doc.update("1215930367") == 1;
        assert doc.update("1114920261") == 1;
        assert doc.update("1023011079") == 1;
        assert doc.update("1932102985") == 1;
        assert doc.update("1841293891") == 1;
        assert doc.update("1750384707") == 1;
        assert doc.update("1669475612") == 1;
        assert doc.update("1578566527") == 1;
        assert doc.update("1487657433") == 1;
        assert doc.update("The HPID for David Wiebe, MD, is 1679576722.") == 1;

        assert doc.update("1588667639") == 0;
        assert doc.update("1497758543") == 0;
        assert doc.update("1306849451") == 0;
        assert doc.update("1215930363") == 0;
        assert doc.update("1114920265") == 0;
        assert doc.update("1023011073") == 0;
        assert doc.update("1932102987") == 0;
        assert doc.update("1841293894") == 0;
        assert doc.update("1750384702") == 0;
        assert doc.update("1669475617") == 0;
        assert doc.update("1578566526") == 0;
        assert doc.update("1487657435") == 0;
    }

    @Test
    public void testABADFA()
    {
        ThinDFA routing = ThinDFA.fromJson(abaDFA);

        assert routing.update("067014822 ") == 1;
        assert routing.update("TD Bank's Connecticut branch ABA routing number is: 0111-0309-3.") == 1;
        assert routing.update("211274450 ") == 1;

        assert routing.update("4588-6673-9 ") == 0;
        assert routing.update("558866639 ") == 0;
        assert routing.update("958867639 ") == 0;

    }
}
