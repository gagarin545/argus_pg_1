import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.lang.System.out;

abstract class Inquiry {
    private static WebDriver driver;
    private static int tmlong = config.INTERVAL_L, tmout = config.INTERVAL;
    private static Connection con;
    private static Statement stmt;

    static SimpleDateFormat formatTime = new SimpleDateFormat("[HH:mm:ss]");
    private static int[] num_shpd = {
            //       номер записи
          // Интервал решения 1
            23,   // Адрес            2
            24,   // Пом.             3
            1,    // Услуга           4
            2,    // Заявлено         5
            16,   // Технология       6
            25,   // Телефон          7, Комментарии      9
       //     21,   // Прим. оп наряду  8
       //     23,   // Комментарии      9
            28,   // Тех.данные       10,   Прим. оп наряду  8
            17,   // Работник         11,   Интервал решения 1
            3,    // Контрольный срок 12
            //4,    // Контрольный срок 12
            //6,    // Контрольный срок 12
            11     // Кл.              13
            };
    private static int[] num_line = {
            //       номер записи
               // 3, Интервал решения 1
            24,   // Адрес            2
            25,   // Пом.             3
            1,    // Услуга           4
            2,    // Заявлено         5
            17,   // Технология       6
            26,   // Телефон          7
               // Прим. оп наряду  8
            29,   // Комментарии      9
            30,   // Тех.данные       10
            18,   // Работник         11  интервал по нар 1
            3,    // Контрольный срок 12
            //5,    // Контрольный срок 12
           // 6,    // Контрольный срок 12
           // 7,    // Контрольный срок 12
            12    // Кл.              13
    };

    void start_bd() {
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:" + config.PORT  + "/zlt36","ura", "Kukish54");
            con.setAutoCommit(false);
            System.out.println("-- Opened database successfully");
            String sql;
        }catch (Exception e) {

            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }

    static Boolean insert_bd(String str[]) {

        str[2] = str[2].substring(str[2].indexOf("до ") + 3);

        String sss ="'";
        for(int i= 2; i < 15; i++) {
            sss= sss  + str[i].replace("'", "") + "','";
        }

        String sql = format("select i_record(array[%s, %s, %s, %s, %s, %d ], array[%s%s', '%s']);"
                , str[0]
                , str[1]
                , str[16]
                , str[17]
                , str[18]
                , config.TIT
                , sss
                , str[15]
                , str[20]
        );

        out.println(sql);

        try {
            stmt = con.createStatement();
            stmt.executeQuery(sql);
            con.commit();
            stmt.executeUpdate(format ("update update_time set time_update = localtimestamp where  kod_tit = %s;", config.TIT));
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(sql);
            return false;
        }

        return true;
    }

    void prepareShpd(String s)  {
        int i, next;
        InquiryShpd testsh;

        i = s.indexOf("et\" target=");
        test<InquiryShpd, String> inShpd = InquiryShpd::new;

        while (i > 0) {
            try {
                next = s.indexOf("target=", i + 11);

                if ( next == -1)
                    testsh = Store.MyClasFactory(inShpd, s.substring(i), "");
                else
                    testsh = Store.MyClasFactory(inShpd, s.substring(i, next), "");
                i += testsh.compare(num_shpd);  StoreInquiry.count++;

            }catch (StringIndexOutOfBoundsException | InterruptedException SIO) {
                    System.out.println("Код завершения -->" + SIO + " " + i);
                    i = s.indexOf("et\" target=", i + 10);
            }
        }
    }


    void prepareLine(String s) {
        int i, next;
        InquiryLine testLine;

        i = s.indexOf("et\" target=");
        test<InquiryLine, String> inLine = InquiryLine::new;

        while (i > 0) {

            try {
                next = s.indexOf("target=", i + 11);

                if ( next == -1)
                    testLine = Store.MyClasFactory(inLine, s.substring(i), "");
                else
                    testLine = Store.MyClasFactory(inLine, s.substring(i, next), "");

                i += testLine.compare(num_line);    StoreInquiry.count++;

            }catch (StringIndexOutOfBoundsException | InterruptedException SIO) {
                System.out.println("Код завершения -->" + SIO + " " + i);
                i = s.indexOf("et\" target=", i + 10);
            }

        }

    }


