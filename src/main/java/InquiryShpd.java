import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;

import static java.lang.String.format;

class InquiryShpd {
    static test<Boolean, String> bol = (t, snn) -> {
        int i = 0;
        for (; snn.length() > i; i++)
            if (snn.charAt(i) >= '0' & snn.charAt(i) <= '9')
                snn.charAt(i);
            else
                break;
        return snn.length() == i;
    };
    private static Function<Character, Character> se = (snn) -> {
        if (snn >= '0' & snn <= '9')
            return snn;
        return ' ';
    };

    static Function<String, String> o = (snn) -> {
        StringBuilder sr = new StringBuilder();
        for(int i=0; snn.length() > i; i++) {
            if ((snn.charAt(i) >= '0' & snn.charAt(i) <= '9') | snn.charAt(i) == ' ' )
                sr.append(se.apply(snn.charAt(i)));
            else
                break;
        }
        return sr.toString().trim();
    };

    private static Function<String, String> o1 = (snn) -> {
        StringBuilder sr = new StringBuilder();
        for (int i = 0; snn.length() > i; i++)
            if (snn.charAt(i) >= '0' & snn.charAt(i) <= '9')
                sr.append(snn.charAt(i));
            else
                break;

        return sr.toString().trim();
    };

    private static Function<String, String> sn = (snn) -> {
        StringBuilder sr = new StringBuilder();
        for(int i=0; snn.length() > i; i++)
            sr.append(se.apply(snn.charAt(i)));
        return sr.toString().trim();
    };

    private static String text;
    private static String[] tech;


    InquiryShpd(String text, String v) {
        this.text = text;

     //   System.out.println("<<<" + text + ">>>");
    }



    int compare(int[] nums) throws InterruptedException {
        String p0 = "clmns-";

        tech = new String[21];
        int n = 3;
        tech[1] = text.substring(text.indexOf('-') + 2, text.indexOf('<'));       // номер наряда
        tech[20] = "ПД";

        for(int i : nums) {

            if(  text.indexOf(p0, text.indexOf(p0 + i ) + 1) > 0  & text.indexOf( p0 + i) > 0 & i < 28)
                tech[n++] = form_title(text.substring( text.indexOf( p0 + i), text.indexOf(p0, text.indexOf( p0 + i) + 1)).replace('|', ' '));
            if(i == 28)
                tech[n++] = form_title( text.substring( text.indexOf(p0 + i) , text.indexOf("div" , text.indexOf(p0 + i) + 1) )).replace('|', ' ');
            if(n == 9) n = 11;

        }
        //System.out.println("!!!Shpd->put" + Arrays.toString(tech) + "\n");

        StoreInquiry.Start = Inquiry.insert_bd(tech);

        return text.length();
    }

