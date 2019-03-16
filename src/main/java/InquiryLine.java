import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import static java.lang.String.format;

class InquiryLine {

    private static String text;
    private static String k_num = "";
    private static String[] tech;

    InquiryLine(String text, String v) {
        this.text = text;

      // System.out.println( text);
    }

    int compare(int[] nums) throws InterruptedException {
        String p0 = "clmns-";
        tech = new String[21];
        int[] n = { 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14};
        int k = 1;
        tech[1] = text.substring(text.indexOf('-') + 2, text.indexOf('<'));       // номер наряда
        tech[20] = "Л";

        for(int i : nums) {
            if(  text.indexOf(p0, text.indexOf(p0 + i ) + 1) > 0  & text.indexOf( p0 + i) > 0 & i < 30) {
                tech[n[k++]] = form_title(text.substring(text.indexOf(p0 + i), text.indexOf(p0, text.indexOf(p0 + i) + 1)).replace("|", ""));
            }
            if(i == 30)
                tech[n[k++]] = form_title( text.substring( text.indexOf(p0 + i) , text.indexOf("div" , text.indexOf(p0 + i) + 1) ).replace("|", ""));
        }

      //  System.out.println("!!!!Line->" + Arrays.toString(tech) + "\n");

        StoreInquiry.Start = Inquiry.insert_bd(tech);

        return text.length();
    }

    private static String form_title(String s) throws InterruptedException {
        int i = 0;
        switch (s.substring(0,s.indexOf('_'))) {
            case "clmns-1-j":
                String r = s.substring(s.indexOf("title=") + 7, s.indexOf('"', s.indexOf("title=") + 7));
                if( r.contains("TUBE"))
                    return  "#77" + r.substring(r.indexOf("TUBE") + 4 );
                if( (s.substring(s.indexOf("title=") + 7).contains("СПД")))
                    tech[0] =  InquiryShpd.o.apply(s.substring(s.indexOf("title=") + 7 , s.indexOf( '"', s.indexOf("title=") + 7))).trim();
                else
                    tech[0] = s.substring(s.indexOf("title=") + 7 , s.indexOf("title=") + 12 ); // Код города

                return  r.substring(4);
            case "clmns-3-j":
                if(!s.contains("КВ"))
                    for(int is = 4; is < 7; is++)
                        if(text.contains("clmns-" + is)) {
                            System.out.println("yes=" + is);
                            s = text.substring(text.indexOf("clmns-" + is));
                            break;
                        }

                if( s.contains("СКВ"))
                    return "(" + s.substring(s.indexOf("СКВ"), s.indexOf(')', s.indexOf("СКВ"))).replace("&nbsp;", "").replace("задачи", "").replace("\n", "") + ")";
                else
                    return "(" +  s.substring(s.indexOf("КВ"), s.indexOf(')', s.indexOf("КВ"))).replace("&nbsp;", "").replace("задачи", "").replace("\n","") + ")";
            //case "clmns-15-j":  return s.substring(s.indexOf("с "), s.indexOf('<' , s.indexOf("с "))) ;
            case "clmns-26-j":
                tech[9] = "";
                String tel = "";
                for( int n = 0;  s.indexOf("title", i) != -1 ; n++) {
                    if( n == 0 )
                        if (InquiryShpd.bol.func("", s.substring(s.indexOf("title=") + 7, s.indexOf('"', s.indexOf("title=") + 7))))
                            tel = s.substring(s.indexOf("title=") + 7, s.indexOf('"', s.indexOf("title=") + 7));
                        else {
                            tel = "-нет номера";
                            tech[9] = s.substring(s.indexOf("title=") + 7, s.indexOf('"', s.indexOf("title=") + 7));
                        }
                    else
                        tech[9] +=  s.substring(s.indexOf("title", i) + 7 , s.indexOf('"', s.indexOf("title", i) + 8) ).replace("\n", "");
                    i = s.indexOf("title", i) + 1;
                }
                return "+7" + tel ;
            case "clmns-18-j":
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
            case "clmns-29-j":
                k_num = s.substring(s.indexOf("title=") + 8, s.indexOf(" ", s.indexOf("title=") + 10));
                setK_num( s.substring(s.indexOf("title=") + 8, s.indexOf(" ", s.indexOf("title=") + 10)));

                return s.substring(s.indexOf("title=") + 7 , s.indexOf( "</span>", s.indexOf("title=") + 7)).replace("&quot;", "");
            case "clmns-30-j":
                for( int n = 0;  s.indexOf("title", n) != -1 ; n =  s.indexOf("title", n) + 1 ) {
                    if( k_num.contains("нет")) {
                        setK_num(s.substring(s.indexOf("title=") + 8, s.indexOf(" ", s.indexOf("title=") + 10)));
                        tech[11] = s.substring(s.indexOf("title=") + 7 , s.indexOf( "</span>", s.indexOf("title=") + 7)).replace("&quot;", "");
                        tech[10] = "нет";
                    }
                    else
                        return  s.substring(s.lastIndexOf("title") + 7,  s.indexOf( '"', s.lastIndexOf("title") + 8)).replace("\n", "");     // Примчание
                }
            default:
                if(s.indexOf("title") > 0 )
                    return s.substring(s.indexOf("title=") + 7 , s.indexOf( '"', s.indexOf("title=") + 7)).replace("&quot;", "");
                else
                    return " ";
        }
    }

    private static void setK_num(String k){
        switch (k){
            case "К62":
            case "К62/1":
            case "К65":
                tech[15] = "192.168.12.1";  break;
            case "К66":
                tech[15] = "10.183.5.66";   break;
            case "К67":
                tech[15] = "10.183.5.67";   break;
            case "К1":
            case "К2":
            case "К3":
            case "К13":
                tech[15] = "10.11.104.21";  break;
            case "К4":
            case "К5":
            case "К6":
            case "К7":
            case "К8":
            case "К9":
            case "К10":
            case "К11":
            case "К12":
                tech[15] = "10.11.104.20"; break;
            default:    tech[15] = "0.0.0.0"; break;
        }
    }
}