    Boolean init() throws InterruptedException {
        int interval = 3;
        String url_name = "http://argusweb.ur.rt.ru:8080/argus";

        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true");
        System.setProperty("webdriver.gecko.driver", config.FIREFOX);

        driver =new FirefoxDriver();
        driver.get(url_name);

        Runid(driver,"login_form-username", config.LOGIN, tmlong );
        Runid(driver,"login_form-password", config.PASSWORD, tmlong );
        Runid(driver,"login_form-submit",null,  tmlong );
        TimeUnit.SECONDS.sleep(interval);


        Runccs(driver,"li.ui-menu-parent:nth-child(2)",tmlong);
        out.println("Задачи");
        TimeUnit.SECONDS.sleep(interval);

        Runccs(driver,"li.ui-menu-parent:nth-child(2) > ul:nth-child(2) > li:nth-child(1) > a:nth-child(1) > span:nth-child(1)",tmlong);
        out.println("список задач");
        TimeUnit.SECONDS.sleep(interval);
        //TimeUnit.SECONDS.sleep(tmout);

        Runccs(driver,"#slcts-slct_acc-dsp_f_title > span:nth-child(1)",tmlong);
        out.println("участки");
        TimeUnit.SECONDS.sleep(interval);
        //TimeUnit.SECONDS.sleep(tmout);

        Runccs(driver,config.MC, tmlong);  // МЦТЭТ
        out.println("МЦТЭТ");
        TimeUnit.SECONDS.sleep(interval);
        //TimeUnit.SECONDS.sleep(tmout);

        return true;
    }

    static Boolean shutdriver() {

        Runccs(driver,"#mmf-logout", tmlong);
        out.println("Выход");

        driver.close();

        return false;
    }


    String ReadShpd() throws InterruptedException {

        Runccs(driver,"#slcts-slct_acc-dsp_f-role_select_label", tmlong);
        TimeUnit.SECONDS.sleep(tmout);

        Runccs(driver,"#slcts-slct_acc-dsp_f-role_select_9", tmlong);
        TimeUnit.SECONDS.sleep(config.INTERVAL_L);

        out.println("ШПД завтра");

        return driver.getPageSource();
    }
    String ReadShpd_today() throws InterruptedException {
        String str = null;

        if(driver.findElements(By.cssSelector("#global_fltr_f-j_idt534-0-status_value > div:nth-child(2) > span:nth-child(1)")).size() > 0)
            str = "#global_fltr_f-j_idt534-0-status_value > div:nth-child(2) > span:nth-child(1)";
        if(driver.findElements(By.cssSelector("#global_fltr_f-j_idt502-0-status_value > div:nth-child(2) > span:nth-child(1)")).size() > 0)
            str = "#global_fltr_f-j_idt502-0-status_value > div:nth-child(2) > span:nth-child(1)";
        if(driver.findElements(By.cssSelector("#global_fltr_f-j_idt484-0-status_value > div:nth-child(2) > span:nth-child(1)")).size() > 0)
            str = "#global_fltr_f-j_idt484-0-status_value > div:nth-child(2) > span:nth-child(1)";
        if(driver.findElements(By.cssSelector("#global_fltr_f-j_idt505-0-status_value > div:nth-child(2) > span:nth-child(1)")).size() > 0)
            str = "#global_fltr_f-j_idt505-0-status_value > div:nth-child(2) > span:nth-child(1)";

        if(driver.findElements(By.cssSelector("#global_fltr_f-j_idt515-0-status_value > div:nth-child(2) > span:nth-child(1)")).size() > 0)
            str = "#global_fltr_f-j_idt515-0-status_value > div:nth-child(2) > span:nth-child(1)";
        if(driver.findElements(By.cssSelector("#global_fltr_f-j_idt513-0-status_value > div:nth-child(2) > span:nth-child(1)")).size() > 0)
            str = "#global_fltr_f-j_idt513eee-0-status_value > div:nth-child(2) > span:nth-child(1)";

        if(driver.findElements(By.cssSelector("#global_fltr_f-j_idt521-0-status_value > div:nth-child(2) > span:nth-child(1)")).size() > 0)
            str = "#global_fltr_f-j_idt521-0-status_value > div:nth-child(2) > span:nth-child(1)";


        System.out.println(str);
        Runccs(driver, str, tmlong);
        TimeUnit.SECONDS.sleep(config.INTERVAL_L);

        out.println("ШПД сегодня");

        return driver.getPageSource();
    }