    private static String form_title(String s) throws InterruptedException {

        int i = 0;
        switch (s.substring(0,s.indexOf('_'))) {
            case "clmns-1-j":
                tech[0] =  o.apply(s.substring(s.indexOf("title=") + 7 , s.indexOf( '"', s.indexOf("title=") + 7))).trim();
                if(s.substring(s.indexOf("title=") + 7 , s.indexOf( '"', s.indexOf("title=") + 7)).contains("IMS"))
                    return tech[0] + sn.apply(s.substring(s.indexOf("title=") + 7 , s.indexOf( '"', s.indexOf("title=") + 7)).substring(tech[0].length() + 1));
                else
                    return "77" + sn.apply(s.substring(s.indexOf("title=") + 7 , s.indexOf( '"', s.indexOf("title=") + 7)).substring(tech[0].length() + 1));
            case "clmns-3-j":
                if(!s.contains("КВ"))
                    for(int is = 4; is < 7; is++)
                        if(text.contains("clmns-" + is)) {
                            System.out.println("yes=" + is);
                            s = text.substring(text.indexOf("clmns-" + is));
                            break;
                }

                if( s.contains("СКВ"))
                    return "(" + s.substring(s.indexOf("СКВ"), s.indexOf('\n', s.indexOf("СКВ"))).replace("&nbsp;", "").replace("задачи", "").replace("\n", "") + ")";
                else
                    return "(" + s.substring(s.indexOf("КВ"), s.indexOf('\n', s.indexOf("КВ"))).replace("&nbsp;", "").replace("задачи", "").replace("\n", "") + ")";
            case "clmns-17-j":
                String work = "";
                for(int n = 0; n < 3 ; n++) {
                    if( n == 2)
                        work = s.substring(s.indexOf("title", i) + 7 , s.indexOf('"', s.indexOf("title", i) + 8) );
                    if(work.contains("Время отображено"))
                        work="  ";
                    else
                        i = s.indexOf("title", i) + 1;
                }
                tech[2] = s.substring(s.indexOf("с ", i), s.indexOf('<' , s.indexOf("с ", i)));
                return work;
            case "clmns-25-j":
                tech[9] = "";
                String tel = "";

                for( int n = 0;  s.indexOf("title", i) != -1 ; n++) {
                    if( n == 0 ) {
                        if (bol.func("", s.substring(s.indexOf("title=") + 7, s.indexOf('"', s.indexOf("title=") + 7))))
                            tel = s.substring(s.indexOf("title=") + 7, s.indexOf('"', s.indexOf("title=") + 7)).replace("\n", "");
                        else {
                            tel = "-нет номера";
                            tech[9] = s.substring(s.indexOf("title=") + 7, s.indexOf('"', s.indexOf("title=") + 7)).replace("\n", "");;
                        }
                    }
                    else
                        tech[9] +=  s.substring(s.indexOf("title", i) + 7 , s.indexOf('"', s.indexOf("title", i) + 8) ).replace("\n", "");
                    i = s.indexOf("title", i) + 1;
                }
                return "+7" + tel;
            case "clmns-28-j":

               // String s1 = s.substring(s.lastIndexOf("|||") + 4 ).replace("&quot;", "").replace("моп", "МОП").replace("GPLT-8(", "").replace("[...]","");
               // System.out.println("!!!->" + s.substring(s.lastIndexOf("|||") + 4 , s.indexOf("</", s.lastIndexOf("|||") + 4)).replace("моп", "МОП").replace("GPLT-8(", "").replace("[...]",""));
                String s1 =  s.substring(s.lastIndexOf("|||") + 4 , s.indexOf("</", s.lastIndexOf("|||") + 4)).replace("моп", "МОП").replace("GPLT-8(", "").replace("[...]","");


                if(s1.contains("DSL") & !s1.contains("[") & s1.contains("("))
                    s1 = s1.replace(s1.substring(s1.indexOf('(', s1.indexOf("-DSL")), s1.indexOf(')', s1.indexOf("-DSL")) + 1),
                            s1.substring(s1.indexOf('(', s1.indexOf("-DSL")), s1.indexOf(')', s1.indexOf("-DSL")) + 1).replace('(', '[').replace(')', ']'));

                if(s1.contains("DSL"))
                    tech[7] = tech[7].replace("FTTx", "DSL");

                if(s1.contains("[")) {
                    tech[15] = s1.substring(s1.lastIndexOf('[') + 1, s1.lastIndexOf(']')); // ip address

                    if(tech[7].contains("DSL")) {
                        tech[16] = o1.apply(s1.substring(s1.indexOf('-', s1.lastIndexOf(']')) + 1).trim()); // slot
                        tech[17] = o1.apply(s1.substring(s1.indexOf('-', s1.indexOf('-', s1.lastIndexOf(']')) + 1) + 1).trim()); // port
                        if( tech[16].length() == 0) tech[16] = null;
                        if( tech[17].length() == 0) tech[17] = null;
                    }

                    if(tech[7].contains("PON")) {

                        tech[16] = o.apply(s1.substring(s1.indexOf('-') + 1));
                        tech[17] = o.apply(s1.substring(  s1.indexOf('-', s1.indexOf('-') + 1 ) + 1 ));

                        if((tech[16].length() == 0 | tech[17].length() == 0) & s1.contains("МОП")) {

                            tech[16] = o.apply(s1.substring(s1.indexOf('-', s1.lastIndexOf(']')) + 1, s1.indexOf("МОП-"))).trim();
                            tech[17] = o.apply(s1.substring(s1.lastIndexOf("МОП-") + 4, s1.indexOf("TS", s1.lastIndexOf("МОП-") + 4)).trim());

                        }
                        tech[18] = o.apply(s1.substring(s1.lastIndexOf('-') + 1).trim()); // Ont
                       // if( tech[16] == null | tech[17] == null | tech[18] == null)
                       //     System.out.println(tech[7] + "=" + tech[16] + "|" + tech[17] + "|" + tech[18]);
                        if(tech[16].length() == 0 || tech[17].length()  == 0 || tech[18].length() == 0)
                            tech[16] = tech[17] = tech[18] = String.valueOf(0);
                    }
                    if(tech[7].contains("FTTx")) {

                        tech[18] = o1.apply(ser( s1));
                        System.out.println("FTTx=" + tech[18] + tech[18].length() );
                        System.out.println( s1 );
                        if(tech[18] == "" || tech[18] == " " || tech[18].length() == 0) tech[18] = null;
                    }
                    if(tech[7].contains("БШПД")) {
                        tech[18] = ser( s1); //port
                        System.out.print("BSHpD=" + tech[18] );
                        System.out.println( s1 );
                    }
                }
                else {
                    tech[15] = "0.0.0.0";
                    System.out.println("нет Тех.данных=" + s1);
                }
                if(s.indexOf("title") == s.lastIndexOf("title"))
                    tech[10] = ";  ";
                else
                    tech[10] = s.substring(s.lastIndexOf("title") + 7,  s.indexOf( '"', s.lastIndexOf("title") + 8)).replace("\n", "");     // Примчание
                System.out.println(tech[1] + '=' + tech[7] + "|" + tech[16] + "|" + tech[17] + "|" + tech[18]);
            default:
                if(s.indexOf("title") > 0 )
                    return s.substring(s.indexOf("title=") + 7 , s.indexOf( '"', s.indexOf("title=") + 7)).replace("&quot;", "").replace('\n', ' ');
                else
                    return "  ";
        }
    }

    private static String ser(String s) {
        String i;
        if (s.indexOf(']') == -1)
            i = o.apply(s.substring(s.indexOf('-') + 1).trim());
        else {
            i = ser(s.substring(s.indexOf(' ', s.indexOf(']')) + 1));
            if(i.equals("0") | i.equals(""))
                i = o.apply(s.substring(s.indexOf('-') + 1).trim());
        }
        return  i;
    }
}

