import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.*;
import static java.lang.String.format;


class StoreInquiry extends Inquiry {

    static boolean Start = false;
    static int count;
    private ScheduledExecutorService executor_inqury = Executors.newScheduledThreadPool(6);
    private  int interval_read = config.INTERVAL;

    void inquiryOrder() throws InterruptedException, ExecutionException, IOException {


        while (!Start) {
            System.out.println("Start");

            start_bd();

            ScheduledFuture<?> fut_init = executor_inqury.schedule(inquiryInit, config.INTERVAL, TimeUnit.SECONDS);
            Start = Boolean.parseBoolean(format("%b",fut_init.get()));

            while (Start) {

                System.out.println(Calendar.getInstance().getTime());
                count = 0;
                prepareLine(String.valueOf(executor_inqury.schedule(inquiryline, config.INTERVAL, TimeUnit.SECONDS).get()));
                if (!Start) {                    System.out.println("line - > " + Start);                    break; }

                prepareLine(String.valueOf(executor_inqury.schedule(next, config.INTERVAL, TimeUnit.SECONDS).get()));
                if (!Start) {                    System.out.println("line - > " + Start);                    break;                }
                System.out.println("Обработанно записей(наряды линия)->" + count + " Время-" + Calendar.getInstance().getTime());

                prepareShpd(String.valueOf(executor_inqury.schedule(inquiryshpd, config.INTERVAL, TimeUnit.SECONDS).get()));
                if (!Start) {                    System.out.println("line - > " + Start);                    break; }

                prepareShpd(String.valueOf(executor_inqury.schedule(previous, config.INTERVAL, TimeUnit.SECONDS).get()));
                if (!Start) {                    System.out.println("line - > " + Start);                    break; }
                System.out.println("Обработанно записей(наряды линия + ШПД)->" + count + " Время-" + Calendar.getInstance().getTime());

                Start = calendar_read.func(config.START, config.STOP);

                System.out.println("Состояние=" + Start + " Пауза=" + interval_read);

                TimeUnit.MINUTES.sleep( interval_read);
            }
            Start = shutdriver();
            System.out.println("Stop!");
            while (!calendar_read.func(config.START, config.STOP));

           // StartSocket.his.clear_history();
        }
    }

    private Callable inquiryInit = this::init;
    private Callable inquiryline = this::ReadLine;
    private Callable next = this::ReadLine_Next;
    private Callable inquiryshpd = this::ReadShpd;
    private Callable previous = this::ReadShpd_today;

    private test<Boolean, Integer> calendar_read = (t1, t2) -> {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY) > t1 & calendar.get(Calendar.HOUR_OF_DAY) < t2 ;
    };
}