    String ReadLine_Next() throws InterruptedException {
        String str = null;

        if(driver.findElements(By.cssSelector("#global_fltr_f-j_idt534-1-status_value > div:nth-child(2) > span:nth-child(1)")).size() > 0)
            str = "#global_fltr_f-j_idt534-1-status_value > div:nth-child(2) > span:nth-child(1)";
        if(driver.findElements(By.cssSelector("#global_fltr_f-j_idt502-1-status_value > div:nth-child(2) > span:nth-child(1)")).size() > 0)
            str = "#global_fltr_f-j_idt502-1-status_value > div:nth-child(2) > span:nth-child(1)";
        if(driver.findElements(By.cssSelector("#global_fltr_f-j_idt484-1-status_value > div:nth-child(2) > span:nth-child(1)")).size() > 0)
            str = "#global_fltr_f-j_idt484-1-status_value > div:nth-child(2) > span:nth-child(1)";
        if(driver.findElements(By.cssSelector("#global_fltr_f-j_idt505-1-status_value > div:nth-child(2) > span:nth-child(1)")).size() > 0)
            str = "#global_fltr_f-j_idt505-1-status_value > div:nth-child(2) > span:nth-child(1)";

        if(driver.findElements(By.cssSelector("#global_fltr_f-j_idt515-1-status_value > div:nth-child(2) > span:nth-child(1)")).size() > 0)
            str = "#global_fltr_f-j_idt515-1-status_value > div:nth-child(2) > span:nth-child(1)";
        if(driver.findElements(By.cssSelector("#global_fltr_f-j_idt541-1-status_value > div:nth-child(2) > span:nth-child(1)")).size() > 0)
            str = "#global_fltr_f-j_idt541-1-status_value > div:nth-child(2) > span:nth-child(1)";

        if(driver.findElements(By.cssSelector("#global_fltr_f-j_idt513-1-status_value > div:nth-child(2) > span:nth-child(1)")).size() > 0)
            str = "#global_fltr_f-j_idt513-1-status_value > div:nth-child(2) > span:nth-child(1)";
        if(driver.findElements(By.cssSelector("#global_fltr_f-j_idt521-1-status_value > div:nth-child(2) > span:nth-child(1)")).size() > 0)
            str = "#global_fltr_f-j_idt521-1-status_value > div:nth-child(2) > span:nth-child(1)";

        System.out.println(str);

        Runccs(driver, str, tmlong);

        TimeUnit.SECONDS.sleep(config.INTERVAL_L);

        out.println("линия завтра");

        return driver.getPageSource();
    }

    String ReadLine() throws InterruptedException {

        Runccs(driver,"#slcts-slct_acc-dsp_f-role_select_label", tmlong);
 //       TimeUnit.SECONDS.sleep(tmout);

        Runccs(driver,"#slcts-slct_acc-dsp_f-role_select_7", tmlong);
        TimeUnit.SECONDS.sleep(config.INTERVAL_L);

        out.println("Линия сегодня");

        return driver.getPageSource();
    }

    private static void Runid(WebDriver dr, final String str, String val, int tm) {

      //  (new WebDriverWait(dr,tm, tm)).until(new ExpectedCondition<WebElement>() {
       //     public WebElement apply(WebDriver d) {
         //       return d.findElement(By.id(str));
           // }
       // });

        WebElement element = dr.findElement(By.id(str));
        (new WebDriverWait(dr, tm)).withMessage("Элемент не найден.")
                .until(ExpectedConditions.visibilityOf(element));

        if( val == null)
            dr.findElement(By.id(str)).click();
        else
            dr.findElement(By.id(str)).sendKeys(val);
    }

    private static void Runccs(WebDriver dr, final String str, long tm)  {

        try {
            WebElement element = dr.findElement(By.cssSelector(str));
            (new WebDriverWait(dr, tm, 500)).withMessage("Элемент не найден.")
                    .until(ExpectedConditions.visibilityOf(element));

           // (new WebDriverWait(dr, tm, 1000)).until(new ExpectedCondition<WebElement>() {
             //   public WebElement apply(WebDriver d) {
               //     return d.findElement(By.cssSelector(str));
             //   }
           // });

            dr.findElement(By.cssSelector(str)).click();
        }catch ( ElementNotInteractableException el) {
            StoreInquiry.Start = false;
            System.out.println("Элемент не найден. Состояние start = " + StoreInquiry.Start );
        }
    }

}

